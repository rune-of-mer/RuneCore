# RuneCore Deploy Guide

RuneCore のデプロイガイド． 

> ![WARNING]
> 
> Rune of Mer で使用されているデプロイガイドではありません．このデプロイガイドは最低限の環境で構築することを目的にしています．本番環境での使用は推奨していません．

<!-- TOC -->
* [RuneCore Deploy Guide](#runecore-deploy-guide)
  * [前提条件](#前提条件)
    * [推奨環境](#推奨環境)
  * [準備](#準備)
    * [1. Java (JDK) のインストール](#1-java-jdk-のインストール)
    * [2. MariaDB のインストール](#2-mariadb-のインストール)
      * [Docker を使用する場合](#docker-を使用する場合)
      * [ネイティブインストール (Linux Ubuntu/Debian)](#ネイティブインストール-linux-ubuntudebian)
    * [3. RuneCore のインストール](#3-runecore-のインストール)
      * [リリース版を使用する (推奨)](#リリース版を使用する-推奨)
      * [ベータ版を使用する](#ベータ版を使用する)
      * [ソースからビルドする](#ソースからビルドする)
    * [4. Paper サーバを構築する](#4-paper-サーバを構築する)
      * [サーバーディレクトリの作成](#サーバーディレクトリの作成)
      * [Paper のダウンロードと起動](#paper-のダウンロードと起動)
      * [サーバ設定の調整](#サーバ設定の調整)
      * [RuneCore と推奨・依存関係のプラグインを導入する](#runecore-と推奨依存関係のプラグインを導入する)
      * [設定ファイルの作成](#設定ファイルの作成)
      * [権限の設定](#権限の設定)
    * [5. サーバの起動](#5-サーバの起動)
    * [6. データベース接続の確認](#6-データベース接続の確認)
    * [7. プレイヤーとして接続する](#7-プレイヤーとして接続する)
  * [ヒント](#ヒント)
    * [JVM の最適化](#jvm-の最適化)
<!-- TOC -->

## 前提条件

RuneCore を導入した Paper サーバを構築するためには以下の環境が必要です．

- **Java 21**
  - Rune of Mer では Zulu OpenJDK を使用しています．
- **MariaDB 12** 
  - RuneCore は MariaDB 上にデータベースを構築します．
- **Paper 1.21.10**
  - RuneCore は Paper API を使用しているため，Spigot では使用できません．

### 推奨環境

- **OS**: Linux
- **メモリ**: 最低8GB，推奨 16GB 以上
  - Minecraft サーバではプレイヤー人数に比例して必要なメモリ量が増えます．
  - MariaDB の動作も考え， 16GB 以上のメモリを用意しておくことを推奨します．
- **CPU**: 高クロックな CPU
  - Minecraft はシングルスレッドの性能を要求するため，なるべく高クロックな CPU を推奨します．

## 準備

### 1. Java (JDK) のインストール

[Zulu JDK](https://www.azul.com/downloads/?version=java-21-lts&os=linux&package=jdk#zulu) や各種ベンダーのドキュメントを参照し， Java 21 の JDK をインストールしてください．

### 2. MariaDB のインストール

#### Docker を使用する場合

- 以下のコマンドを実行するか， Compose で起動してください．

```bash
docker run -d \
  --name runecore-mariadb \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=runecore_db \
  -e MYSQL_USER=runecore \
  -e MYSQL_PASSWORD=runecore \
  -v mariadb-data:/var/lib/mysql \
  mariadb:12.1.2
```

```yaml
  mariadb:
    image: mariadb:12.1.2
    container_name: runecore-mariadb
    ports:
      - "3306:3306"
    volumes:
      - mariadb-data:/var/lib/mysql
    environment:
      MYSQL_DATABASE: "runecore_db"
      MYSQL_USER: "runecore" # 適切なユーザーに変更する
      MYSQL_PASSWORD: "runecore" # 適切なパスワードに変更する
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
      interval: 10s
      timeout: 5s
      retries: 5
```

#### ネイティブインストール (Linux Ubuntu/Debian)

```bash
sudo apt update
sudo apt install mariadb-server

# セキュリティ設定
sudo mysql_secure_installation

# データベースとユーザーの作成
sudo mysql -u root -p
```

```sql
CREATE DATABASE runecore_db;
CREATE USER 'runecore'@'localhost' IDENTIFIED BY 'runecore';
GRANT ALL PRIVILEGES ON runecore_db.* TO 'runecore'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

- 初回起動時のマイグレーションなどは自動で行われます．

### 3. RuneCore のインストール

#### リリース版を使用する (推奨)

RuneCore のリリース版は [GitHub Releases](https://github.com/rune-of-mer/RuneCore/releases) からダウンロード可能です．

#### ベータ版を使用する

- RuneCore では CI でのビルド時にアーティファクトとして RuneCore の成果物が公開されています．
  - CI 完了後の1週間限定でダウンロードできます．
  - ただし，ベータ版は不具合が存在する場合があります．**本番環境では使用しないでください**．

1. RuneCore の [CI](https://github.com/rune-of-mer/RuneCore/actions/workflows/build.yml?query=is%3Asuccess) を開く
2. `main` ブランチで実行が成功している CI を開く
3. `Artifacts` にある `rune-core-jar-*` をダウンロードする
4. ダウンロードされた ZIP ファイルの中に Jar が入っています．

![](https://github.com/user-attachments/assets/ff7cd772-86e2-4c5e-94f0-0e3ce93d12b0)

#### ソースからビルドする

```bash
# リポジトリのクローン
git clone https://github.com/rune-of-mer/RuneCore.git
cd RuneCore

# ビルド
./gradlew shadowJar

# 成果物は build/libs/rune-core-x.x.x-all.jar に生成される
```

### 4. Paper サーバを構築する

#### サーバーディレクトリの作成

```bash
# サーバー用のディレクトリを作成
mkdir -p ~/minecraft-server
cd ~/minecraft-server
```

#### Paper のダウンロードと起動

[Paper](https://papermc.io/) の公式サイトから Paper をダウンロードします．

```bash
# 初回起動
java -Xms2G -Xmx2G -jar paper.jar --nogui

# EULA への同意
echo "EULA=TRUE" > eula.txt
```

#### サーバ設定の調整

```properties
# 基本設定
server-name=RuneCore Server
motd=RuneCore を導入した Minecraft サーバー
max-players=15
difficulty=normal
gamemode=survival
pvp=true

# パフォーマンス設定
view-distance=10
simulation-distance=8

# その他
online-mode=true
spawn-protection=16
```

#### RuneCore と推奨・依存関係のプラグインを導入する

- RuneCore を `plugins/` に配置します．

```bash
cp /path/to/rune-core-all.jar plugins/
```

- また，以下の推奨・依存関係プラグインを導入します．
- 推奨プラグイン:
  - [LuckPerms](https://luckperms.net/download)

#### 設定ファイルの作成

- Paper を一度起動してデフォルト設定を生成します．

```bash
java -Xms2G -Xmx2G -jar paper.jar --nogui
```

- `plugins/RuneCore/config.yml` にデフォルト設定が生成されるので編集する．

#### 権限の設定

- 権限を LuckPerms で設定します．
  - 権限に関するドキュメントは [こちら](./permission.md) を参照してください．

### 5. サーバの起動

- サーバを以下のコマンドで起動します．

```bash
java -Xms2G -Xmx2G -jar paper.jar --nogui
```

- 以下のログが出力されることを確認します．

```log
[RuneCore] Connecting to database...
[RuneCore] Database connected.
[RuneCore] RuneCore enabled.
```

### 6. データベース接続の確認

- 以下のコマンドを実行し， RuneCore の DB が生成されているかを確認してください．

```bash
# MariaDB に接続して確認
mysql -u runecore -p runecore_db

# テーブルが作成されていることを確認
SHOW TABLES;
```

### 7. プレイヤーとして接続する

- Minecraft クライアントから接続し，以下のコマンドを試してください:

```
/playerlist
/level
/playtime
/patchnote
```

## ヒント

### JVM の最適化

```bash
java -Xms4G -Xmx4G \
  -XX:+UseG1GC \
  -XX:+ParallelRefProcEnabled \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+DisableExplicitGC \
  -XX:+AlwaysPreTouch \
  -XX:G1NewSizePercent=30 \
  -XX:G1MaxNewSizePercent=40 \
  -XX:G1HeapRegionSize=8M \
  -XX:G1ReservePercent=20 \
  -XX:G1HeapWastePercent=5 \
  -XX:G1MixedGCCountTarget=4 \
  -XX:InitiatingHeapOccupancyPercent=15 \
  -XX:G1MixedGCLiveThresholdPercent=90 \
  -XX:G1RSetUpdatingPauseTimePercent=5 \
  -XX:SurvivorRatio=32 \
  -XX:+PerfDisableSharedMem \
  -XX:MaxTenuringThreshold=1 \
  -jar paper.jar --nogui
```
