package org.lyralis.runeCore.item

import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.lyralis.runeCore.item.model.CustomItemAction
import org.lyralis.runeCore.item.model.CustomItemEnchantment
import org.lyralis.runeCore.item.model.CustomItemStatus

/**
 * アイテムの種別を表す sealed interface
 */
sealed interface CustomItemType {
    /**
     * 使用可能アイテム（右クリック/左クリックで効果発動）
     */
    interface Usable : CustomItemType {
        /**
         * 右クリックで使用可能か
         */
        val isUsableOnRightClick: Boolean
            get() = true

        /**
         * 左クリックで使用可能か
         */
        val isUsableOnLeftClick: Boolean
            get() = false

        val enchantments: List<CustomItemEnchantment>
            get() = emptyList()

        /**
         * アイテム使用時の処理
         *
         * @param player 使用したプレイヤー
         * @param action 実行されたアクション
         * @return 処理成功時は true
         */
        fun onUse(
            player: Player,
            action: CustomItemAction,
        ): Boolean
    }

    /**
     * 消費アイテム（使用後に消滅）
     */
    interface Consumable : Usable {
        /**
         * 1回の使用で消費する個数
         */
        val consumeAmount: Int
            get() = 1
    }

    /**
     * 装備アイテム（武器・防具）
     */
    interface Equippable : CustomItemType {
        /**
         * 装備スロット
         */
        val equipmentSlot: EquipmentSlot

        /**
         * アイテムのステータス
         */
        val stats: CustomItemStatus

        /**
         * エンチャントリスト
         */
        val enchantments: List<CustomItemEnchantment>
            get() = emptyList()
    }

    /**
     * 武器アイテム
     */
    interface Weapon : Equippable {
        override val equipmentSlot: EquipmentSlot
            get() = EquipmentSlot.HAND
    }

    /**
     * 防具アイテム
     */
    interface Armor : Equippable
}
