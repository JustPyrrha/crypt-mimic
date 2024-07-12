package gay.pyrrha.mimic.mixin.client;

import gay.pyrrha.mimic.client.entity.ClientNPCEntity;
import gay.pyrrha.mimic.entity.ServerNPCEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public abstract class PlayerEntityModelMixin<T extends LivingEntity> extends BipedEntityModel<T> {

    @Shadow @Final public ModelPart jacket;
    @Shadow @Final public ModelPart leftSleeve;
    @Shadow @Final public ModelPart rightSleeve;
    @Shadow @Final public ModelPart leftPants;
    @Shadow @Final public ModelPart rightPants;

    public PlayerEntityModelMixin(ModelPart root) {
        super(root);
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At("HEAD"), cancellable = true)
    private void mimic$setAngles(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof ClientNPCEntity npcEntity) {
            var offset = 0.017453292F;
            this.head.pitch = offset * npcEntity.getHeadRotation().getPitch();
            this.head.yaw = offset * npcEntity.getHeadRotation().getYaw();
            this.head.roll = offset * npcEntity.getHeadRotation().getRoll();
            this.body.pitch = offset * npcEntity.getBodyRotation().getPitch();
            this.body.yaw = offset * npcEntity.getBodyRotation().getYaw();
            this.body.roll = offset * npcEntity.getBodyRotation().getRoll();
            this.leftArm.pitch = offset * npcEntity.getLeftArmRotation().getPitch();
            this.leftArm.yaw = offset * npcEntity.getLeftArmRotation().getYaw();
            this.leftArm.roll = offset * npcEntity.getLeftArmRotation().getRoll();
            this.rightArm.pitch = offset * npcEntity.getRightArmRotation().getPitch();
            this.rightArm.yaw = offset * npcEntity.getRightArmRotation().getYaw();
            this.rightArm.roll = offset * npcEntity.getRightArmRotation().getRoll();
            this.leftLeg.pitch = offset * npcEntity.getLeftLegRotation().getPitch();
            this.leftLeg.yaw = offset * npcEntity.getLeftLegRotation().getYaw();
            this.leftLeg.roll = offset * npcEntity.getLeftLegRotation().getRoll();
            this.rightLeg.pitch = offset * npcEntity.getRightLegRotation().getPitch();
            this.rightLeg.yaw = offset * npcEntity.getRightLegRotation().getYaw();
            this.rightLeg.roll = offset * npcEntity.getRightLegRotation().getRoll();

            this.hat.copyTransform(this.head);
            this.jacket.copyTransform(this.body);
            this.leftSleeve.copyTransform(this.leftArm);
            this.rightSleeve.copyTransform(this.rightArm);
            this.leftPants.copyTransform(this.leftLeg);
            this.rightPants.copyTransform(this.rightLeg);
            ci.cancel();
        }
    }
}
