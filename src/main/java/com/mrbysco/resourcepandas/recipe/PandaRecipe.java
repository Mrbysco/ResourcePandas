package com.mrbysco.resourcepandas.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipeCodecs;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public class PandaRecipe implements Recipe<Container> {
	protected final String name;
	protected final Ingredient ingredient;
	protected final ItemStack result;
	protected final String hexColor;
	protected final float alpha;
	protected final float chance;
	public ResourcePandaEntity panda = null;

	public PandaRecipe(String name, Ingredient ingredient, ItemStack stack, String hexColor, float alpha, float chance) {
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

	public static class Serializer implements RecipeSerializer<PandaRecipe> {
		private static final Codec<PandaRecipe> CODEC = PandaRecipe.Serializer.RawPandaRecipe.CODEC.flatXmap(rawLootRecipe -> {
			return DataResult.success(new PandaRecipe(
					rawLootRecipe.group,
					rawLootRecipe.ingredient,
					rawLootRecipe.result,
					rawLootRecipe.hexColor,
					rawLootRecipe.alpha,
					rawLootRecipe.chance
			));
		}, recipe -> {
			throw new NotImplementedException("Serializing UpgradeRecipe is not implemented yet.");
		});

		@Override
		public Codec<PandaRecipe> codec() {
			return CODEC;
		}

		@Nullable
		@Override
		public PandaRecipe fromNetwork(FriendlyByteBuf buffer) {
			String s = buffer.readUtf(32767);
			Ingredient ingredient = Ingredient.fromNetwork(buffer);
			ItemStack itemstack = buffer.readItem();
			String hex = buffer.readUtf(32767);
			float alpha = buffer.readFloat();
			float chance = buffer.readFloat();
			return new PandaRecipe(s, ingredient, itemstack, hex, alpha, chance);
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

		static record RawPandaRecipe(
				String group, Ingredient ingredient, ItemStack result, String hexColor, float alpha, float chance
		) {
			public static final Codec<RawPandaRecipe> CODEC = RecordCodecBuilder.create(
					instance -> instance.group(
									ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
									Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(recipe -> recipe.ingredient),
									CraftingRecipeCodecs.ITEMSTACK_OBJECT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
									Codec.STRING.optionalFieldOf("hexColor", "#ffffff").forGetter(recipe -> recipe.hexColor),
									Codec.FLOAT.optionalFieldOf("alpha", 1.0F).forGetter(recipe -> recipe.alpha),
									Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(recipe -> recipe.chance)
							)
							.apply(instance, RawPandaRecipe::new)
			);
		}
	}
}
