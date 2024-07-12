package gay.pyrrha.mimic.net.payload.s2c

import gay.pyrrha.mimic.ident
import gay.pyrrha.mimic.net.payload.api.PayloadChannel
import gay.pyrrha.mimic.net.payload.api.SerializedPayload
import gay.pyrrha.mimic.net.payload.api.SerializedPayloadConfiguration
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

@Serializable
public data class OpenDialogScreenPayload(
    @Contextual
    public val dialog: Identifier,
    @Contextual
    public val npc: Identifier,
    public val entityId: Int
) : SerializedPayload<OpenDialogScreenPayload>() {
    override fun codec(): PacketCodec<RegistryByteBuf, OpenDialogScreenPayload> = CODEC
    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    public companion object Configuration : SerializedPayloadConfiguration<OpenDialogScreenPayload> {
        override val ID: CustomPayload.Id<OpenDialogScreenPayload> = CustomPayload.Id(ident("s2c_open_dialog"))
        override val CODEC: PacketCodec<RegistryByteBuf, OpenDialogScreenPayload> = codec()
        override val CHANNEL: PayloadChannel = PayloadChannel.ClientboundPlay
    }
}
