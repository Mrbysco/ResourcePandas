package com.mrbysco.resourcepandas.registry;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Objects;

public class PandaGroups {
	private static CreativeModeTab SPAWN_EGGS;

	@SubscribeEvent
	public void registerCreativeTabs(final CreativeModeTabEvent.Register event) {
		SPAWN_EGGS = event.registerCreativeModeTab(new ResourceLocation(Reference.MOD_ID, "tab"), builder ->
				builder.icon(() -> new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get()))
						.title(Component.translatable("itemGroup.resourcepandas:spawn_eggs"))
						.displayItems((displayParameters, output) -> {
							ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
							for (PandaRecipe recipe : level.getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE.get())) {
								ItemStack storageItem = new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
								CompoundTag tag = storageItem.getTag() == null ? new CompoundTag() : storageItem.getTag();
								tag.putInt("primaryColor", Integer.decode("0x" + recipe.getHexColor().replaceFirst("#", "")));
								tag.putString("resourceType", recipe.getId().toString());
								storageItem.setTag(tag);

								output.accept(storageItem);
							}
						}));
	}
}
