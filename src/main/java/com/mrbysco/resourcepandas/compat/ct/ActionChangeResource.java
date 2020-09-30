package com.mrbysco.resourcepandas.compat.ct;

import com.blamejared.crafttweaker.api.actions.IRuntimeAction;
import com.mrbysco.resourcepandas.resource.ResourceEntry;
import com.mrbysco.resourcepandas.resource.ResourceRegistry;
import com.mrbysco.resourcepandas.resource.ResourceStorage;

public class ActionChangeResource implements IRuntimeAction {
    public final ResourceEntry entry;

    public ActionChangeResource(CTPandaResource data) {
        this.entry = data.getInternal();
    }

    @Override
    public void apply() {
        if(ResourceRegistry.RESOURCE_STORAGE.containsKey(entry.getId())) {
            ResourceRegistry.RESOURCE_STORAGE.put(entry.getId(), new ResourceStorage(entry));
        }
    }

    @Override
    public String describe() {
        if(!ResourceRegistry.RESOURCE_STORAGE.containsKey(entry.getId())) {
            return String.format("Failed to change. A Resource Panda doesn't exists with the ID %s", entry.getId());
        }
        return String.format("Changed Resource Panda entry with ID %s", entry.getId());
    }
}
