package dev.m1sk9.runeCore.command.register

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

    fun execute(content: RuneCommandContext): CommandResult

    fun suggest(content: SuggestionContext): List<String> = emptyList()
}
