package com.mrbysco.resourcepandas.handler;

import com.mrbysco.resourcepandas.ResourcePandas;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import com.mrbysco.resourcepandas.util.InventoryHelper;
import com.mrbysco.resourcepandas.util.SingularInventory;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Conversionhandler {

    @SubscribeEvent
    public void interactEvent(PlayerInteractEvent.EntityInteractSpecific event) {
        Level world = event.getWorld();
        if(!world.isClientSide()) {
            ItemStack heldStack = event.getItemStack();
            Entity target = event.getTarget();
            if(target.getType() == EntityType.PANDA) {
                SingularInventory inventory = InventoryHelper.createSingularInventory(heldStack);
                PandaRecipe recipe = world.getRecipeManager().getRecipeFor(PandaRecipes.PANDA_RECIPE_TYPE, inventory, world).orElse(null);
                if(recipe != null) {
                    ResourcePandas.LOGGER.info(recipe.getId());
                    ResourcePandaEntity resourcePanda = ((Panda)target).convertTo(PandaRegistry.RESOURCE_PANDA.get(), true);
                    if(resourcePanda != null) {
                        resourcePanda.setResourceVariant(recipe.getId().toString());
                        resourcePanda.checkValues(recipe);
                        resourcePanda.startTransforming(300);
                        world.playSound((Player)null, event.getPos(), SoundEvents.PANDA_EAT, SoundSource.NEUTRAL, 0.5F + 0.5F * (float)resourcePanda.getRandom().nextInt(2), (resourcePanda.getRandom().nextFloat() - resourcePanda.getRandom().nextFloat()) * 0.2F + 1.0F);

                        if(!event.getPlayer().getAbilities().instabuild) {
                            heldStack.shrink(1);
                        }
                    }
                }
            }
        }
    }
}
