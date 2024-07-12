package gay.pyrrha.mimic.mixin.integration.ears;

import gay.pyrrha.mimic.client.integration.ears.EarsResourceTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.ResourceTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ResourceTexture.class)
public class ResourceTextureMixin {
    @ModifyVariable(method = "load", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;isOnRenderThreadOrInit()Z"))
    private NativeImage mimic$processEarsTexture(NativeImage original) {
        if ((ResourceTexture)(Object)this instanceof EarsResourceTexture earsTexture)
            earsTexture.processSkin(original);
        return original;
    }
}
