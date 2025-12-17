package dev.m1sk9.runeCore.command

import dev.m1sk9.runeCore.command.register.CommandResult
import dev.m1sk9.runeCore.command.register.RuneCommandContext
import dev.m1sk9.runeCore.command.register.SuggestionContext

interface RuneCommand {
    /**
     * コマンドに使用される名前．
     *
     * ここで指定された文字列が実際のコマンドになる．
     */
    val name: String

    /**
     * コマンドの説明．
     *
     * ここで指定された文字列は Paper 側のヘルプメッセージなどに使用される．
     */
    val description: String

    /**
     *　コマンドのエイリアス．
     */
    val aliases: List<String> get() = emptyList()

    /**
     * コマンドのサブコマンド．
     */
    val subcommands: List<RuneCommand> get() = emptyList()

    /**
     * コマンドの実行内容を実装する関数
     *
     * @param context [RuneCommandContext] - コマンドのコンテキスト情報
     * @return [CommandResult] - コマンドの実行結果
     */
    fun execute(context: RuneCommandContext): CommandResult

    /**
     * コマンドのサジェスト内容を実装する関数
     *
     * @param context [SuggestionContext] - サジェストのコンテキスト情報
     * @return サジェスト候補のリスト
     */
    fun suggest(context: SuggestionContext): List<String> = emptyList()
}
