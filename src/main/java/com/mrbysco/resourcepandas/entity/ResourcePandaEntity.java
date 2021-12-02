package com.mrbysco.resourcepandas.entity;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public class ResourcePandaEntity extends Panda {
    private static final PandaRecipe MISSING_RECIPE = new PandaRecipe(new ResourceLocation(Reference.MOD_ID, "missing"), "Missing",  Ingredient.of(Items.EGG), new ItemStack(Items.EGG), "#ffd79a", 1.0F, 2.0F);

    private static final EntityDataAccessor<String> RESOURCE_VARIANT = SynchedEntityData.defineId(ResourcePandaEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> RESOURCE_COLOR = SynchedEntityData.defineId(ResourcePandaEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> RESOURCE_ALPHA = SynchedEntityData.defineId(ResourcePandaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<String> RESOURCE_NAME = SynchedEntityData.defineId(ResourcePandaEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> TRANSFORMED = SynchedEntityData.defineId(ResourcePandaEntity.class, EntityDataSerializers.BOOLEAN);
    private int resourceTransformationTime;
    private PandaRecipe cachedRecipe;

    public ResourcePandaEntity(EntityType<? extends ResourcePandaEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder genAttributeMap() {
        return Panda.createAttributes();
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
        this.entityData.define(RESOURCE_COLOR, "#FFFFFF");
        this.entityData.define(RESOURCE_ALPHA, 1.0F);
        this.entityData.define(RESOURCE_NAME, "");
        this.entityData.define(TRANSFORMED, false);
    }

    @Override
    public Component getName() {
        return !this.hasCustomName() ? new TextComponent(String.format("%s ", this.getResourceName())).append(super.getName()) : super.getName();
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        ItemStack stack = new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
        CompoundTag compoundNBT = stack.getOrCreateTag();
        compoundNBT.putString("resourceType", getResourceVariant().toString());
        compoundNBT.putInt("primaryColor", Integer.decode("0x" + getPandaRecipe().getHexColor().replaceFirst("#", "")));
        stack.setTag(compoundNBT);
        return stack;
    }

    public ResourceLocation getResourceVariant() {
        String variant = this.entityData.get(RESOURCE_VARIANT);
        if(variant.contains(":")) {
            return ResourceLocation.tryParse(variant);
        } else {
            //Convert old resource panda's
            setResourceVariant(Reference.MOD_PREFIX +  variant);
            PandaRecipe recipe = getPandaRecipe();
            setHexcolor(recipe.getHexColor());
            setAlpha(recipe.getAlpha());
            return new ResourceLocation(Reference.MOD_ID, variant);
        }
    }

    public void setResourceVariant(String variant) {
        this.entityData.set(RESOURCE_VARIANT, variant);
        refresh();
    }

    public String getHexColor() {
        return this.entityData.get(RESOURCE_COLOR);
    }

    public void setHexcolor(String hex) {
        this.entityData.set(RESOURCE_COLOR, hex);
    }

    public float getAlpha() {
        return this.entityData.get(RESOURCE_ALPHA);
    }

    public void setAlpha(float alpha) {
        this.entityData.set(RESOURCE_ALPHA, alpha);
    }

    public boolean isTransformed() {
        return this.entityData.get(TRANSFORMED);
    }

    public String getResourceName() {
        return this.entityData.get(RESOURCE_NAME);
    }

    public void setResourceName(String name) {
        this.entityData.set(RESOURCE_NAME, name);
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
            this.level.levelEvent((Player)null, 1040, this.blockPosition(), 0);
        }
    }

    public boolean hasResourceVariant() {
       return getResourceVariant() != null;
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
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("ResourceVariant", this.getResourceVariant().toString());
        compound.putString("ResourceName", this.getResourceName());
        compound.putString("ResourceHex", this.getHexColor());
        compound.putFloat("ResourceAlpha", this.getAlpha());
        compound.putBoolean("Transformed", this.isTransformed());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setResourceVariant(compound.getString("ResourceVariant"));
        this.setResourceName(compound.getString("ResourceName"));
        this.setHexcolor(compound.getString("ResourceHex"));
        this.setAlpha(compound.getFloat("ResourceAlpha"));
        this.setTransformed(compound.getBoolean("Transformed"));
    }

    public PandaRecipe getPandaRecipe() {
        if(cachedRecipe == null || !cachedRecipe.getId().equals(getResourceVariant())) {
            List<PandaRecipe> recipes = getCommandSenderWorld().getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE);
            for(PandaRecipe recipe : recipes) {
                if(recipe.getId().equals(getResourceVariant())) {
                    checkValues(recipe);
                    return this.cachedRecipe = recipe;
                }
            }
            checkValues(MISSING_RECIPE);
            return MISSING_RECIPE;
        }
        return this.cachedRecipe;
    }

    public void refresh() {
        List<PandaRecipe> recipes = getCommandSenderWorld().getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE);
        for(PandaRecipe recipe : recipes) {
            if(recipe.getId().equals(getResourceVariant())) {
                checkValues(recipe);
                break;
            }
        }
    }

    public void checkValues(PandaRecipe recipe) {
        if(!getResourceName().equals(recipe.getName()))
            setResourceName(recipe.getName());
        if(!getHexColor().equals(recipe.getHexColor()))
            setHexcolor(recipe.getHexColor());
        if(getAlpha() != recipe.getAlpha())
            setAlpha(recipe.getAlpha());
    }

    @Override
    public void afterSneeze() {
        Vec3 vector3d = this.getDeltaMovement();
        this.level.addParticle(ParticleTypes.SNEEZE, this.getX() - (double)(this.getBbWidth() + 1.0F) * 0.5D * (double) Mth.sin(this.yBodyRot * ((float)Math.PI / 180F)), this.getEyeY() - (double)0.1F, this.getZ() + (double)(this.getBbWidth() + 1.0F) * 0.5D * (double)Mth.cos(this.yBodyRot * ((float)Math.PI / 180F)), vector3d.x, 0.0D, vector3d.z);
        this.playSound(SoundEvents.PANDA_SNEEZE, 1.0F, 1.0F);

        for(Panda panda : this.level.getEntitiesOfClass(Panda.class, this.getBoundingBox().inflate(10.0D))) {
            if (!panda.isBaby() && panda.isOnGround() && !panda.isInWater() && panda.canPerformAction()) {
                jump(panda);
            }
        }

        if (!this.level.isClientSide() && this.random.nextFloat() <= getPandaRecipe().getChance() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            PandaRecipe recipe = getPandaRecipe();
            this.spawnAtLocation(recipe.getResultItem());
        }
    }

    public void jump(Panda panda) {
        float f = 0.42F * getJumpFactor(panda);
        if (panda.hasEffect(MobEffects.JUMP)) {
            f += 0.1F * (float)(panda.getEffect(MobEffects.JUMP).getAmplifier() + 1);
        }

        Vec3 vector3d = panda.getDeltaMovement();
        panda.setDeltaMovement(vector3d.x, (double)f, vector3d.z);
        if (panda.isSprinting()) {
            float f1 = panda.getYRot() * ((float)Math.PI / 180F);
            panda.setDeltaMovement(panda.getDeltaMovement().add((double)(-Mth.sin(f1) * 0.2F), 0.0D, (double)(Mth.cos(f1) * 0.2F)));
        }

        panda.hasImpulse = true;
        net.minecraftforge.common.ForgeHooks.onLivingJump(panda);
    }

    protected float getJumpFactor(Panda panda) {
        float f = panda.level.getBlockState(panda.blockPosition()).getBlock().getJumpFactor();
        float f1 = panda.level.getBlockState(getPositionUnderneath(panda)).getBlock().getJumpFactor();
        return (double)f == 1.0D ? f1 : f;
    }

    protected BlockPos getPositionUnderneath(Panda panda) {
        return new BlockPos(panda.position().x, panda.getBoundingBox().minY - 0.5000001D, panda.position().z);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData entityData = super.finalizeSpawn(levelAccessor, difficultyIn, reason, spawnDataIn, dataTag);
        this.setMainGene(Gene.WEAK);
        this.setHiddenGene(Gene.WEAK);
        if(reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.SPAWNER) {
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
