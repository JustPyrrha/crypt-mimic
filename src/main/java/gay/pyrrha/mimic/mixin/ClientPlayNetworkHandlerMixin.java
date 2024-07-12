package gay.pyrrha.mimic.mixin;

import gay.pyrrha.mimic.client.entity.ClientNPCEntity;
import gay.pyrrha.mimic.entity.ModEntityTypes;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow private ClientWorld world;

    @Inject(method = "createEntity", at = @At("HEAD"), cancellable = true)
    private void mimic$createEntity(EntitySpawnS2CPacket packet, CallbackInfoReturnable<Entity> cir) {
        if (packet.getEntityType() == ModEntityTypes.INSTANCE.getNPC()) {
            cir.setReturnValue(new ClientNPCEntity(world));
        }
    }
}
