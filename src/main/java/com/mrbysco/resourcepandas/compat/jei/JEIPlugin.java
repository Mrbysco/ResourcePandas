package com.mrbysco.resourcepandas.compat.jei;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.compat.jei.panda.PandaCategory;
import com.mrbysco.resourcepandas.compat.jei.panda.PandaWrapper;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import com.mrbysco.resourcepandas.resource.ResourceRegistry;
import com.mrbysco.resourcepandas.resource.ResourceStorage;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	private static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "jei_plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new PandaCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get()), PandaCategory.UID);
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		List<ResourceStorage> entries = new LinkedList<>();
		for(ResourceStorage resourceStorage : ResourceRegistry.RESOURCE_STORAGE.values()) {
			if(!resourceStorage.getInputs().isEmpty() && !resourceStorage.getOutput().isEmpty()) {
				entries.add(resourceStorage);
			}
		}
		registration.addRecipes(asRecipes(entries, PandaWrapper::new), PandaCategory.UID);
	}

	private static <T, R> Collection<R> asRecipes(Collection<T> collection, Function<T, R> transformer) {
		return collection.stream().map(transformer).collect(Collectors.toList());
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.useNbtForSubtypes(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
	}
}
