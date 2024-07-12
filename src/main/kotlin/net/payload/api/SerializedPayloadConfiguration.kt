package gay.pyrrha.mimic.net.payload.api

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload

@Suppress("PropertyName")
public interface SerializedPayloadConfiguration<T : SerializedPayload<T>> {
    public val ID: CustomPayload.Id<T>
    public val CODEC: PacketCodec<RegistryByteBuf, T>
    public val CHANNEL: PayloadChannel
}
