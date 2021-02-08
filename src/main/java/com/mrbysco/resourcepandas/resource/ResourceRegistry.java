package com.mrbysco.resourcepandas.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mrbysco.resourcepandas.ResourcePandas;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ResourceRegistry {
    private static final Random rand = new Random();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final File JSON_DIR = new File(FMLPaths.CONFIGDIR.get().toFile() + "/resourcepandas");

    public static final Map<String, ResourceEntry> RESOURCE_ENTRIES = new HashMap<>();
    public static final Map<String, ResourceStorage> RESOURCE_STORAGE = new HashMap<>();

    public static final ResourceStorage MISSING = new ResourceStorage(new ResourceEntry("missing", new String[]{"minecraft:egg"}, "minecraft:egg", "#ffd79a", 2.0F));

    public static void constructEntries() {
        RESOURCE_STORAGE.clear();
        for (final ResourceEntry entry : RESOURCE_ENTRIES.values()) {
            try {
                final ResourceStorage storage = new ResourceStorage(entry);
                if (!storage.isValid()) {
                    RESOURCE_STORAGE.put(storage.getId(), storage);
                }
            } catch (final Exception e) {
                ResourcePandas.LOGGER.error("Failed to load resource panda entry {}.", entry.getId());
                ResourcePandas.LOGGER.catching(e);
            }
        }
    }

    public static void loadResourceEntries() {
        if (!JSON_DIR.exists()) {
            JSON_DIR.mkdirs();

            List<ResourceEntry> entries = new ArrayList<>();
            entries.add(new ResourceEntry("iron_panda", "tag:forge:storage_blocks/iron", "tag:forge:nuggets/iron", "#d9dfe7", 0.6F));
            entries.add(new ResourceEntry("gold_panda", "tag:forge:storage_blocks/gold", "tag:forge:nuggets/gold", "#f8d26a", 0.5F));
            entries.add(new ResourceEntry("redstone_panda", "tag:forge:storage_blocks/redstone", "tag:forge:dusts/redstone", "#aa0f01", 0.6F));
            entries.add(new ResourceEntry("lapis_panda", "tag:forge:storage_blocks/lapis", "tag:forge:gems/lapis", "#345ec3", 0.6F));
            entries.add(new ResourceEntry("quartz_panda", "tag:forge:storage_blocks/quartz", "tag:forge:gems/quartz", "#ddd4c6", 0.6F));
            entries.add(new ResourceEntry("coal_panda", "tag:forge:storage_blocks/coal", "minecraft:coal", "#363636", 0.6F));
            entries.add(new ResourceEntry("diamond_panda", "tag:forge:storage_blocks/diamond", "tag:forge:gems/diamond", "#a1fbe8", 0.11F));
            entries.add(new ResourceEntry("emerald_panda", "tag:forge:storage_blocks/emerald", "tag:forge:gems/emerald", "#17dd62", 0.08F));
            entries.add(new ResourceEntry("netherite_panda", "tag:forge:storage_blocks/netherite", "minecraft:netherite_scrap", "#4c4143", 0.06F));

            for(ResourceEntry entry : entries) {
                try(FileWriter writer = new FileWriter(new File(JSON_DIR, entry.getId() + ".json"))) {
                    GSON.toJson(entry, writer);
                    writer.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        RESOURCE_ENTRIES.clear();
        for (final File file : JSON_DIR.listFiles()) {
            final String fileName = file.getName();
            if (fileName.endsWith(".json")) {
                try (FileReader json = new FileReader(file)) {
                    final ResourceEntry entry = GSON.fromJson(json, ResourceEntry.class);
                    if (entry != null) {
                        RESOURCE_ENTRIES.put(entry.getId(), entry);
                    } else {
                        ResourcePandas.LOGGER.error("Could not load resource panda entry from {}.", fileName);
                    }
                } catch (final Exception e) {
                    ResourcePandas.LOGGER.error("Unable to load file {}. Please make sure it's a valid json.", fileName);
                    ResourcePandas.LOGGER.catching(e);
                }
            } else {
                ResourcePandas.LOGGER.error("Found invalid file {} in the resourcepandas config folder. It must be a .json file!", fileName);
            }
        }
    }

    public static ResourceStorage getRandomType() {
        final List<ResourceStorage> valuesList = new ArrayList<>(RESOURCE_STORAGE.values());
        return valuesList.get(rand.nextInt(valuesList.size()));
    }

    public static ResourceStorage getStorageForItem(ItemStack stack) {
        for(ResourceStorage storage : RESOURCE_STORAGE.values()) {
            for(ItemStack inputStack : storage.getInputs()) {
                if(inputStack.isItemEqual(stack)) {
                    return storage;
                }
            }
        }
        return null;
    }

    public static ResourceStorage getType(String name) {
        return RESOURCE_STORAGE.getOrDefault(name, MISSING);
    }
}
