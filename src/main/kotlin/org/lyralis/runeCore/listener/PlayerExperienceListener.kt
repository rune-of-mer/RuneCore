package org.lyralis.runeCore.listener

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.lyralis.runeCore.component.infoMessage
import org.lyralis.runeCore.component.systemMessage
import org.lyralis.runeCore.database.impl.experience.ExperienceService
import org.lyralis.runeCore.database.model.experience.MobExperience
import org.lyralis.runeCore.experience.source.OreExperience
import org.lyralis.runeCore.experience.source.PvPExperience

class PlayerExperienceListener(
    private val experienceService: ExperienceService,
) : Listener {
    // モブ殺害時・PvP時の経験値獲得
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onMobKill(event: EntityDeathEvent) {
        val killer = event.entity.killer ?: return
        val victim = event.entity

        // PvP 時
        if (victim is Player) {
            val killerLevel = experienceService.getLevel(killer.uniqueId)
            val victimLevel = experienceService.getLevel(victim.uniqueId)

            val expAmount = PvPExperience.calculateExperienceWithLevelDiff(killerLevel, victimLevel)
            experienceService.grantExperience(killer, expAmount)

            if (killerLevel > victimLevel) {
                killer.sendMessage("下剋上! 獲得経験値にボーナスが適用されました".infoMessage())
            }

            killer.sendMessage("${victim.name} を倒しました".infoMessage())
            victim.sendMessage("${killer.name} に倒されました".infoMessage())
            Bukkit.broadcast(
                "[PvP] ${killer.name}(${killerLevel}Lv) >>> ${victim.name}(${victimLevel}Lv)".systemMessage(),
            )
            return
        }

        // モブ殺害時
        val expAmount = MobExperience.getExperience(victim.type)
        if (expAmount == 0uL) return
        experienceService.grantExperience(killer, expAmount)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreakOre(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        if (!OreExperience.isOre(block.type)) return

        val tool = player.inventory.itemInMainHand
        if (tool.containsEnchantment(Enchantment.SILK_TOUCH)) {
            player.sendActionBar(Component.text("+0EXP (シルクタッチでの破壊)").color(NamedTextColor.WHITE))
            return
        }

        val expAmount = OreExperience.getExperience(block.type)
        if (expAmount == 0uL) return

        experienceService.grantExperience(player, expAmount)
    }
}
