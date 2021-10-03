package com.mrbysco.resourcepandas.registry;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.recipe.PandaRecipes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

public class PandaGroups {
    public static final ItemGroup SPAWN_EGGS = new ItemGroup(Reference.MOD_PREFIX + "spawn_eggs") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            ClientWorld world = Objects.requireNonNull(Minecraft.getInstance().level);
            for(PandaRecipe recipe : world.getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE)) {
                ItemStack storageItem = new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
                CompoundNBT tag = storageItem.getTag() == null ? new CompoundNBT() : storageItem.getTag();
                tag.putInt("primaryColor", Integer.decode("0x" + recipe.getHexColor().replaceFirst("#", "")));
                tag.putString("resourceType", recipe.getId().toString());
                storageItem.setTag(tag);

                items.add(storageItem);
            }
        }
    };
}
