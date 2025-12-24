package org.lyralis.runeCore.gui.impl.gacha

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.database.impl.gacha.GachaRewardItem
import org.lyralis.runeCore.database.impl.gacha.GachaService
import org.lyralis.runeCore.gui.asGuiItem
import org.lyralis.runeCore.gui.openGui
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.item.ItemRarity

/**
 * ガチャ排出アイテム一覧GUI
 */
class GachaItemListGui(
    private val gachaService: GachaService,
    private val detailGui: GachaDetailGui,
) {
    companion object {
        private const val ITEMS_PER_PAGE = 45
    }

    /**
     * アイテム一覧GUIを開く
     */
    fun open(
        player: Player,
        eventId: String,
        page: Int = 0,
    ): GuiResult<Unit> {
        val event = gachaService.getEventById(eventId) ?: return GuiResult.Failure.Custom("ガチャイベントが見つかりません")

        val allItems =
            gachaService.getGachaItems(eventId).sortedByDescending {
                getRarityOrder(it.rarity)
            }

        val totalPages = (allItems.size + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE
        val currentPage = page.coerceIn(0, maxOf(0, totalPages - 1))
        val startIndex = currentPage * ITEMS_PER_PAGE
        val pageItems = allItems.drop(startIndex).take(ITEMS_PER_PAGE)

        return player.openGui {
            title = "§6${event.displayName} - 排出アイテム"
            rows = 6

            structure {
                +". . . . . . . . ."
                +". . . . . . . . ."
                +". . . . . . . . ."
                +". . . . . . . . ."
                +". . . . . . . . ."
                +"B # # < I > # # #"
            }

            decoration('#', Material.BLACK_STAINED_GLASS_PANE)
            decoration('.', Material.AIR)

            // アイテムを配置（最初の45スロット）
            val slots =
                listOf(
                    '0', '1', '2', '3', '4', '5', '6', '7', '8',
                    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
                    'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
                    's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A',
                    'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L',
                )

            // 構造を再定義（アイテム用）
            structure {
                +"0 1 2 3 4 5 6 7 8"
                +"a b c d e f g h i"
                +"j k l m n o p q r"
                +"s t u v w x y z A"
                +"C D E F G H J K L"
                +"B # # < I > # # #"
            }

            pageItems.forEachIndexed { index, rewardItem ->
                if (index < slots.size) {
                    item(slots[index]) {
                        customItem = createPreviewItem(rewardItem)
                    }
                }
            }

            // 空きスロットを装飾
            for (index in pageItems.size until slots.size) {
                decoration(slots[index], Material.LIGHT_GRAY_STAINED_GLASS_PANE)
            }

            // ページ情報
            item('I') {
                customItem =
                    Material.PAPER.asGuiItem {
                        displayName = "§eページ ${currentPage + 1} / $totalPages"
                        lore(
                            "",
                            "§7全 ${allItems.size} アイテム",
                        )
                    }
            }

            // 前のページ
            item('<') {
                customItem =
                    (if (currentPage > 0) Material.ARROW else Material.GRAY_DYE).asGuiItem {
                        displayName = if (currentPage > 0) "§a前のページ" else "§7前のページ"
                    }
                onClick { action ->
                    if (currentPage > 0) {
                        open(action.player, eventId, currentPage - 1)
                    }
                    GuiResult.Silent
                }
            }

            // 次のページ
            item('>') {
                customItem =
                    (if (currentPage < totalPages - 1) Material.ARROW else Material.GRAY_DYE).asGuiItem {
                        displayName = if (currentPage < totalPages - 1) "§a次のページ" else "§7次のページ"
                    }
                onClick { action ->
                    if (currentPage < totalPages - 1) {
                        open(action.player, eventId, currentPage + 1)
                    }
                    GuiResult.Silent
                }
            }

            // 戻るボタン
            item('B') {
                customItem =
                    Material.DARK_OAK_DOOR.asGuiItem {
                        displayName = "§7戻る"
                    }
                onClick { action ->
                    detailGui.open(action.player, eventId)
                    GuiResult.Silent
                }
            }
        }
    }

    /**
     * プレビュー用のItemStackを作成
     */
    private fun createPreviewItem(rewardItem: GachaRewardItem): ItemStack {
        val itemStack = rewardItem.toItemStack().clone()
        val meta = itemStack.itemMeta

        val lore =
            (meta.lore() ?: mutableListOf()).toMutableList().apply {
                add(Component.empty())
                add(
                    Component.text("レアリティ: ")
                        .color(net.kyori.adventure.text.format.NamedTextColor.GRAY)
                        .append(
                            Component.text(rewardItem.rarity.displayName)
                                .color(rewardItem.rarity.color),
                        ),
                )
                add(
                    Component.text("排出率: ${getRarityWeight(rewardItem.rarity)}")
                        .color(net.kyori.adventure.text.format.NamedTextColor.GRAY),
                )
            }
        meta.lore(lore)
        itemStack.itemMeta = meta

        return itemStack
    }

    /**
     * レアリティのソート順序を取得（高いほど先に表示）
     */
    private fun getRarityOrder(rarity: ItemRarity): Int =
        when (rarity) {
            ItemRarity.LEGENDARY -> 5
            ItemRarity.EPIC -> 4
            ItemRarity.RARE -> 3
            ItemRarity.UNCOMMON -> 2
            ItemRarity.COMMON -> 1
            else -> 0
        }

    /**
     * レアリティの排出率表記を取得
     */
    private fun getRarityWeight(rarity: ItemRarity): String =
        when (rarity) {
            ItemRarity.LEGENDARY -> "§6★★★★★ (激レア)"
            ItemRarity.EPIC -> "§d★★★★ (超レア)"
            ItemRarity.RARE -> "§b★★★ (レア)"
            ItemRarity.UNCOMMON -> "§a★★ (やや稀)"
            ItemRarity.COMMON -> "§f★ (よく出る)"
            else -> "§8-"
        }
}
