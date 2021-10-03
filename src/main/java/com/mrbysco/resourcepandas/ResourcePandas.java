package com.mrbysco.resourcepandas;

import com.mrbysco.resourcepandas.client.ClientHandler;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.handler.Conversionhandler;
import com.mrbysco.resourcepandas.item.PandaSpawnEggItem;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
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

        PandaRegistry.ENTITIES.register(eventBus);
        PandaRegistry.ITEMS.register(eventBus);
        PandaRecipes.RECIPE_SERIALIZERS.register(eventBus);

        MinecraftForge.EVENT_BUS.register(this);
        eventBus.addListener(this::registerEntityAttributes);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            eventBus.addListener(ClientHandler::doClientStuff);
            eventBus.addListener(ClientHandler::registerItemColors);
        });
    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            for(RegistryObject<Item> registryObject : PandaRegistry.ITEMS.getEntries()) {
                if(registryObject.get() instanceof PandaSpawnEggItem) {
                    PandaSpawnEggItem spawnEgg = (PandaSpawnEggItem)registryObject.get();
                    SpawnEggItem.BY_ID.put(spawnEgg.entityType.get(), spawnEgg);
                }
            }
        });
    }

    public void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(PandaRegistry.RESOURCE_PANDA.get(), ResourcePandaEntity.genAttributeMap().build());
    }
}
