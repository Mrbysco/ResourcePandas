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
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

public class PandaSpawnEggItem extends SpawnEggItem {
	public PandaSpawnEggItem(final Properties properties) {
		super(PandaRegistry.RESOURCE_PANDA.get(), properties);
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
						ResourcePandaEntity panda = type.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
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
			ResourcePandaEntity panda = type.spawn((ServerLevel) level, itemstack, context.getPlayer(), pos2, EntitySpawnReason.SPAWN_ITEM_USE, true, !Objects.equals(blockpos, pos2) && direction == Direction.UP);
			if (panda != null) {
				initializePanda(panda, resourceType);
				itemstack.shrink(1);
			}

			return InteractionResult.CONSUME;
		}
	}

	public void initializePanda(ResourcePandaEntity panda, @Nullable ResourceLocation resourceType) {
		if (resourceType != null) {
			panda.setResourceDataById(resourceType);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> consumer, TooltipFlag flag) {
		super.appendHoverText(stack, context, display, consumer, flag);
		ResourceLocation resourceType = stack.get(PandaDataComponents.RESOURCE_TYPE);
		if (resourceType != null) {
			if (Screen.hasShiftDown()) {
				consumer.accept(Component.literal("Resource: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(resourceType.toString()).withStyle(ChatFormatting.GOLD)));
			} else {
				consumer.accept(Component.literal("Resource: ").withStyle(ChatFormatting.YELLOW).append(Component.literal(resourceType.getPath()).withStyle(ChatFormatting.GOLD)));
			}
		}
	}
}