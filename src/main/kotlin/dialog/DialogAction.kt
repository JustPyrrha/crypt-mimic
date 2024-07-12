package gay.pyrrha.mimic.dialog

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import gay.pyrrha.mimic.entity.NPCEntity
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.Identifier
import java.util.*
import java.util.function.BiFunction
import kotlin.jvm.optionals.getOrNull

@JvmRecord
@Serializable
public data class DialogAction(
    @Contextual
    public val action: Identifier,
    public val value: String?
) {
    public companion object {
        @JvmStatic
        public val CODEC: Codec<DialogAction> = RecordCodecBuilder.create { instance ->
            return@create instance.group(
                Identifier.CODEC.stable().fieldOf("action").forGetter { it.action },
                PrimitiveCodec.STRING.stable().optionalFieldOf("value").forGetter { Optional.ofNullable(it.value) }
            ).apply(instance, instance.stable(BiFunction(::of)))
        }

        @JvmStatic
        public val EVENT: Event<DialogActionCallback> =
            EventFactory.createArrayBacked(DialogActionCallback::class.java) { callbacks ->
                DialogActionCallback { player, entity, action -> callbacks.forEach { it.onAction(player, entity, action) } }
            }

        @JvmStatic
        public fun of(action: Identifier, value: Optional<String>): DialogAction =
            DialogAction(action, value.getOrNull())
    }
}

public fun interface DialogActionCallback {
    public fun onAction(player: PlayerEntity, entity: NPCEntity, action: DialogAction)
}
