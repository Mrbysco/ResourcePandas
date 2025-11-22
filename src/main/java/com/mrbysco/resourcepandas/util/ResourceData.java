package com.mrbysco.resourcepandas.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

public record ResourceData(ResourceLocation id, String name, String hexColor,
                           float alpha, float chance) {
	public static final ResourceData MISSING = new ResourceData(
			Reference.modLoc("missing"), "Missing", "#ffd79a", 1.0F, 2.0F);

	public static final Codec<ResourceData> CODEC = RecordCodecBuilder.create(inst ->
			inst.group(
							ResourceLocation.CODEC.fieldOf("id").forGetter(ResourceData::id),
							Codec.STRING.fieldOf("name").forGetter(ResourceData::name),
							Codec.STRING.fieldOf("hexColor").forGetter(ResourceData::hexColor),
							Codec.FLOAT.fieldOf("alpha").forGetter(ResourceData::alpha),
							Codec.FLOAT.fieldOf("chance").forGetter(ResourceData::chance))
					.apply(inst, ResourceData::new));

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
}
