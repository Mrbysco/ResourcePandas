package com.mrbysco.resourcepandas.recipe;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.recipe.PandaRecipe.SerializerPandaRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PandaRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID);
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Reference.MOD_ID);

	public static final RegistryObject<RecipeType<PandaRecipe>> PANDA_RECIPE_TYPE = RECIPE_TYPES.register("panda_recipe", () -> new RecipeType<>() {
	});
	public static final RegistryObject<SerializerPandaRecipe> PANDA_SERIALIZER = RECIPE_SERIALIZERS.register("panda_recipe", SerializerPandaRecipe::new);
}
