package org.lyralis.runeCore.item

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.lyralis.runeCore.permission.Permission

/**
 * カスタムアイテムの基底インターフェース
 *
 * 全てのカスタムアイテムはこのインターフェースを実装する．
 * アイテムの種別に応じて [CustomItemType] の sub-interface も実装する．
 *
 * - 使用可能アイテム: [CustomItemType.Usable]
 * - 消費アイテム: [CustomItemType.Consumable]
 * - 武器: [CustomItemType.Weapon]
 * - 防具: [CustomItemType.Armor]
 */
interface CustomItem {
    /**
     * アイテムの一意な識別子
     *
     * PDC (PersistentDataContainer) でアイテムを識別するために使用される．
     */
    val id: String

    /**
     * アイテムの表示名
     */
    val displayName: String

    /**
     * アイテムの説明文（Lore）
     */
    val lore: List<String>
        get() = emptyList()

    /**
     * アイテムのマテリアル
     */
    val material: Material

    /**
     * アイテムのレアリティ
     */
    val rarity: ItemRarity

    /**
     * アイテムを使用するために必要な権限
     *
     * null の場合は権限チェックを行わない．
     */
    val requiredPermission: Permission?
        get() = null

    /**
     * このカスタムアイテムの ItemStack を生成する
     *
     * @param amount アイテムの個数
     * @return 生成された ItemStack
     */
    fun createItemStack(amount: Int = 1): ItemStack {
        val item = ItemStack(material, amount)
        val meta = item.itemMeta

        meta.displayName(
            Component.text(displayName).color(rarity.color),
        )

        val fullLore =
            buildList {
                addAll(lore.map { Component.text(it).color(NamedTextColor.GRAY) })
                add(Component.empty())
                add(Component.text(rarity.displayName).color(rarity.color))
            }
        meta.lore(fullLore)

        ItemRegistry.setItemId(meta, id)

        if (this is CustomItemType.Equippable) {
            val slotGroup = equipmentSlot.getGroup()

            stats.toAttributeModifiers(slotGroup).forEach { (attribute, modifier) ->
                meta.addAttributeModifier(attribute, modifier)
            }

            enchantments.forEach { itemEnchantment ->
                meta.addEnchant(itemEnchantment.enchantment, itemEnchantment.level, true)
            }

            if (meta is Damageable) {
                if (stats.unbreakable) {
                    meta.isUnbreakable = true
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE)
                } else {
                    stats.durability?.let { customDurability ->
                        val maxDurability = material.maxDurability.toInt()
                        if (customDurability < maxDurability) {
                            meta.damage = maxDurability - customDurability
                        }
                    }
                }
            }
        } else if (this is CustomItemType.Usable) {
            enchantments.forEach { itemEnchantment ->
                meta.addEnchant(itemEnchantment.enchantment, itemEnchantment.level, true)
            }
        }

        item.itemMeta = meta
        return item
    }
}
