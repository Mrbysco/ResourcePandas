package com.mrbysco.resourcepandas;

import net.minecraft.resources.Identifier;

public class Reference {
	public static final String MOD_ID = "resourcepandas";
	public static final String MOD_PREFIX = MOD_ID + ":";

	public static final Identifier RESOURCE_COLOR = modLoc("resource_color");

	public static Identifier modLoc(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
