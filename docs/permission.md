# RuneCore Permission Guide

<!-- TOC -->
* [RuneCore Permission Guide](#runecore-permission-guide)
  * [親ノード (ロール)](#親ノード-ロール)
  * [子ノード](#子ノード)
    * [運営 (`runecore.role.admin`)](#運営-runecoreroleadmin)
<!-- TOC -->

## 親ノード (ロール)

RuneCore には **運営・メンバー** に対して2つの権限ノードがロールとして用意されています．

LuckPerms の Group に対して以下の権限ノードを指定するのを推奨します．

- `runecore.role.player`
- `runecore.role.admin`

## 子ノード

[ロール](#親ノード-ロール) に対して各種子ノードが用意されています．

基本的には対応している LuckPerms の Group に対しての権限ノードを指定します．

### 運営 (`runecore.role.admin`)

| 権限ノード                                           | 説明                                                         |
|-------------------------------------------------|------------------------------------------------------------|
| `runecore.player.admin.debugmode`               | デバッグモードの使用を許可する．この権限ノードは全ての機能に関する子ノードをオーバーライドします．          |
| `runecore.player.admin.debugmode.switchinggame` | デバッグモードのゲームモード切り替えを許可する．                                   |
| `runecore.player.admin.command.experience`      | `/experience` コマンドの使用を許可する．この権限ノードはサブコマンドの子ノードをオーバーライドします． |
| `runecore.player.admin.command.experience.add`  | `/experience add` コマンドの使用を許可する．                            |
