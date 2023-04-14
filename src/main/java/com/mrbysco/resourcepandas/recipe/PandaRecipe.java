package com.mrbysco.resourcepandas.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PandaRecipe implements Recipe<Container> {
	protected final ResourceLocation id;
	protected final String name;
	protected final Ingredient ingredient;
	protected final ItemStack result;
	protected final String hexColor;
	protected final float alpha;
	protected final float chance;
	public ResourcePandaEntity panda = null;

	public PandaRecipe(ResourceLocation id, String name, Ingredient ingredient, ItemStack stack, String hexColor, float alpha, float chance) {
		this.id = id;
		this.name = name;
		this.ingredient = ingredient;
		this.result = stack;
		this.hexColor = hexColor;
		this.alpha = alpha;
		this.chance = chance;
	}

	@Override
	public RecipeType<?> getType() {
		return PandaRecipes.PANDA_RECIPE_TYPE.get();
	}

	@Override
	public boolean matches(Container inv, Level level) {
		return this.ingredient.test(inv.getItem(0));
	}

	@Override
	public ItemStack assemble(Container inventory, RegistryAccess access) {
		return getResultItem(access);
	}

	@Override
	public NonNullList<Ingredient> getIngredients() {
		NonNullList<Ingredient> nonnulllist = NonNullList.create();
		nonnulllist.add(this.ingredient);
		return nonnulllist;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess access) {
		return this.result.copy();
	}

	@Override
	public boolean canCraftInDimensions(int x, int y) {
		return false;
	}

	public String getHexColor() {
		return hexColor;
	}

	public ResourcePandaEntity getResourcePanda() {
		return com.mrbysco.resourcepandas.client.ClientHelper.getResourcePanda(this);
	}

	public float getAlpha() {
		return alpha;
	}

	public float getChance() {
		return chance;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return PandaRecipes.PANDA_SERIALIZER.get();
	}

	public static class SerializerPandaRecipe implements RecipeSerializer<PandaRecipe> {
		@Override
		public PandaRecipe fromJson(ResourceLocation recipeId, JsonObject jsonObject) {
			String s = GsonHelper.getAsString(jsonObject, "name", "");
			JsonElement jsonelement = GsonHelper.isArrayNode(jsonObject, "ingredient") ? GsonHelper.convertToJsonArray(jsonObject, "ingredient") : GsonHelper.getAsJsonObject(jsonObject, "ingredient");
			Ingredient ingredient = Ingredient.fromJson(jsonelement);
			//Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
			ItemStack itemstack;
			if (jsonObject.get("result").isJsonObject())
				itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "result"));
			else {
				String s1 = GsonHelper.getAsString(jsonObject, "result");
				ResourceLocation resourcelocation = new ResourceLocation(s1);
				itemstack = new ItemStack(BuiltInRegistries.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
					return new IllegalStateException("Item: " + s1 + " does not exist");
				}));
			}

			String hex = GsonHelper.getAsString(jsonObject, "hexColor", "#ffffff");
			if (!hex.startsWith("#") || hex.length() != 7 || !hex.substring(1).matches("[0-9a-fA-F]+")) {
				throw new IllegalStateException("HexColor: " + hex + " is not a valid hex");
			}
			float alpha = GsonHelper.getAsFloat(jsonObject, "alpha", 1.0F);
			float chance = GsonHelper.getAsFloat(jsonObject, "chance", 1.0F);
			return new PandaRecipe(recipeId, s, ingredient, itemstack, hex, alpha, chance);
		}

		@Nullable
		@Override
		public PandaRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			String s = buffer.readUtf(32767);
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			ItemStack itemstack = buffer.readItem();
			String hex = buffer.readUtf(32767);
			float alpha = buffer.readFloat();
			float chance = buffer.readFloat();
			return new PandaRecipe(recipeId, s, ingredient, itemstack, hex, alpha, chance);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, PandaRecipe recipe) {
			buffer.writeUtf(recipe.name);
			recipe.ingredient.toNetwork(buffer);
			buffer.writeItem(recipe.result);
			buffer.writeUtf(recipe.hexColor);
			buffer.writeFloat(recipe.alpha);
			buffer.writeFloat(recipe.chance);
		}
	}
}
