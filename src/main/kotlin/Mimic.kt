package gay.pyrrha.mimic

import gay.pyrrha.mimic.dialog.DialogAction
import gay.pyrrha.mimic.entity.ModEntityTypes
import gay.pyrrha.mimic.entity.ServerNPCEntity
import gay.pyrrha.mimic.net.payload.ModPayloadRegistry
import gay.pyrrha.mimic.net.payload.c2s.DialogActionPayload
import gay.pyrrha.mimic.net.payload.s2c.OpenDialogScreenPayload
import gay.pyrrha.mimic.npc.NpcAction
import gay.pyrrha.mimic.registry.MimicRegistries
import gay.pyrrha.mimic.server.command.MimicNPCCommand
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import kotlin.system.measureTimeMillis

public val LOGGER: KLogger = KotlinLogging.logger { }

public object Mimic : ModInitializer {
    override fun onInitialize() {
        LOGGER.info { "$TAG Initializing..." }
        val startTimeMs = measureTimeMillis {
            ModPayloadRegistry.register()
            ModEntityTypes.register()
            MimicRegistries.register()

            CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, _ ->
                MimicNPCCommand.register(dispatcher, registryAccess)
            }

            NpcAction.EVENT.register { player, entity, action ->
                if (!player.world.isClient && player is ServerPlayerEntity) {
                    when (action.action) {
                        ident("show_dialog") -> ServerPlayNetworking.send(
                            player,
                            OpenDialogScreenPayload(Identifier.of(action.value!!), entity.getNpcId(), entity.asPlayer().id)
                        )
                    }
                }
            }

            DialogAction.EVENT.register { player, entity, action ->
                if (!player.world.isClient && player is ServerPlayerEntity) {
                    when (action.action) {
                        ident("show_dialog") -> ServerPlayNetworking.send(
                            player,
                            OpenDialogScreenPayload(Identifier.of(action.value!!), entity.getNpcId(), entity.asPlayer().id)
                        )
                    }
                }
            }

            ServerLifecycleEvents.SERVER_STARTED.register { _ ->
                ServerPlayNetworking.registerGlobalReceiver(DialogActionPayload.ID) { payload, context ->
                    DialogAction.EVENT.invoker().onAction(
                        context.player(),
                        context.player().world!!.getEntityById(payload.npcEntityId) as ServerNPCEntity,
                        DialogAction(
                            payload.action,
                            payload.value
                        )
                    )
                }
            }
        }
        LOGGER.info { "$TAG Initialized. (Took ${startTimeMs}ms)" }
    }
}
