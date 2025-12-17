package dev.m1sk9.runeCore.command.annotation

/**
 * コマンド実行をプレイヤー経由に制限する場合のアノテーション
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PlayerOnlyCommand
