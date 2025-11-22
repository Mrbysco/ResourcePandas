//package com.mrbysco.resourcepandas.compat.rei;
//
//import com.mrbysco.resourcepandas.Reference;
//import com.mrbysco.resourcepandas.compat.rei.display.PandaDisplay;
//import com.mrbysco.resourcepandas.recipe.PandaRecipe;
//import com.mrbysco.resourcepandas.recipe.PandaRecipes;
//import me.shedaniel.rei.api.common.category.CategoryIdentifier;
//import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
//import me.shedaniel.rei.api.common.plugins.REICommonPlugin;
//import me.shedaniel.rei.api.common.registry.display.ServerDisplayRegistry;
//import me.shedaniel.rei.forge.REIPluginCommon;
//
//@REIPluginCommon
//public class PandaREIPlugin implements REICommonPlugin {
//	public static final CategoryIdentifier<PandaDisplay> PANDAS = CategoryIdentifier.of(Reference.MOD_ID, "plugins/pandas");
//
//	@Override
//	public void registerDisplays(ServerDisplayRegistry registry) {
//		registry.beginRecipeFiller(PandaRecipe.class)
//				.filterType(PandaRecipes.PANDA_RECIPE_TYPE.get())
//				.fill(PandaDisplay::new);
//	}
//
//	@Override
//	public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
//		registry.register(Reference.modLoc("panda_recipes"), PandaDisplay.SERIALIZER);
//	}
//}
