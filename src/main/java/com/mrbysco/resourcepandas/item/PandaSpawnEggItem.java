package com.mrbysco.resourcepandas.item;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeSpawnEggItem;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PandaSpawnEggItem extends ForgeSpawnEggItem {
	public PandaSpawnEggItem(final Properties properties) {
		super(PandaRegistry.RESOURCE_PANDA, 0, 1776418, properties);
	}

	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (!(level instanceof ServerLevel)) {
			return InteractionResult.SUCCESS;
		} else {
			ItemStack itemstack = context.getItemInHand();
			BlockPos blockpos = context.getClickedPos();
			Direction direction = context.getClickedFace();
			BlockState blockstate = level.getBlockState(blockpos);
			CompoundTag tag = itemstack.getTag() == null ? new CompoundTag() : itemstack.getTag();
			if (blockstate.is(Blocks.SPAWNER)) {
				BlockEntity blockentity = level.getBlockEntity(blockpos);
				if (blockentity instanceof SpawnerBlockEntity spawnerblockentity) {
					EntityType<ResourcePandaEntity> type = PandaRegistry.RESOURCE_PANDA.get();
					if (tag.contains("resourceType")) {
						String resourceType = tag.getString("resourceType");
						List<PandaRecipe> recipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE.get()));
						recipes.removeIf((recipe) -> !recipe.getId().equals(ResourceLocation.tryParse(resourceType)));
						ResourcePandaEntity panda = type.create(level);
						if (panda != null) {
							initializePanda(level, panda, tag);
						} else {
							spawnerblockentity.setEntityId(type, level.getRandom());
						}
					} else {
						spawnerblockentity.setEntityId(type, level.getRandom());
					}
					blockentity.setChanged();
					level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
					itemstack.shrink(1);
					return InteractionResult.CONSUME;
				}
			}

			BlockPos pos2;
			if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
				pos2 = blockpos;
			} else {
				pos2 = blockpos.relative(direction);
			}

			EntityType<ResourcePandaEntity> type = PandaRegistry.RESOURCE_PANDA.get();
			ResourcePandaEntity panda = (ResourcePandaEntity) type.spawn((ServerLevel) level, itemstack, context.getPlayer(), pos2, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, pos2) && direction == Direction.UP);
			if (panda != null) {

				initializePanda(level, panda, tag);
				itemstack.shrink(1);
			}

			return InteractionResult.CONSUME;
		}
	}

	public ResourcePandaEntity initializePanda(Level level, ResourcePandaEntity panda, CompoundTag tag) {
		if (tag.contains("resourceType")) {
			panda.setResourceVariant(tag.getString("resourceType"));
			panda.refresh();
		}
		return panda;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
		if (tag != null && !tag.getString("resourceType").isEmpty()) {
			ResourceLocation location = ResourceLocation.tryParse(tag.getString("resourceType"));
			if (location != null) {
				if (Screen.hasShiftDown()) {
					tooltip.add(Component.literal("Resource: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(location.toString()).withStyle(ChatFormatting.GOLD)));
				} else {
					tooltip.add(Component.literal("Resource: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(location.getPath()).withStyle(ChatFormatting.GOLD)));
				}
			}
		}
	}

	public int getColor(ItemStack stack, int tintIndex) {
		CompoundTag tag = stack.getTag();
		return tintIndex == 0 ? (tag != null && tag.contains("primaryColor") ? tag.getInt("primaryColor") : 15198183) : this.highlightColor;
	}
}