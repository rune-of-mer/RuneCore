package org.lyralis.runeCore.item

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin

/**
 * カスタムアイテムの登録と検索を管理するレジストリ
 *
 * 全てのカスタムアイテムはここに登録され，PDC を通じて識別される．
 */
object ItemRegistry {
    private lateinit var itemIdKey: NamespacedKey
    private val items = mutableMapOf<String, CustomItem>()

    /**
     * レジストリを初期化する
     *
     * @param plugin プラグインインスタンス
     */
    fun initialize(plugin: JavaPlugin) {
        itemIdKey = NamespacedKey(plugin, "custom_item_id")
    }

    /**
     * カスタムアイテムを登録する
     *
     * @param item 登録するカスタムアイテム
     * @throws IllegalArgumentException 同じIDのアイテムが既に登録されている場合
     */
    fun register(item: CustomItem) {
        require(!items.containsKey(item.id)) {
            "Item with id '${item.id}' is already registered"
        }
        items[item.id] = item
    }

    /**
     * 複数のカスタムアイテムを一括登録する
     *
     * @param itemList 登録するカスタムアイテムのリスト
     */
    fun registerAll(vararg itemList: CustomItem) {
        itemList.forEach { register(it) }
    }

    /**
     * IDからカスタムアイテムを取得する
     *
     * @param id アイテムID
     * @return 該当するカスタムアイテム，存在しない場合は null
     */
    fun getById(id: String): CustomItem? = items[id]

    /**
     * ItemStack からカスタムアイテムを取得する
     *
     * @param itemStack 検査する ItemStack
     * @return 該当するカスタムアイテム，カスタムアイテムでない場合は null
     */
    fun getFromItemStack(itemStack: ItemStack?): CustomItem? {
        if (itemStack == null || !itemStack.hasItemMeta()) return null
        val id = getItemId(itemStack.itemMeta) ?: return null
        return getById(id)
    }

    /**
     * ItemStack がカスタムアイテムかどうかを判定する
     *
     * @param itemStack 検査する ItemStack
     * @return カスタムアイテムの場合は true
     */
    fun isCustomItem(itemStack: ItemStack?): Boolean = getFromItemStack(itemStack) != null

    /**
     * 登録されている全てのカスタムアイテムを取得する
     *
     * @return 登録済みカスタムアイテムのリスト
     */
    fun getAllItems(): List<CustomItem> = items.values.toList()

    /**
     * 指定したレアリティのアイテムを取得する
     *
     * @param rarity フィルタするレアリティ
     * @return 該当するカスタムアイテムのリスト
     */
    fun getByRarity(rarity: ItemRarity): List<CustomItem> = items.values.filter { it.rarity == rarity }

    /**
     * ガチャで排出可能なアイテムを取得する（weight > 0）
     *
     * @return ガチャ対象のカスタムアイテムのリスト
     */
    fun getGachaItems(): List<CustomItem> = items.values.filter { it.rarity.weight > 0 }

    /**
     * ItemMeta に アイテムID を設定する
     *
     * @param meta 対象の ItemMeta
     * @param id 設定するアイテムID
     */
    internal fun setItemId(
        meta: ItemMeta,
        id: String,
    ) {
        meta.persistentDataContainer.set(itemIdKey, PersistentDataType.STRING, id)
    }

    /**
     * ItemMeta からアイテムID を取得する
     *
     * @param meta 対象の ItemMeta
     * @return アイテムID，存在しない場合は null
     */
    private fun getItemId(meta: ItemMeta): String? = meta.persistentDataContainer.get(itemIdKey, PersistentDataType.STRING)
}
