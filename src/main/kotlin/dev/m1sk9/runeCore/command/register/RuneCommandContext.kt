package dev.m1sk9.runeCore.command.register

import io.papermc.paper.command.brigadier.CommandSourceStack
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * 実装されたコマンドの各種コンテキスト情報を実装するデータクラス．
 *
 * @param source [CommandSourceStack] - Paper から返ってくるコマンドの情報
 * @param args コマンドの引数
 */
data class RuneCommandContext(
    val source: CommandSourceStack,
    val args: Array<String>,
) {
    /**
     * コマンドの実行者を返す．
     *
     * - 必ずしもプレイヤーとは限らず，コンソールを示す場合がある．
     * - この API は内部実装のために使用される．これを使って [Player] へのアクションは実行しないこと．
     */
    val sender: CommandSender get() = source.sender

    /**
     * コマンドの実行者を返す．
     *
     * - [dev.m1sk9.runeCore.command.annotation.PlayerOnlyCommand] アノテーションを付与しない場合，この値は必ずしもプレイヤーとは限らず，コンソールを示す場合がある．
     * - この API は内部実装のために使用される．これを使って [Player] へのアクションは非推奨となる．
     * - 非 null アサーション演算子を使って，この値から [Player] を取り出すことはせず， [playerOrThrow] を使うこと．
     */
    val player: Player? get() = sender as? Player

    /**
     * コマンドの実行者を返す．
     *
     * [Player] であることを保証できる唯一の値である．
     *
     * - [dev.m1sk9.runeCore.command.annotation.PlayerOnlyCommand] アノテーションを付与しない場合，この値は必ずしもプレイヤーとは限らず，コンソールを示す場合がある．
     *
     * @throws IllegalStateException [dev.m1sk9.runeCore.command.annotation.PlayerOnlyCommand] アノテーションを付与されたクラスでコンソールからの処理が行われた場合にスローされる．
     */
    val playerOrThrow: Player
        get() = player ?: throw IllegalStateException("Player only command")

    /**
     * 指定したインデックスの引数を返す．
     *
     * @param index 引数のインデックス
     * @param def 引数が指定されていなかった場合のデフォルト値
     * @return 引数の値．指定されていなかった場合は `def` のデフォルト値が使用される．
     */
    fun arg(
        index: Int,
        def: String,
    ): String = args.getOrNull(index) ?: def

    /**
     * 指定したインデックスの引数を返す．
     *
     * - 指定されたインデックスの引数が存在していて [String] であることを保証できる唯一の値である．
     * - Spigot/Paper API の仕様上，引数の null 安全を保証できないため，基本は [arg] を使います．
     *
     * @param index 引数のインデックス
     * @return 引数の値．存在していない場合はエラーとなる．
     * @throws IllegalStateException 値が存在していなかった場合にスローされる．
     */
    fun argOrThrow(index: Int): String = args.getOrNull(index) ?: throw IllegalArgumentException("Argument at index $index not found")

    /**
     * このコンテキストと他のオブジェクトが等しいかどうかを判定します．
     *
     * @param other 比較対象のオブジェクト
     * @return 等しい場合は true, それ以外は false
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as RuneCommandContext
        return source == other.source && args.contentEquals(other.args)
    }

    /**
     * このコンテキストのハッシュコードを計算します．
     *
     * @return ハッシュコード
     */
    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
}
