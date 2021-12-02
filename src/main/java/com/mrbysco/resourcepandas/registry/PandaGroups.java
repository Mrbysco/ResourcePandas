package com.mrbysco.resourcepandas.registry;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

public class PandaGroups {
    public static final CreativeModeTab SPAWN_EGGS = new CreativeModeTab(Reference.MOD_PREFIX + "spawn_eggs") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);
            for(PandaRecipe recipe : level.getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE)) {
                ItemStack storageItem = new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
                CompoundTag tag = storageItem.getTag() == null ? new CompoundTag() : storageItem.getTag();
                tag.putInt("primaryColor", Integer.decode("0x" + recipe.getHexColor().replaceFirst("#", "")));
                tag.putString("resourceType", recipe.getId().toString());
                storageItem.setTag(tag);

                items.add(storageItem);
            }
        }
    };
}
