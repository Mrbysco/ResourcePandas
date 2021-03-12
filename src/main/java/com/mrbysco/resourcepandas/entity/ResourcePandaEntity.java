package com.mrbysco.resourcepandas.entity;

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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.GameRules;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ResourcePandaEntity extends PandaEntity {
    private static final DataParameter<String> RESOURCE_VARIANT = EntityDataManager.createKey(ResourcePandaEntity.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> TRANSFORMED = EntityDataManager.createKey(ResourcePandaEntity.class, DataSerializers.BOOLEAN);
    private int resourceTransformationTime;

    public ResourcePandaEntity(EntityType<? extends ResourcePandaEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap.MutableAttribute genAttributeMap() {
        return PandaEntity.func_234204_eW_();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(8, new ResourcePandaEntity.ResourceSneezingGoal(this));
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(RESOURCE_VARIANT, "");
        this.dataManager.register(TRANSFORMED, false);
    }

    @Override
    public ITextComponent getName() {
        return !this.hasCustomName() ? new StringTextComponent(String.format("%s ", this.getResourceEntry().getName())).appendSibling(super.getName()) : super.getName();
    }

    public String getResourceVariant() {
        return this.dataManager.get(RESOURCE_VARIANT);
    }

    public void setResourceVariant(String variant) {
        this.dataManager.set(RESOURCE_VARIANT, variant);
    }

    public boolean isTransformed() {
        return this.dataManager.get(TRANSFORMED);
    }

    public void setTransformed(Boolean transformed) {
        this.dataManager.set(TRANSFORMED, transformed);
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
            this.world.playEvent((PlayerEntity)null, 1040, this.getPosition(), 0);
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
    public boolean isBreedingItem(ItemStack stack) {
        return false;
    }

    @Override
    public void tick() {
        if (!this.world.isRemote && this.isAlive() && !this.isAIDisabled()) {
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
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putString("ResourceVariant", this.getResourceVariant());
        compound.putBoolean("Transformed", this.isTransformed());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.setResourceVariant(compound.getString("ResourceVariant"));
        this.setTransformed(compound.getBoolean("Transformed"));
    }

    public ResourceStorage getResourceEntry() {
        return ResourceRegistry.getType(getResourceVariant());
    }

    @Override
    public void func_213577_ez() {
        Vector3d vector3d = this.getMotion();
        this.world.addParticle(ParticleTypes.SNEEZE, this.getPosX() - (double)(this.getWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180F)), this.getPosYEye() - (double)0.1F, this.getPosZ() + (double)(this.getWidth() + 1.0F) * 0.5D * (double)MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180F)), vector3d.x, 0.0D, vector3d.z);
        this.playSound(SoundEvents.ENTITY_PANDA_SNEEZE, 1.0F, 1.0F);

        for(PandaEntity pandaentity : this.world.getEntitiesWithinAABB(PandaEntity.class, this.getBoundingBox().grow(10.0D))) {
            if (!pandaentity.isChild() && pandaentity.isOnGround() && !pandaentity.isInWater() && pandaentity.canPerformAction()) {
                jump(pandaentity);
            }
        }

        if (!this.world.isRemote() && this.rand.nextFloat() <= getResourceEntry().getChance() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.entityDropItem(getResourceEntry().getOutput());
        }
    }

    public void jump(PandaEntity pandaentity) {
        float f = 0.42F * getJumpFactor(pandaentity);
        if (pandaentity.isPotionActive(Effects.JUMP_BOOST)) {
            f += 0.1F * (float)(pandaentity.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1);
        }

        Vector3d vector3d = pandaentity.getMotion();
        pandaentity.setMotion(vector3d.x, (double)f, vector3d.z);
        if (pandaentity.isSprinting()) {
            float f1 = pandaentity.rotationYaw * ((float)Math.PI / 180F);
            pandaentity.setMotion(pandaentity.getMotion().add((double)(-MathHelper.sin(f1) * 0.2F), 0.0D, (double)(MathHelper.cos(f1) * 0.2F)));
        }

        pandaentity.isAirBorne = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(pandaentity);
    }

    protected float getJumpFactor(PandaEntity pandaentity) {
        float f = pandaentity.world.getBlockState(pandaentity.getPosition()).getBlock().getJumpFactor();
        float f1 = pandaentity.world.getBlockState(getPositionUnderneath(pandaentity)).getBlock().getJumpFactor();
        return (double)f == 1.0D ? f1 : f;
    }

    protected BlockPos getPositionUnderneath(PandaEntity pandaentity) {
        return new BlockPos(pandaentity.getPositionVec().x, pandaentity.getBoundingBox().minY - 0.5000001D, pandaentity.getPositionVec().z);
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if(reason == SpawnReason.SPAWN_EGG || reason == SpawnReason.SPAWNER) {
            setTransformed(true);
        } else {
            this.startTransforming(300);
        }
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
        public boolean shouldExecute() {
            if (this.resourcePanda.canPerformAction()) {
                if (this.resourcePanda.isTransformed() && this.resourcePanda.isWeak() && this.resourcePanda.getRNG().nextInt(500) == 1) {
                    return true;
                } else {
                    return this.resourcePanda.getRNG().nextInt(6000) == 1;
                }
            } else {
                return false;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean shouldContinueExecuting() {
            return false;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.resourcePanda.func_213581_u(true);
        }
    }
}
