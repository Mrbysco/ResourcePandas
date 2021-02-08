package com.mrbysco.resourcepandas;

import com.mrbysco.resourcepandas.resource.ResourceRegistry;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PandaReloadManager implements IResourceManagerReloadListener {
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		ResourceRegistry.loadResourceEntries();
		ResourceRegistry.constructEntries();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(this);
	}
}
