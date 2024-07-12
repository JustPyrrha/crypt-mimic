package gay.pyrrha.mimic.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import gay.pyrrha.mimic.client.entity.ClientNPCEntity;
import gay.pyrrha.mimic.registry.MimicRegistries;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    @Unique
    private SkinTextures mimic$skinTextures;

    @ModifyReturnValue(method = "getSkinTextures", at = @At("RETURN"))
    private SkinTextures mimic$getSkinTextures(SkinTextures original) {
        if ((AbstractClientPlayerEntity)(Object) this instanceof ClientNPCEntity npc) {
            if (mimic$skinTextures == null) {
                var npcSkin = npc.getRegistryManager().get(MimicRegistries.getNPC()).get(npc.getNpcId()).skin();
                mimic$skinTextures = new SkinTextures(npcSkin.texture(), null, null, null, npcSkin.hasSlimArms() ? SkinTextures.Model.SLIM : SkinTextures.Model.WIDE, true);
            }
            return mimic$skinTextures;
        }
        return original;
    }
}
