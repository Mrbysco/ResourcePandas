package com.mrbysco.resourcepandas.datagen.builder;

import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

public class ResourceRecipeBuilder implements RecipeBuilder {
	private final HolderGetter<Item> items;
	private final ItemStackTemplate result;
	private final Ingredient ingredient;
	private String name;
	private String hexColor;
	private float alpha;
	private float chance;

	public ResourceRecipeBuilder(HolderGetter<Item> items, Ingredient input, ItemLike output, int count) {
		this.items = items;
		this.ingredient = input;
		this.result = new ItemStackTemplate(output.asItem());
	}

	public static ResourceRecipeBuilder resource(HolderGetter<Item> items, Ingredient input, ItemLike output) {
		return new ResourceRecipeBuilder(items, input, output, 1);
	}

	public static ResourceRecipeBuilder resource(HolderGetter<Item> items, Ingredient input, ItemLike output, int count) {
		return new ResourceRecipeBuilder(items, input, output, count);
	}

	public ResourceRecipeBuilder name(@Nullable String name) {
		this.name = name;
		return this;
	}

	public ResourceRecipeBuilder color(@Nullable String hexColor) {
		this.hexColor = hexColor;
		return this;
	}

	public ResourceRecipeBuilder alpha(@Nullable float alpha) {
		this.alpha = alpha;
		return this;
	}

	public ResourceRecipeBuilder chance(@Nullable float chance) {
		this.chance = chance;
		return this;
	}

	@Override
	public RecipeBuilder unlockedBy(String s, Criterion<?> criterion) {
		return this;
	}

	public ResourceRecipeBuilder group(@Nullable String s) {
		return this;
	}

	public ResourceKey<Recipe<?>> defaultId() {
		return RecipeBuilder.getDefaultRecipeId(this.result);
	}

	public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> resourceKey) {
		PandaRecipe recipe = new PandaRecipe(this.name, this.ingredient, this.result, this.hexColor, this.alpha, this.chance);
		recipeOutput.accept(resourceKey, recipe, null);
	}

	public void save(RecipeOutput recipeOutput, Identifier id) {
		this.save(recipeOutput, ResourceKey.create(Registries.RECIPE, id));
	}
}