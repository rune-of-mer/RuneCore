package org.lyralis.runeCore.gui.result

/**
 * GUI 操作の結果を表す sealed class
 * 成功 ([Success], [Silent]) または失敗 ([Failure]) のいずれかの状態を持つ
 *
 * @param T 成功時に返されるデータの型
 */
sealed class GuiResult<out T> {
    /**
     * 操作が成功したことを示す結果
     * @param data 取得されたデータ
     */
    data class Success<T>(
        val data: T,
    ) : GuiResult<T>()

    /**
     * メッセージなしの成功を示す結果
     */
    data object Silent : GuiResult<Nothing>()

    /**
     * 操作が失敗したことを示す結果
     */
    sealed class Failure : GuiResult<Nothing>() {
        /**
         * GUI のオープンに失敗した場合
         *
         * @param reason 理由
         */
        data class OpenFailed(
            val reason: String,
        ) : Failure()

        /**
         * アイテムのクリック処理に失敗した場合
         *
         * @param reason 理由
         * @param cause 存在する場合，例外
         */
        data class ClickFailed(
            val reason: String,
            val cause: Throwable? = null,
        ) : Failure()

        /**
         * ユーザーがキャンセルした場合
         */
        data object Cancelled : Failure()

        /**
         * 権限不足の場合
         *
         * @param permission 必要な権限
         */
        data class NoPermission(
            val permission: String,
        ) : Failure()

        /**
         * カスタムエラー
         *
         * @param message エラーメッセージ
         */
        data class Custom(
            val message: String,
        ) : Failure()
    }

    val isSuccess: Boolean get() = this is Success || this is Silent

    fun getOrNull(): T? =
        when (this) {
            is Success -> data
            else -> null
        }

    fun getOrThrow(): T =
        when (this) {
            is Success -> data
            is Silent -> throw IllegalStateException("Silent result has no data")
            is Failure.OpenFailed -> throw IllegalStateException("Open failed: $reason")
            is Failure.ClickFailed -> throw cause ?: IllegalStateException(reason)
            is Failure.Cancelled -> throw IllegalStateException("Operation cancelled")
            is Failure.NoPermission -> throw SecurityException("Permission required: $permission")
            is Failure.Custom -> throw IllegalStateException(message)
        }

    fun <R> map(transform: (T) -> R): GuiResult<R> =
        when (this) {
            is Success -> Success(transform(data))
            is Silent -> Silent
            is Failure.OpenFailed -> this
            is Failure.ClickFailed -> this
            is Failure.Cancelled -> this
            is Failure.NoPermission -> this
            is Failure.Custom -> this
        }

    inline fun onSuccess(action: (T) -> Unit): GuiResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onFailure(action: (Failure) -> Unit): GuiResult<T> {
        if (this is Failure) action(this)
        return this
    }
}

/**
 * 確認ダイアログの結果を表す sealed class
 *
 */
sealed class ConfirmationResult {
    data object Confirmed : ConfirmationResult()

    data object Denied : ConfirmationResult()

    data object Cancelled : ConfirmationResult()
}
