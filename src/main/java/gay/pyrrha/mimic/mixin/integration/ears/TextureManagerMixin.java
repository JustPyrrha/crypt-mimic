package gay.pyrrha.mimic.mixin.integration.ears;

import gay.pyrrha.mimic.client.integration.ears.EarsIntegrationUtil;
import gay.pyrrha.mimic.client.integration.ears.EarsResourceTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TextureManager.class)
public class TextureManagerMixin {
    @ModifyVariable(method = "registerTexture", at = @At("HEAD"), argsOnly = true)
    private AbstractTexture mimic$bindEarsTexture(AbstractTexture value, Identifier id) {
        if (EarsIntegrationUtil.getALLOWED_PATHS().stream().anyMatch(s -> id.getPath().startsWith(s)) && value instanceof ResourceTexture)
            return new EarsResourceTexture(id);
        return value;
    }
}
