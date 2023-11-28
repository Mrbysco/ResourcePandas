package com.mrbysco.resourcepandas.datagen.builder;

import com.google.gson.JsonObject;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ResourceRecipeBuilder implements RecipeBuilder {
	private final Item result;
	private final int count;
	private final Ingredient ingredient;
	private String name;
	private String hexColor;
	private float alpha;
	private float chance;

	public ResourceRecipeBuilder(Ingredient input, ItemLike output, int count) {
		this.ingredient = input;
		this.result = output.asItem();
		this.count = count;
	}

	public static ResourceRecipeBuilder resource(Ingredient input, ItemLike output) {
		return new ResourceRecipeBuilder(input, output, 1);
	}

	public static ResourceRecipeBuilder resource(Ingredient input, ItemLike output, int count) {
		return new ResourceRecipeBuilder(input, output, count);
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

	public Item getResult() {
		return this.result;
	}

	public void save(RecipeOutput recipeOutput, ResourceLocation id) {
		recipeOutput.accept(new ResourceRecipeBuilder.Result(id, this.result, this.count, this.ingredient,
				this.name, this.hexColor, this.alpha, this.chance));
	}

	public static class Result implements FinishedRecipe {
		private final ResourceLocation id;
		private final Item result;
		private final int count;
		private final Ingredient ingredient;
		private final String name;
		private final String hexColor;
		private final float alpha;
		private final float chance;

		public Result(ResourceLocation id, Item result, int count, Ingredient ingredient, String name, String hex,
					  float alpha, float chance) {
			this.id = id;
			this.result = result;
			this.count = count;
			this.ingredient = ingredient;
			this.name = name;
			this.hexColor = hex;
			this.alpha = alpha;
			this.chance = chance;
		}

		public void serializeRecipeData(JsonObject jsonObject) {
			jsonObject.add("ingredient", ingredient.toJson(false));
			jsonObject.addProperty("name", this.name);
			jsonObject.addProperty("hexColor", this.hexColor);
			jsonObject.addProperty("alpha", this.alpha);
			jsonObject.addProperty("chance", this.chance);
			JsonObject jsonobject = new JsonObject();
			jsonobject.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
			if (this.count > 1) {
				jsonobject.addProperty("count", this.count);
			}

			jsonObject.add("result", jsonobject);
		}

		@Override
		public RecipeSerializer<?> type() {
			return PandaRecipes.PANDA_SERIALIZER.get();
		}

		@Nullable
		@Override
		public AdvancementHolder advancement() {
			return null;
		}

		@Override
		public ResourceLocation id() {
			return this.id;
		}

	}
}