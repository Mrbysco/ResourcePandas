package com.mrbysco.resourcepandas;

import com.mrbysco.resourcepandas.client.ClientHandler;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.handler.Conversionhandler;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class ResourcePandas {
    public static final Logger LOGGER = LogManager.getLogger();

    public ResourcePandas() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(new Conversionhandler());
        MinecraftForge.EVENT_BUS.register(new PandaReloadManager());

        PandaRegistry.ENTITIES.register(eventBus);
        PandaRegistry.ITEMS.register(eventBus);

        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::doClientStuff);
            eventBus.addListener(ClientHandler::registerItemColors);
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        GlobalEntityTypeAttributes.put(PandaRegistry.RESOURCE_PANDA.get(), ResourcePandaEntity.genAttributeMap().create());
    }
}
