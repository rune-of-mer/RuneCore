package dev.m1sk9.runeCore.command.register

import org.bukkit.entity.Player

/**
 * プレイヤーのコマンド実行の結果を表す sealed class
 */
sealed class CommandResult {
    /**
     * 実行が成功したことを示す結果
     *
     * @param message プレイヤーに渡される実行結果のメッセージ
     */
    data class Success<T>(
        val message: String? = null,
    ) : CommandResult()

    /**
     * メッセージなしの実行が成功したことを示す結果
     */
    data object Silent : CommandResult()

    /**
     * 実行が失敗したことを示す結果
     */
    sealed class Failure : CommandResult() {
        /**
         * 引数が不正である場合に使用される結果
         *
         * @param usage コマンドの使用方法
         */
        data class InvalidArgument(
            val usage: String,
        ) : Failure()

        /**
         * 権限が不足している場合に使用される結果
         *
         * @param permission 本来必要な権限ノード
         */
        data class NoPermission(
            val permission: String,
        ) : Failure()

        /**
         * プレイヤーのみ実行可能な場合に使用される結果
         *
         * @param reason 理由
         */
        data class PlayerOnly(
            val reason: String = "This command can only be executed by the player.",
        ) : Failure()

        /**
         * 実行対象が存在しない場合に使用される結果
         *
         * @param target 実行対象のプレイヤー
         */
        data class TargetNotFound(
            val target: Player,
        ) : Failure()

        /**
         * 実行に失敗した場合に使用される結果
         *
         * @param reason 理由
         * @param cause 存在する場合，例外
         */
        data class ExecutionFailed(
            val reason: String,
            val cause: Throwable? = null,
        ) : Failure()

        data class Custom(
            val message: String,
        ) : Failure()
    }
}
