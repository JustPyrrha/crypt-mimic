package gay.pyrrha.mimic.npc

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gay.pyrrha.mimic.entity.NPCEntity
import gay.pyrrha.mimic.entity.ServerNPCEntity
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import java.util.Optional
import java.util.function.BiFunction
import kotlin.jvm.optionals.getOrNull

@JvmRecord
public data class NpcAction(
    val action: Identifier,
    val value: String?
) {
    public companion object {
        @JvmStatic
        public val CODEC: Codec<NpcAction> = RecordCodecBuilder.create { instance ->
            return@create instance.group(
                Identifier.CODEC.stable().fieldOf("action").forGetter { it.action },
                PrimitiveCodec.STRING.stable().optionalFieldOf("value").forGetter { Optional.ofNullable(it.value) }
            ).apply(instance, instance.stable(BiFunction(::of)))
        }

        @JvmStatic
        public val EVENT: Event<NpcActionCallback> = EventFactory.createArrayBacked(NpcActionCallback::class.java) { callbacks ->
            NpcActionCallback { player, entity, action -> callbacks.forEach { it.onAction(player, entity, action) } }
        }

        @JvmStatic
        public fun of(action: Identifier, value: Optional<String>): NpcAction = NpcAction(action, value.getOrNull())
    }
}

public fun interface NpcActionCallback {
    public fun onAction(player: PlayerEntity, entity: NPCEntity, action: NpcAction)
}
