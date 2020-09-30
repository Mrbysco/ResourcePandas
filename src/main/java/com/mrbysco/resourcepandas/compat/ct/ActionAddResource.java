package com.mrbysco.resourcepandas.compat.ct;

import com.blamejared.crafttweaker.api.actions.IUndoableAction;
import com.mrbysco.resourcepandas.resource.ResourceEntry;
import com.mrbysco.resourcepandas.resource.ResourceRegistry;
import com.mrbysco.resourcepandas.resource.ResourceStorage;

public class ActionAddResource implements IUndoableAction {
    public final ResourceEntry entry;

    public ActionAddResource(CTPandaResource data) {
        this.entry = data.getInternal();
    }

    @Override
    public void apply() {
        if(!ResourceRegistry.RESOURCE_STORAGE.containsKey(entry.getId())) {
            ResourceRegistry.RESOURCE_STORAGE.put(entry.getId(), new ResourceStorage(entry));
        }
    }

    @Override
    public String describe() {
        if(ResourceRegistry.RESOURCE_STORAGE.containsKey(entry.getId())) {
            return String.format("A Resource Panda already exists with the ID %s", entry.getId());
        }
        return String.format("Added Resource Panda entry with ID %s", entry.getId());
    }

    @Override
    public void undo() {
        ResourceRegistry.RESOURCE_STORAGE.remove(entry.getId(), new ResourceStorage(entry));
    }

    @Override
    public String describeUndo() {
        return String.format("Removing Resource Panda entry with id %s again", entry.getId());
    }
}
