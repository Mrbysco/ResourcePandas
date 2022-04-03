package com.mrbysco.resourcepandas.client;

import com.mrbysco.resourcepandas.client.renderer.ResourcePandaRenderer;
import com.mrbysco.resourcepandas.item.PandaSpawnEggItem;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.client.color.item.ItemColors;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class ClientHandler {
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(PandaRegistry.RESOURCE_PANDA.get(), ResourcePandaRenderer::new);
	}

	public static void registerItemColors(final ColorHandlerEvent.Item event) {
		ItemColors colors = event.getItemColors();

		PandaSpawnEggItem pandaEgg = (PandaSpawnEggItem) PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get();
		colors.register((stack, tintIndex) -> {
			return pandaEgg.getColor(stack, tintIndex);
		}, pandaEgg);
	}
}
