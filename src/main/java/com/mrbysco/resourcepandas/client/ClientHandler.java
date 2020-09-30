package com.mrbysco.resourcepandas.client;

import com.mrbysco.resourcepandas.client.renderer.ResourcePandaRenderer;
import com.mrbysco.resourcepandas.item.PandaSpawnEggItem;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientHandler {
    public static void doClientStuff(final FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(PandaRegistry.RESOURCE_PANDA.get(), ResourcePandaRenderer::new);
    }

    public static void registerItemColors(final ColorHandlerEvent.Item event) {
        ItemColors colors = event.getItemColors();

        PandaSpawnEggItem pandaEgg = (PandaSpawnEggItem) PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get();
        colors.register((stack, p_198141_2_) -> {
            return pandaEgg.getColor(stack, p_198141_2_);
        }, pandaEgg);
    }
}
