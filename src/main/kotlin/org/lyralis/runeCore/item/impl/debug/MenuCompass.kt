package org.lyralis.runeCore.item.impl.debug

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.lyralis.runeCore.item.CustomItem
import org.lyralis.runeCore.item.CustomItemType
import org.lyralis.runeCore.item.ItemRarity
import org.lyralis.runeCore.item.model.CustomItemAction
import org.lyralis.runeCore.item.model.CustomItemEnchantment

object MenuCompass : CustomItem, CustomItemType.Usable {
    override val id = "menu_book"
    override val displayName = "メニューブック"
    override val lore =
        listOf(
            "左クリックでメニューを開きます",
            "/menu でも開くことができます",
        )
    override val material = Material.COMPASS
    override val rarity = ItemRarity.RARE
    override val enchantments: List<CustomItemEnchantment> =
        listOf(
            CustomItemEnchantment(Enchantment.UNBREAKING, 3),
        )

    override fun onUse(
        player: Player,
        action: CustomItemAction,
    ): Boolean {
        if (action != CustomItemAction.LEFT_CLICK) return false
        player.performCommand("menu")
        return true
    }
}
