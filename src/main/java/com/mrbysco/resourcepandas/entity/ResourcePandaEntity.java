package com.mrbysco.resourcepandas.entity;

import com.mrbysco.resourcepandas.registry.PandaRegistry;
import com.mrbysco.resourcepandas.resource.ResourceRegistry;
import com.mrbysco.resourcepandas.resource.ResourceStorage;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ResourcePandaEntity extends PandaEntity {
    private static final DataParameter<String> RESOURCE_VARIANT = EntityDataManager.defineId(ResourcePandaEntity.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> TRANSFORMED = EntityDataManager.defineId(ResourcePandaEntity.class, DataSerializers.BOOLEAN);
    private int resourceTransformationTime;

    public ResourcePandaEntity(EntityType<? extends ResourcePandaEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute genAttributeMap() {
        return PandaEntity.createAttributes();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new ResourcePandaEntity.ResourceSneezingGoal(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(RESOURCE_VARIANT, "");
        this.entityData.define(TRANSFORMED, false);
    }

    @Override
    public ITextComponent getName() {
        return !this.hasCustomName() ? new StringTextComponent(String.format("%s ", this.getResourceEntry().getName())).append(super.getName()) : super.getName();
    }

    @Override
    public ItemStack getPickedResult(RayTraceResult target) {
        ItemStack stack = new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        compoundNBT.putString("resourceType", getResourceVariant());
        compoundNBT.putInt("primaryColor", Integer.decode("0x" + getResourceEntry().getHex().replaceFirst("#", "")));
        stack.setTag(compoundNBT);
        return stack;
    }

    public String getResourceVariant() {
        return this.entityData.get(RESOURCE_VARIANT);
    }

    public void setResourceVariant(String variant) {
        this.entityData.set(RESOURCE_VARIANT, variant);
    }

    public boolean isTransformed() {
        return this.entityData.get(TRANSFORMED);
    }

    public void setTransformed(Boolean transformed) {
        this.entityData.set(TRANSFORMED, transformed);
    }

    public void startTransforming(int transformationTime) {
        this.resourceTransformationTime = transformationTime;
        this.setTransformed(false);
    }

    protected void onTransformed() {
        setTransformed(true);

        this.setMainGene(Gene.WEAK);
        this.setHiddenGene(Gene.WEAK);

        if (!this.isSilent()) {
            this.level.levelEvent((PlayerEntity)null, 1040, this.blockPosition(), 0);
        }
    }

    public boolean hasResourceVariant() {
       return !getResourceVariant().isEmpty();
    }

    @Override
    public boolean canBreed() {
        return false;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
            if (!this.isTransformed()) {
                --this.resourceTransformationTime;
                if (this.resourceTransformationTime < 0) {
                    this.onTransformed();
                }
            }
        }
        super.tick();
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("ResourceVariant", this.getResourceVariant());
        compound.putBoolean("Transformed", this.isTransformed());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setResourceVariant(compound.getString("ResourceVariant"));
        this.setTransformed(compound.getBoolean("Transformed"));
    }

    public ResourceStorage getResourceEntry() {
        return ResourceRegistry.getType(getResourceVariant());
    }

    @Override
    public void afterSneeze() {
        Vector3d vector3d = this.getDeltaMovement();
        this.level.addParticle(ParticleTypes.SNEEZE, this.getX() - (double)(this.getBbWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(this.yBodyRot * ((float)Math.PI / 180F)), this.getEyeY() - (double)0.1F, this.getZ() + (double)(this.getBbWidth() + 1.0F) * 0.5D * (double)MathHelper.cos(this.yBodyRot * ((float)Math.PI / 180F)), vector3d.x, 0.0D, vector3d.z);
        this.playSound(SoundEvents.PANDA_SNEEZE, 1.0F, 1.0F);

        for(PandaEntity pandaentity : this.level.getEntitiesOfClass(PandaEntity.class, this.getBoundingBox().inflate(10.0D))) {
            if (!pandaentity.isBaby() && pandaentity.isOnGround() && !pandaentity.isInWater() && pandaentity.canPerformAction()) {
                jump(pandaentity);
            }
        }

        if (!this.level.isClientSide() && this.random.nextFloat() <= getResourceEntry().getChance() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(getResourceEntry().getOutput());
        }
    }

    public void jump(PandaEntity pandaentity) {
        float f = 0.42F * getJumpFactor(pandaentity);
        if (pandaentity.hasEffect(Effects.JUMP)) {
            f += 0.1F * (float)(pandaentity.getEffect(Effects.JUMP).getAmplifier() + 1);
        }

        Vector3d vector3d = pandaentity.getDeltaMovement();
        pandaentity.setDeltaMovement(vector3d.x, (double)f, vector3d.z);
        if (pandaentity.isSprinting()) {
            float f1 = pandaentity.yRot * ((float)Math.PI / 180F);
            pandaentity.setDeltaMovement(pandaentity.getDeltaMovement().add((double)(-MathHelper.sin(f1) * 0.2F), 0.0D, (double)(MathHelper.cos(f1) * 0.2F)));
        }

        pandaentity.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(pandaentity);
    }

    protected float getJumpFactor(PandaEntity pandaentity) {
        float f = pandaentity.level.getBlockState(pandaentity.blockPosition()).getBlock().getJumpFactor();
        float f1 = pandaentity.level.getBlockState(getPositionUnderneath(pandaentity)).getBlock().getJumpFactor();
        return (double)f == 1.0D ? f1 : f;
    }

    protected BlockPos getPositionUnderneath(PandaEntity pandaentity) {
        return new BlockPos(pandaentity.position().x, pandaentity.getBoundingBox().minY - 0.5000001D, pandaentity.position().z);
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData entityData = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setMainGene(Gene.WEAK);
        this.setHiddenGene(Gene.WEAK);
        if(reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER) {
            setTransformed(true);
        } else {
            this.startTransforming(300);
        }
        return entityData;
    }

    static class ResourceSneezingGoal extends Goal {
        private final ResourcePandaEntity resourcePanda;

        public ResourceSneezingGoal(ResourcePandaEntity pandaIn) {
            this.resourcePanda = pandaIn;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            if (this.resourcePanda.canPerformAction()) {
                if (this.resourcePanda.isTransformed() && this.resourcePanda.isWeak() && this.resourcePanda.getRandom().nextInt(500) == 1) {
                    return true;
                } else {
                    return this.resourcePanda.getRandom().nextInt(6000) == 1;
                }
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean canContinueToUse() {
            return false;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            this.resourcePanda.sneeze(true);
        }
    }
}
