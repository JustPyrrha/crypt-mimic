package gay.pyrrha.mimic.entity

import gay.pyrrha.mimic.ident
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricDefaultAttributeRegistry
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.mob.MobEntity
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

public object ModEntityTypes {
    public val NPC: EntityType<ServerNPCEntity> =
        Registry.register(
            Registries.ENTITY_TYPE,
            ident("npc"),
            EntityType.Builder.create(::ServerNPCEntity, SpawnGroup.MISC)
                .disableSummon()
                .dimensions(0.6f, 1.8f)
                .eyeHeight(1.62f)
                .maxTrackingRange(32)
                .trackingTickInterval(2)
                .build()
        )

    public fun register() {
        FabricDefaultAttributeRegistry.register(NPC, MobEntity.createMobAttributes())
    }
}
