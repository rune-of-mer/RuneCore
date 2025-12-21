package org.lyralis.runeCore.item.model

import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup

/**
 * カスタムアイテムのステータス定義
 *
 * @param attackDamage 攻撃力ボーナス（武器用）
 * @param attackSpeed 攻撃速度ボーナス（武器用）
 * @param armor 防御力ボーナス（防具用）
 * @param armorToughness 防具強度ボーナス（防具用）
 * @param maxHealth 最大体力ボーナス
 * @param movementSpeed 移動速度ボーナス
 * @param knockbackResistance ノックバック耐性ボーナス
 * @param durability カスタム耐久値（null の場合はデフォルト）
 * @param unbreakable 耐久無限かどうか
 */
data class CustomItemStatus(
    val attackDamage: Double? = null,
    val attackSpeed: Double? = null,
    val armor: Double? = null,
    val armorToughness: Double? = null,
    val maxHealth: Double? = null,
    val movementSpeed: Double? = null,
    val knockbackResistance: Double? = null,
    val durability: Int? = null,
    val unbreakable: Boolean = false,
) {
    /**
     * AttributeModifier のリストを生成する
     *
     * @param slotGroup 装備スロット
     * @return 属性モディファイアのマップ
     */
    fun toAttributeModifiers(slotGroup: EquipmentSlotGroup): Map<Attribute, AttributeModifier> {
        val modifiers = mutableMapOf<Attribute, AttributeModifier>()

        attackDamage?.let {
            modifiers[Attribute.ATTACK_DAMAGE] =
                AttributeModifier(
                    NamespacedKey.minecraft("custom_attack_damage"),
                    it,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slotGroup,
                )
        }

        attackSpeed?.let {
            modifiers[Attribute.ATTACK_SPEED] =
                AttributeModifier(
                    NamespacedKey.minecraft("custom_attack_speed"),
                    it,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slotGroup,
                )
        }

        armor?.let {
            modifiers[Attribute.ARMOR] =
                AttributeModifier(
                    NamespacedKey.minecraft("custom_armor"),
                    it,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slotGroup,
                )
        }

        armorToughness?.let {
            modifiers[Attribute.ARMOR_TOUGHNESS] =
                AttributeModifier(
                    NamespacedKey.minecraft("custom_armor_toughness"),
                    it,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slotGroup,
                )
        }

        maxHealth?.let {
            modifiers[Attribute.MAX_HEALTH] =
                AttributeModifier(
                    NamespacedKey.minecraft("custom_max_health"),
                    it,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slotGroup,
                )
        }

        movementSpeed?.let {
            modifiers[Attribute.MOVEMENT_SPEED] =
                AttributeModifier(
                    NamespacedKey.minecraft("custom_movement_speed"),
                    it,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slotGroup,
                )
        }

        knockbackResistance?.let {
            modifiers[Attribute.KNOCKBACK_RESISTANCE] =
                AttributeModifier(
                    NamespacedKey.minecraft("custom_knockback_resistance"),
                    it,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slotGroup,
                )
        }

        return modifiers
    }
}
