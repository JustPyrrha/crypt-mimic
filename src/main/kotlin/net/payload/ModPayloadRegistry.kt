package gay.pyrrha.mimic.net.payload

import gay.pyrrha.mimic.net.payload.api.SerializedPayloadConfiguration
import gay.pyrrha.mimic.net.payload.s2c.OpenDialogScreenPayload
import gay.pyrrha.mimic.net.payload.s2c.SpawnNPCEntityPayload

public object ModPayloadRegistry {
    public fun register(register: (List<SerializedPayloadConfiguration<*>>) -> Unit) {
        register(listOf(
            // Clientbound
            OpenDialogScreenPayload.Configuration,

            // Serverbound
            OpenDialogScreenPayload.Configuration,
            SpawnNPCEntityPayload.Configuration
        ))
    }
}
