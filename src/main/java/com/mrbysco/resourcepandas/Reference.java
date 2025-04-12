package com.mrbysco.resourcepandas;

import net.minecraft.resources.ResourceLocation;

public class Reference {
	public static final String MOD_ID = "resourcepandas";
	public static final String MOD_PREFIX = MOD_ID + ":";

	public static final ResourceLocation RESOURCE_COLOR = modLoc("resource_color");

	public static ResourceLocation modLoc(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
