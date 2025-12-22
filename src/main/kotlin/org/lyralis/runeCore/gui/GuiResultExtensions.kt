package org.lyralis.runeCore.gui

import org.lyralis.runeCore.command.register.CommandResult
import org.lyralis.runeCore.gui.result.GuiResult

/**
 * GuiResult を CommandResult に変換する
 *
 * ```kotlin
 * override fun execute(context: RuneCommandContext): CommandResult {
 *     return context.player.showPaginatedGui<CustomItem> {
 *         // ...
 *     }.toCommandResult()
 * }
 * ```
 */
fun GuiResult<*>.toCommandResult(): CommandResult =
    when (this) {
        is GuiResult.Success -> CommandResult.Silent
        is GuiResult.Silent -> CommandResult.Silent
        is GuiResult.Failure.OpenFailed -> CommandResult.Failure.ExecutionFailed("GUI を開けませんでした: $reason")
        is GuiResult.Failure.ClickFailed -> CommandResult.Failure.ExecutionFailed("クリック処理に失敗しました: $reason", cause)
        is GuiResult.Failure.Cancelled -> CommandResult.Silent
        is GuiResult.Failure.NoPermission -> CommandResult.Failure.NoPermission(permission)
        is GuiResult.Failure.Custom -> CommandResult.Failure.Custom(message)
    }

/**
 * GuiResult が失敗の場合のみ CommandResult に変換し、成功の場合は指定されたメッセージを返す
 *
 * ```kotlin
 * override fun execute(context: RuneCommandContext): CommandResult {
 *     return context.player.showPaginatedGui<CustomItem> {
 *         // ...
 *     }.toCommandResult("GUI を開きました")
 * }
 * ```
 */
fun GuiResult<*>.toCommandResult(successMessage: String?): CommandResult =
    when (this) {
        is GuiResult.Success -> CommandResult.Success(successMessage)
        is GuiResult.Silent -> CommandResult.Silent
        is GuiResult.Failure.OpenFailed -> CommandResult.Failure.ExecutionFailed("GUI を開けませんでした: $reason")
        is GuiResult.Failure.ClickFailed -> CommandResult.Failure.ExecutionFailed("クリック処理に失敗しました: $reason", cause)
        is GuiResult.Failure.Cancelled -> CommandResult.Silent
        is GuiResult.Failure.NoPermission -> CommandResult.Failure.NoPermission(permission)
        is GuiResult.Failure.Custom -> CommandResult.Failure.Custom(message)
    }
