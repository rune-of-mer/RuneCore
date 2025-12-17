package dev.m1sk9.runeCore.command.annotation

import dev.m1sk9.runeCore.permission.Permission
import kotlin.reflect.KClass

/**
 * コマンド実行をパーミッションで制限する場合のアノテーション
 *
 * @property value 必要な Permission クラス
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandPermission(
    val value: KClass<out Permission>,
)
