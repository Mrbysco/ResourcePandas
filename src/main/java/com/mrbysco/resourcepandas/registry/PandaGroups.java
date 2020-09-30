package com.mrbysco.resourcepandas.registry;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.resource.ResourceRegistry;
import com.mrbysco.resourcepandas.resource.ResourceStorage;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PandaGroups {
    public static final ItemGroup SPAWN_EGGS = new ItemGroup(Reference.MOD_PREFIX + "spawn_eggs") {
        @OnlyIn(Dist.CLIENT)
        public ItemStack createIcon() {
            return new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
        }

        @Override
        public void fill(NonNullList<ItemStack> items) {
            for(ResourceStorage storage : ResourceRegistry.RESOURCE_STORAGE.values()) {
                ItemStack storageItem = new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
                CompoundNBT tag = storageItem.getTag() == null ? new CompoundNBT() : storageItem.getTag();
                tag.putInt("primaryColor", Integer.decode("0x" + storage.getHex().replaceFirst("#", "")));
                tag.putString("resourceType", storage.getId());
                storageItem.setTag(tag);

                items.add(storageItem);
            }
        }
    };
}
