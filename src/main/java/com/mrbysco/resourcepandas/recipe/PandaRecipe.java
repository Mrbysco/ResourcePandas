package com.mrbysco.resourcepandas.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

public class PandaRecipe implements IRecipe<IInventory> {
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
	public IRecipeType<?> getType() {
		return PandaRecipes.PANDA_RECIPE_TYPE;
	}

	@Override
	public boolean matches(IInventory inv, World worldIn) {
		return this.ingredient.test(inv.getItem(0));
	}

	@Override
	public ItemStack assemble(IInventory inventory) {
		return getResultItem();
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
	public ItemStack getResultItem() {
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
	public IRecipeSerializer<?> getSerializer() {
		return PandaRecipes.PANDA_SERIALIZER.get();
	}
	
	public static class SerializerPandaRecipe extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<PandaRecipe> {
		@Override
		public PandaRecipe fromJson(ResourceLocation recipeId, JsonObject jsonObject) {
			String s = JSONUtils.getAsString(jsonObject, "name", "");
			JsonElement jsonelement = (JsonElement)(JSONUtils.isArrayNode(jsonObject, "ingredient") ? JSONUtils.convertToJsonArray(jsonObject, "ingredient") : JSONUtils.getAsJsonObject(jsonObject, "ingredient"));
			Ingredient ingredient = Ingredient.fromJson(jsonelement);
			//Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
			ItemStack itemstack;
			if(jsonObject.has("result")) {
				if (jsonObject.get("result").isJsonObject()) itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(jsonObject, "result"));
				else {
					String s1 = JSONUtils.getAsString(jsonObject, "result");
					ResourceLocation resourcelocation = new ResourceLocation(s1);
					itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
						return new IllegalStateException("Item: " + s1 + " does not exist");
					}));
				}
			} else {
				itemstack = new ItemStack(Items.EGG);
			}

			String hex = JSONUtils.getAsString(jsonObject, "hexColor", "#ffffff");
			if(!hex.startsWith("#") || hex.length() != 7 || !hex.substring(1).matches("[0-9a-fA-F]+")) {
				throw new IllegalStateException("HexColor: " + hex + " is not a valid hex");
			}
			float alpha = JSONUtils.getAsFloat(jsonObject, "alpha", 1.0F);
			float chance = JSONUtils.getAsFloat(jsonObject, "chance", 1.0F);
			return new PandaRecipe(recipeId, s, ingredient, itemstack, hex, alpha, chance);
		}

		@Nullable
		@Override
		public PandaRecipe fromNetwork(ResourceLocation recipeId, PacketBuffer buffer) {
			String s = buffer.readUtf(32767);
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			ItemStack itemstack = buffer.readItem();
			String hex = buffer.readUtf(32767);
			float alpha = buffer.readFloat();
			float chance = buffer.readFloat();
			return new PandaRecipe(recipeId, s, ingredient, itemstack, hex, alpha, chance);
		}

		@Override
		public void toNetwork(PacketBuffer buffer, PandaRecipe recipe) {
			buffer.writeUtf(recipe.name);
			recipe.ingredient.toNetwork(buffer);
			buffer.writeItem(recipe.result);
			buffer.writeUtf(recipe.hexColor);
			buffer.writeFloat(recipe.alpha);
			buffer.writeFloat(recipe.chance);
		}
	}
}
