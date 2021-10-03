package com.mrbysco.resourcepandas.item;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class PandaSpawnEggItem extends SpawnEggItem {
    public final Supplier<EntityType<? extends Mob>> entityType;

    private static final DefaultDispenseItemBehavior SPAWN_EGG_BEHAVIOR = new DefaultDispenseItemBehavior() {
        public ItemStack execute(BlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().getValue(DispenserBlock.FACING);
            ((PandaSpawnEggItem) stack.getItem()).getType(stack.getTag()).spawn(source.getLevel(), stack, null,
                    source.getPos().relative(direction), MobSpawnType.DISPENSER, direction != Direction.UP, false);
            stack.shrink(1);
            return stack;
        }
    };

    public PandaSpawnEggItem(final Properties properties) {
        super(null, 0, 1776418, properties);
        this.entityType = () -> PandaRegistry.RESOURCE_PANDA.get();
        DispenserBlock.registerBehavior(this, SPAWN_EGG_BEHAVIOR);
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
                if (blockentity instanceof SpawnerBlockEntity) {
                    BaseSpawner spawner = ((SpawnerBlockEntity)blockentity).getSpawner();
                    EntityType<ResourcePandaEntity> type = PandaRegistry.RESOURCE_PANDA.get();
                    if(tag.contains("resourceType")) {
                        String resourceType = tag.getString("resourceType");
                        List<PandaRecipe> recipes = new ArrayList<>(level.getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE));
                        recipes.removeIf((recipe) -> !recipe.getId().equals(ResourceLocation.tryParse(resourceType)));
                        ResourcePandaEntity panda = type.create(level);
                        if(panda != null) {
                            initializePanda(level, panda, tag);
                        } else {
                            spawner.setEntityId(type);
                        }
                    } else {
                        spawner.setEntityId(type);
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
            ResourcePandaEntity panda = (ResourcePandaEntity)type.spawn((ServerLevel)level, itemstack, context.getPlayer(), pos2, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockpos, pos2) && direction == Direction.UP);
            if(panda != null) {

                initializePanda(level, panda, tag);
                itemstack.shrink(1);
            }

            return InteractionResult.CONSUME;
        }
    }

    public ResourcePandaEntity initializePanda(Level level, ResourcePandaEntity panda, CompoundTag tag) {
        if(tag.contains("resourceType")) {
            panda.setResourceVariant(tag.getString("resourceType"));
            panda.refresh();
        }
        return panda;
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int p_195983_1_) {
        CompoundTag tag = stack.getTag();
        return p_195983_1_ == 0 ? (tag != null && tag.contains("primaryColor") ? tag.getInt("primaryColor") : 15198183) : this.highlightColor;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
        if (tag != null && !tag.getString("resourceType").isEmpty()) {
            ResourceLocation location = ResourceLocation.tryParse(tag.getString("resourceType"));
            if(location != null) {
                if(Screen.hasShiftDown()) {
                    tooltip.add(new TextComponent("Resource: ").withStyle(ChatFormatting.YELLOW).append(new TextComponent(location.toString()).withStyle(ChatFormatting.GOLD)));
                } else {
                    tooltip.add(new TextComponent("Resource: ").withStyle(ChatFormatting.YELLOW).append(new TextComponent(location.getPath()).withStyle(ChatFormatting.GOLD)));
                }
            }
        }
    }

    @Override
    public EntityType<?> getType(@Nullable final CompoundTag nbt) {
        if (nbt != null && nbt.contains("EntityTag", Constants.NBT.TAG_COMPOUND)) {
            final CompoundTag entityTag = nbt.getCompound("EntityTag");
            if (entityTag.contains("id", Constants.NBT.TAG_STRING)) {
                return EntityType.byString(entityTag.getString("id")).orElse(entityType.get());
            }
        }

        return entityType.get();
    }
}