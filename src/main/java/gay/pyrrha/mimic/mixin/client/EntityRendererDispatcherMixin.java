package gay.pyrrha.mimic.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import gay.pyrrha.mimic.client.entity.ClientNPCEntity;
import gay.pyrrha.mimic.client.entity.renderer.NPCEntityRenderer;
import gay.pyrrha.mimic.registry.MimicRegistries;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.Entity;
import net.minecraft.resource.ResourceManager;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public class EntityRendererDispatcherMixin {
    @Unique
    private Map<Boolean, EntityRenderer<AbstractClientPlayerEntity>> cryptMimic$npcRenderers = Map.of();

    @Inject(method = "reload", at = @At("TAIL"))
    private void crypt$reload(ResourceManager manager, CallbackInfo ci, @Local EntityRendererFactory.Context context) {
        cryptMimic$npcRenderers = Map.of(
                true, new NPCEntityRenderer(context, true),
                false, new NPCEntityRenderer(context, false)
        );
    }

    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void cryptMimic$getRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T>> cir) {
        if (entity instanceof ClientNPCEntity npc && !cryptMimic$npcRenderers.isEmpty()) {
            //noinspection unchecked
            cir.setReturnValue((EntityRenderer<? super T>) cryptMimic$npcRenderers.get(npc.getRegistryManager().get(MimicRegistries.getNPC()).get(npc.getNpcId()).skin().hasSlimArms()));
        }
    }
}
