# RuneCore Contributing Guide

<!-- TOC -->
* [RuneCore Contributing Guide](#runecore-contributing-guide)
  * [開発環境](#開発環境)
    * [推奨](#推奨)
    * [セットアップ](#セットアップ)
  * [技術スタック](#技術スタック)
  * [コントリビューション時の注意点](#コントリビューション時の注意点)
  * [ブランチ・コミット戦略](#ブランチコミット戦略)
  * [コーディング規約](#コーディング規約)
    * [命名規則](#命名規則)
    * [Import 文](#import-文)
  * [権限管理](#権限管理)
    * [権限を追加する](#権限を追加する)
    * [権限の確認](#権限の確認)
  * [メッセージコンポーネント](#メッセージコンポーネント)
  * [コマンドシステム](#コマンドシステム)
      * [プレイヤー専用のコマンドを実装する](#プレイヤー専用のコマンドを実装する)
      * [権限付きコマンドを実装する](#権限付きコマンドを実装する)
      * [サブコマンドを持つコマンドを実装する](#サブコマンドを持つコマンドを実装する)
    * [CommandResult](#commandresult)
    * [RuneCommandContext](#runecommandcontext)
    * [Tab 補完](#tab-補完)
    * [コマンドの登録](#コマンドの登録)
    * [コマンドの命名規則](#コマンドの命名規則)
  * [データベース](#データベース)
    * [`runecore_db` について](#runecore_db-について)
    * [データの扱い方](#データの扱い方)
  * [デバッグサーバ](#デバッグサーバ)
    * [外部プラグインの導入](#外部プラグインの導入)
  * [付録: コラム](#付録-コラム)
    * [データを扱う際に UUID を使う理由](#データを扱う際に-uuid-を使う理由)
  * [付録: タスク索引](#付録-タスク索引)
    * [ktlint (ktlint-gradle)](#ktlint-ktlint-gradle)
<!-- TOC -->

## 開発環境

RuneCore の開発に必要な環境は以下の通りです:

- Java 21
  - Minecraft でサポートされている Java バージョンです．
  - Java は基本的に後方互換性があるため， Java 25 などでも動作しますが，RuneCore では非推奨とさせていただきます．
  - Rune of Mer では [Zulu OpenJDK](https://www.azul.com/downloads/?package=jdk) を使用しています．
- Gradle 8.8 以上
- MariaDB 12.1.2

### 推奨

- Linux/macOS での開発を推奨します．
  - WSL2 の使用は自己責任です．
- 使用する IDE は [IntelliJ IDEA Ultimate または Community Edition](https://www.jetbrains.com/ja-jp/idea/) を使用してください．
  - Ultimate Edition の機能が使えた方が楽ですが，RuneCore の開発では Community Edition で十分です．
  - [学生なら無料で使えます](https://www.jetbrains.com/ja-jp/academy/student-pack/)．
- [Git](https://git-scm.com/)
  - GitHub Desktop などについてはサポートできないので自己責任で使用してください．
- [Docker](https://www.docker.com/)
    - デバッグサーバを立ち上げるために使用します．
    - 別途で，[Docker Compose](https://docs.docker.com/compose/) も必要です．

### セットアップ

[mise](https://github.com/jdx/mise) を使用して，開発環境を簡単にセットアップできます．

```bash
mise install
```

## 技術スタック

- 言語: [Kotlin](https://kotlinlang.org/)
- ビルドツール: [Gradle](https://gradle.org/)
- データベース: [MariaDB](https://mariadb.org/)
  - ORM: [Exposed](https://github.com/JetBrains/Exposed)
  - JDBC: [HikariCP](https://github.com/brettwooldridge/HikariCP)

## コントリビューション時の注意点

- RuneCore は保守性を重視しています．コードの可読性を損なう変更は避けてください．
- フォーマッターとして [ktlint](https://github.com/pinterest/ktlint), [ktlint-gradle](https://github.com/jlleitschuh/ktlint-gradle) を使用しています．コードをコミットする前に `./gradlew ktlintFormat` を実行してコードを整形してください．
  - 整形されていないコードにおいては CI が失敗します．忘れないようにしてください．
  - 使用できるタスクは [付録: タスク索引](#付録-タスク索引) を参照してください．

## ブランチ・コミット戦略

- 新しい機能やバグ修正は，`main` ブランチから派生したトピックブランチで行ってください．
  - ブランチの命名規則は特にありませんが，`feat/` や `fix/` などのプレフィックスを使用することを推奨します．
    - 例: `feat/add-new-api`, `fix/fix-null-pointer`
  - 全く意味をなさないブランチ名は禁止します．
- コミットメッセージは Conventional Commits に準拠します． (参考: [Conventional Commitsとは?](https://www.conventionalcommits.org/ja/v1.0.0/#%e6%a6%82%e8%a6%81))
  - また，コミットメッセージは英語で記述してください．
  - 例外なく **gitmoji の使用を禁止します**．
  - 例: `feat: add new API for user authentication`

## コーディング規約

- RuneCore は全面 [Kotlin](https://kotlinlang.org/) を使用します．よほどの理由がない限り Java の使用は禁じます．
- コードスタイルガイドラインは [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html) に準拠します．
    - IntelliJ IDEA の設定で，Kotlin のコードスタイルを上記ガイドラインに合わせることを推奨します． (`Settings > Editor > Code Style > Kotlin > Set from... > Kotlin style guide`)

![](https://github.com/user-attachments/assets/b5996dd1-7f0c-4ed4-929e-1f52bf11212f)

### 命名規則

- 関数や変数の命名はキャメルケースを使用してください．

```kotlin
val playerName = "JaneDoe"
fun getPlayerName(): String {}
```

- クラス名やインターフェースの命名はパスカルケースを使用してください．

```kotlin
class PlayerCharacter {}
interface GameService {}
```

- 定数の命名はアッパースネークケースを使用してください．

```kotlin
const val MAX_HEALTH_POINTS = 100
```

- パッケージ名はすべて小文字で記述し，ドメイン名の逆順を使用してください．
  - RuneCore　では `org.lyralis.runeCore` を使用しています．

```kotlin
package org.lyralis.runeCore.action ...
```

### Import 文

- 未使用の Import 文は削除してください．
- Import 文に対してワイルドカードを使用することは禁止します．
  - どのクラスが実際に使用されているかが不明瞭になることや，意図しないクラスのインポート，名前衝突の可能性があるためです．
  - これは Bukkit API であっても RuneCore 内部であっても同様です．

```kotlin
// Good
import org.bukkit.entity.Player
// Bad
import org.bukkit.entity.*
```

## 権限管理

- RuneCore の権限は sealed class として実装してるため，Spigot/Paper の作法とは少し違う扱いをしています．
- 権限は全て [Permission.kt](../src/main/kotlin/dev/m1sk9/runeCore/permission/Permission.kt) で管理しています．

### 権限を追加する

権限を追加する際は次の手順に従います．

1. [paper-plugin.yml](../src/main/resources/paper-plugin.yml) に権限ノードを追加する

    - プレイヤーの権限は `runecore.player.basic.*` または `runecore.player.admin.*` に従います．x

    ```yaml
    runecore.player.admin.*:
        default: op
        children:
            ## Debug mode
            - runecore.player.admin.debugmode
            - runecore.player.admin.debugmode.switchinggam
    ```

2. [Permission.kt](../src/main/kotlin/dev/m1sk9/runeCore/permission/Permission.kt) に新しい object を定義する．

    ```kotlin
    sealed class Admin(
       node: String,
    ) : Permission(node) {
       object DebugMode : Admin("runecore.player.admin.debugmode")
       object DebugModeSwitchingGameMode : Admin("runecore.player.admin.debugmode.switchinggame")
    }
    ```
   
### 権限の確認

- 権限の確認は [PermissionChecker.kt](../src/main/kotlin/dev/m1sk9/runeCore/permission/PermissionChecker.kt) に定義されているヘルパー関数を使用します．
- 詳しい使用方法は JavaDoc または IDE の [Render Javadocs](https://www.jetbrains.com/help/idea/javadocs.html#toggle-rendered-view) を使用して確認してください．

## メッセージコンポーネント

- RuneCore のメッセージには一貫性を持たせるため，Kotlin の [拡張関数](https://kotlinlang.org/docs/extensions.html) と言う機能を使用し，[既存の `String`, `List<String>` クラスに RuneCore 独自のヘルパー関数を追加しています](../src/main/kotlin/dev/m1sk9/runeCore/component/MessageComponent.kt)．
- プレイヤー向けに送信するメッセージには適したヘルパー関数を使用してコンポーネントを付与した上で `sendMessage()` や `sendActionBar()` を使用してください．

```kotlin
fun String.systemMessage(): Component = Component.text(this).color(SYSTEM_COLOR)
fun String.errorMessage(): Component = Component.text(this).color(ERROR_COLOR)
```

```kotlin
player.sendActionBar("ゲームモードを変更しました: ${player.gameMode}".systemMessage())
```

## コマンドシステム

- RuneCore は Paper の `LifecycleEventManager` を使用した型安全なコマンドシステムを採用しています．

```
src/main/kotlin/dev/m1sk9/runeCore/command/
├── annotation/
│   ├── CommandPermission.kt    # 権限アノテーション
│   └── PlayerOnlyCommand.kt    # プレイヤー専用アノテーション
├── register/
│   ├── RuneCommand.kt          # コマンドインターフェース
│   ├── CommandRegistry.kt      # コマンド登録
│   ├── CommandResult.kt        # 実行結果
│   ├── RuneCommandContext.kt   # 実行コンテキスト
│   └── SuggestionContext.kt    # Tab補完コンテキスト
└── impl/                       # コマンド実装
    └── ...
```

- コマンドの実装は `impl/` ディレクトリに配置します．
- `RuneCommand` を継承します．
  - Spigot/Paper の `CommandExecutor` は使用しないでください．

```kotlin
class MyCommand : RuneCommand {
    override val name = "mycommand"
    override val description = "コマンドの説明"

    override fun execute(context: RuneCommandContext): CommandResult {
        val sender = context.sender
        return CommandResult.Success("実行しました")
    }
}
```

#### プレイヤー専用のコマンドを実装する

- `@PlayerOnlyCommand` アノテーションを付与すると，コンソールからの実行を自動的に拒否します．
- Spigot/Paper の `CommandExecutor` は本来 `sender` が Player かどうかの判定が必要ですが，このアノテーションが付与されたコマンドは基本的に Player であることが確定するので， `.playerOrThrow` で null安全を保ちながら実装ができます．
- 基本的なコマンドはコンソールからの実行を想定しないと思いますので，`@PlayerOnlyCommand` アノテーションを付与することを忘れないようにしてください．

```kotlin
@PlayerOnlyCommand
class PlayerCommand : RuneCommand {
    override val name = "playeronly"
    override val description = "プレイヤー専用コマンド"

    override fun execute(context: RuneCommandContext): CommandResult {
        val player = context.playerOrThrow
        
        player.sendMessage("Hello, ${player.name}!")
        return CommandResult.Silent
    }
}
```

#### 権限付きコマンドを実装する

- `@CommandPermission` アノテーションで必要な権限を指定します．

```kotlin
@CommandPermission("runecore.admin")
class AdminCommand : RuneCommand {
    override val name = "admin"
    override val description = "管理者専用コマンド"

    override fun execute(context: RuneCommandContext): CommandResult {
        return CommandResult.Success("管理者コマンドを実行しました")
    }
}
```

#### サブコマンドを持つコマンドを実装する

```kotlin
class ParentCommand : RuneCommand {
    override val name = "parent"
    override val description = "親コマンド"

    override val subcommands: List<RuneCommand> = listOf(
        ChildCommand(),
        AnotherChildCommand(),
    )

    override fun execute(context: RuneCommandContext): CommandResult {
        // サブコマンドなしで実行された場合
        return CommandResult.Success("使用法: /parent <subcommand>")
    }
}

class ChildCommand : RuneCommand {
    override val name = "child"
    override val description = "子コマンド"

    override fun execute(context: RuneCommandContext): CommandResult {
        return CommandResult.Success("子コマンドを実行しました")
    }
}
```

### CommandResult

コマンドの実行結果は `CommandResult` で表現します．

| 型                                                       | 用途          |
|---------------------------------------------------------|-------------|
| `CommandResult.Success(message?)`                       | 成功（メッセージ付き） |
| `CommandResult.Silent`                                  | 成功（メッセージなし） |
| `CommandResult.Failure.InvalidArguments(usage)`         | 引数エラー       |
| `CommandResult.Failure.NoPermission(permission)`        | 権限エラー       |
| `CommandResult.Failure.PlayerOnly(reason?)`             | プレイヤー専用エラー  |
| `CommandResult.Failure.TargetNotFound(target)`          | 対象が見つからない   |
| `CommandResult.Failure.ExecutionFailed(reason, cause?)` | 実行エラー       |
| `CommandResult.Failure.Custom(message)`                 | カスタムエラー     |

```kotlin
override fun execute(context: RuneCommandContext): CommandResult {
    val player = context.player
        ?: return CommandResult.Failure.PlayerOnly()

    if (player.isFlying) {
        return CommandResult.Failure.ExecutionFailed("飛行中は実行できません")
    }

    return CommandResult.Success("完了しました")
}
```

### RuneCommandContext

コマンド実行時のコンテキスト情報を提供します．

| プロパティ/メソッド             | 型                    | 説明                  |
|------------------------|----------------------|---------------------|
| `source`               | `CommandSourceStack` | Paper のコマンドソース      |
| `sender`               | `CommandSender`      | コマンド実行者             |
| `player`               | `Player?`            | プレイヤー（nullable）     |
| `playerOrThrow`        | `Player`             | プレイヤー（非null，失敗時は例外） |
| `args`                 | `Map<String, Any>`   | コマンド引数              |
| `getArgument<T>(name)` | `T?`                 | 型安全な引数取得            |

### Tab 補完

`suggest` メソッドをオーバーライドして Tab 補完を実装します．

```kotlin
class TeleportCommand : RuneCommand {
    override val name = "teleport"
    override val description = "プレイヤーにテレポート"

    override fun suggest(context: SuggestionContext): List<String> {
        val playerNames = Bukkit.getOnlinePlayers().map { it.name }
        return context.filterStartsWith(playerNames)
    }

    override fun execute(context: RuneCommandContext): CommandResult {
        // ...
    }
}
```

### コマンドの登録

作成したコマンドは `RuneCore.kt` の `CommandRegistry` に登録します．

```kotlin
// RuneCore.kt
override fun onEnable() {
    // ...

    CommandRegistry(this)
        .register(RuneRootCommand())  // ルートコマンドを登録
        .registerAll(lifecycleManager)

    // ...
}
```

サブコマンドは親コマンドの `subcommands` プロパティに追加するだけで自動的に登録されます．

### コマンドの命名規則

- コマンドクラス名: `Rune{機能名}Command`（例: `RuneLogoutCommand`, `RuneInfoCommand`）
- パッケージ: `org.lyralis.runeCore.command.impl`

## データベース

RuneCore ではデータベースに MariaDB を採用しています．

### `runecore_db` について

RuneCore 内部で使用している `runecore_db` は以下のテーブルを持ちます．

いずれもプレイヤーの UUID を主キーとします．([UUID を主キーとしている理由はこちら](#データを扱う際に-uuid-を使う理由))

- [`Players`](../src/main/kotlin/dev/m1sk9/runeCore/database/table/Players.kt):
  - プレイヤーデータを格納する基本テーブル
- [`PlayerStats`](../src/main/kotlin/dev/m1sk9/runeCore/database/table/PlayerStats.kt):
  - プレイヤーのスタッツ (統計情報) を格納するテーブル

### データの扱い方

- データベースへの作用は基本的に Repository として実装します．
  - RuneCore では `kotlin/dev/m1sk9/runeCore/database/repository` 配下におきます．
  - SQL 文での直接操作は避けます．
- データベース操作の結果 (成功か失敗か) を表現するための sealed class が各 Repository に実装されているため，データベース操作は基本的に `when` を使い，エラー時の振る舞いも書く必要があります．
  - イメージ的には Rust の `match` 文を使った `Result<T, E>` の処理と似ています．


```kotlin
when (val result = playerRepository.existsByUUID(uuid)) {
    is RepositoryResult.Success -> {
        if (!result.data) {
            when (playerRepository.createPlayer(uuid)) {
                is RepositoryResult.Success -> {
                    logger.info("Created new player data: $uuid")
                }
                is RepositoryResult.Error -> {
                    logger.severe("Error creating player data: $uuid")
                }
                else -> {}
            }
        }
    }
    is RepositoryResult.Error -> {
        logger.severe("Failed to check player: $uuid")
    }
    else -> {}
}
```

- [`RepositoryResult`](../src/main/kotlin/dev/m1sk9/runeCore/database/repository/RepositoryResult.kt) にはエラー型が用意されているため， `is` で取り出して，その型に沿った処理を書いてください．
  - IDEA なら補完が効くはずです．

![](https://github.com/user-attachments/assets/51b83ebb-5167-4d6f-bd55-2e47fb5506ca)

## デバッグサーバ

- RuneCore の開発環境には Docker を使用したデバッグサーバが付属しています．
- デバッグサーバには以下の環境が付属しています．
  - Paper
  - MariaDB
- Make コマンドを使用して，デバッグサーバを起動・停止できます．

```bash
# デバッグサーバの起動
make start

# デバッグサーバの停止
make stop

# デバッグサーバの再起動
make restart

# デバッグサーバのリセット
make clean

# Rcon クライアントの起動
make rcon

# ログの表示
make logs
```

### 外部プラグインの導入

**前提として:** Rune of Mer は外部プラグインを積極的に採用していません．この機能は最低限導入する外部プラグインおよび開発支援のためにあります．

- 外部プラグインをデバックサーバに導入するには `docker/plugins.txt` にプラグインのダウンロード URL を追加します．
- 追加後， `make start` を実行するか `docker/download-plugins.sh` スクリプトを実行するとプラグインをダウンロードできます．
  - RuneCore の再ビルドは `make start` 時に自動で行われるので不要です．
- サポートされる URL フォーマットは次の通りです:
  - Jar ファイルへの直接ダウンロード URL
  - GitHub のリリース URL (例: `https://github.com/owner/repo/releases/download/version/plugin.jar`)
  - SpigotMC のリソース URL (手動ダウンロードが必要です)

```text
# Examples:
# https://github.com/EssentialsX/Essentials/releases/download/2.20.1/EssentialsX-2.20.1.jar
# https://github.com/PlaceholderAPI/PlaceholderAPI/releases/download/2.11.3/PlaceholderAPI-2.11.3.jar

https://github.com/EssentialsX/Essentials/releases/download/2.21.2/EssentialsX-2.21.2.jar
```

## 付録: コラム

### データを扱う際に UUID を使う理由

- Minecraft においてプレイヤーの ID として機能としている MCID は可変です． (つまり，いつでも変更できます．)
- そのため，データベースなどプレイヤーのデータとして扱っているもので MCID を使って管理すると，プレイヤーが MCID を変更した時点で，そのデータは **[完全性 *Data integrity* ](https://en.wikipedia.org/wiki/Data_integrity) を失うことになります**．
- 幸運にもプレイヤーには **重複しないこと，衝突しないこと，不変であること** が約束されている ID として UUID が存在しているため，データベースなどでは UUID を主キーとして扱うべきです．
- RuneCore ではプレイヤーデータに MCID を含めていないのは MCID が信用できるものではないからです．
  - Paper API から都度取得した方が信頼できます．

## 付録: タスク索引

### ktlint (ktlint-gradle)

- `./gradlew ktlintCheck` : コードスタイルのチェックを実行します．
- `./gradlew ktlintFormat` : コードスタイルの自動整形を実行します．

[その他のタスク一覧](https://github.com/jlleitschuh/ktlint-gradle?tab=readme-ov-file#tasks-added)
