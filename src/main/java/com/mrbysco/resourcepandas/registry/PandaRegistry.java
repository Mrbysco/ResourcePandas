package com.mrbysco.resourcepandas.registry;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.item.PandaSpawnEggItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PandaRegistry {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Reference.MOD_ID);

	public static final net.minecraftforge.registries.RegistryObject<EntityType<ResourcePandaEntity>> RESOURCE_PANDA = ENTITY_TYPES.register("resource_panda", () ->
			register("resource_panda", EntityType.Builder.<ResourcePandaEntity>of(ResourcePandaEntity::new, MobCategory.CREATURE)
					.sized(1.3F, 1.25F).clientTrackingRange(10)));

	public static final RegistryObject<Item> RESOURCE_PANDA_SPAWN_EGG = ITEMS.register("resource_panda_spawn_egg", () ->
			new PandaSpawnEggItem(new Item.Properties().tab(PandaGroups.SPAWN_EGGS)));

	public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder, boolean sendVelocityUpdates) {
		return builder.setTrackingRange(64).setUpdateInterval(3).setShouldReceiveVelocityUpdates(sendVelocityUpdates).build(id);
	}

	public static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> builder) {
		return register(id, builder, true);
	}
}
