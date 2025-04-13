package com.mrbysco.resourcepandas.client;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.function.Function;

public class ClientHelper {
	private static final HashMap<ResourceLocation, ResourcePandaEntity> pandaCache = new HashMap<>();

	public static ResourcePandaEntity getResourcePanda(ResourceLocation id) {
		return pandaCache.computeIfAbsent(id, key -> {
			CompoundTag nbt = new CompoundTag();
			nbt.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(PandaRegistry.RESOURCE_PANDA.get()).toString());
			Minecraft mc = Minecraft.getInstance();
			Level level = mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null ? mc.getSingleplayerServer().getAllLevels().iterator().next() : mc.level;
			if (level != null) {
				ResourcePandaEntity resourcePanda = (ResourcePandaEntity) EntityType.loadEntityRecursive(nbt, level, EntitySpawnReason.COMMAND, Function.identity());
				if (resourcePanda != null) {
					resourcePanda.setResourceDataById(id);
					resourcePanda.setTransformed(true);
					return resourcePanda;
				}
			}
			return null;
		});
	}

}
