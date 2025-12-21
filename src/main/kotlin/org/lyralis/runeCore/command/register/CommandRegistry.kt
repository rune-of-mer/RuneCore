package org.lyralis.runeCore.command.register

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.CommandPermission
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.utils.errorMessage
import org.lyralis.runeCore.utils.infoMessage

private const val COMMAND_SUCCESS = 1
private const val COMMAND_FAILURE = 0

/**
 * コマンドの登録処理を司るクラス．
 *
 * このクラスは [RuneCommand] の継承クラスを [mutableListOf] で受け取り，それを [LifecycleEvents] で登録する．
 */
class CommandRegistry(
    // NOTE: `plugin` は必要なので触らない. IDE が never used と推論してしまっているがこれは誤り
    private val plugin: JavaPlugin,
) {
    private val runeCommands = mutableListOf<RuneCommand>()

    /**
     * コマンド [RuneCommand] を [CommandRegistry] の [runeCommands] に登録する．
     *
     * @return [CommandRegistry]
     */
    fun register(runeCommand: RuneCommand): CommandRegistry {
        runeCommands.add(runeCommand)
        return this
    }

    /**
     * [LifecycleEventManager] を Paper サーバーに登録する，
     *
     * @param lifecycleManager [Plugin] をもつ [LifecycleEventManager]
     */
    fun registerAll(lifecycleManager: LifecycleEventManager<Plugin>) {
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { event ->
            val registrar = event.registrar()
            runeCommands.forEach { command ->
                val literal = buildLiteral(command)
                registrar.register(literal.build(), command.description, command.aliases)
            }
        }
    }

    /**
     * 実際のコマンドのコンテキストを Paper 側の情報を照らし合わせて [LiteralArgumentBuilder] を生成する．
     *
     * [CommandPermission] の権限チェックもここで行われる．
     *
     * @param runeCommand コマンドを実装した [RuneCommand] の継承クラス
     */
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

    /**
     * 実際のコマンドを Paper で実行する処理．
     *
     * @param runeCommand コマンドを実装した [RuneCommand] の継承クラス
     * @param ctx コマンドのコンテキスト情報を持つ [CommandContext]
     * @return コマンドの結果．成功 - [COMMAND_SUCCESS], 失敗 - [COMMAND_FAILURE]
     */
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
     * コマンドの深さを計算する．
     *
     * 例えば，/rune info -> depth = 2, /rune -> depth = 1 となる．
     *
     * @param runeCommand コマンドを実装した [RuneCommand] の継承クラス
     * @param parts コマンドの各パーツを持つ配列
     * @return コマンドの深さに対応した数値
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

    /**
     * Paper 側に実際に実装する [CommandResult] に合わせたコマンドの各種結果
     *
     * @param result コマンドの各種結果
     * @param sender コマンドの実行者
     */
    private fun handleResult(
        result: CommandResult,
        sender: CommandSender,
    ) {
        when (result) {
            is CommandResult.Success -> {
                result.message?.let { message ->
                    sender.sendMessage(message.infoMessage())
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
