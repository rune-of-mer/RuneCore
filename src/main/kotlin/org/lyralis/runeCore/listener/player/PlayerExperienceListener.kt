package org.lyralis.runeCore.listener.player

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.lyralis.runeCore.component.actionbar.ActionBarManager
import org.lyralis.runeCore.component.message.infoMessage
import org.lyralis.runeCore.component.message.systemMessage
import org.lyralis.runeCore.domain.experience.ExperienceService
import org.lyralis.runeCore.domain.experience.MobExperience
import org.lyralis.runeCore.domain.experience.OreExperience
import org.lyralis.runeCore.domain.experience.PvPExperience
import org.lyralis.runeCore.domain.money.MobMoney
import org.lyralis.runeCore.domain.money.MoneyService
import org.lyralis.runeCore.domain.money.OreMoney
import org.lyralis.runeCore.domain.money.PvPMoney
import org.lyralis.runeCore.domain.player.PlayerService
import org.lyralis.runeCore.utils.ExcludedBlockMaterials

/**
 * プレイヤーの各行動に対して処理を行うリスナー
 */
class PlayerExperienceListener(
    private val playerService: PlayerService,
    private val experienceService: ExperienceService,
    private val moneyService: MoneyService,
) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onMobKill(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val victim = event.entity

        if (victim is Player) {
            val killerLevel = experienceService.getLevel(killer.uniqueId)
            val victimLevel = experienceService.getLevel(victim.uniqueId)

            val expAmount = PvPExperience.calculateExperienceWithLevelDiff(killerLevel, victimLevel)
            experienceService.grantExperience(killer, expAmount)

            val moneyAmount = PvPMoney.calculateMoneyWithLevelDiff(killerLevel, victimLevel)
            moneyService.addBalance(killer, moneyAmount)

            if (killerLevel < victimLevel) {
                killer.sendMessage("下剋上! 獲得経験値・ルーンにボーナスが適用されました".infoMessage())
            }

            killer.sendMessage("${victim.name} を倒しました".infoMessage())
            victim.sendMessage("${killer.name} に倒されました".infoMessage())
            playerService.incrementKills(killer.uniqueId)
            playerService.incrementDeaths(victim.uniqueId)
            Bukkit.broadcast(
                "[PvP] ${killer.name}(${killerLevel}Lv) >>> ${victim.name}(${victimLevel}Lv)".systemMessage(),
            )
            return
        }

        val expAmount = MobExperience.getExperience(victim.type)
        val moneyAmount = MobMoney.getMoney(victim.type)
        if (expAmount == 0uL || moneyAmount == 0uL) return
        experienceService.grantExperience(killer, expAmount)
        moneyService.addBalance(killer, moneyAmount)
        playerService.incrementMobKills(killer.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.player

        playerService.incrementDeaths(player.uniqueId)
        event.deathMessage()
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreakOre(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (!OreExperience.isOre(block.type)) return

        val tool = player.inventory.itemInMainHand
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            ActionBarManager.showTemporaryNotification(
                player,
                Component.text("+0Rune / +0EXP (シルクタッチでの破壊)").color(NamedTextColor.WHITE),
            )
            return
        }

        val expAmount = OreExperience.getExperience(block.type)
        val moneyAmount = OreMoney.getMoney(block.type)
        if (expAmount == 0uL || moneyAmount == 0uL) return

        experienceService.grantExperience(player, expAmount)
        moneyService.addBalance(player, moneyAmount)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreakBlock(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (ExcludedBlockMaterials.isExcluded(block.type)) return

        playerService.incrementBlocksDestroyed(player.uniqueId)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onPlaceBlock(event: BlockPlaceEvent) {
        val player = event.player
        val block = event.block

        if (ExcludedBlockMaterials.isExcluded(block.type)) return

        playerService.incrementBlocksPlaced(player.uniqueId)
    }
}
