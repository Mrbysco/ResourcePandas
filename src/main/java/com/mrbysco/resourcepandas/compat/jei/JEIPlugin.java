package com.mrbysco.resourcepandas.compat.jei;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.compat.jei.panda.PandaCategory;
import com.mrbysco.resourcepandas.recipe.PandaCache;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	private static final Identifier UID = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "jei_plugin");

	public static final IRecipeType<RecipeHolder<PandaRecipe>> PANDA_RECIPE_TYPE = createPandaType();

	@Nullable
	private IRecipeCategory<RecipeHolder<PandaRecipe>> pandaCategory;

	@Override
	public Identifier getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		registration.addRecipeCategories(
				pandaCategory = new PandaCategory(guiHelper)
		);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addCraftingStation(PANDA_RECIPE_TYPE, new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(PANDA_RECIPE_TYPE, PandaCache.PANDA_RECIPES);
	}

	public static <R extends Recipe<?>> IRecipeType<RecipeHolder<R>> createPandaType() {
		@SuppressWarnings({"unchecked", "RedundantCast"})
		Class<? extends RecipeHolder<R>> holderClass = (Class<? extends RecipeHolder<R>>) (Object) RecipeHolder.class;
		return IRecipeType.create(Reference.MOD_ID, "panda_recipe", holderClass);
	}
}
