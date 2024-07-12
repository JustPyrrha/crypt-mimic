package gay.pyrrha.mimic.net.payload.api

import gay.pyrrha.mimic.ident
import kotlinx.serialization.Serializable
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload

@Serializable
public sealed class EmptySerializedPayload : SerializedPayload<EmptySerializedPayload>() {
    override fun codec(): PacketCodec<RegistryByteBuf, EmptySerializedPayload> = CODEC
    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    public companion object Configuration : SerializedPayloadConfiguration<EmptySerializedPayload> {
        override val ID: CustomPayload.Id<EmptySerializedPayload> = CustomPayload.Id(ident("empty"))
        override val CODEC: PacketCodec<RegistryByteBuf, EmptySerializedPayload> = codec()
        override val CHANNEL: PayloadChannel = PayloadChannel.ClientboundPlay
    }
}
