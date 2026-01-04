package org.lyralis.runeCore.domain.gacha

import org.bukkit.inventory.ItemStack
import org.lyralis.runeCore.database.repository.GachaRepository
import org.lyralis.runeCore.database.repository.RepositoryResult
import org.lyralis.runeCore.domain.gacha.GachaEventData
import org.lyralis.runeCore.domain.gacha.GachaVanillaItem
import org.lyralis.runeCore.domain.gacha.GachaVanillaItems
import org.lyralis.runeCore.item.CustomItem
import org.lyralis.runeCore.item.ItemRarity
import org.lyralis.runeCore.item.ItemRegistry
import java.util.UUID
import java.util.logging.Logger
import kotlin.random.Random

/**
 * ガチャの抽選アイテム（カスタムまたはバニラ）
 */
sealed class GachaRewardItem {
    abstract val rarity: ItemRarity

    abstract fun toItemStack(): ItemStack

    data class Custom(
        val item: CustomItem,
    ) : GachaRewardItem() {
        override val rarity: ItemRarity get() = item.rarity

        override fun toItemStack(): ItemStack = item.createItemStack()
    }

    data class Vanilla(
        val vanillaItem: GachaVanillaItem,
    ) : GachaRewardItem() {
        override val rarity: ItemRarity get() = vanillaItem.rarity

        override fun toItemStack(): ItemStack = ItemStack(vanillaItem.material, vanillaItem.amount)
    }
}

/**
 * ガチャ抽選結果
 */
data class GachaResult(
    val items: List<GachaRewardItem>,
    val isPityTriggered: Boolean,
    val newPityCount: UInt,
)

/**
 * ガチャシステムのサービスクラス
 */
class GachaService(
    private val gachaRepository: GachaRepository,
    private val logger: Logger,
) {
    companion object {
        const val GACHA_TICKET_ID = "gacha_ticket"
    }

    /**
     * アクティブなガチャイベントを取得
     */
    fun getActiveEvents(): List<GachaEventData> =
        when (val result = gachaRepository.getActiveEvents()) {
            is RepositoryResult.Success -> result.data
            else -> emptyList()
        }

    /**
     * イベントIDでガチャイベントを取得
     */
    fun getEventById(eventId: String): GachaEventData? =
        when (val result = gachaRepository.getEventById(eventId)) {
            is RepositoryResult.Success -> result.data
            else -> null
        }

    /**
     * プレイヤーの現在の天井カウントを取得
     */
    fun getPlayerPityCount(
        playerUuid: UUID,
        eventId: String,
    ): UInt =
        when (val result = gachaRepository.getPlayerPity(playerUuid, eventId)) {
            is RepositoryResult.Success -> result.data.pullCount
            else -> 0u
        }

    /**
     * プレイヤーが持っているガチャチケットの枚数を取得
     */
    fun getPlayerTicketCount(playerInventory: Array<ItemStack?>): Int =
        playerInventory.filterNotNull().sumOf { itemStack ->
            val customItem = ItemRegistry.getFromItemStack(itemStack)
            if (customItem?.id == GACHA_TICKET_ID) {
                itemStack.amount
            } else {
                0
            }
        }

    /**
     * プレイヤーからチケットを消費
     *
     * @return 消費に成功した場合true
     */
    fun consumeTickets(
        playerInventory: Array<ItemStack?>,
        amount: Int,
    ): Boolean {
        var remaining = amount
        for (i in playerInventory.indices) {
            val itemStack = playerInventory[i] ?: continue
            val customItem = ItemRegistry.getFromItemStack(itemStack)
            if (customItem?.id != GACHA_TICKET_ID) continue

            val takeAmount = minOf(itemStack.amount, remaining)
            itemStack.amount -= takeAmount
            remaining -= takeAmount

            if (itemStack.amount <= 0) {
                playerInventory[i] = null
            }

            if (remaining <= 0) break
        }
        return remaining <= 0
    }

    /**
     * ガチャを引く
     *
     * @param playerUuid プレイヤーのUUID
     * @param eventId ガチャイベントID
     * @param pullCount 引く回数（1 or 10）
     * @return 抽選結果
     */
    fun pullGacha(
        playerUuid: UUID,
        eventId: String,
        pullCount: Int,
    ): GachaResult {
        val event = getEventById(eventId) ?: return GachaResult(emptyList(), false, 0u)

        val customItems =
            ItemRegistry.getGachaItemsByEventId(eventId).map {
                GachaRewardItem.Custom(it)
            }
        val vanillaItems = GachaVanillaItems.all().map { GachaRewardItem.Vanilla(it) }
        val allItems = customItems + vanillaItems

        if (allItems.isEmpty()) {
            logger.warning("ガチャイベント '$eventId' に排出アイテムがありません")
            return GachaResult(emptyList(), false, 0u)
        }

        val currentPity = getPlayerPityCount(playerUuid, eventId)
        var pityTriggered = false

        val results =
            (1..pullCount).map { pullIndex ->
                val pullNumber = currentPity + pullIndex.toUInt()

                if (pullNumber >= event.pityThreshold) {
                    pityTriggered = true
                    selectHighRarityItem(allItems)
                } else {
                    selectRandomItem(allItems)
                }
            }

        val newPityCount =
            if (pityTriggered) {
                gachaRepository.resetPlayerPity(playerUuid, eventId)
                0u
            } else {
                val newCount = currentPity + pullCount.toUInt()
                gachaRepository.updatePlayerPity(playerUuid, eventId, newCount)
                newCount
            }

        return GachaResult(results, pityTriggered, newPityCount)
    }

    /**
     * 重み付き抽選でアイテムを選択
     */
    private fun selectRandomItem(items: List<GachaRewardItem>): GachaRewardItem {
        val totalWeight = items.sumOf { it.rarity.weight }
        if (totalWeight == 0) return items.random()

        val random = Random.nextInt(totalWeight)
        var cumulative = 0

        for (item in items) {
            cumulative += item.rarity.weight
            if (random < cumulative) {
                return item
            }
        }

        return items.last()
    }

    /**
     * 高レアリティアイテムを選択（天井用）
     */
    private fun selectHighRarityItem(items: List<GachaRewardItem>): GachaRewardItem {
        val highRarityItems =
            items.filter {
                it.rarity == ItemRarity.LEGENDARY ||
                    it.rarity == ItemRarity.EPIC
            }

        return if (highRarityItems.isNotEmpty()) {
            highRarityItems.random()
        } else {
            val rareItems = items.filter { it.rarity == ItemRarity.RARE }
            rareItems.randomOrNull() ?: items.random()
        }
    }

    /**
     * ガチャで排出される全アイテムを取得（プレビュー用）
     */
    fun getGachaItems(eventId: String): List<GachaRewardItem> {
        val customItems =
            ItemRegistry.getGachaItemsByEventId(eventId).map {
                GachaRewardItem.Custom(it)
            }
        val vanillaItems = GachaVanillaItems.all().map { GachaRewardItem.Vanilla(it) }
        return customItems + vanillaItems
    }

    /**
     * ガチャイベントを作成または更新
     */
    fun upsertEvent(event: GachaEventData): Boolean =
        when (gachaRepository.upsertEvent(event)) {
            is RepositoryResult.Success -> true
            else -> false
        }

    /**
     * デフォルトのガチャイベントを初期化（DBに存在しない場合のみ）
     */
    fun initializeDefaultEvents() {
        val defaultEvents =
            listOf(
                GachaEventData(
                    id = "standard",
                    displayName = "スタンダードガチャ",
                    ticketCost = 1u,
                    isActive = true,
                    pityThreshold = 100u,
                ),
            )

        defaultEvents.forEach { event ->
            if (getEventById(event.id) == null) {
                upsertEvent(event)
                logger.info("デフォルトガチャイベント '${event.id}' を作成しました")
            }
        }
    }
}
