package com.mrbysco.resourcepandas.item;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
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

public class PandaSpawnEggItem extends Item {
    private final int secondaryColor = 1776418;

    public PandaSpawnEggItem(Properties properties) {
        super(properties);
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        World worldIn = context.getWorld();
        if (worldIn.isRemote) {
            return ActionResultType.SUCCESS;
        } else {
            ItemStack stack = context.getItem();
            BlockPos pos = context.getPos();
            Direction dir = context.getFace();
            BlockState state = worldIn.getBlockState(pos);
            Block block = state.getBlock();
            CompoundNBT tag = stack.getTag() == null ? new CompoundNBT() : stack.getTag();
            if (block == Blocks.SPAWNER) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile instanceof MobSpawnerTileEntity) {
                    AbstractSpawner spawner = ((MobSpawnerTileEntity)tile).getSpawnerBaseLogic();
                    EntityType<ResourcePandaEntity> type = PandaRegistry.RESOURCE_PANDA.get();
                    if(tag.contains("resourceType")) {
                        ResourcePandaEntity panda = type.create(worldIn);
                        if(panda != null) {
                            panda.setResourceVariant(tag.getString("resourceType"));
                            spawner.setNextSpawnData(new WeightedSpawnerEntity(1, panda.serializeNBT()));
                        } else {
                            spawner.setEntityType(type);
                        }
                    } else {
                        spawner.setEntityType(type);
                    }
                    tile.markDirty();
                    worldIn.notifyBlockUpdate(pos, state, state, 3);
                    stack.shrink(1);
                    return ActionResultType.SUCCESS;
                }
            }

            BlockPos pos2;
            if (state.getCollisionShape(worldIn, pos).isEmpty()) {
                pos2 = pos;
            } else {
                pos2 = pos.offset(dir);
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

    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity player, Hand handIn) {
        ItemStack stack = player.getHeldItem(handIn);
        RayTraceResult traceResult = rayTrace(worldIn, player, FluidMode.SOURCE_ONLY);
        CompoundNBT tag = stack.getTag() == null ? new CompoundNBT() : stack.getTag();
        if (traceResult.getType() != Type.BLOCK) {
            return ActionResult.resultPass(stack);
        } else if (worldIn.isRemote) {
            return ActionResult.resultSuccess(stack);
        } else {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)traceResult;
            BlockPos pos = blockRayTraceResult.getPos();
            if (!(worldIn.getBlockState(pos).getBlock() instanceof FlowingFluidBlock)) {
                return ActionResult.resultPass(stack);
            } else if (worldIn.isBlockModifiable(player, pos) && player.canPlayerEdit(pos, blockRayTraceResult.getFace(), stack)) {
                EntityType<ResourcePandaEntity> type = PandaRegistry.RESOURCE_PANDA.get();
                ResourcePandaEntity panda = (ResourcePandaEntity)type.spawn((ServerWorld) worldIn, stack, player, pos, SpawnReason.SPAWN_EGG, false, false);
                if (panda == null) {
                    return ActionResult.resultPass(stack);
                } else {
                    if(tag.contains("resourceType")) {
                        panda.setResourceVariant(tag.getString("resourceType"));
                    }

                    if (!player.abilities.isCreativeMode) {
                        stack.shrink(1);
                    }

                    player.addStat(Stats.ITEM_USED.get(this));
                    return ActionResult.resultSuccess(stack);
                }
            } else {
                return ActionResult.resultFail(stack);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int p_195983_1_) {
        CompoundNBT tag = stack.getTag();
        return p_195983_1_ == 0 ? (tag != null && tag.contains("primaryColor") ? tag.getInt("primaryColor") : 15198183) : this.secondaryColor;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        CompoundNBT tag = stack.hasTag() ? stack.getTag() : new CompoundNBT();
        if (tag != null && !tag.getString("resourceType").isEmpty()) {
            tooltip.add(new StringTextComponent("Resource: ").mergeStyle(TextFormatting.YELLOW).appendSibling(new StringTextComponent(tag.getString("resourceType")).mergeStyle(TextFormatting.GOLD)));
        }
    }
}