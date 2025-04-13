package com.mrbysco.resourcepandas.registry;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.item.PandaSpawnEggItem;
import com.mrbysco.resourcepandas.util.ResourceData;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Optional;
import java.util.function.Supplier;

public class PandaRegistry {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Reference.MOD_ID);
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Reference.MOD_ID);
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, Reference.MOD_ID);
	public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZER = DeferredRegister.create(NeoForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Reference.MOD_ID);

	public static final Supplier<EntityType<ResourcePandaEntity>> RESOURCE_PANDA = ENTITY_TYPES.register("resource_panda", () ->
			EntityType.Builder.<ResourcePandaEntity>of(ResourcePandaEntity::new, MobCategory.CREATURE)
					.sized(1.3F, 1.25F).clientTrackingRange(10).build(entityId("resource_panda")));

	public static final Supplier<EntityDataSerializer<Optional<ResourceData>>> RESOURCE_DATA = ENTITY_DATA_SERIALIZER.register("resource_data", () -> EntityDataSerializer.forValueType(
			ResourceData.STREAM_CODEC.apply(ByteBufCodecs::optional)
	));

	private static ResourceKey<EntityType<?>> entityId(String path) {
		return ResourceKey.create(Registries.ENTITY_TYPE, Reference.modLoc(path));
	}

	public static final DeferredItem<PandaSpawnEggItem> RESOURCE_PANDA_SPAWN_EGG = ITEMS.registerItem("resource_panda_spawn_egg", PandaSpawnEggItem::new);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPAWN_EGGS = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get()))
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.title(Component.translatable("itemGroup.resourcepandas:spawn_eggs"))
			.build());
}
