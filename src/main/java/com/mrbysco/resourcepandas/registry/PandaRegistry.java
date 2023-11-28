package com.mrbysco.resourcepandas.registry;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.item.PandaSpawnEggItem;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;
import java.util.function.Supplier;

public class PandaRegistry {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Reference.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Reference.MOD_ID);

	public static final Supplier<EntityType<ResourcePandaEntity>> RESOURCE_PANDA = ENTITY_TYPES.register("resource_panda", () ->
			EntityType.Builder.<ResourcePandaEntity>of(ResourcePandaEntity::new, MobCategory.CREATURE)
					.sized(1.3F, 1.25F).clientTrackingRange(10).build("resource_panda"));

	public static final DeferredItem<PandaSpawnEggItem> RESOURCE_PANDA_SPAWN_EGG = ITEMS.register("resource_panda_spawn_egg", () ->
			new PandaSpawnEggItem(new Item.Properties()));

	public static final Supplier<CreativeModeTab> SPAWN_EGGS = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.resourcepandas:spawn_eggs"))
			.displayItems((displayParameters, output) -> {
				ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
				for (RecipeHolder<PandaRecipe> recipe : level.getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE.get())) {
					ItemStack storageItem = new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
					CompoundTag tag = storageItem.getTag() == null ? new CompoundTag() : storageItem.getTag();
					tag.putInt("primaryColor", Integer.decode("0x" + recipe.value().getHexColor().replaceFirst("#", "")));
					tag.putString("resourceType", recipe.id().toString());
					storageItem.setTag(tag);

					output.accept(storageItem);
				}
			}).build());
}
