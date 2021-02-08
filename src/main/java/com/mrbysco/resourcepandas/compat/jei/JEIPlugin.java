package com.mrbysco.resourcepandas.compat.jei;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
	private static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "jei_plugin");

	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.useNbtForSubtypes(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
	}
}
