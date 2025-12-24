package org.lyralis.runeCore.gui.impl.shop

import org.bukkit.Material
import org.bukkit.entity.Player
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.database.impl.money.MoneyService
import org.lyralis.runeCore.database.model.shop.ShopCategory
import org.lyralis.runeCore.gui.asGuiItem
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * ショップメインGUI - カテゴリー選択画面
 */
class ShopMainGui(
    private val moneyService: MoneyService,
) {
    /**
     * カテゴリー選択画面を開く
     *
     * @param player 対象のプレイヤー
     * @return GuiResult
     */
    fun open(player: Player): GuiResult<Unit> {
        val balance = moneyService.getBalance(player.uniqueId)

        return player.openGui {
            title = "ショップ"
            rows = 4

            structure {
                +"# # # # 1 # # # #"
                +"# A B C D E F G #"
                +"# H I . . . . S #"
                +"# # # # # # # # #"
            }

            decoration('#', Material.BLACK_STAINED_GLASS_PANE)
            decoration('.', Material.GRAY_STAINED_GLASS_PANE)

            val categories = ShopCategory.entries
            val slots = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I')

            item('1') {
                material = Material.EMERALD
                displayName = "所持金: $balance Rune"
                lore =
                    listOf(
                        "お金は動物を倒したり、鉱物を破壊すると入手できます",
                    )
            }

            categories.forEachIndexed { index, category ->
                if (index < slots.size) {
                    item(slots[index]) {
                        customItem =
                            category.icon.asGuiItem {
                                displayName = "§e${category.displayName}"
                                lore(
                                    "",
                                    "§7${category.description}",
                                    "",
                                    "§aクリックでカテゴリーを開きます",
                                )
                            }
                        onClick {
                            ShopCategoryGui(moneyService, category, this@ShopMainGui).open(it.player)
                            GuiResult.Silent
                        }
                    }
                }
            }

            item('S') {
                customItem =
                    Material.COMPASS.asGuiItem {
                        displayName = "§b検索"
                        lore(
                            "",
                            "§7キーワードで購入するアイテムを検索することができます",
                            "",
                            "§aクリックしてキーワードを入力",
                        )
                    }
                onClick { action ->
                    action.player.closeInventory()
                    ShopSearchManager.startAwaiting(action.player)
                    action.player.sendMessage("検索したいアイテム名をチャットに入力してください ('cancel' でキャンセルします)".infoMessage())
                    GuiResult.Silent
                }
            }
        }
    }
}
