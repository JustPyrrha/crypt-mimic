package gay.pyrrha.mimic.mixin;

import gay.pyrrha.mimic.entity.ServerNPCEntity;
import net.minecraft.command.EntityDataObject;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityDataObject.class)
public class EntityDataObjectMixin {
    @Shadow @Final private Entity entity;

    @Inject(method = "setNbt", at = @At("HEAD"), cancellable = true)
    private void mimic$setNbt(NbtCompound nbt, CallbackInfo ci) {
        if (entity instanceof ServerNPCEntity npc) {
            npc.saveNbt(nbt);
            npc.readNbt(nbt);
            ci.cancel();
        }
    }
}
