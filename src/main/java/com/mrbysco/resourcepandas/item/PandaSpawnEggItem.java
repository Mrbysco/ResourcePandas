package com.mrbysco.resourcepandas.item;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class PandaSpawnEggItem extends DeferredSpawnEggItem {
	public PandaSpawnEggItem(final Properties properties) {
		super(PandaRegistry.RESOURCE_PANDA, 0, 1776418, properties);
	}

	@Override
	@NotNull
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		if (!(level instanceof ServerLevel)) {
			return InteractionResult.SUCCESS;
		} else {
			ItemStack itemstack = context.getItemInHand();
			ResourceLocation resourceType = itemstack.get(PandaDataComponents.RESOURCE_TYPE);
			BlockPos blockpos = context.getClickedPos();
			Direction direction = context.getClickedFace();
			BlockState blockstate = level.getBlockState(blockpos);
			if (blockstate.is(Blocks.SPAWNER)) {
				BlockEntity blockentity = level.getBlockEntity(blockpos);
				if (blockentity instanceof SpawnerBlockEntity spawnerblockentity) {
					EntityType<ResourcePandaEntity> type = PandaRegistry.RESOURCE_PANDA.get();
					if (resourceType != null) {
						ResourcePandaEntity panda = type.create(level);
						if (panda != null) {
							initializePanda(panda, resourceType);
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
			ResourcePandaEntity panda = type.spawn((ServerLevel) level, itemstack, context.getPlayer(), pos2, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, pos2) && direction == Direction.UP);
			if (panda != null) {
				initializePanda(panda, resourceType);
				itemstack.shrink(1);
			}

			return InteractionResult.CONSUME;
		}
	}

	public void initializePanda(ResourcePandaEntity panda, @Nullable ResourceLocation resourceType) {
		if (resourceType != null) {
			panda.setResourceVariant(resourceType.toString());
			panda.refresh();
		}
	}


	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext pContext, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
		super.appendHoverText(stack, pContext, tooltip, flagIn);
		ResourceLocation resourceType = stack.get(PandaDataComponents.RESOURCE_TYPE);
		if (resourceType != null) {
			if (Screen.hasShiftDown()) {
				tooltip.add(Component.literal("Resource: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(resourceType.toString()).withStyle(ChatFormatting.GOLD)));
			} else {
				tooltip.add(Component.literal("Resource: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(resourceType.getPath()).withStyle(ChatFormatting.GOLD)));
			}
		}
	}

	public int getColor(ItemStack stack, int tintIndex) {
		return tintIndex == 0 ? stack.getOrDefault(PandaDataComponents.COLOR, 15198183) : this.getColor(1);
	}
}