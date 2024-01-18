package com.mrbysco.resourcepandas.compat.rei;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.compat.rei.category.PandaCategory;
import com.mrbysco.resourcepandas.compat.rei.display.PandaDisplay;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@REIPluginClient
public class REIPlugin implements REIClientPlugin {
	public static final CategoryIdentifier<PandaDisplay> PANDAS = CategoryIdentifier.of(Reference.MOD_ID, "plugins/pandas");

	@Override
	public void registerCategories(CategoryRegistry registry) {
		registry.add(new PandaCategory());

		registry.addWorkstations(PANDAS, EntryStacks.of(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get()));
	}

	@Override
	public void registerDisplays(DisplayRegistry registry) {
		List<RecipeHolder<PandaRecipe>> recipeHolders = registry.getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE.get());
		recipeHolders.forEach(recipe -> registry.add(new PandaDisplay(recipe)));
	}
}
