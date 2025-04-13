package com.mrbysco.resourcepandas.util;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

public record ResourceData(ResourceLocation id, String name, String hexColor,
                           float alpha, float chance) {
	public static final ResourceData MISSING = new ResourceData(
			Reference.modLoc("missing"), "Missing", "#ffd79a", 1.0F, 2.0F);

	public static final StreamCodec<RegistryFriendlyByteBuf, ResourceData> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC,
			spoilTimer -> spoilTimer.id,
			ByteBufCodecs.STRING_UTF8,
			spoilTimer -> spoilTimer.name,
			ByteBufCodecs.STRING_UTF8,
			spoilTimer -> spoilTimer.hexColor,
			ByteBufCodecs.FLOAT,
			spoilTimer -> spoilTimer.alpha,
			ByteBufCodecs.FLOAT,
			spoilTimer -> spoilTimer.chance,
			ResourceData::new
	);

	public static ResourceData fromRecipe(RecipeHolder<PandaRecipe> recipeHolder) {
		PandaRecipe recipe = recipeHolder.value();
		return new ResourceData(recipeHolder.id().location(), recipe.getName(), recipe.getHexColor(), recipe.getAlpha(), recipe.getChance());
	}

	public void saveToTag(CompoundTag tag) {
		CompoundTag dataTag = new CompoundTag();
		dataTag.putString("id", this.id.toString());
		dataTag.putString("name", this.name);
		dataTag.putString("hexColor", this.hexColor);
		dataTag.putFloat("alpha", this.alpha);
		dataTag.putFloat("chance", this.chance);
		tag.put("resource_data", dataTag);
	}

	@Nullable
	public static ResourceData fromTag(CompoundTag tag) {
		if (tag.contains("resource_data")) {
			CompoundTag dataTag = tag.getCompound("resource_data");
			ResourceLocation id = ResourceLocation.tryParse(dataTag.getString("id"));
			String name = dataTag.getString("name");
			String hexColor = dataTag.getString("hexColor");
			float alpha = dataTag.getFloat("alpha");
			float chance = dataTag.getFloat("chance");
			return new ResourceData(id, name, hexColor, alpha, chance);
		}
		return null;
	}
}
