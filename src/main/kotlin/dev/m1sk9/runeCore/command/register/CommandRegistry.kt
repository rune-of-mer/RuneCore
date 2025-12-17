package dev.m1sk9.runeCore.command.register

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.m1sk9.runeCore.command.annotation.CommandPermission
import dev.m1sk9.runeCore.command.annotation.PlayerOnlyCommand
import dev.m1sk9.runeCore.component.errorMessage
import dev.m1sk9.runeCore.component.systemMessage
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

private const val COMMAND_SUCCESS = 1
private const val COMMAND_FAILURE = 0

class CommandRegistry(
    private val plugin: Plugin,
) {
    private val runeCommands = mutableListOf<RuneCommand>()

    fun register(runeCommand: RuneCommand): CommandRegistry {
        runeCommands.add(runeCommand)
        return this
    }

    fun registerAll(lifecycleManager: LifecycleEventManager<Plugin>) {
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val registrar = event.registrar()
            runeCommands.forEach { command ->
                val literal = buildLiteral(command)
                registrar.register(literal.build(), command.description, command.aliases)
            }
        }
    }

    private fun buildLiteral(runeCommand: RuneCommand): LiteralArgumentBuilder<CommandSourceStack> {
        var literal = Commands.literal(runeCommand.name)

        runeCommand::class
            .annotations
            .filterIsInstance<CommandPermission>()
            .firstOrNull()
            ?.let { permission ->
                literal =
                    literal.requires { source ->
                        source.sender.hasPermission(permission.value.toString())
                    }
            }

        runeCommand.subcommands.forEach { subcommand ->
            literal = literal.then(buildLiteral(subcommand))
        }

        literal =
            literal.executes { ctx ->
                executeCommand(runeCommand, ctx)
            }

        literal =
            literal.then(
                Commands
                    .argument("args", StringArgumentType.greedyString())
                    .executes { ctx ->
                        executeCommand(runeCommand, ctx)
                    },
            )

        return literal
    }

    private fun executeCommand(
        runeCommand: RuneCommand,
        ctx: CommandContext<CommandSourceStack>,
    ): Int {
        val source = ctx.source
        val sender = source.sender

        val isPlayerOnly =
            runeCommand::class
                .annotations
                .filterIsInstance<PlayerOnlyCommand>()
                .isNotEmpty()

        if (isPlayerOnly && sender !is Player) {
            sender.sendMessage(
                "このコマンドはプレイヤーのみ実行できます".errorMessage(),
            )
            return COMMAND_FAILURE
        }

        val input = ctx.input
        val parts = input.removePrefix("/").split(" ")

        // サブコマンドの深さを計算して適切な位置から引数を取得
        val commandDepth = countCommandDepth(runeCommand, parts)
        val args = parts.drop(commandDepth).toTypedArray()

        val context =
            RuneCommandContext(
                source = source,
                args = args,
            )

        val result = runeCommand.execute(context)
        handleResult(result, sender)

        return COMMAND_SUCCESS
    }

    /**
     * コマンドの深さを計算する
     * 例: /rune info -> depth = 2, /rune -> depth = 1
     */
    private fun countCommandDepth(
        runeCommand: RuneCommand,
        parts: List<String>,
    ): Int {
        // 最低でもルートコマンド分の 1
        var depth = 1

        // parts の中でサブコマンド名と一致するものをカウント
        var currentSubcommands = runeCommand.subcommands
        for (i in 1 until parts.size) {
            val matchingSubcommand = currentSubcommands.find { it.name == parts[i] }
            if (matchingSubcommand != null) {
                depth++
                currentSubcommands = matchingSubcommand.subcommands
            } else {
                break
            }
        }

        return depth
    }

    private fun handleResult(
        result: CommandResult,
        sender: CommandSender,
    ) {
        when (result) {
            is CommandResult.Success -> {
                result.message?.let { message ->
                    sender.sendMessage(message.systemMessage())
                }
            }
            is CommandResult.Failure.InvalidArgument -> {
                sender.sendMessage("引数が不正です: ${result.usage}".errorMessage())
            }
            is CommandResult.Failure.NoPermission -> {
                sender.sendMessage("権限が不足しています: ${result.permission}".errorMessage())
            }
            is CommandResult.Failure.PlayerOnly -> {
                sender.sendMessage(result.reason.errorMessage())
            }
            is CommandResult.Failure.TargetNotFound -> {
                sender.sendMessage("対象が見つかりません: ${result.target.name}".errorMessage())
            }
            is CommandResult.Failure.ExecutionFailed -> {
                sender.sendMessage("実行に失敗しました: ${result.reason}".errorMessage())
            }
            is CommandResult.Failure.Custom -> {
                sender.sendMessage(result.message.errorMessage())
            }
            else -> {}
        }
    }
}
