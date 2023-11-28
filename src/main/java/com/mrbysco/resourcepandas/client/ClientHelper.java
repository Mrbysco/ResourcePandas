package com.mrbysco.resourcepandas.client;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public class ClientHelper {
	public static ResourcePandaEntity getResourcePanda(RecipeHolder<PandaRecipe> recipeHolder) {
		PandaRecipe recipe = recipeHolder.value();
		if (recipe.panda == null) {
			CompoundTag nbt = new CompoundTag();
			nbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(PandaRegistry.RESOURCE_PANDA.get()).toString());
			Minecraft mc = Minecraft.getInstance();
			Level level = mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null ? mc.getSingleplayerServer().getAllLevels().iterator().next() : mc.level;
			if (level != null) {
				ResourcePandaEntity resourcePanda = (ResourcePandaEntity) EntityType.loadEntityRecursive(nbt, level, Function.identity());
				if (resourcePanda != null) {
					resourcePanda.setResourceVariant(recipeHolder.id().toString());
					resourcePanda.setHexcolor(recipe.getHexColor());
					resourcePanda.setAlpha(recipe.getAlpha());
					resourcePanda.setTransformed(true);
					return recipe.panda = resourcePanda;
				}
			}
		}
		return recipe.panda;
	}
}
