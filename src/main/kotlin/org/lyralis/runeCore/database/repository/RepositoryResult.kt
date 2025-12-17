package org.lyralis.runeCore.database.repository

/**
 * データベース操作の結果を表す sealed class
 * 成功 ([Success]) 失敗([NotFound], [InsufficientBalance], [Error])のいずれかの状態を持ちます。
 *
 * @param T 成功時に返されるデータの型
 */
sealed class RepositoryResult<out T> {
    /**
     * 操作が成功したことを示す結果
     * @param data 取得されたデータ
     */
    data class Success<T>(
        val data: T,
    ) : RepositoryResult<T>()

    /**
     * 要求されたリソースが見つからなかったことを示す結果
     * @param message エラーメッセージ
     */
    data class NotFound(
        val message: String,
    ) : RepositoryResult<Nothing>()

    /**
     * 残高不足を示す結果
     * @param current 現在の残高
     * @param required 必要な残高
     */
    data class InsufficientBalance(
        val current: ULong,
        val required: ULong,
    ) : RepositoryResult<Nothing>()

    /**
     * 予期しないエラーが発生したことを示す結果
     * @param exception 発生した例外
     */
    data class Error(
        val exception: Throwable,
    ) : RepositoryResult<Nothing>()

    val isSuccess: Boolean get() = this is Success

    fun getOrNull(): T? =
        when (this) {
            is Success -> data
            else -> null
        }

    fun getOrThrow(): T =
        when (this) {
            is Success -> data
            is NotFound -> throw NoSuchElementException(message)
            is InsufficientBalance -> throw IllegalStateException("Insufficient balance: $current < $required")
            is Error -> throw exception
        }

    fun <R> map(transform: (T) -> R): RepositoryResult<R> =
        when (this) {
            is Success -> Success(transform(data))
            is NotFound -> this
            is InsufficientBalance -> this
            is Error -> this
        }

    inline fun onSuccess(action: (T) -> Unit): RepositoryResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onFailure(action: (Failure) -> Unit): RepositoryResult<T> {
        val failure = failureOrNull()
        if (failure != null) action(failure)
        return this
    }

    fun failureOrNull(): Failure? =
        when (this) {
            is Success -> null
            is NotFound -> Failure.NotFound(message)
            is InsufficientBalance -> Failure.InsufficientBalance(current, required)
            is Error -> Failure.Error(exception)
        }

    sealed class Failure {
        data class NotFound(
            val message: String,
        ) : Failure()

        data class InsufficientBalance(
            val current: ULong,
            val required: ULong,
        ) : Failure()

        data class Error(
            val exception: Throwable,
        ) : Failure()
    }
}
