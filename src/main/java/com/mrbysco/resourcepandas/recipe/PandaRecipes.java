package com.mrbysco.resourcepandas.recipe;

import com.mrbysco.resourcepandas.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PandaRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Reference.MOD_ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Reference.MOD_ID);

	public static final Supplier<RecipeType<PandaRecipe>> PANDA_RECIPE_TYPE = RECIPE_TYPES.register("panda_recipe", () -> new RecipeType<>() {
	});
	public static final Supplier<PandaRecipe.Serializer> PANDA_SERIALIZER = RECIPE_SERIALIZERS.register("panda_recipe", PandaRecipe.Serializer::new);
}
