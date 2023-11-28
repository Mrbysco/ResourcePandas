package com.mrbysco.resourcepandas.handler;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ConversionHandler {

	@SubscribeEvent
	public void interactEvent(PlayerInteractEvent.EntityInteractSpecific event) {
		Level level = event.getLevel();
		if (!level.isClientSide()) {
			ItemStack heldStack = event.getItemStack();
			Entity target = event.getTarget();
			if (target.getType() == EntityType.PANDA) {
				SimpleContainer inventory = new SimpleContainer(heldStack);
				RecipeHolder<PandaRecipe> recipe = level.getRecipeManager().getRecipeFor(PandaRecipes.PANDA_RECIPE_TYPE.get(), inventory, level).orElse(null);
				if (recipe != null) {
//					ResourcePandas.LOGGER.info(recipe.getId());
					ResourcePandaEntity resourcePanda = ((Panda) target).convertTo(PandaRegistry.RESOURCE_PANDA.get(), true);
					if (resourcePanda != null) {
						resourcePanda.setResourceVariant(recipe.id().toString());
						resourcePanda.checkValues(recipe.value());
						resourcePanda.startTransforming(300);
						level.playSound((Player) null, event.getPos(), SoundEvents.PANDA_EAT, SoundSource.NEUTRAL, 0.5F + 0.5F * (float) resourcePanda.getRandom().nextInt(2), (resourcePanda.getRandom().nextFloat() - resourcePanda.getRandom().nextFloat()) * 0.2F + 1.0F);

						if (!event.getEntity().getAbilities().instabuild) {
							heldStack.shrink(1);
						}
					}
				}
			}
		}
	}
}
