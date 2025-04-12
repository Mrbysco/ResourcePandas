package com.mrbysco.resourcepandas.client;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrbysco.resourcepandas.item.PandaDataComponents;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ResourceColor(int defaultColor) implements ItemTintSource {
	public static final MapCodec<ResourceColor> MAP_CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(ExtraCodecs.RGB_COLOR_CODEC.fieldOf("default")
					.forGetter(ResourceColor::defaultColor)).apply(instance, ResourceColor::new)
	);

	public ResourceColor() {
		this(-13083194);
	}

	@Override
	public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
		return ARGB.opaque(stack.getOrDefault(PandaDataComponents.COLOR, defaultColor()));
	}

	@Override
	public MapCodec<ResourceColor> type() {
		return MAP_CODEC;
	}
}
