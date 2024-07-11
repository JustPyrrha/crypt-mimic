package gay.pyrrha.mimic.entity

import com.mojang.authlib.GameProfile
import gay.pyrrha.mimic.npc.Npc
import gay.pyrrha.mimic.npc.NpcAction
import gay.pyrrha.mimic.registry.MimicRegistries
import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.block.piston.PistonBehavior
import net.minecraft.entity.*
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Arm
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.EulerAngle
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import net.minecraft.world.event.GameEvent
import net.minecraft.world.explosion.Explosion
import net.minecraft.entity.Npc as MojNpc

public class NPCEntity(world: ServerWorld) : FakePlayer(world, GameProfile(MathHelper.randomUuid(), "[Mimic NPC]")),
    MojNpc {

    public companion object {
        @JvmStatic
        public val NPC_ID: TrackedData<String> =
            DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.STRING)

        private val DEFAULT_HEAD_ROT: EulerAngle = EulerAngle(0f, 0f, 0f)
        private val DEFAULT_BODY_ROT: EulerAngle = EulerAngle(0f, 0f, 0f)
        private val DEFAULT_LEFT_ARM_ROT: EulerAngle = EulerAngle(-10f, 0f, -10f)
        private val DEFAULT_RIGHT_ARM_ROT: EulerAngle = EulerAngle(-15f, 0f, 10f)
        private val DEFAULT_LEFT_LEG_ROT: EulerAngle = EulerAngle(-1f, 0f, -1f)
        private val DEFAULT_RIGHT_LEG_ROT: EulerAngle = EulerAngle(1f, 0f, 1f)
        private val SMALL_DIMENSIONS: EntityDimensions =
            EntityType.PLAYER.dimensions.scaled(0.5f).withEyeHeight(0.9875f)

        public val TRACKED_IS_SMALL: TrackedData<Boolean> =
            DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        public val TRACKED_IS_LOCKED: TrackedData<Boolean> =
            DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.BOOLEAN)
        public val TRACKED_HEAD_ROT: TrackedData<EulerAngle> =
            DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.ROTATION)
        public val TRACKED_BODY_ROT: TrackedData<EulerAngle> =
            DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.ROTATION)
        public val TRACKED_LEFT_ARM_ROT: TrackedData<EulerAngle> =
            DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.ROTATION)
        public val TRACKED_RIGHT_ARM_ROT: TrackedData<EulerAngle> =
            DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.ROTATION)
        public val TRACKED_LEFT_LEG_ROT: TrackedData<EulerAngle> =
            DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.ROTATION)
        public val TRACKED_RIGHT_LEG_ROT: TrackedData<EulerAngle> =
            DataTracker.registerData(NPCEntity::class.java, TrackedDataHandlerRegistry.ROTATION)
    }

    private val heldItems: DefaultedList<ItemStack> = DefaultedList.ofSize(2, ItemStack.EMPTY)
    private val armourItems: DefaultedList<ItemStack> = DefaultedList.ofSize(4, ItemStack.EMPTY)
    public var isLocked: Boolean = false
        private set
    public var isSmall: Boolean = false
        private set
    public var headRotation: EulerAngle = DEFAULT_HEAD_ROT
        private set
    public var bodyRotation: EulerAngle = DEFAULT_BODY_ROT
        private set
    public var leftArmRotation: EulerAngle = DEFAULT_LEFT_ARM_ROT
        private set
    public var rightArmRotation: EulerAngle = DEFAULT_RIGHT_ARM_ROT
        private set
    public var leftLegRotation: EulerAngle = DEFAULT_LEFT_LEG_ROT
        private set
    public var rightLegRotation: EulerAngle = DEFAULT_RIGHT_LEG_ROT
        private set

    override fun getName(): Text {
        return registryManager[MimicRegistries.NPC].get(getNpcId())?.name ?: Text.literal("INVALID")
    }

    public fun getTitle(): Text? {
        return registryManager[MimicRegistries.NPC].get(getNpcId())?.title
    }

    override fun interactAt(player: PlayerEntity, hitPos: Vec3d, hand: Hand): ActionResult {
        if (player.world.isClient) {
            return ActionResult.CONSUME
        } else if (isLocked && player is ServerPlayerEntity) {
            val action = registryManager[MimicRegistries.NPC].get(Identifier.of(dataTracker[NPC_ID]))!!.action
            if (action != null) {
                NpcAction.EVENT.invoker().onAction(player, this, action)
            }
            return ActionResult.SUCCESS
        } else {
            val stack = player.getStackInHand(hand)
            val slot = getPreferredEquipmentSlot(stack)
            val clickedSlot = getSlotFromPosition(hitPos)
            if (stack.isEmpty) {
                if (hasStackEquipped(clickedSlot) && equip(player, clickedSlot, stack, hand)) {
                    return ActionResult.SUCCESS
                }
            } else {
                if (isLocked) {
                    return ActionResult.FAIL
                }

                if (equip(player, slot, stack, hand)) {
                    return ActionResult.SUCCESS
                }
            }
            return ActionResult.PASS
        }
    }

    private fun equip(player: PlayerEntity, slot: EquipmentSlot, stack: ItemStack, hand: Hand): Boolean {
        val equippedStack = getEquippedStack(slot)
        return if (!equippedStack.isEmpty && isLocked) {
            false
        } else if (equippedStack.isEmpty && isLocked) {
            false
        } else if (player.isInCreativeMode && equippedStack.isEmpty && !stack.isEmpty) {
            equipStack(slot, stack.copyWithCount(1))
            true
        } else if (stack.isEmpty || stack.count <= 1) {
            equipStack(slot, stack)
            player.setStackInHand(hand, equippedStack)
            true
        } else {
            false
        }
    }

    private fun getSlotFromPosition(hitPos: Vec3d): EquipmentSlot {
        val isSmall = isSmall
        val hitY = hitPos.y / (scale * scaleFactor)
        return if (hitY >= 0.1 && hitY < 0.1 + (if (isSmall) 0.8 else 0.45) && hasStackEquipped(EquipmentSlot.FEET)) {
            EquipmentSlot.FEET
        } else if (hitY >= 0.9 + (if (isSmall) 0.3 else 0.0) && hitY < 0.9 + (if (isSmall) 1.0 else 0.7) && hasStackEquipped(EquipmentSlot.CHEST)) {
            EquipmentSlot.CHEST
        } else if (hitY >= 0.4 && hitY < 0.4 + (if (isSmall) 1.0 else 0.8) && hasStackEquipped(EquipmentSlot.LEGS)) {
            EquipmentSlot.LEGS
        } else if (hitY >= 1.6 && hasStackEquipped(EquipmentSlot.HEAD)) {
            EquipmentSlot.HEAD
        } else if (!hasStackEquipped(EquipmentSlot.MAINHAND) && hasStackEquipped(EquipmentSlot.OFFHAND)) {
            EquipmentSlot.OFFHAND
        } else {
            EquipmentSlot.MAINHAND
        }
    }

    public fun getNpcId(): Identifier =
        Identifier.of(dataTracker[NPC_ID])

    public fun setNpcId(npcId: Identifier) {
        dataTracker[NPC_ID] = npcId.toString()
    }

    override fun initDataTracker(builder: DataTracker.Builder) {
        super.initDataTracker(builder)
        builder.add(NPC_ID, Npc.DEFAULT.toString())
        builder.add(TRACKED_IS_SMALL, false)
        builder.add(TRACKED_IS_LOCKED, false)
        builder.add(TRACKED_HEAD_ROT, DEFAULT_HEAD_ROT)
        builder.add(TRACKED_BODY_ROT, DEFAULT_BODY_ROT)
        builder.add(TRACKED_LEFT_ARM_ROT, DEFAULT_LEFT_ARM_ROT)
        builder.add(TRACKED_RIGHT_ARM_ROT, DEFAULT_RIGHT_ARM_ROT)
        builder.add(TRACKED_LEFT_LEG_ROT, DEFAULT_LEFT_LEG_ROT)
        builder.add(TRACKED_RIGHT_LEG_ROT, DEFAULT_RIGHT_LEG_ROT)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        val npcId = nbt.getString("NpcId")
        dataTracker[NPC_ID] = npcId

        if (nbt.contains("ArmourItems", NbtElement.LIST_TYPE.toInt())) {
            val nbtList = nbt.getList("ArmourItems", NbtElement.COMPOUND_TYPE.toInt())
            nbtList.forEachIndexed { idx, _ ->
                armourItems[idx] = ItemStack.fromNbtOrEmpty(registryManager, nbtList.getCompound(idx))
            }
        }

        if (nbt.contains("HandItems", NbtElement.LIST_TYPE.toInt())) {
            val nbtList = nbt.getList("HandItems", NbtElement.COMPOUND_TYPE.toInt())
            nbtList.forEachIndexed { idx, _ ->
                heldItems[idx] = ItemStack.fromNbtOrEmpty(registryManager, nbtList.getCompound(idx))
            }
        }

        setSmall(nbt.getBoolean("Small"))
        setLocked(nbt.getBoolean("Locked"))
        poseFromNbt(nbt.getCompound("Pose"))
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putString("NpcId", dataTracker[NPC_ID])

        nbt.put("ArmourItems", NbtList().apply {
            armourItems.forEach { add(it.encodeAllowEmpty(registryManager)) }
        })

        nbt.put("HandItems", NbtList().apply {
            heldItems.forEach { add(it.encodeAllowEmpty(registryManager)) }
        })

        nbt.putBoolean("Small", isSmall)
        nbt.putBoolean("Locked", isLocked)
        nbt.put("Pose", poseToNbt())
    }

    private fun poseFromNbt(nbt: NbtCompound) {
        val headNbt = nbt.getList("Head", NbtElement.FLOAT_TYPE.toInt())
        setHeadRotation(if (headNbt.isEmpty()) DEFAULT_HEAD_ROT else EulerAngle(headNbt))

        val bodyNbt = nbt.getList("Body", NbtElement.FLOAT_TYPE.toInt())
        setBodyRotation(if (bodyNbt.isEmpty()) DEFAULT_BODY_ROT else EulerAngle(bodyNbt))

        val leftArmNbt = nbt.getList("LeftArm", NbtElement.FLOAT_TYPE.toInt())
        setLeftArmRotation(if (leftArmNbt.isEmpty()) DEFAULT_LEFT_ARM_ROT else EulerAngle(leftArmNbt))

        val rightArmNbt = nbt.getList("RightArm", NbtElement.FLOAT_TYPE.toInt())
        setRightArmRotation(if (rightArmNbt.isEmpty()) DEFAULT_RIGHT_ARM_ROT else EulerAngle(rightArmNbt))

        val leftLegNbt = nbt.getList("LeftLeg", NbtElement.FLOAT_TYPE.toInt())
        setLeftLegRotation(if (leftLegNbt.isEmpty()) DEFAULT_LEFT_LEG_ROT else EulerAngle(leftLegNbt))

        val rightLegNbt = nbt.getList("RightLeg", NbtElement.FLOAT_TYPE.toInt())
        setRightLegRotation(if (rightLegNbt.isEmpty()) DEFAULT_RIGHT_LEG_ROT else EulerAngle(rightLegNbt))
    }

    private fun poseToNbt(): NbtCompound {
        val nbt = NbtCompound()

        if (headRotation != DEFAULT_HEAD_ROT) {
            nbt.put("Head", headRotation.toNbt())
        }
        if (bodyRotation != DEFAULT_BODY_ROT) {
            nbt.put("Body", bodyRotation.toNbt())
        }
        if (leftArmRotation != DEFAULT_LEFT_ARM_ROT) {
            nbt.put("LeftArm", leftArmRotation.toNbt())
        }
        if (rightArmRotation != DEFAULT_RIGHT_ARM_ROT) {
            nbt.put("RightArm", rightArmRotation.toNbt())
        }
        if (leftLegRotation != DEFAULT_LEFT_LEG_ROT) {
            nbt.put("LeftLeg", leftLegRotation.toNbt())
        }
        if (rightLegRotation != DEFAULT_RIGHT_LEG_ROT) {
            nbt.put("RightLeg", rightLegRotation.toNbt())
        }

        return nbt
    }

    override fun tick() {
        super.tick()

        val headRot = dataTracker[TRACKED_HEAD_ROT]
        if (headRot != headRotation) {
            setHeadRotation(headRot)
        }

        val bodyRot = dataTracker[TRACKED_BODY_ROT]
        if (bodyRot != bodyRotation) {
            setBodyRotation(bodyRot)
        }

        val leftArmRot = dataTracker[TRACKED_LEFT_ARM_ROT]
        if (leftArmRot != leftArmRotation) {
            setLeftArmRotation(leftArmRot)
        }

        val rightArmRot = dataTracker[TRACKED_RIGHT_ARM_ROT]
        if (rightArmRot != rightArmRotation) {
            setRightArmRotation(rightArmRot)
        }

        val leftLegRot = dataTracker[TRACKED_LEFT_LEG_ROT]
        if (leftLegRot != leftLegRotation) {
            setLeftLegRotation(leftLegRot)
        }

        val rightLegRot = dataTracker[TRACKED_RIGHT_LEG_ROT]
        if (rightLegRot != rightLegRotation) {
            setRightLegRotation(rightLegRot)
        }

        val small = dataTracker[TRACKED_IS_SMALL]
        if (small != isSmall) {
            setSmall(small)
        }

        val locked = dataTracker[TRACKED_IS_LOCKED]
        if (locked != isLocked) {
            setLocked(locked)
        }
    }

    override fun getPistonBehavior(): PistonBehavior =
        if (isLocked) {
            PistonBehavior.IGNORE
        } else {
            super.getPistonBehavior()
        }

    override fun canAvoidTraps(): Boolean = isLocked

    private fun setSmall(isSmall: Boolean) {
        this.isSmall = isSmall
        dataTracker[TRACKED_IS_SMALL] = isSmall
    }

    private fun setLocked(isLocked: Boolean) {
        this.isLocked = isLocked
        dataTracker[TRACKED_IS_LOCKED] = isLocked
    }

    private fun setHeadRotation(angle: EulerAngle) {
        headRotation = angle
        dataTracker[TRACKED_HEAD_ROT] = angle
    }

    private fun setBodyRotation(angle: EulerAngle) {
        bodyRotation = angle
        dataTracker[TRACKED_BODY_ROT] = angle
    }

    private fun setLeftArmRotation(angle: EulerAngle) {
        leftArmRotation = angle
        dataTracker[TRACKED_LEFT_LEG_ROT] = angle
    }

    private fun setRightArmRotation(angle: EulerAngle) {
        rightArmRotation = angle
        dataTracker[TRACKED_RIGHT_ARM_ROT] = angle
    }

    private fun setLeftLegRotation(angle: EulerAngle) {
        leftLegRotation = angle
        dataTracker[TRACKED_LEFT_LEG_ROT] = angle
    }

    private fun setRightLegRotation(angle: EulerAngle) {
        rightLegRotation = angle
        dataTracker[TRACKED_RIGHT_LEG_ROT] = angle
    }

    override fun kill() {
        this.remove(RemovalReason.KILLED)
        this.emitGameEvent(GameEvent.ENTITY_DIE)
    }

    public override fun getBaseDimensions(pose: EntityPose): EntityDimensions =
        if(isSmall) SMALL_DIMENSIONS else type.dimensions

    override fun getMainArm(): Arm = Arm.RIGHT
    override fun isBaby(): Boolean = isSmall
    override fun isPushable(): Boolean = false
    override fun pushAway(entity: Entity) {}
    override fun canMoveVoluntarily(): Boolean = false
    override fun travel(movementInput: Vec3d) {}
    override fun canTakeDamage(): Boolean = false
    override fun isImmuneToExplosion(explosion: Explosion): Boolean = true
    override fun damage(source: DamageSource?, amount: Float): Boolean = false
}
