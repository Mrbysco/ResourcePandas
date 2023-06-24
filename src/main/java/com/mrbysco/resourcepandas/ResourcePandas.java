package com.mrbysco.resourcepandas;

import com.mrbysco.resourcepandas.client.ClientHandler;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.handler.ConversionHandler;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class ResourcePandas {
	public static final Logger LOGGER = LogManager.getLogger();

	public ResourcePandas() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

		MinecraftForge.EVENT_BUS.register(new ConversionHandler());

		PandaRegistry.ENTITY_TYPES.register(eventBus);
		PandaRegistry.ITEMS.register(eventBus);
		PandaRegistry.CREATIVE_MODE_TABS.register(eventBus);
		PandaRecipes.RECIPE_TYPES.register(eventBus);
		PandaRecipes.RECIPE_SERIALIZERS.register(eventBus);

		MinecraftForge.EVENT_BUS.register(this);
		eventBus.addListener(this::registerEntityAttributes);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			eventBus.addListener(ClientHandler::registerEntityRenders);
			eventBus.addListener(ClientHandler::registerItemColors);
		});
	}

	public void registerEntityAttributes(EntityAttributeCreationEvent event) {
		event.put(PandaRegistry.RESOURCE_PANDA.get(), ResourcePandaEntity.genAttributeMap().build());
	}
}
