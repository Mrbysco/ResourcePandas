package com.mrbysco.resourcepandas.datagen;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.client.ResourceColor;
import com.mrbysco.resourcepandas.datagen.builder.ResourceRecipeBuilder;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber
public class ResourceDatagen {
	@SubscribeEvent
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();

		generator.addProvider(true, new ResourceModelProvider(packOutput));
		generator.addProvider(true, new ResourceRecipeProvider.Runner(packOutput, event.getLookupProvider()));
	}

	public static class ResourceModelProvider extends ModelProvider {
		public ResourceModelProvider(PackOutput packOutput) {
			super(packOutput, Reference.MOD_ID);
		}

		@Override
		protected void registerModels(@NotNull BlockModelGenerators blockModels, @NotNull ItemModelGenerators itemModels) {
			Identifier baseTexture = Reference.modLoc("item/panda_spawn_egg");
			Identifier customOverlay = Reference.modLoc("item/panda_spawn_egg_overlay");

			Identifier layeredModel = itemModels.generateLayeredItem(
					PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get(), customOverlay, baseTexture
			);
			itemModels.itemModelOutput.accept(
					PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get(),
					ItemModelUtils.tintedModel(layeredModel, new ResourceColor(), ItemModelUtils.constantTint(1776418))
			);
		}

	}

	public static class ResourceRecipeProvider extends RecipeProvider {
		private final HolderGetter<Item> items;

		public ResourceRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
			super(provider, recipeOutput);
			this.items = provider.lookupOrThrow(Registries.ITEM);
		}

		@Override
		protected void buildRecipes() {
			resource(tag(Tags.Items.STORAGE_BLOCKS_COAL), Items.COAL, 1)
					.name("Coal").color("#363636").alpha(1.0F).chance(0.6F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "coal_panda"));

			resource(tag(Tags.Items.STORAGE_BLOCKS_COPPER), Items.COPPER_INGOT, 1)
					.name("Copper").color("#e77c56").alpha(1.0F).chance(0.6F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "copper_panda"));

			resource(tag(Tags.Items.STORAGE_BLOCKS_DIAMOND), Items.DIAMOND, 1)
					.name("Diamond").color("#a1fbe8").alpha(1.0F).chance(0.11F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "diamond_panda"));

			resource(tag(Tags.Items.STORAGE_BLOCKS_EMERALD), Items.EMERALD, 1)
					.name("Emerald").color("#17dd62").alpha(1.0F).chance(0.08F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "emerald_panda"));

			resource(tag(Tags.Items.STORAGE_BLOCKS_GOLD), Items.GOLD_NUGGET, 1)
					.name("Gold").color("#f8d26a").alpha(1.0F).chance(0.5F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "gold_panda"));

			resource(tag(Tags.Items.STORAGE_BLOCKS_IRON), Items.IRON_NUGGET, 1)
					.name("Iron").color("#d9dfe7").alpha(1.0F).chance(0.6F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "iron_panda"));

			resource(tag(Tags.Items.STORAGE_BLOCKS_LAPIS), Items.LAPIS_LAZULI, 1)
					.name("Lapis").color("#345ec3").alpha(1.0F).chance(0.6F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "lapis_panda"));

			resource(tag(Tags.Items.STORAGE_BLOCKS_NETHERITE), Items.NETHERITE_SCRAP, 1)
					.name("Netherite").color("#4c4143").alpha(1.0F).chance(0.06F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "netherite_panda"));

			resource(Ingredient.of(Blocks.QUARTZ_BLOCK), Items.QUARTZ, 1)
					.name("Quartz").color("#ddd4c6").alpha(1.0F).chance(0.6F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "quartz_panda"));

			resource(tag(Tags.Items.STORAGE_BLOCKS_REDSTONE), Items.REDSTONE, 1)
					.name("Redstone").color("#aa0f01").alpha(1.0F).chance(0.6F).save(output,
							Identifier.fromNamespaceAndPath(Reference.MOD_ID, "redstone_panda"));
		}

		private ResourceRecipeBuilder resource(Ingredient input, ItemLike output, int count) {
			return ResourceRecipeBuilder.resource(this.items, input, output, count);
		}

		protected Ingredient tag(TagKey<Item> tag) {
			return Ingredient.of(this.items.getOrThrow(tag));
		}

		public static class Runner extends RecipeProvider.Runner {
			public Runner(PackOutput output, CompletableFuture<Provider> completableFuture) {
				super(output, completableFuture);
			}

			@Override
			protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
				return new ResourceRecipeProvider(provider, recipeOutput);
			}

			@Override
			public String getName() {
				return "Resource Pandas Recipes";
			}
		}
	}
}