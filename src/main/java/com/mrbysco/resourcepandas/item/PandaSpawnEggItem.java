package com.mrbysco.resourcepandas.item;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.MobSpawnerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.AbstractSpawner;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.Item.Properties;

public class PandaSpawnEggItem extends Item {
    private final int secondaryColor = 1776418;

    public PandaSpawnEggItem(Properties properties) {
        super(properties);
    }

    public ActionResultType useOn(ItemUseContext context) {
        World worldIn = context.getLevel();
        if (worldIn.isClientSide) {
            return ActionResultType.SUCCESS;
        } else {
            ItemStack stack = context.getItemInHand();
            BlockPos pos = context.getClickedPos();
            Direction dir = context.getClickedFace();
            BlockState state = worldIn.getBlockState(pos);
            Block block = state.getBlock();
            CompoundNBT tag = stack.getTag() == null ? new CompoundNBT() : stack.getTag();
            if (block == Blocks.SPAWNER) {
                TileEntity tile = worldIn.getBlockEntity(pos);
                if (tile instanceof MobSpawnerTileEntity) {
                    AbstractSpawner spawner = ((MobSpawnerTileEntity)tile).getSpawner();
                    EntityType<ResourcePandaEntity> type = PandaRegistry.RESOURCE_PANDA.get();
                    if(tag.contains("resourceType")) {
                        ResourcePandaEntity panda = type.create(worldIn);
                        if(panda != null) {
                            panda.setResourceVariant(tag.getString("resourceType"));
                            spawner.setNextSpawnData(new WeightedSpawnerEntity(1, panda.serializeNBT()));
                        } else {
                            spawner.setEntityId(type);
                        }
                    } else {
                        spawner.setEntityId(type);
                    }
                    tile.setChanged();
                    worldIn.sendBlockUpdated(pos, state, state, 3);
                    stack.shrink(1);
                    return ActionResultType.SUCCESS;
                }
            }

            BlockPos pos2;
            if (state.getBlockSupportShape(worldIn, pos).isEmpty()) {
                pos2 = pos;
            } else {
                pos2 = pos.relative(dir);
            }

            EntityType<ResourcePandaEntity> type = PandaRegistry.RESOURCE_PANDA.get();
            ResourcePandaEntity panda = (ResourcePandaEntity)type.spawn((ServerWorld)worldIn, stack, context.getPlayer(), pos2, SpawnReason.SPAWN_EGG, true, !Objects.equals(pos, pos2) && dir == Direction.UP);
            if(panda != null) {
                if(tag.contains("resourceType")) {
                    panda.setResourceVariant(tag.getString("resourceType"));
                }
                stack.shrink(1);
            }


            return ActionResultType.SUCCESS;
        }
    }

    public ActionResult<ItemStack> use(World worldIn, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getItemInHand(handIn);
        RayTraceResult traceResult = getPlayerPOVHitResult(worldIn, player, FluidMode.SOURCE_ONLY);
        CompoundNBT tag = stack.getTag() == null ? new CompoundNBT() : stack.getTag();
        if (traceResult.getType() != Type.BLOCK) {
            return ActionResult.pass(stack);
        } else if (worldIn.isClientSide) {
            return ActionResult.success(stack);
        } else {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)traceResult;
            BlockPos pos = blockRayTraceResult.getBlockPos();
            if (!(worldIn.getBlockState(pos).getBlock() instanceof FlowingFluidBlock)) {
                return ActionResult.pass(stack);
            } else if (worldIn.mayInteract(player, pos) && player.mayUseItemAt(pos, blockRayTraceResult.getDirection(), stack)) {
                EntityType<ResourcePandaEntity> type = PandaRegistry.RESOURCE_PANDA.get();
                ResourcePandaEntity panda = (ResourcePandaEntity)type.spawn((ServerWorld) worldIn, stack, player, pos, SpawnReason.SPAWN_EGG, false, false);
                if (panda == null) {
                    return ActionResult.pass(stack);
                } else {
                    if(tag.contains("resourceType")) {
                        panda.setResourceVariant(tag.getString("resourceType"));
                    }

                    if (!player.abilities.instabuild) {
                        stack.shrink(1);
                    }

                    player.awardStat(Stats.ITEM_USED.get(this));
                    return ActionResult.success(stack);
                }
            } else {
                return ActionResult.fail(stack);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int p_195983_1_) {
        CompoundNBT tag = stack.getTag();
        return p_195983_1_ == 0 ? (tag != null && tag.contains("primaryColor") ? tag.getInt("primaryColor") : 15198183) : this.secondaryColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundNBT tag = stack.hasTag() ? stack.getTag() : new CompoundNBT();
        if (tag != null && !tag.getString("resourceType").isEmpty()) {
            ResourceLocation location = ResourceLocation.tryParse(tag.getString("resourceType"));
            if(location != null) {
                if(Screen.hasShiftDown()) {
                    tooltip.add(new StringTextComponent("Resource: ").withStyle(TextFormatting.YELLOW).append(new StringTextComponent(location.toString()).withStyle(TextFormatting.GOLD)));
                } else {
                    tooltip.add(new StringTextComponent("Resource: ").withStyle(TextFormatting.YELLOW).append(new StringTextComponent(location.getPath()).withStyle(TextFormatting.GOLD)));
                }
            }
        }
    }
}