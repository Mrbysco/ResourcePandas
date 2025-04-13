package com.mrbysco.resourcepandas;

import com.mrbysco.resourcepandas.client.ClientHandler;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.handler.ConversionHandler;
import com.mrbysco.resourcepandas.item.PandaDataComponents;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class ResourcePandas {
	public static final Logger LOGGER = LogManager.getLogger();

	public ResourcePandas(IEventBus eventBus, Dist dist) {
		NeoForge.EVENT_BUS.register(new ConversionHandler());

		PandaDataComponents.DATA_COMPONENT_TYPES.register(eventBus);
		PandaRegistry.ITEMS.register(eventBus);
		PandaRegistry.ENTITY_DATA_SERIALIZER.register(eventBus);
		PandaRegistry.ENTITY_TYPES.register(eventBus);
		PandaRegistry.CREATIVE_MODE_TABS.register(eventBus);
		PandaRecipes.RECIPE_TYPES.register(eventBus);
		PandaRecipes.RECIPE_SERIALIZERS.register(eventBus);

		eventBus.addListener(this::registerEntityAttributes);
		eventBus.addListener(this::addTabContents);

		if (dist.isClient()) {
			eventBus.addListener(ClientHandler::registerEntityRenders);
			eventBus.addListener(ClientHandler::registerItemColors);
		}
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
			LOGGER.info("Environment: {}", ServerLifecycleHooks.getCurrentServer());
			LOGGER.info("IS SERVER {}", FMLEnvironment.dist.isDedicatedServer());
		}
	}
}
