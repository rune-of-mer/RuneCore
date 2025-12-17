package org.lyralis.runeCore.command.impl

import org.lyralis.runeCore.command.RuneCommand
import org.lyralis.runeCore.command.annotation.PlayerOnlyCommand
import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.command.register.RuneCommandContext
import org.lyralis.runeCore.config.ConfigManager

/**
 * /patchnote コマンドを定義するクラス
 *
 * コマンドの詳細は Dokka 上の [org.lyralis.runeCore.command] で確認可能
 */
@PlayerOnlyCommand
class RunePatchNoteCommand : RuneCommand {
    private val patchNoteURL = ConfigManager.get().plugin.patchNoteURL

    override val name = "patchnote"
    override val description = "公式サイトのパッチノートページを開く"
    override val aliases = listOf("update")

    /**
     * パッチノートコマンドを実行する．
     *
     * @param context コマンドのコンテキスト情報
     * @return コマンドの実行結果
     */
    override fun execute(context: RuneCommandContext): CommandResult {
        val result =
            listOf(
                patchNoteURL,
                "(URLをクリックするとブラウザが開きます)",
            ).joinToString("\n")

        return CommandResult.Success("パッチノートページ: $result")
    }
}
