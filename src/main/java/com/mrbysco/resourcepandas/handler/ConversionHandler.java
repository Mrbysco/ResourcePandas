package com.mrbysco.resourcepandas.handler;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class ConversionHandler {

	@SubscribeEvent
	public void interactEvent(PlayerInteractEvent.EntityInteract event) {
		Level level = event.getLevel();
		if (level instanceof ServerLevel serverLevel) {
			ItemStack heldStack = event.getItemStack();
			Entity target = event.getTarget();
			if (target.getType() == EntityTypes.PANDA && target instanceof Panda panda) {
				SingleRecipeInput inventory = new SingleRecipeInput(heldStack);
				RecipeHolder<PandaRecipe> recipe = serverLevel.recipeAccess().getRecipeFor(PandaRecipes.PANDA_RECIPE_TYPE.get(), inventory, level).orElse(null);
				if (recipe != null) {
//					ResourcePandas.LOGGER.info(recipe.getId());
					ResourcePandaEntity resourcePanda = panda.convertTo(PandaRegistry.RESOURCE_PANDA.get(),
							ConversionParams.single(panda, true, true), cow -> {
								net.neoforged.neoforge.event.EventHooks.onLivingConvert(panda, cow);

							});
					if (resourcePanda != null) {
						resourcePanda.setResourceDataById(recipe.id().identifier());
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
