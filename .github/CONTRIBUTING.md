# RuneCore Contributing Guide

## 開発環境

RuneCore は以下の環境で動作します:

- Java 21 以上
    - Rune of Mel では [Zulu OpenJDK](https://www.azul.com/downloads/?package=jdk) を使用しています．
- Gradle 8.8 以上

開発するために必要なツールは以下の通りです:

- [IntelliJ IDEA Ultimate または Community Edition](https://www.jetbrains.com/ja-jp/idea/)
- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/)
    - デバッグサーバを立ち上げるために使用します．
    - 別途で，[Docker Compose](https://docs.docker.com/compose/) も必要です．

### 非推奨・サポート外の開発環境

- WSL2
- Eclipse などの IntelliJ IDEA を除く IDE
- GitHub Desktop などの GUI クライアント
    - サポートしません．

### 開発環境のセットアップ

[mise](https://github.com/jdx/mise) を使用して，開発環境を簡単にセットアップできます．

```bash
mise install
```

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
  - RuneCore　では `dev.m1sk9.runeCore` を使用しています．

```kotlin
package dev.m1sk9.runeCore.action ...
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

## デバッグサーバ

RuneCore の開発環境には Docker を使用したデバッグサーバが付属しています．

Make コマンドを使用して，デバッグサーバを起動・停止できます．

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

**前提として:** Rune of Mel は外部プラグインを積極的に採用していません．この機能は最低限導入する外部プラグインおよび開発支援のためにあります．

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

## 付録: タスク索引

### ktlint (ktlint-gradle)

- `./gradlew ktlintCheck` : コードスタイルのチェックを実行します．
- `./gradlew ktlintFormat` : コードスタイルの自動整形を実行します．

[その他のタスク一覧](https://github.com/jlleitschuh/ktlint-gradle?tab=readme-ov-file#tasks-added)
