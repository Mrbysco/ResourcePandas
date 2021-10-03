package com.mrbysco.resourcepandas.client;

import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import com.mrbysco.resourcepandas.resource.ResourceStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Function;

public class ClientHelper {
	public static ResourcePandaEntity getResourcePanda(ResourceStorage storage) {
		if(storage.panda == null) {
			CompoundNBT nbt = new CompoundNBT();
			nbt.putString("id", ForgeRegistries.ENTITIES.getKey(PandaRegistry.RESOURCE_PANDA.get()).toString());
			Minecraft mc = Minecraft.getInstance();
			World world = mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null ? mc.getSingleplayerServer().getAllLevels().iterator().next() : mc.level;
			if(world != null) {
				ResourcePandaEntity resourcePanda = (ResourcePandaEntity) EntityType.loadEntityRecursive(nbt, world, Function.identity());
				if(resourcePanda != null) {
					resourcePanda.setResourceVariant(storage.getId());
					resourcePanda.setTransformed(true);
					return storage.panda = resourcePanda;
				}
			}
		}
		return storage.panda;
	}
}
