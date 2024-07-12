package gay.pyrrha.mimic.net.payload

import gay.pyrrha.mimic.entity.ServerNPCEntity
import gay.pyrrha.mimic.net.payload.api.SerializedPayload
import gay.pyrrha.mimic.net.payload.api.SerializedPayloadCompanion
import gay.pyrrha.mimic.net.payload.c2s.DialogActionPayload
import gay.pyrrha.mimic.net.payload.s2c.OpenDialogScreenPayload
import gay.pyrrha.mimic.net.payload.s2c.SpawnNPCEntityPayload
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.network.packet.CustomPayload

public object ModPayloadRegistry {
    public fun register() {
        // C2S Play Payloads
        listOf(
            payload(DialogActionPayload.Companion)
        ).forEach { PayloadTypeRegistry.playC2S().register(it.id, it.codec) }

        // S2C Play Payloads
        listOf(
            payload(OpenDialogScreenPayload.Companion)
        ).forEach { PayloadTypeRegistry.playS2C().register(it.id, it.codec) }
    }

    private fun <T : SerializedPayload<T>> payload(
        companion: SerializedPayloadCompanion<T>,
    ) = CustomPayload.Type(companion.ID, companion.CODEC)
}
