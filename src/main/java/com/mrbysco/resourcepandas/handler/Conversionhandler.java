package com.mrbysco.resourcepandas.handler;

import com.mrbysco.resourcepandas.ResourcePandas;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import com.mrbysco.resourcepandas.util.InventoryHelper;
import com.mrbysco.resourcepandas.util.SingularInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Conversionhandler {

    @SubscribeEvent
    public void interactEvent(PlayerInteractEvent.EntityInteractSpecific event) {
        World world = event.getWorld();
        if(!world.isClientSide()) {
            ItemStack heldStack = event.getItemStack();
            Entity target = event.getTarget();
            if(target.getType() == EntityType.PANDA) {
                SingularInventory inventory = InventoryHelper.createSingularInventory(heldStack);
                PandaRecipe recipe = world.getRecipeManager().getRecipeFor(PandaRecipes.PANDA_RECIPE_TYPE, inventory, world).orElse(null);
                if(recipe != null) {
                    ResourcePandas.LOGGER.info(recipe.getId());
                    ResourcePandaEntity resourcePanda = ((PandaEntity)target).convertTo(PandaRegistry.RESOURCE_PANDA.get(), true);
                    if(resourcePanda != null) {
                        resourcePanda.setResourceVariant(recipe.getId().toString());
                        resourcePanda.startTransforming(300);
                        world.playSound((PlayerEntity)null, event.getPos(), SoundEvents.PANDA_EAT, SoundCategory.NEUTRAL, 0.5F + 0.5F * (float)resourcePanda.getRandom().nextInt(2), (resourcePanda.getRandom().nextFloat() - resourcePanda.getRandom().nextFloat()) * 0.2F + 1.0F);

                        if(!event.getPlayer().abilities.instabuild) {
                            heldStack.shrink(1);
                        }
                    }
                }
            }
        }
    }
}
