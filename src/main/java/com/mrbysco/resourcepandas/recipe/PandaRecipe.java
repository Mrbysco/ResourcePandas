package com.mrbysco.resourcepandas.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.List;

public class PandaRecipe implements Recipe<SingleRecipeInput> {
	protected final String name;
	protected final Ingredient ingredient;
	protected final ItemStack result;
	protected final String hexColor;
	protected final float alpha;
	protected final float chance;

	public PandaRecipe(String name, Ingredient ingredient, ItemStack stack, String hexColor, float alpha, float chance) {
		this.name = name;
		this.ingredient = ingredient;
		this.result = stack;
		this.hexColor = hexColor;
		this.alpha = alpha;
		this.chance = chance;
	}

	@Override
	public RecipeType<PandaRecipe> getType() {
		return PandaRecipes.PANDA_RECIPE_TYPE.get();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public PlacementInfo placementInfo() {
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CRAFTING_MISC;
	}

	@Override
	public boolean matches(SingleRecipeInput inv, Level level) {
		return this.ingredient.test(inv.getItem(0));
	}

	@Override
	public ItemStack assemble(SingleRecipeInput inventory, HolderLookup.Provider provider) {
		return getResultItem(provider);
	}

	public Ingredient getIngredient() {
		return this.ingredient;
	}

	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(this.ingredient);
		return nonnulllist;
	}

	public String getName() {
		return name;
	}

//	@Override
	public ItemStack getResultItem(HolderLookup.Provider provider) {
		return this.result.copy();
	}
//
//	@Override
//	public boolean canCraftInDimensions(int x, int y) {
//		return false;
//	}

	public String getHexColor() {
		return hexColor;
	}

	public float getAlpha() {
		return alpha;
	}

	public float getChance() {
		return chance;
	}

	@Override
	public RecipeSerializer<PandaRecipe> getSerializer() {
		return PandaRecipes.PANDA_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<PandaRecipe> {
		public static final MapCodec<PandaRecipe> CODEC = RecordCodecBuilder.mapCodec(
				instance -> instance.group(
								Codec.STRING.fieldOf("name").forGetter(recipe -> recipe.name),
								Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
								ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
								Codec.STRING.optionalFieldOf("hexColor", "#ffffff").forGetter(recipe -> recipe.hexColor),
								Codec.FLOAT.optionalFieldOf("alpha", 1.0F).forGetter(recipe -> recipe.alpha),
								Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(recipe -> recipe.chance)
						)
						.apply(instance, PandaRecipe::new)
		);
		public static final StreamCodec<RegistryFriendlyByteBuf, PandaRecipe> STREAM_CODEC = StreamCodec.of(
				PandaRecipe.Serializer::toNetwork, PandaRecipe.Serializer::fromNetwork
		);

		@Override
		public MapCodec<PandaRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, PandaRecipe> streamCodec() {
			return STREAM_CODEC;
		}

		public static PandaRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
			String s = buffer.readUtf(32767);
			Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(buffer);
			ItemStack itemstack = ItemStack.STREAM_CODEC.decode(buffer);
			String hex = buffer.readUtf(32767);
			float alpha = buffer.readFloat();
			float chance = buffer.readFloat();
			return new PandaRecipe(s, ingredient, itemstack, hex, alpha, chance);
		}

		public static void toNetwork(RegistryFriendlyByteBuf buffer, PandaRecipe recipe) {
			buffer.writeUtf(recipe.name);
			Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.ingredient);
			ItemStack.STREAM_CODEC.encode(buffer, recipe.result);
			buffer.writeUtf(recipe.hexColor);
			buffer.writeFloat(recipe.alpha);
			buffer.writeFloat(recipe.chance);
		}
	}
}
