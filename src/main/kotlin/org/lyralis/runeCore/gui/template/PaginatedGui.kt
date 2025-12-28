package org.lyralis.runeCore.gui.template

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.gui.annotation.GuiDsl
import org.lyralis.runeCore.gui.handler.ClickAction
import org.lyralis.runeCore.gui.result.GuiResult
import org.lyralis.runeCore.item.impl.debug.DebugCompassItem.displayName
import xyz.xenondevs.invui.gui.PagedGui
import xyz.xenondevs.invui.item.ItemProvider
import xyz.xenondevs.invui.item.impl.AbstractItem
import xyz.xenondevs.invui.item.impl.controlitem.PageItem
import xyz.xenondevs.invui.window.Window

/**
 * ページネーション付き GUI テンプレートビルダー
 *
 * ```kotlin
 * player.showPaginatedGui<CustomItem> {
 *     title = "アイテム一覧"
 *     itemsPerPage = 28
 *
 *     items(ItemRegistry.getAllItems())
 *
 *     render { customItem ->
 *         customItem.createItemStack()
 *     }
 *
 *     onItemClick { item, action ->
 *         if (action.isLeftClick) {
 *             player.inventory.addItem(item.createItemStack())
 *             GuiResult.Success(Unit)
 *         } else {
 *             GuiResult.Silent
 *         }
 *     }
 * }
 * ```
 */
@GuiDsl
class PaginatedGuiBuilder<T> {
    var title: String = "一覧"
    var itemsPerPage: Int = 28

    private var itemsList: List<T> = emptyList()
    private var itemRenderer: ((T) -> ItemStack)? = null
    private var onItemClickHandler: ((T, ClickAction) -> GuiResult<Unit>)? = null
    private var filterPredicate: ((T) -> Boolean) = { true }
    private var onBackHandler: ((Player) -> Unit)? = null

    /**
     * 表示するアイテムを設定
     */
    fun items(items: List<T>) {
        this.itemsList = items
    }

    /**
     * アイテムの表示方法を定義
     */
    fun render(renderer: (T) -> ItemStack) {
        itemRenderer = renderer
    }

    /**
     * アイテムクリック時のハンドラー
     */
    fun onItemClick(handler: (T, ClickAction) -> GuiResult<Unit>) {
        onItemClickHandler = handler
    }

    /**
     * フィルター条件を設定
     */
    fun filter(predicate: (T) -> Boolean) {
        filterPredicate = predicate
    }

    /**
     * 戻るボタンクリック時のハンドラー
     * 設定すると左下に戻るボタンが表示される
     */
    fun onBack(handler: (Player) -> Unit) {
        onBackHandler = handler
    }

    internal fun buildAndShow(player: Player): GuiResult<Unit> {
        val renderer = itemRenderer ?: return GuiResult.Failure.Custom("itemRenderer が設定されていません")

        val filteredItems = itemsList.filter(filterPredicate)

        val items =
            filteredItems.map { item ->
                PaginatedItem(item, renderer, onItemClickHandler)
            }

        val borderItem =
            ItemStack(Material.BLACK_STAINED_GLASS_PANE).apply {
                editMeta { meta ->
                    meta.displayName(Component.text(""))
                }
            }

        val guiBuilder =
            PagedGui
                .items()
                .setStructure(
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "x x x x x x x x x",
                    "# # < # B # > # #",
                ).addIngredient('x', xyz.xenondevs.invui.gui.structure.Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('#', borderItem)
                .addIngredient('<', BackwardPageItem())
                .addIngredient('>', ForwardPageItem())
                .setContent(items)

        val backHandler = onBackHandler
        if (backHandler != null) {
            guiBuilder.addIngredient('B', BackButtonItem(backHandler))
        } else {
            guiBuilder.addIngredient('B', borderItem)
        }

        val gui = guiBuilder.build()

        return try {
            Window
                .single()
                .setViewer(player)
                .setTitle(title)
                .setGui(gui)
                .build()
                .open()

            GuiResult.Success(Unit)
        } catch (e: Exception) {
            GuiResult.Failure.OpenFailed(e.message ?: "Unknown error")
        }
    }

    private inner class PaginatedItem<T>(
        private val item: T,
        private val renderer: (T) -> ItemStack,
        private val clickHandler: ((T, ClickAction) -> GuiResult<Unit>)?,
    ) : AbstractItem() {
        override fun getItemProvider(): ItemProvider = ItemProvider { renderer(item) }

        override fun handleClick(
            clickType: ClickType,
            player: Player,
            event: InventoryClickEvent,
        ) {
            val handler = clickHandler ?: return

            val action =
                ClickAction(
                    player = player,
                    clickType = clickType,
                    slot = event.slot,
                    currentItem = event.currentItem,
                    cursorItem = event.cursor,
                )

            handler(item, action)
        }
    }
}

/**
 * ページネーション付き GUI を表示する拡張関数
 *
 * ```kotlin
 * player.showPaginatedGui<CustomItem> {
 *     title = "アイテム一覧"
 *
 *     items(ItemRegistry.getAllItems())
 *
 *     render { item ->
 *         item.createItemStack()
 *     }
 *
 *     onItemClick { item, action ->
 *         GuiResult.Success(Unit)
 *     }
 * }
 * ```
 */
fun <T> Player.showPaginatedGui(block: PaginatedGuiBuilder<T>.() -> Unit): GuiResult<Unit> {
    val builder = PaginatedGuiBuilder<T>().apply(block)
    return builder.buildAndShow(this)
}

/**
 * 次ページへのナビゲーションアイテム
 */
private class ForwardPageItem : PageItem(true) {
    override fun getItemProvider(gui: PagedGui<*>): ItemProvider =
        ItemProvider {
            ItemStack(Material.ARROW).apply {
                editMeta { meta ->
                    meta.displayName(
                        Component.text(
                            if (gui.hasNextPage()) {
                                "次のページ (${gui.currentPage + 1}/${gui.pageAmount})"
                            } else {
                                "最後のページです"
                            },
                        ),
                    )
                }
            }
        }
}

/**
 * 前ページへのナビゲーションアイテム
 */
private class BackwardPageItem : PageItem(false) {
    override fun getItemProvider(gui: PagedGui<*>): ItemProvider =
        ItemProvider {
            ItemStack(Material.ARROW).apply {
                editMeta { meta ->
                    meta.displayName(
                        Component.text(
                            if (gui.hasPreviousPage()) {
                                "前のページ (${gui.currentPage + 1}/${gui.pageAmount})"
                            } else {
                                "最初のページです"
                            },
                        ),
                    )
                }
            }
        }
}

/**
 * 戻るボタンアイテム
 */
private class BackButtonItem(
    private val onBack: (Player) -> Unit,
) : AbstractItem() {
    override fun getItemProvider(): ItemProvider =
        ItemProvider {
            ItemStack(Material.BARRIER).apply {
                editMeta { meta ->
                    meta.displayName(Component.text("§c戻る"))
                    meta.lore(listOf(Component.text("§7クリックで前の画面に戻る")))
                }
            }
        }

    override fun handleClick(
        clickType: ClickType,
        player: Player,
        event: InventoryClickEvent,
    ) {
        onBack(player)
    }
}
