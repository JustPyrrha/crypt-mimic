package gay.pyrrha.mimic.net.payload.c2s

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
public data class DialogActionPayload(
    @Contextual
    public val action: Identifier,
    public val npcEntityId: Int,
    public val value: String?
) : SerializedPayload<DialogActionPayload>() {
    override fun codec(): PacketCodec<RegistryByteBuf, DialogActionPayload> = CODEC
    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    public companion object Configuration : SerializedPayloadConfiguration<DialogActionPayload> {
        override val ID: CustomPayload.Id<DialogActionPayload> = CustomPayload.Id(ident("c2s_dialog_action"))
        override val CODEC: PacketCodec<RegistryByteBuf, DialogActionPayload> = codec()
        override val CHANNEL: PayloadChannel = PayloadChannel.ServerboundPlay
    }
}
