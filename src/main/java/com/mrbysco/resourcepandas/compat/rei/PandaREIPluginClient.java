//package com.mrbysco.resourcepandas.compat.rei;
//
//import com.mrbysco.resourcepandas.Reference;
//import com.mrbysco.resourcepandas.compat.rei.category.PandaCategory;
//import com.mrbysco.resourcepandas.compat.rei.display.PandaDisplay;
//import com.mrbysco.resourcepandas.registry.PandaRegistry;
//import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
//import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
//import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
//import me.shedaniel.rei.api.common.category.CategoryIdentifier;
//import me.shedaniel.rei.api.common.util.EntryStacks;
//import me.shedaniel.rei.forge.REIPluginClient;
//
//@REIPluginClient
//public class PandaREIPluginClient implements REIClientPlugin {
//	public static final CategoryIdentifier<PandaDisplay> PANDAS = CategoryIdentifier.of(Reference.MOD_ID, "plugins/pandas");
//
//	@Override
//	public void registerCategories(CategoryRegistry registry) {
//		registry.add(new PandaCategory());
//
//		registry.addWorkstations(PANDAS, EntryStacks.of(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.asItem()));
//	}
//
//	@Override
//	public void registerDisplays(DisplayRegistry registry) {
//		REIClientPlugin.super.registerDisplays(registry);
//	}
//}
