package com.mrbysco.resourcepandas.compat.ct;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
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
	public IRecipeType getRecipeType() {
		return PandaRecipes.PANDA_RECIPE_TYPE;
	}
}
