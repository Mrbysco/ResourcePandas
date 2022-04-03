package com.mrbysco.resourcepandas.compat.ct;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeGlobals.Global;
import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;

@ZenRegister
@Name("mods.resourcepandas.PandaManager")
public class PandaManager implements IRecipeManager {
	@Global("pandas")
	public static final PandaManager INSTANCE = new PandaManager();

	private PandaManager() {
	}

	@Method
	public void addPanda(String id, String name, IIngredient input, IItemStack output, String hex, float alpha, float chance) {
		final ResourceLocation location = new ResourceLocation("crafttweaker", id);
		final Ingredient ingredient = input.asVanillaIngredient();
		final ItemStack resultItemStack = output.getInternal();
		final PandaRecipe recipe = new PandaRecipe(location, name, ingredient, resultItemStack, hex, alpha, chance);
		CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
	}

	@Method
	public void addPanda(String id, String name, IIngredient input, IItemStack output, String hex, float chance) {
		final ResourceLocation location = new ResourceLocation("crafttweaker", id);
		final Ingredient ingredient = input.asVanillaIngredient();
		final ItemStack resultItemStack = output.getInternal();
		final PandaRecipe recipe = new PandaRecipe(location, name, ingredient, resultItemStack, hex, 1.0F, chance);
		CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
	}

	@Override
	public RecipeType<PandaRecipe> getRecipeType() {
		return PandaRecipes.PANDA_RECIPE_TYPE.get();
	}
}
