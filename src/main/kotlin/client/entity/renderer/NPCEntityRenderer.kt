package gay.pyrrha.mimic.client.entity.renderer

import com.unascribed.ears.EarsFeatureRenderer
import gay.pyrrha.mimic.client.entity.ClientNPCEntity
import gay.pyrrha.mimic.registry.MimicRegistries
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.PlayerEntityRenderer
import net.minecraft.client.render.entity.model.BipedEntityModel
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.EntityPose
import net.minecraft.text.Text
import net.minecraft.util.Identifier

public class NPCEntityRenderer(ctx: EntityRendererFactory.Context, slimArms: Boolean = false) :
    PlayerEntityRenderer(
        ctx,
        slimArms
    ) {

    init {
        if (FabricLoader.getInstance().isModLoaded("ears"))
            addFeature(EarsFeatureRenderer(this))
    }

    public companion object NpcGetter {
        @JvmStatic
        public fun asNpc(entity: AbstractClientPlayerEntity) : ClientNPCEntity {
            return entity as ClientNPCEntity
        }
    }


    override fun render(
        livingEntity: AbstractClientPlayerEntity,
        f: Float,
        g: Float,
        matrixStack: MatrixStack?,
        vertexConsumerProvider: VertexConsumerProvider?,
        i: Int
    ) {
        setModelPose(getModel())
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i)
    }

    override fun getTexture(entity: AbstractClientPlayerEntity): Identifier =
        asNpc(entity).registryManager[MimicRegistries.NPC].get(asNpc(entity).getNpcId())?.skin?.texture
            ?: Identifier.ofVanilla("textures/entity/player/wide/steve.png")

    public override fun scale(entity: AbstractClientPlayerEntity, matrices: MatrixStack, amount: Float) {
        val scale = 0.9375f
        matrices.scale(scale, scale, scale)
    }

    private fun setModelPose(model: PlayerEntityModel<AbstractClientPlayerEntity>) {
        model.setVisible(true)
        model.sneaking = false
        model.leftArmPose = BipedEntityModel.ArmPose.EMPTY
        model.rightArmPose = BipedEntityModel.ArmPose.EMPTY
    }

    override fun renderLabelIfPresent(
        entity: AbstractClientPlayerEntity,
        text: Text,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        tickDelta: Float
    ) {
        val npc = asNpc(entity)
        val distance = dispatcher.getSquaredDistanceToCamera(entity)
        val titleText = npc.getTitle()
        matrices.push()
        if (npc.isSmall) {
            matrices.translate(0f, -(entity.getBaseDimensions(EntityPose.STANDING).height - 0.1f), 0f)
        }
        if (distance < 100 && titleText != null) {
            super.renderLabelIfPresent(entity, text, matrices, vertexConsumers, light, tickDelta)
            matrices.translate(0.0f, 0.25875f, 0.0f)
            super.renderLabelIfPresent(entity, titleText, matrices, vertexConsumers, light, tickDelta)
        } else {
            super.renderLabelIfPresent(entity, text, matrices, vertexConsumers, light, tickDelta)
        }

        matrices.pop()
    }
}
