package com.mrbysco.resourcepandas.datagen;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.datagen.builder.ResourceRecipeBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ResourceDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		ExistingFileHelper helper = event.getExistingFileHelper();

		generator.addProvider(event.includeServer(), new ResourceRecipeProvider(packOutput, event.getLookupProvider()));
	}

	public static class ResourceRecipeProvider extends RecipeProvider {
		public ResourceRecipeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
			super(packOutput, lookupProvider);
		}

		@Override
		protected void buildRecipes(RecipeOutput recipeOutput) {
			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_COAL), Items.COAL, 1)
					.name("Coal").color("#363636").alpha(1.0F).chance(0.6F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "coal_panda"));

			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_COPPER), Items.COPPER_INGOT, 1)
					.name("Copper").color("#e77c56").alpha(1.0F).chance(0.6F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "copper_panda"));

			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND), Items.DIAMOND, 1)
					.name("Diamond").color("#a1fbe8").alpha(1.0F).chance(0.11F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "diamond_panda"));

			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_EMERALD), Items.EMERALD, 1)
					.name("Emerald").color("#17dd62").alpha(1.0F).chance(0.08F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "emerald_panda"));

			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_GOLD), Items.GOLD_NUGGET, 1)
					.name("Gold").color("#f8d26a").alpha(1.0F).chance(0.5F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "gold_panda"));

			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON), Items.IRON_NUGGET, 1)
					.name("Iron").color("#d9dfe7").alpha(1.0F).chance(0.6F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "iron_panda"));

			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_LAPIS), Items.LAPIS_LAZULI, 1)
					.name("Lapis").color("#345ec3").alpha(1.0F).chance(0.6F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "lapis_panda"));

			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_NETHERITE), Items.NETHERITE_SCRAP, 1)
					.name("Netherite").color("#4c4143").alpha(1.0F).chance(0.06F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "netherite_panda"));

			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_QUARTZ), Items.QUARTZ, 1)
					.name("Quartz").color("#ddd4c6").alpha(1.0F).chance(0.6F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "quartz_panda"));

			ResourceRecipeBuilder.resource(Ingredient.of(Tags.Items.STORAGE_BLOCKS_REDSTONE), Items.REDSTONE, 1)
					.name("Redstone").color("#aa0f01").alpha(1.0F).chance(0.6F).save(recipeOutput,
							new ResourceLocation(Reference.MOD_ID, "redstone_panda"));
		}
	}
}