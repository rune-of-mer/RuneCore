package org.lyralis.runeCore.domain.money

import org.bukkit.entity.EntityType

/**
 * モブ殺害時の所持金定義
 *
 * 各モブに対応する所持金とカテゴリを定義する．
 */
enum class MobMoney(
    val entityType: EntityType,
    override val money: ULong,
    override val category: MoneyCategory,
) : MoneySource {
    // ========== ボスモブ ==========
    ENDER_DRAGON(EntityType.ENDER_DRAGON, 10000uL, MoneyCategory.MOB_BOSS),
    WITHER(EntityType.WITHER, 6000uL, MoneyCategory.MOB_BOSS),
    WARDEN(EntityType.WARDEN, 4000uL, MoneyCategory.MOB_BOSS),
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, 2000uL, MoneyCategory.MOB_BOSS),
    RAVAGER(EntityType.RAVAGER, 1000uL, MoneyCategory.MOB_BOSS),
    EVOKER(EntityType.EVOKER, 600uL, MoneyCategory.MOB_BOSS),

    // ========== 敵対モブ（ネザー） ==========
    GHAST(EntityType.GHAST, 70uL, MoneyCategory.MOB_HOSTILE),
    BLAZE(EntityType.BLAZE, 60uL, MoneyCategory.MOB_HOSTILE),
    WITHER_SKELETON(EntityType.WITHER_SKELETON, 50uL, MoneyCategory.MOB_HOSTILE),
    HOGLIN(EntityType.HOGLIN, 40uL, MoneyCategory.MOB_HOSTILE),
    ZOGLIN(EntityType.ZOGLIN, 50uL, MoneyCategory.MOB_HOSTILE),
    PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE, 400uL, MoneyCategory.MOB_HOSTILE),
    PIGLIN(EntityType.PIGLIN, 24uL, MoneyCategory.MOB_HOSTILE),
    ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN, 30uL, MoneyCategory.MOB_HOSTILE),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, 16uL, MoneyCategory.MOB_HOSTILE),
    STRIDER(EntityType.STRIDER, 10uL, MoneyCategory.MOB_HOSTILE),

    // ========== 敵対モブ（エンド） ==========
    ENDERMAN(EntityType.ENDERMAN, 50uL, MoneyCategory.MOB_HOSTILE),
    SHULKER(EntityType.SHULKER, 60uL, MoneyCategory.MOB_HOSTILE),
    ENDERMITE(EntityType.ENDERMITE, 10uL, MoneyCategory.MOB_HOSTILE),

    // ========== 敵対モブ（オーバーワールド） ==========
    CREEPER(EntityType.CREEPER, 30uL, MoneyCategory.MOB_HOSTILE),
    SKELETON(EntityType.SKELETON, 24uL, MoneyCategory.MOB_HOSTILE),
    STRAY(EntityType.STRAY, 28uL, MoneyCategory.MOB_HOSTILE),
    BOGGED(EntityType.BOGGED, 28uL, MoneyCategory.MOB_HOSTILE),
    ZOMBIE(EntityType.ZOMBIE, 20uL, MoneyCategory.MOB_HOSTILE),
    HUSK(EntityType.HUSK, 24uL, MoneyCategory.MOB_HOSTILE),
    DROWNED(EntityType.DROWNED, 24uL, MoneyCategory.MOB_HOSTILE),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, 24uL, MoneyCategory.MOB_HOSTILE),
    SPIDER(EntityType.SPIDER, 20uL, MoneyCategory.MOB_HOSTILE),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, 24uL, MoneyCategory.MOB_HOSTILE),
    SLIME(EntityType.SLIME, 10uL, MoneyCategory.MOB_HOSTILE),
    PHANTOM(EntityType.PHANTOM, 40uL, MoneyCategory.MOB_HOSTILE),
    SILVERFISH(EntityType.SILVERFISH, 6uL, MoneyCategory.MOB_HOSTILE),
    WITCH(EntityType.WITCH, 50uL, MoneyCategory.MOB_HOSTILE),
    GUARDIAN(EntityType.GUARDIAN, 200uL, MoneyCategory.MOB_HOSTILE),
    BREEZE(EntityType.BREEZE, 40uL, MoneyCategory.MOB_HOSTILE),

    // ========== 敵対モブ（襲撃） ==========
    PILLAGER(EntityType.PILLAGER, 30uL, MoneyCategory.MOB_HOSTILE),
    VINDICATOR(EntityType.VINDICATOR, 300uL, MoneyCategory.MOB_HOSTILE),
    VEX(EntityType.VEX, 20uL, MoneyCategory.MOB_HOSTILE),

    // ========== 中立モブ ==========
    IRON_GOLEM(EntityType.IRON_GOLEM, 100uL, MoneyCategory.MOB_NATURAL),
    BEE(EntityType.BEE, 10uL, MoneyCategory.MOB_NATURAL),
    LLAMA(EntityType.LLAMA, 10uL, MoneyCategory.MOB_NATURAL),
    TRADER_LLAMA(EntityType.TRADER_LLAMA, 10uL, MoneyCategory.MOB_NATURAL),
    PANDA(EntityType.PANDA, 10uL, MoneyCategory.MOB_NATURAL),
    POLAR_BEAR(EntityType.POLAR_BEAR, 30uL, MoneyCategory.MOB_NATURAL),
    WOLF(EntityType.WOLF, 16uL, MoneyCategory.MOB_NATURAL),
    DOLPHIN(EntityType.DOLPHIN, 10uL, MoneyCategory.MOB_NATURAL),
    GOAT(EntityType.GOAT, 10uL, MoneyCategory.MOB_NATURAL),

    // ========== 受動モブ（家畜） ==========
    PIG(EntityType.PIG, 6uL, MoneyCategory.MOB_PASSIVE),
    COW(EntityType.COW, 6uL, MoneyCategory.MOB_PASSIVE),
    MOOSHROOM(EntityType.MOOSHROOM, 10uL, MoneyCategory.MOB_PASSIVE),
    SHEEP(EntityType.SHEEP, 6uL, MoneyCategory.MOB_PASSIVE),
    CHICKEN(EntityType.CHICKEN, 4uL, MoneyCategory.MOB_PASSIVE),
    RABBIT(EntityType.RABBIT, 4uL, MoneyCategory.MOB_PASSIVE),
    HORSE(EntityType.HORSE, 10uL, MoneyCategory.MOB_PASSIVE),
    DONKEY(EntityType.DONKEY, 10uL, MoneyCategory.MOB_PASSIVE),
    MULE(EntityType.MULE, 10uL, MoneyCategory.MOB_PASSIVE),
    CAMEL(EntityType.CAMEL, 16uL, MoneyCategory.MOB_PASSIVE),
    SNIFFER(EntityType.SNIFFER, 20uL, MoneyCategory.MOB_PASSIVE),
    ARMADILLO(EntityType.ARMADILLO, 10uL, MoneyCategory.MOB_PASSIVE),

    // ========== 受動モブ（水生） ==========
    SQUID(EntityType.SQUID, 6uL, MoneyCategory.MOB_PASSIVE),
    GLOW_SQUID(EntityType.GLOW_SQUID, 10uL, MoneyCategory.MOB_PASSIVE),
    COD(EntityType.COD, 2uL, MoneyCategory.MOB_PASSIVE),
    SALMON(EntityType.SALMON, 2uL, MoneyCategory.MOB_PASSIVE),
    TROPICAL_FISH(EntityType.TROPICAL_FISH, 2uL, MoneyCategory.MOB_PASSIVE),
    PUFFERFISH(EntityType.PUFFERFISH, 4uL, MoneyCategory.MOB_PASSIVE),
    TURTLE(EntityType.TURTLE, 10uL, MoneyCategory.MOB_PASSIVE),
    AXOLOTL(EntityType.AXOLOTL, 10uL, MoneyCategory.MOB_PASSIVE),
    FROG(EntityType.FROG, 6uL, MoneyCategory.MOB_PASSIVE),
    TADPOLE(EntityType.TADPOLE, 2uL, MoneyCategory.MOB_PASSIVE),

    // ========== 受動モブ（その他） ==========
    BAT(EntityType.BAT, 2uL, MoneyCategory.MOB_PASSIVE),
    CAT(EntityType.CAT, 6uL, MoneyCategory.MOB_PASSIVE),
    OCELOT(EntityType.OCELOT, 10uL, MoneyCategory.MOB_PASSIVE),
    PARROT(EntityType.PARROT, 6uL, MoneyCategory.MOB_PASSIVE),
    FOX(EntityType.FOX, 10uL, MoneyCategory.MOB_PASSIVE),
    ALLAY(EntityType.ALLAY, 20uL, MoneyCategory.MOB_PASSIVE),
    HAPPYGHAST(EntityType.HAPPY_GHAST, 10uL, MoneyCategory.MOB_PASSIVE),
    ;

    companion object {
        private val byEntityType: Map<EntityType, MobMoney> =
            entries.associateBy { it.entityType }

        /**
         * EntityType から所持金を取得する
         *
         * @param entityType エンティティタイプ
         * @return 所持金．未定義の場合は 0
         */
        fun getMoney(entityType: EntityType): ULong = byEntityType[entityType]?.money ?: 0uL

        /**
         * EntityType から MobMoney を取得する
         *
         * @param entityType エンティティタイプ
         * @return MobMoney．未定義の場合は null
         */
        fun fromEntityType(entityType: EntityType): MobMoney? = byEntityType[entityType]

        /**
         * カテゴリでフィルタリングする
         *
         * @param category フィルタするカテゴリ
         * @return 該当する MobMoney のリスト
         */
        fun byCategory(category: MoneyCategory): List<MobMoney> = entries.filter { it.category == category }

        /**
         * 所持金が定義されているかどうか
         *
         * @param entityType エンティティタイプ
         * @return 定義されている場合は true
         */
        fun isDefined(entityType: EntityType): Boolean = byEntityType.containsKey(entityType)
    }
}
