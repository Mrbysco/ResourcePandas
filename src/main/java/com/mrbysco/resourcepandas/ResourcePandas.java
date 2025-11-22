package com.mrbysco.resourcepandas;

import com.mrbysco.resourcepandas.client.ClientHandler;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.handler.ConversionHandler;
import com.mrbysco.resourcepandas.item.PandaDataComponents;
import com.mrbysco.resourcepandas.recipe.PandaCache;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class ResourcePandas {
	public static final Logger LOGGER = LogManager.getLogger();

	public ResourcePandas(IEventBus eventBus, Dist dist) {

		PandaDataComponents.DATA_COMPONENT_TYPES.register(eventBus);
		PandaRegistry.ITEMS.register(eventBus);
		PandaRegistry.ENTITY_DATA_SERIALIZER.register(eventBus);
		PandaRegistry.ENTITIES.register(eventBus);
		PandaRegistry.CREATIVE_MODE_TABS.register(eventBus);
		PandaRecipes.RECIPE_TYPES.register(eventBus);
		PandaRecipes.RECIPE_SERIALIZERS.register(eventBus);

		eventBus.addListener(this::registerEntityAttributes);
		eventBus.addListener(this::addTabContents);

		NeoForge.EVENT_BUS.register(new ConversionHandler());
		NeoForge.EVENT_BUS.addListener(this::onDatapackSync);
		NeoForge.EVENT_BUS.addListener(this::onRecipeReceived);

		if (dist.isClient()) {
			eventBus.addListener(ClientHandler::registerEntityRenders);
			eventBus.addListener(ClientHandler::registerItemColors);
		}
	}

	public void onDatapackSync(OnDatapackSyncEvent event) {
		event.sendRecipes(PandaRecipes.PANDA_RECIPE_TYPE.get());
	}

	public void onRecipeReceived(RecipesReceivedEvent event) {
		PandaCache.PANDA_RECIPES.clear();
		PandaCache.PANDA_RECIPES.addAll(event.getRecipeMap().byType(PandaRecipes.PANDA_RECIPE_TYPE.get()));
	}

	public void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(PandaRegistry.RESOURCE_PANDA.get(), ResourcePandaEntity.genAttributeMap().build());
	}

	public void addTabContents(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == PandaRegistry.SPAWN_EGGS.getKey()) {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			if (server != null) {
				for (RecipeHolder<PandaRecipe> recipe : server.getRecipeManager().recipeMap().byType(PandaRecipes.PANDA_RECIPE_TYPE.get())) {
					ItemStack storageItem = new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
					storageItem.set(PandaDataComponents.COLOR, Integer.decode("0x" + recipe.value().getHexColor().replaceFirst("#", "")));
					storageItem.set(PandaDataComponents.RESOURCE_TYPE, recipe.id().location());
					event.accept(storageItem);
				}
			}
		}
	}
}
