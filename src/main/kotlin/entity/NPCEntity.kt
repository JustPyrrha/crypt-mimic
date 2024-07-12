package gay.pyrrha.mimic.entity

import gay.pyrrha.mimic.registry.MimicRegistries
import net.minecraft.entity.EntityDimensions
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.EulerAngle

public interface NPCEntity {
    public fun asPlayer() : PlayerEntity {
        return this as PlayerEntity
    }

    public fun getTitle(): Text? {
        return asPlayer().registryManager[MimicRegistries.NPC].get(getNpcId())?.title
    }

    public fun getNpcId() : Identifier
    public fun setNpcId(npcId: Identifier)


    public companion object {
        internal val DEFAULT_HEAD_ROT: EulerAngle = EulerAngle(0f, 0f, 0f)
        internal val DEFAULT_BODY_ROT: EulerAngle = EulerAngle(0f, 0f, 0f)
        internal val DEFAULT_LEFT_ARM_ROT: EulerAngle = EulerAngle(-10f, 0f, -10f)
        internal val DEFAULT_RIGHT_ARM_ROT: EulerAngle = EulerAngle(-15f, 0f, 10f)
        internal val DEFAULT_LEFT_LEG_ROT: EulerAngle = EulerAngle(-1f, 0f, -1f)
        internal val DEFAULT_RIGHT_LEG_ROT: EulerAngle = EulerAngle(1f, 0f, 1f)
        internal val SMALL_DIMENSIONS: EntityDimensions =
            ModEntityTypes.NPC.dimensions.scaled(0.5f).withEyeHeight(0.9875f)
    }
}