package gay.pyrrha.mimic.net.payload.s2c

import gay.pyrrha.mimic.ident
import gay.pyrrha.mimic.net.payload.api.SerializedPayload
import gay.pyrrha.mimic.net.payload.api.SerializedPayloadCompanion
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import java.util.UUID

@Serializable
public data class SpawnNPCEntityPayload(
    val id: Int,
    @Contextual
    val uuid: UUID,
    val x: Double,
    val y: Double,
    val z: Double,
    val velocityX: Double,
    val velocityY: Double,
    val velocityZ: Double,
    val pitch: Float,
    val yaw: Float,
    val headYaw: Float
) : SerializedPayload<SpawnNPCEntityPayload>() {
    override fun codec(): PacketCodec<RegistryByteBuf, SpawnNPCEntityPayload> = CODEC
    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    public companion object : SerializedPayloadCompanion<SpawnNPCEntityPayload> {
        override val ID: CustomPayload.Id<SpawnNPCEntityPayload> = CustomPayload.Id(ident("s2c_spawn_npc_entity"))
        override val CODEC: PacketCodec<RegistryByteBuf, SpawnNPCEntityPayload> = codec()
    }
}