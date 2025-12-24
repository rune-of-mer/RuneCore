package org.lyralis.runeCore.command.impl.gacha

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.command.register.SuggestionContext
import org.lyralis.runeCore.database.impl.gacha.GachaService
import org.lyralis.runeCore.database.model.gacha.GachaEventData
import org.lyralis.runeCore.permission.Permission

@CommandPermission(Permission.Admin.GachaAdminCommand::class)
class RuneGachaAdminCommand(
    private val gachaService: GachaService,
) : RuneCommand {
    override val name = "gachaadmin"
    override val description = "ガチャイベントを管理します（管理者用）"
    override val aliases = listOf("ga")

    override fun execute(context: RuneCommandContext): CommandResult {
        val subCommand =
            context.args.getOrNull(0)?.lowercase()
                ?: return showHelp()

        return when (subCommand) {
            "create" -> handleCreate(context)
            "list" -> handleList()
            "activate" -> handleSetActive(context, true)
            "deactivate" -> handleSetActive(context, false)
            "info" -> handleInfo(context)
            else -> showHelp()
        }
    }

    private fun showHelp(): CommandResult =
        CommandResult.Success(
            """
            §6=== ガチャ管理コマンド ===
            §e/gachaadmin create <id> <表示名> [チケット数] [天井回数]
            §7  新規ガチャイベントを作成
            §e/gachaadmin list
            §7  全イベント一覧を表示
            §e/gachaadmin activate <id>
            §7  イベントを有効化
            §e/gachaadmin deactivate <id>
            §7  イベントを無効化
            §e/gachaadmin info <id>
            §7  イベントの詳細を表示
            """.trimIndent(),
        )

    private fun handleCreate(context: RuneCommandContext): CommandResult {
        val id =
            context.args.getOrNull(1)
                ?: return CommandResult.Failure.Custom("使用法: /gachaadmin create <id> <表示名> [チケット数] [天井回数]")

        val displayName =
            context.args.getOrNull(2)
                ?: return CommandResult.Failure.Custom("表示名を指定してください")

        val ticketCost = context.args.getOrNull(3)?.toUIntOrNull() ?: 1u
        val pityThreshold = context.args.getOrNull(4)?.toUIntOrNull() ?: 100u

        // 既存チェック
        if (gachaService.getEventById(id) != null) {
            return CommandResult.Failure.Custom("イベントID '$id' は既に存在します")
        }

        val event =
            GachaEventData(
                id = id,
                displayName = displayName,
                ticketCost = ticketCost,
                isActive = true,
                pityThreshold = pityThreshold,
            )

        return if (gachaService.upsertEvent(event)) {
            CommandResult.Success(
                """
                §aガチャイベントを作成しました
                §7ID: §f$id
                §7表示名: §f$displayName
                §7チケット数: §e$ticketCost 枚/回
                §7天井: §e$pityThreshold 回
                §7状態: §aアクティブ
                """.trimIndent(),
            )
        } else {
            CommandResult.Failure.Custom("イベントの作成に失敗しました")
        }
    }

    private fun handleList(): CommandResult {
        gachaService.getActiveEvents()
        val allEvents = getAllEvents()

        if (allEvents.isEmpty()) {
            return CommandResult.Success("§7登録されているガチャイベントはありません")
        }

        val list =
            allEvents.joinToString("\n") { event ->
                val status = if (event.isActive) "§a有効" else "§c無効"
                "§7- §f${event.id} §7(${event.displayName}) [$status§7] チケット:${event.ticketCost} 天井:${event.pityThreshold}"
            }

        return CommandResult.Success("§6=== ガチャイベント一覧 ===\n$list")
    }

    private fun handleSetActive(
        context: RuneCommandContext,
        active: Boolean,
    ): CommandResult {
        val id =
            context.args.getOrNull(1)
                ?: return CommandResult.Failure.Custom("イベントIDを指定してください")

        val event =
            gachaService.getEventById(id)
                ?: return CommandResult.Failure.Custom("イベント '$id' が見つかりません")

        val updated = event.copy(isActive = active)
        return if (gachaService.upsertEvent(updated)) {
            val status = if (active) "§a有効化" else "§c無効化"
            CommandResult.Success("イベント '${event.displayName}' を$status§fしました")
        } else {
            CommandResult.Failure.Custom("更新に失敗しました")
        }
    }

    private fun handleInfo(context: RuneCommandContext): CommandResult {
        val id =
            context.args.getOrNull(1)
                ?: return CommandResult.Failure.Custom("イベントIDを指定してください")

        val event =
            gachaService.getEventById(id)
                ?: return CommandResult.Failure.Custom("イベント '$id' が見つかりません")

        val items = gachaService.getGachaItems(id)
        val status = if (event.isActive) "§a有効" else "§c無効"

        return CommandResult.Success(
            """
            §6=== ${event.displayName} ===
            §7ID: §f${event.id}
            §7チケット数: §e${event.ticketCost} 枚/回
            §7天井: §e${event.pityThreshold} 回
            §7状態: $status
            §7排出アイテム数: §f${items.size} 個
            """.trimIndent(),
        )
    }

    private fun getAllEvents(): List<GachaEventData> {
        // アクティブ・非アクティブ両方を取得するため、Repositoryを直接使う必要があるが
        // 現状はアクティブのみを取得して表示
        return gachaService.getActiveEvents()
    }

    override fun suggest(context: SuggestionContext): List<String> =
        when (context.args.size) {
            1 ->
                listOf("create", "list", "activate", "deactivate", "info")
                    .filter { it.startsWith(context.currentArg.lowercase()) }
            2 ->
                when (context.args[0].lowercase()) {
                    "activate", "deactivate", "info" ->
                        gachaService
                            .getActiveEvents()
                            .map { it.id }
                            .filter { it.startsWith(context.currentArg.lowercase()) }
                    else -> emptyList()
                }
            else -> emptyList()
        }
}
