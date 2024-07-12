package gay.pyrrha.mimic.server.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import gay.pyrrha.mimic.entity.ModEntityTypes
import gay.pyrrha.mimic.entity.ServerNPCEntity
import gay.pyrrha.mimic.npc.Npc
import gay.pyrrha.mimic.registry.MimicRegistries
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.NbtCompoundArgumentType
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType
import net.minecraft.command.argument.Vec3ArgumentType
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

public object MimicNPCCommand {
    public fun register(dispatcher: CommandDispatcher<ServerCommandSource>, registryAccess: CommandRegistryAccess) {
        dispatcher.register(
            CommandManager.literal("crypt-mimic")
                .requires { it.hasPermissionLevel(2) }
                .then(
                    CommandManager.argument(
                        "npc",
                        RegistryEntryReferenceArgumentType.registryEntry(registryAccess, MimicRegistries.NPC)
                    )
                        .suggests(MimicSuggestionProviders.NPCs)
                        .executes { ctx ->
                            execute(
                                ctx.source,
                                RegistryEntryReferenceArgumentType.getRegistryEntry(ctx, "npc", MimicRegistries.NPC),
                                ctx.source.position
                            )
                        }.then(
                            CommandManager.argument("pos", Vec3ArgumentType.vec3())
                                .executes { ctx ->
                                    execute(
                                        ctx.source,
                                        RegistryEntryReferenceArgumentType.getRegistryEntry(
                                            ctx,
                                            "npc",
                                            MimicRegistries.NPC
                                        ),
                                        Vec3ArgumentType.getVec3(ctx, "pos")
                                    )
                                }
                                .then(
                                    CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                        .executes { ctx ->
                                            execute(
                                                ctx.source,
                                                RegistryEntryReferenceArgumentType.getRegistryEntry(
                                                    ctx,
                                                    "npc",
                                                    MimicRegistries.NPC
                                                ),
                                                Vec3ArgumentType.getVec3(ctx, "pos"),
                                                NbtCompoundArgumentType.getNbtCompound(ctx, "nbt")
                                            )
                                        }
                                )
                        )
                )
        )
    }

    private val INVALID_POSITION_EXCEPTION =
        SimpleCommandExceptionType(Text.translatable("commands.crypt-mimic.npc.invalidPosition"))
    private val FAILED_UUID_EXCEPTION = SimpleCommandExceptionType(Text.translatable("commands.crypt-mimic.npc.failed.uuid"))

    public fun summonNPC(
        source: ServerCommandSource,
        npc: RegistryEntry.Reference<Npc>,
        pos: Vec3d,
        nbt: NbtCompound
    ): ServerNPCEntity {
        val blockPos = BlockPos.ofFloored(pos)
        if (!World.isValid(blockPos)) {
            throw INVALID_POSITION_EXCEPTION.create()
        } else {
            val nbtCompound = nbt.copy()
            nbtCompound.putString("NpcId", npc.idAsString)
            nbtCompound.putBoolean("Invulnerable", true)
            val entity = ServerNPCEntity(ModEntityTypes.NPC, source.world)
            entity.setNpcId(npc.registryKey().value)
            entity.updatePositionAndAngles(pos.x, pos.y, pos.z, entity.yaw, entity.pitch)

            if (!source.world.spawnNewEntityAndPassengers(entity)) {
                throw FAILED_UUID_EXCEPTION.create()
            } else {
                return entity
            }
        }
    }

    private fun execute(
        source: ServerCommandSource,
        npc: RegistryEntry.Reference<Npc>,
        pos: Vec3d,
        nbt: NbtCompound = NbtCompound()
    ): Int {
        val entity = summonNPC(source, npc, pos, nbt)
        source.sendFeedback({ Text.translatable("commands.crypt-mimic.npc.success", entity.displayName) }, true)
        return Command.SINGLE_SUCCESS
    }
}