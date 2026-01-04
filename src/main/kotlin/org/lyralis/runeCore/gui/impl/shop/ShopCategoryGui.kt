package org.lyralis.runeCore.gui.impl.shop

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.component.message.errorMessage
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.domain.shop.ShopCategory
import org.lyralis.runeCore.domain.shop.ShopItem
import org.lyralis.runeCore.domain.shop.ShopItemRegistry
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.gui.template.showPaginatedGui
import org.lyralis.runeCore.item.ItemRegistry

/**
 * カテゴリー別アイテム一覧GUI
 */
class ShopCategoryGui(
    private val moneyService: MoneyService,
    private val category: ShopCategory,
    private val parentGui: ShopMainGui,
) {
    /**
     * カテゴリー別アイテム一覧を開く
     *
     * @param player 対象のプレイヤー
     * @return GuiResult
     */
    fun open(player: Player): GuiResult<Unit> {
        val items = ShopItemRegistry.byCategory(category)

        if (items.isEmpty()) {
            player.sendMessage("このカテゴリーにはまだアイテムがありません".errorMessage())
            return parentGui.open(player)
        }

        return player.showPaginatedGui {
            title = category.displayName

            items(items)

            render { shopItem ->
                createShopItemStack(shopItem, player)
            }

            onItemClick { shopItem, action ->
                when {
                    action.isLeftClick -> processPurchase(action.player, shopItem, 1)
                    action.isRightClick && shopItem.bulkPurchaseEnabled ->
                        processPurchase(action.player, shopItem, 64)
                }
                open(action.player)
                GuiResult.Silent
            }

            onBack { p ->
                parentGui.open(p)
            }
        }
    }

    private fun createShopItemStack(
        shopItem: ShopItem,
        player: Player,
    ): ItemStack {
        val balance = moneyService.getBalance(player.uniqueId)
        val canAfford = balance >= shopItem.price
        val canAfford64 = balance >= shopItem.price * 64uL

        // カスタムアイテムの場合は ItemRegistry から取得
        val customItemId = shopItem.customItemId
        val baseItem =
            if (customItemId != null) {
                ItemRegistry.getById(customItemId)?.createItemStack(1)
                    ?: ItemStack(shopItem.material)
            } else {
                ItemStack(shopItem.material)
            }

        return baseItem.apply {
            editMeta { meta ->
                meta.displayName(
                    Component.text(shopItem.displayName).color(shopItem.rarity.color),
                )

                val loreList = mutableListOf<Component>()

                // アイテムの説明を追加
                if (shopItem.description.isNotEmpty()) {
                    shopItem.description.forEach { line ->
                        loreList.add(Component.text("§7$line"))
                    }
                    loreList.add(Component.empty())
                }

                loreList.add(
                    Component
                        .text("§7レア度: ")
                        .append(Component.text(shopItem.rarity.displayName).color(shopItem.rarity.color)),
                )
                loreList.add(Component.text("§7価格: §e${shopItem.price} Rune"))
                loreList.add(Component.empty())

                if (canAfford) {
                    loreList.add(Component.text("§a左クリック: 1個購入"))
                    if (shopItem.bulkPurchaseEnabled) {
                        if (canAfford64) {
                            loreList.add(Component.text("§a右クリック: 64個購入"))
                        } else {
                            loreList.add(Component.text("§c右クリック: 残高不足 (64個)"))
                        }
                    }
                } else {
                    loreList.add(Component.text("§c残高不足のため，購入できません"))
                }

                meta.lore(loreList)
            }
        }
    }

    private fun processPurchase(
        player: Player,
        shopItem: ShopItem,
        amount: Int,
    ) {
        val totalPrice = shopItem.price * amount.toULong()

        val result = moneyService.subtractBalance(player, totalPrice)
        if (result != null) {
            val itemStack = shopItem.createPurchaseItemStack(amount)
            val leftover = player.inventory.addItem(itemStack)

            leftover.values.forEach { excess ->
                player.world.dropItemNaturally(player.location, excess)
            }

            player.sendMessage("${shopItem.displayName} x$amount を購入しました ($totalPrice Rune)".infoMessage())
        } else {
            player.sendMessage("残高が不足しています".errorMessage())
        }
    }
}
