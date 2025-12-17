package dev.m1sk9.runeCore.command.annotation

/**
 * コマンド実行をプレイヤー経由に制限する場合のアノテーション．
 *
 * [dev.m1sk9.runeCore.command.RuneCommand] を継承したクラスにこのアノテーションを付与するとコマンドがプレイヤー経由での実装に制限される，詳しくは [Contributing Guide](https://github.com/rune-of-mer/RuneCore/blob/main/.github/CONTRIBUTING.md#プレイヤー専用のコマンドを実装する) を参照すること．
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class PlayerOnlyCommand
