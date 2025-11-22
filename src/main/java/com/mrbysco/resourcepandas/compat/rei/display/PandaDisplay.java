//package com.mrbysco.resourcepandas.compat.rei.display;
//
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import com.mrbysco.resourcepandas.compat.rei.PandaREIPlugin;
//import com.mrbysco.resourcepandas.recipe.PandaRecipe;
//import me.shedaniel.rei.api.common.category.CategoryIdentifier;
//import me.shedaniel.rei.api.common.display.Display;
//import me.shedaniel.rei.api.common.display.DisplaySerializer;
//import me.shedaniel.rei.api.common.entry.EntryIngredient;
//import me.shedaniel.rei.api.common.util.EntryIngredients;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.crafting.RecipeHolder;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.List;
//import java.util.Optional;
//
//public class PandaDisplay implements Display {
//	public static final DisplaySerializer<PandaDisplay> SERIALIZER = DisplaySerializer.of(
//			RecordCodecBuilder.mapCodec(instance -> instance.group(
//					EntryIngredient.codec().fieldOf("input").forGetter(d -> d.input),
//					EntryIngredient.codec().fieldOf("output").forGetter(d -> d.output),
//					Codec.STRING.fieldOf("hexColor").forGetter(d -> d.hexColor),
//					Codec.FLOAT.fieldOf("alpha").forGetter(d -> d.alpha),
//					Codec.FLOAT.fieldOf("chance").forGetter(d -> d.chance),
//					ResourceLocation.CODEC.fieldOf("recipeId").forGetter(d -> d.recipeId)
//			).apply(instance, PandaDisplay::new)),
//			StreamCodec.composite(
//					EntryIngredient.streamCodec(),
//					d -> d.input,
//					EntryIngredient.streamCodec(),
//					d -> d.output,
//					ByteBufCodecs.STRING_UTF8,
//					d -> d.hexColor,
//					ByteBufCodecs.FLOAT,
//					d -> d.alpha,
//					ByteBufCodecs.FLOAT,
//					d -> d.chance,
//					ResourceLocation.STREAM_CODEC,
//					d -> d.recipeId,
//					PandaDisplay::new
//			));
//
//	private final EntryIngredient input;
//	private final EntryIngredient output;
//	private final String hexColor;
//	private final float alpha;
//	private final float chance;
//	private final ResourceLocation recipeId;
//
//	public PandaDisplay(RecipeHolder<PandaRecipe> recipeHolder) {
//		PandaRecipe recipe = recipeHolder.value();
//		this.input = EntryIngredients.ofIngredient(recipe.getIngredient());
//		this.output = EntryIngredients.of(recipe.getResult());
//		this.hexColor = recipe.getHexColor();
//		this.alpha = recipe.getAlpha();
//		this.chance = recipe.getChance();
//		this.recipeId = recipeHolder.id().location();
//	}
//
//	public PandaDisplay(EntryIngredient input, EntryIngredient output, String hexColor, Float alpha, Float chance, ResourceLocation recipeId) {
//		this.input = input;
//		this.output = output;
//		this.hexColor = hexColor;
//		this.alpha = alpha;
//		this.chance = chance;
//		this.recipeId = recipeId;
//	}
//
//	@Override
//	public List<EntryIngredient> getInputEntries() {
//		return List.of(this.input);
//	}
//
//	@Override
//	public List<EntryIngredient> getOutputEntries() {
//		return List.of(this.output);
//	}
//
//	@Override
//	public CategoryIdentifier<?> getCategoryIdentifier() {
//		return PandaREIPlugin.PANDAS;
//	}
//
//	@Override
//	public Optional<ResourceLocation> getDisplayLocation() {
//		return Optional.empty();
//	}
//
//	public ResourceLocation getRecipeId() {
//		return recipeId;
//	}
//
//	public String getHexColor() {
//		return hexColor;
//	}
//
//	public float getAlpha() {
//		return alpha;
//	}
//
//	public float getChance() {
//		return chance;
//	}
//
//	@Nullable
//	@Override
//	public DisplaySerializer<? extends Display> getSerializer() {
//		return SERIALIZER;
//	}
//}
