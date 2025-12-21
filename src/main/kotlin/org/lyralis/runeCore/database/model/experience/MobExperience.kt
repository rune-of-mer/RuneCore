package org.lyralis.runeCore.experience.source

import org.bukkit.entity.EntityType
import org.lyralis.runeCore.database.model.experience.ExperienceCategory
import org.lyralis.runeCore.database.model.experience.ExperienceSource

// TODO: 経験値を獲得できる Mob の調整は必要．特にカッパーゴーレム・ハッピーガストなどの特殊Mobに至っては経験値が獲得できるのはおかしい

/**
 * モブ殺害時の経験値定義
 *
 * 各モブに対応する経験値とカテゴリを定義する．
 */
enum class MobExperience(
    val entityType: EntityType,
    override val experience: ULong,
    override val category: ExperienceCategory,
) : ExperienceSource {
    // ========== ボスモブ ==========
    ENDER_DRAGON(EntityType.ENDER_DRAGON, 5000uL, ExperienceCategory.MOB_BOSS),
    WITHER(EntityType.WITHER, 3000uL, ExperienceCategory.MOB_BOSS),
    WARDEN(EntityType.WARDEN, 2000uL, ExperienceCategory.MOB_BOSS),
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, 1000uL, ExperienceCategory.MOB_BOSS),
    RAVAGER(EntityType.RAVAGER, 500uL, ExperienceCategory.MOB_BOSS),
    EVOKER(EntityType.EVOKER, 300uL, ExperienceCategory.MOB_BOSS),

    // ========== 敵対モブ（ネザー） ==========
    GHAST(EntityType.GHAST, 35uL, ExperienceCategory.MOB_HOSTILE),
    BLAZE(EntityType.BLAZE, 30uL, ExperienceCategory.MOB_HOSTILE),
    WITHER_SKELETON(EntityType.WITHER_SKELETON, 25uL, ExperienceCategory.MOB_HOSTILE),
    HOGLIN(EntityType.HOGLIN, 20uL, ExperienceCategory.MOB_HOSTILE),
    ZOGLIN(EntityType.ZOGLIN, 25uL, ExperienceCategory.MOB_HOSTILE),
    PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE, 200uL, ExperienceCategory.MOB_HOSTILE),
    PIGLIN(EntityType.PIGLIN, 12uL, ExperienceCategory.MOB_HOSTILE),
    ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN, 15uL, ExperienceCategory.MOB_HOSTILE),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, 8uL, ExperienceCategory.MOB_HOSTILE),
    STRIDER(EntityType.STRIDER, 5uL, ExperienceCategory.MOB_HOSTILE),

    // ========== 敵対モブ（エンド） ==========
    ENDERMAN(EntityType.ENDERMAN, 25uL, ExperienceCategory.MOB_HOSTILE),
    SHULKER(EntityType.SHULKER, 30uL, ExperienceCategory.MOB_HOSTILE),
    ENDERMITE(EntityType.ENDERMITE, 5uL, ExperienceCategory.MOB_HOSTILE),

    // ========== 敵対モブ（オーバーワールド） ==========
    CREEPER(EntityType.CREEPER, 15uL, ExperienceCategory.MOB_HOSTILE),
    SKELETON(EntityType.SKELETON, 12uL, ExperienceCategory.MOB_HOSTILE),
    STRAY(EntityType.STRAY, 14uL, ExperienceCategory.MOB_HOSTILE),
    BOGGED(EntityType.BOGGED, 14uL, ExperienceCategory.MOB_HOSTILE),
    ZOMBIE(EntityType.ZOMBIE, 10uL, ExperienceCategory.MOB_HOSTILE),
    HUSK(EntityType.HUSK, 12uL, ExperienceCategory.MOB_HOSTILE),
    DROWNED(EntityType.DROWNED, 12uL, ExperienceCategory.MOB_HOSTILE),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, 12uL, ExperienceCategory.MOB_HOSTILE),
    SPIDER(EntityType.SPIDER, 10uL, ExperienceCategory.MOB_HOSTILE),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, 12uL, ExperienceCategory.MOB_HOSTILE),
    SLIME(EntityType.SLIME, 5uL, ExperienceCategory.MOB_HOSTILE),
    PHANTOM(EntityType.PHANTOM, 20uL, ExperienceCategory.MOB_HOSTILE),
    SILVERFISH(EntityType.SILVERFISH, 3uL, ExperienceCategory.MOB_HOSTILE),
    WITCH(EntityType.WITCH, 25uL, ExperienceCategory.MOB_HOSTILE),
    GUARDIAN(EntityType.GUARDIAN, 100uL, ExperienceCategory.MOB_HOSTILE),
    BREEZE(EntityType.BREEZE, 20uL, ExperienceCategory.MOB_HOSTILE),

    // ========== 敵対モブ（襲撃） ==========
    PILLAGER(EntityType.PILLAGER, 15uL, ExperienceCategory.MOB_HOSTILE),
    VINDICATOR(EntityType.VINDICATOR, 150uL, ExperienceCategory.MOB_HOSTILE),
    VEX(EntityType.VEX, 10uL, ExperienceCategory.MOB_HOSTILE),

    // ========== 中立モブ ==========
    IRON_GOLEM(EntityType.IRON_GOLEM, 50uL, ExperienceCategory.MOB_NATURAL),
    BEE(EntityType.BEE, 5uL, ExperienceCategory.MOB_NATURAL),
    LLAMA(EntityType.LLAMA, 5uL, ExperienceCategory.MOB_NATURAL),
    TRADER_LLAMA(EntityType.TRADER_LLAMA, 5uL, ExperienceCategory.MOB_NATURAL),
    PANDA(EntityType.PANDA, 5uL, ExperienceCategory.MOB_NATURAL),
    POLAR_BEAR(EntityType.POLAR_BEAR, 15uL, ExperienceCategory.MOB_NATURAL),
    WOLF(EntityType.WOLF, 8uL, ExperienceCategory.MOB_NATURAL),
    DOLPHIN(EntityType.DOLPHIN, 5uL, ExperienceCategory.MOB_NATURAL),
    GOAT(EntityType.GOAT, 5uL, ExperienceCategory.MOB_NATURAL),
    CAMELHUSK(EntityType.CAMEL_HUSK, 10uL, ExperienceCategory.MOB_NATURAL),
    NAUTILUS(EntityType.NAUTILUS, 5uL, ExperienceCategory.MOB_NATURAL),
    ZOMBIE_NAUTILUS(EntityType.ZOMBIE_NAUTILUS, 10uL, ExperienceCategory.MOB_NATURAL),

    // ========== 受動モブ（家畜） ==========
    PIG(EntityType.PIG, 3uL, ExperienceCategory.MOB_PASSIVE),
    COW(EntityType.COW, 3uL, ExperienceCategory.MOB_PASSIVE),
    MOOSHROOM(EntityType.MOOSHROOM, 5uL, ExperienceCategory.MOB_PASSIVE),
    SHEEP(EntityType.SHEEP, 3uL, ExperienceCategory.MOB_PASSIVE),
    CHICKEN(EntityType.CHICKEN, 2uL, ExperienceCategory.MOB_PASSIVE),
    RABBIT(EntityType.RABBIT, 2uL, ExperienceCategory.MOB_PASSIVE),
    HORSE(EntityType.HORSE, 5uL, ExperienceCategory.MOB_PASSIVE),
    DONKEY(EntityType.DONKEY, 5uL, ExperienceCategory.MOB_PASSIVE),
    MULE(EntityType.MULE, 5uL, ExperienceCategory.MOB_PASSIVE),
    CAMEL(EntityType.CAMEL, 8uL, ExperienceCategory.MOB_PASSIVE),
    SNIFFER(EntityType.SNIFFER, 10uL, ExperienceCategory.MOB_PASSIVE),
    ARMADILLO(EntityType.ARMADILLO, 5uL, ExperienceCategory.MOB_PASSIVE),

    // ========== 受動モブ（水生） ==========
    SQUID(EntityType.SQUID, 3uL, ExperienceCategory.MOB_PASSIVE),
    GLOW_SQUID(EntityType.GLOW_SQUID, 5uL, ExperienceCategory.MOB_PASSIVE),
    COD(EntityType.COD, 1uL, ExperienceCategory.MOB_PASSIVE),
    SALMON(EntityType.SALMON, 1uL, ExperienceCategory.MOB_PASSIVE),
    TROPICAL_FISH(EntityType.TROPICAL_FISH, 1uL, ExperienceCategory.MOB_PASSIVE),
    PUFFERFISH(EntityType.PUFFERFISH, 2uL, ExperienceCategory.MOB_PASSIVE),
    TURTLE(EntityType.TURTLE, 5uL, ExperienceCategory.MOB_PASSIVE),
    AXOLOTL(EntityType.AXOLOTL, 5uL, ExperienceCategory.MOB_PASSIVE),
    FROG(EntityType.FROG, 3uL, ExperienceCategory.MOB_PASSIVE),
    TADPOLE(EntityType.TADPOLE, 1uL, ExperienceCategory.MOB_PASSIVE),

    // ========== 受動モブ（その他） ==========
    BAT(EntityType.BAT, 1uL, ExperienceCategory.MOB_PASSIVE),
    CAT(EntityType.CAT, 3uL, ExperienceCategory.MOB_PASSIVE),
    OCELOT(EntityType.OCELOT, 5uL, ExperienceCategory.MOB_PASSIVE),
    PARROT(EntityType.PARROT, 3uL, ExperienceCategory.MOB_PASSIVE),
    FOX(EntityType.FOX, 5uL, ExperienceCategory.MOB_PASSIVE),
    ALLAY(EntityType.ALLAY, 10uL, ExperienceCategory.MOB_PASSIVE),
    HAPPYGHAST(EntityType.HAPPY_GHAST, 5uL, ExperienceCategory.MOB_PASSIVE),
    ;

    companion object {
        private val byEntityType: Map<EntityType, MobExperience> =
            entries.associateBy { it.entityType }

        /**
         * EntityType から経験値を取得する
         *
         * @param entityType エンティティタイプ
         * @return 経験値．未定義の場合は 0
         */
        fun getExperience(entityType: EntityType): ULong = byEntityType[entityType]?.experience ?: 0uL

        /**
         * EntityType から MobExperience を取得する
         *
         * @param entityType エンティティタイプ
         * @return MobExperience．未定義の場合は null
         */
        fun fromEntityType(entityType: EntityType): MobExperience? = byEntityType[entityType]

        /**
         * カテゴリでフィルタリングする
         *
         * @param category フィルタするカテゴリ
         * @return 該当する MobExperience のリスト
         */
        fun byCategory(category: ExperienceCategory): List<MobExperience> = entries.filter { it.category == category }

        /**
         * 経験値が定義されているかどうか
         *
         * @param entityType エンティティタイプ
         * @return 定義されている場合は true
         */
        fun isDefined(entityType: EntityType): Boolean = byEntityType.containsKey(entityType)
    }
}
