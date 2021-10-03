package com.mrbysco.resourcepandas.recipe;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.recipe.PandaRecipe.SerializerPandaRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class PandaRecipes {
	public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID);

	public static final IRecipeType<PandaRecipe> PANDA_RECIPE_TYPE = IRecipeType.register(new ResourceLocation(Reference.MOD_ID, "panda_recipe").toString());

	public static final RegistryObject<SerializerPandaRecipe> PANDA_SERIALIZER = RECIPE_SERIALIZERS.register("panda_recipe", SerializerPandaRecipe::new);
}
