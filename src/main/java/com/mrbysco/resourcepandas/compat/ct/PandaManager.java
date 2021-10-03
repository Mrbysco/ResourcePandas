package com.mrbysco.resourcepandas.compat.ct;

//@ZenRegister
//@Name("mods.resourcepandas.PandaManager")
public class PandaManager {//implements IRecipeManager {
//	@Global("pandas")
	public static final PandaManager INSTANCE = new PandaManager();

	private PandaManager() {
	}

//	@Method
//	public void addPanda(String id, String name, IIngredient input, IItemStack output, String hex, float alpha, float chance) {
//		final ResourceLocation location = new ResourceLocation("crafttweaker", id);
//		final Ingredient ingredient = input.asVanillaIngredient();
//		final ItemStack resultItemStack = output.getInternal();
//		final PandaRecipe recipe = new PandaRecipe(location, name, ingredient, resultItemStack, hex, alpha, chance);
//		CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
//	}

//	@Method
//	public void addPanda(String id, String name, IIngredient input, IItemStack output, String hex, float chance) {
//		final ResourceLocation location = new ResourceLocation("crafttweaker", id);
//		final Ingredient ingredient = input.asVanillaIngredient();
//		final ItemStack resultItemStack = output.getInternal();
//		final PandaRecipe recipe = new PandaRecipe(location, name, ingredient, resultItemStack, hex, 1.0F, chance);
//		CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
//	}

//	@Override
//	public IRecipeType getRecipeType() {
//		return PandaRecipes.PANDA_RECIPE_TYPE;
//	}
}
