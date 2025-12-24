# RuneCore

[![Build](https://github.com/rune-of-mer/RuneCore/actions/workflows/build.yml/badge.svg)](https://github.com/rune-of-mer/RuneCore/actions/workflows/build.yml)
[![Release](https://github.com/rune-of-mer/RuneCore/actions/workflows/release.yml/badge.svg)](https://github.com/rune-of-mer/RuneCore/actions/workflows/release.yml)
[![Apache License 2.0](https://img.shields.io/github/license/rune-of-mer/RuneCore?color=%239944ee)](https://github.com/rune-of-mer/RuneCore/blob/main/LICENSE)

Rune of Mer におけるコアプラグイン．

貢献に関するガイドは [こちら](.github/CONTRIBUTING.md)を参照してください．

- [Contributing Guide](.github/CONTRIBUTING.md)
- [Dokka (RuneCore API Documentation)](http://runeofmer-api.lyralis.org/)
- [Discord](https://discord.gg/jSsce2T4Xv)

```shell
git clone git@github.com:rune-of-mer/RuneCore.git
cd RuneCore

./gradlew shadowJar
```

_[Supports Minecraft 1.21.10](https://ja.minecraft.wiki/w/Java_Edition_1.21.10) | [Requires Java 21+ and Gradle 9+](.github/CONTRIBUTING.md#開発環境)_

## 依存関係プラグイン

RuneCore を動作させるには次のプラグインが必要です．

- [Multiverse-Core](https://modrinth.com/plugin/multiverse-core)

## セットアップ

- RuneCore は Minecraft 1.21.10 をサポートしている **Paper プラグイン** です．
  - Paper API を全面的にサポートしているため， [Spigot](https://www.spigotmc.org/) では使用できません．
  - また，マルチコアでの動作は未検証のため，[Folia](https://github.com/PaperMC/Folia) では動作しません．

詳しいセットアップ方法は [RuneCore Deploy Guide](./docs/deploy.md) を参照してください，

## ライセンス

RuneCore は GNU General Public License v3.0 の下でライセンスされています．

当プラグインを再利用する場合はこれらのコードを GPL でライセンスし，ソースコードを公開する必要があります．

当プラグインを使用する場合は [GNU General Public License v3.0](https://gpl.mhatta.org/gpl.ja.html) への理解を深めた後， [LICENSE](LICENSE) ファイルを参照してください．

<sub>
  © 2025 Sho Sakuma and Rune of Mer DevTeam.
  <br />
  RuneCore and Rune of Mer are not affiliated with Mojang Studios or Microsoft.
</sub>
