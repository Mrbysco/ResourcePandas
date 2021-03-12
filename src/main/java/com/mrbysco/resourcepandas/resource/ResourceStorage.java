package com.mrbysco.resourcepandas.resource;

import com.mrbysco.resourcepandas.ResourcePandas;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class ResourceStorage{
    private final String id;
    private final String name;
    private final float chance;

    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;
    private final String hex;

    private final List<ItemStack> inputs = new ArrayList<>();
    private final ItemStack output;

    public ResourceStorage(ResourceEntry entry) {
        this.id = entry.getId();
        this.name = entry.getName();
        this.chance = entry.getChance();

        this.hex = entry.getHexColor();
        this.red = entry.getRed();
        this.green = entry.getGreen();
        this.blue = entry.getBlue();
        this.alpha = entry.getAlpha();

        for (final String input : entry.getInputs()) {
            this.inputs.addAll(getStacksFromString(input));
        }
        final List<ItemStack> outputs = getStacksFromString(entry.getOutput());
        this.output = outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0);
    }

    public static List<ItemStack> getStacksFromString(String input) {
        final List<ItemStack> items = new ArrayList<>();
        final String[] parts = input.split(":");
        ITagCollectionSupplier tagCollection = TagCollectionManager.getManager();
        if (parts.length > 0) {
            if(parts[0].equalsIgnoreCase("tag") && parts.length == 3) {
                final ResourceLocation tagLocation = new ResourceLocation(parts[1], parts[2]);
                Tag<Item> tagContents = (Tag<Item>) tagCollection.getItemTags().get(tagLocation);
                if(tagContents != null) {
                    List<Item> itemList = tagContents.getAllElements();
                    if(!itemList.isEmpty()) {
                        for(Item item : itemList) {
                            items.add(new ItemStack(item));
                        }
                    }
                }
            } else if (parts.length > 1) {
                final ResourceLocation itemLocation = new ResourceLocation(parts[0], parts[1]);
                final int amount = parts.length > 2 ? Integer.parseInt(parts[2]) : 1;
                Item item = ForgeRegistries.ITEMS.getValue(itemLocation);
                if(item != null) {
                    items.add(new ItemStack(item, amount));
                }
            }
        }

        return items;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<ItemStack> getInputs() {
        return inputs;
    }

    public ItemStack getOutput() {
        return output;
    }

    public float getChance() {
        return chance;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public String getHex() {
        return hex;
    }

    public boolean isValid () {
        boolean foundErrors = false;
        if (this.inputs.isEmpty()) {
            foundErrors = true;
            ResourcePandas.LOGGER.error(this.id + " errored. No valid inputs.");
        }

        if (this.output == null || this.output.isEmpty()) {
            foundErrors = true;
            ResourcePandas.LOGGER.error(this.id + " errored. No valid output.");
        }

        if (this.hex.isEmpty()) {
            foundErrors = true;
            ResourcePandas.LOGGER.error(this.id + " errored. No valid hex.");
        }

        return foundErrors;
    }
}
