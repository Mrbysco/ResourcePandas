package com.mrbysco.resourcepandas.client;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.client.renderer.ResourcePandaRenderer;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

public class ClientHandler {
	public static void registerEntityRenders(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(PandaRegistry.RESOURCE_PANDA.get(), ResourcePandaRenderer::new);
	}

	public static void registerItemColors(final RegisterColorHandlersEvent.ItemTintSources event) {
		event.register(Reference.RESOURCE_COLOR, ResourceColor.MAP_CODEC);
	}
}
