package com.mrbysco.resourcepandas.entity;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.item.PandaDataComponents;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import com.mrbysco.resourcepandas.util.ResourceData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Optional;

public class ResourcePandaEntity extends Panda {
	private static final RecipeHolder<PandaRecipe> MISSING_RECIPE = new RecipeHolder<>(
			ResourceKey.create(Registries.RECIPE, Reference.modLoc("missing")),
			new PandaRecipe("Missing", Ingredient.of(Items.EGG), new ItemStack(Items.EGG), "#ffd79a", 1.0F, 2.0F));

	private static final EntityDataAccessor<Optional<ResourceData>> RESOURCE_DATA = SynchedEntityData.defineId(ResourcePandaEntity.class, PandaRegistry.RESOURCE_DATA.get());
	private static final EntityDataAccessor<Boolean> TRANSFORMED = SynchedEntityData.defineId(ResourcePandaEntity.class, EntityDataSerializers.BOOLEAN);
	private int resourceTransformationTime;
	private RecipeHolder<PandaRecipe> cachedRecipe;

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
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(RESOURCE_DATA, Optional.empty());
		builder.define(TRANSFORMED, false);
	}

	@Override
	public Component getName() {
		return !this.hasCustomName() ? Component.literal(String.format("%s", this.getResourceName())).append(super.getName()) : super.getName();
	}

	@Nullable
	@Override
	public ItemStack getPickResult() {
		ItemStack stack = PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.toStack();
		Optional<ResourceData> optionalData = getResourceData();
		if (optionalData.isPresent()) {
			stack.set(PandaDataComponents.RESOURCE_TYPE, optionalData.get().id());
			stack.set(PandaDataComponents.COLOR, Integer.decode("0x" + optionalData.get().hexColor().replaceFirst("#", "")));
		} else {
			stack.set(PandaDataComponents.COLOR, Integer.decode("0x" + ResourceData.MISSING.hexColor().replaceFirst("#", "")));
		}
		return stack;
	}

	public Optional<ResourceData> getResourceData() {
		return this.entityData.get(RESOURCE_DATA);
	}

	public void setResourceDataById(Identifier variant) {
		RecipeHolder<PandaRecipe> recipeHolder = getRecipeFromID(variant);
		if (recipeHolder != null) {
			this.setResourceData(ResourceData.fromRecipe(recipeHolder));
		}
	}

	public void setResourceData(@Nullable ResourceData data) {
		if (data == null) {
			this.entityData.set(RESOURCE_DATA, Optional.empty());
		} else {
			this.entityData.set(RESOURCE_DATA, Optional.of(data));
		}
		this.getPandaRecipe(); //Call to cache the recipe
	}

	public String getHexColor() {
		return getResourceData().map(ResourceData::hexColor).orElse("#FFFFFF");
	}

	public float getAlpha() {
		return getResourceData().map(ResourceData::alpha).orElse(1.0F);
	}

	public boolean isTransformed() {
		return this.entityData.get(TRANSFORMED);
	}

	public String getResourceName() {
		return getResourceData().map(ResourceData::name).orElse("");
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
			this.level().levelEvent((Player) null, 1040, this.blockPosition(), 0);
		}
	}

	public boolean hasResourceVariant() {
		return getResourceData().isPresent();
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
		if (!this.level().isClientSide() && this.isAlive() && !this.isNoAi()) {
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
	protected void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		if (this.getResourceData().isPresent()) {
			output.store("resource_data", ResourceData.CODEC, this.getResourceData().get());
		}
		output.putBoolean("Transformed", this.isTransformed());
	}

	@Override
	public void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);

		Optional<ResourceData> resourceData = input.read("resource_data", ResourceData.CODEC);
		if (resourceData.isPresent()) {
			this.setResourceData(resourceData.get());
		} else {
			this.setResourceData(null);
		}
		this.setTransformed(input.getBooleanOr("Transformed", false));
	}

	public RecipeHolder<PandaRecipe> getPandaRecipe() {
		if (this.level() instanceof ServerLevel serverLevel && getResourceData().isPresent()) {
			Identifier dataId = getResourceData().get().id();
			if (cachedRecipe == null || !cachedRecipe.id().identifier().equals(dataId)) {
				Collection<RecipeHolder<PandaRecipe>> recipes = serverLevel.recipeAccess().recipeMap().byType(PandaRecipes.PANDA_RECIPE_TYPE.get());
				for (RecipeHolder<PandaRecipe> recipe : recipes) {
					if (recipe.id().identifier().equals(dataId)) {
						return this.cachedRecipe = recipe;
					}
				}
				return this.cachedRecipe = null;
			}
			return this.cachedRecipe;
		}
		return null;
	}

	/**
	 * Get the recipe from the ID of the panda.
	 *
	 * @param id The ID of the panda
	 * @return The recipe holder of the panda (null if client-side)
	 */
	private RecipeHolder<PandaRecipe> getRecipeFromID(Identifier id) {
		if (this.level() instanceof ServerLevel serverLevel) {
			Collection<RecipeHolder<PandaRecipe>> recipes = serverLevel.recipeAccess().recipeMap().byType(PandaRecipes.PANDA_RECIPE_TYPE.get());
			for (RecipeHolder<PandaRecipe> recipe : recipes) {
				if (recipe.id().identifier().equals(id)) {
					return recipe;
				}
			}
		}
		return null;
	}

	@Override
	public void afterSneeze() {
		Vec3 vector3d = this.getDeltaMovement();
		this.level().addParticle(ParticleTypes.SNEEZE, this.getX() - (double) (this.getBbWidth() + 1.0F) * 0.5D * (double) Mth.sin(this.yBodyRot * ((float) Math.PI / 180F)), this.getEyeY() - (double) 0.1F, this.getZ() + (double) (this.getBbWidth() + 1.0F) * 0.5D * (double) Mth.cos(this.yBodyRot * ((float) Math.PI / 180F)), vector3d.x, 0.0D, vector3d.z);
		this.playSound(SoundEvents.PANDA_SNEEZE, 1.0F, 1.0F);

		for (Panda panda : this.level().getEntitiesOfClass(Panda.class, this.getBoundingBox().inflate(10.0D))) {
			if (!panda.isBaby() && panda.onGround() && !panda.isInWater() && panda.canPerformAction()) {
				jump(panda);
			}
		}

		if (this.level() instanceof ServerLevel serverLevel && this.random.nextFloat() <= getPandaRecipe().value().getChance() &&
				serverLevel.getGameRules().get(GameRules.MOB_DROPS)) {
			PandaRecipe recipe = getPandaRecipe().value();
			this.spawnAtLocation(serverLevel, recipe.getResult());
		}
	}

	public void jump(Panda panda) {
		float f = 0.42F * getJumpFactor(panda);
		MobEffectInstance jumpEffect = panda.getEffect(MobEffects.JUMP_BOOST);
		if (jumpEffect != null) {
			f += 0.1F * (float) (jumpEffect.getAmplifier() + 1);
		}

		Vec3 vector3d = panda.getDeltaMovement();
		panda.setDeltaMovement(vector3d.x, (double) f, vector3d.z);
		if (panda.isSprinting()) {
			float f1 = panda.getYRot() * ((float) Math.PI / 180F);
			panda.setDeltaMovement(panda.getDeltaMovement().add((double) (-Mth.sin(f1) * 0.2F), 0.0D, (double) (Mth.cos(f1) * 0.2F)));
		}

		panda.needsSync = true;
		CommonHooks.onLivingJump(panda);
	}

	protected float getJumpFactor(Panda panda) {
		float f = panda.level().getBlockState(panda.blockPosition()).getBlock().getJumpFactor();
		float f1 = panda.level().getBlockState(getPositionUnderneath(panda)).getBlock().getJumpFactor();
		return (double) f == 1.0D ? f1 : f;
	}

	protected BlockPos getPositionUnderneath(Panda panda) {
		return BlockPos.containing(panda.position().x, panda.getBoundingBox().minY - 0.5000001D, panda.position().z);
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor levelAccessor, DifficultyInstance difficultyIn, EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn) {
		SpawnGroupData entityData = super.finalizeSpawn(levelAccessor, difficultyIn, reason, spawnDataIn);
		this.setMainGene(Gene.WEAK);
		this.setHiddenGene(Gene.WEAK);
		if (reason == EntitySpawnReason.SPAWN_ITEM_USE || reason == EntitySpawnReason.SPAWNER) {
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
