package gay.pyrrha.mimic.client

import gay.pyrrha.mimic.CLIENT_TAG
import gay.pyrrha.mimic.client.entity.ClientNPCEntity
import gay.pyrrha.mimic.client.screen.DialogScreen
import gay.pyrrha.mimic.entity.ServerNPCEntity
import gay.pyrrha.mimic.net.payload.s2c.OpenDialogScreenPayload
import gay.pyrrha.mimic.net.payload.s2c.SpawnNPCEntityPayload
import gay.pyrrha.mimic.registry.MimicRegistries
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import kotlin.system.measureTimeMillis

public val LOGGER: KLogger = KotlinLogging.logger {  }

public object MimicClient : ClientModInitializer {
    override fun onInitializeClient() {
        LOGGER.info { "$CLIENT_TAG Initializing..." }
        val startTimeMs = measureTimeMillis {
            ClientPlayConnectionEvents.INIT.register { _, client ->
                ClientPlayNetworking.registerReceiver(OpenDialogScreenPayload.ID) { payload, context ->
                    context.client().setScreen(
                        DialogScreen(
                            client.world!!.registryManager[MimicRegistries.DIALOG][payload.dialog]!!,
                            client.world!!.registryManager[MimicRegistries.NPC][payload.npc]!!,
                            context.client().world!!.getEntityById(payload.entityId) as ClientNPCEntity
                        )
                    )
                }
            }
        }
        LOGGER.info { "$CLIENT_TAG Initialized. (Took ${startTimeMs}ms)" }
    }
}
