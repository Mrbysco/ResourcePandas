package com.mrbysco.resourcepandas.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PandaRecipe implements Recipe<SingleRecipeInput> {
	public static final MapCodec<PandaRecipe> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
							Codec.STRING.optionalFieldOf("name", "").forGetter(recipe -> recipe.name),
							Ingredient.CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
							ItemStackTemplate.CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
							Codec.STRING.optionalFieldOf("hexColor", "#ffffff").forGetter(recipe -> recipe.hexColor),
							Codec.FLOAT.optionalFieldOf("alpha", 1.0F).forGetter(recipe -> recipe.alpha),
							Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(recipe -> recipe.chance)
					)
					.apply(instance, PandaRecipe::new)
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, PandaRecipe> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			o -> o.name,
			Ingredient.CONTENTS_STREAM_CODEC,
			o -> o.ingredient,
			ItemStackTemplate.STREAM_CODEC,
			o -> o.result,
			ByteBufCodecs.STRING_UTF8,
			o -> o.hexColor,
			ByteBufCodecs.FLOAT,
			o -> o.alpha,
			ByteBufCodecs.FLOAT,
			o -> o.chance,
			PandaRecipe::new
	);
	public static final RecipeSerializer<PandaRecipe> SERIALIZER = new RecipeSerializer<>(CODEC, STREAM_CODEC);

	protected final String name;
	protected final Ingredient ingredient;
	protected final ItemStackTemplate result;
	protected final String hexColor;
	protected final float alpha;
	protected final float chance;

	public PandaRecipe(String name, Ingredient ingredient, ItemStackTemplate stack, String hexColor, float alpha, float chance) {
		this.name = name;
		this.ingredient = ingredient;
		this.result = stack;
		this.hexColor = hexColor;
		this.alpha = alpha;
		this.chance = chance;
	}

	@Override
	@NotNull
	public RecipeType<PandaRecipe> getType() {
		return PandaRecipes.PANDA_RECIPE_TYPE.get();
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean showNotification() {
		return false;
	}

	@Override
	public String group() {
		return "";
	}

	@Override
	@NotNull
	public PlacementInfo placementInfo() {
		return PlacementInfo.NOT_PLACEABLE;
	}

	@Override
	@NotNull
	public RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.CRAFTING_MISC;
	}

	@Override
	public boolean matches(SingleRecipeInput inv, @NotNull Level level) {
		return this.ingredient.test(inv.getItem(0));
	}

	@Override
	@NotNull
	public ItemStack assemble(@NotNull SingleRecipeInput inventory) {
		return getResult();
	}

	public Ingredient getIngredient() {
		return this.ingredient;
	}

	public String getName() {
		return name;
	}

	public ItemStack getResult() {
		return result.create();
	}

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
	@NotNull
	public RecipeSerializer<PandaRecipe> getSerializer() {
		return PandaRecipes.PANDA_SERIALIZER.get();
	}
}
