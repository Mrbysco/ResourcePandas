package com.mrbysco.resourcepandas.compat.ct;

import com.blamejared.crafttweaker.api.actions.IUndoableAction;
import com.mrbysco.resourcepandas.resource.ResourceRegistry;
import com.mrbysco.resourcepandas.resource.ResourceStorage;

public class ActionRemoveResource implements IUndoableAction {
    public final String entryID;
    public final ResourceStorage storage;

    public ActionRemoveResource(String id) {
        this.entryID = id;
        this.storage = ResourceRegistry.getType(id);
    }

    @Override
    public void apply() {
        if(storage.getId() != ResourceRegistry.MISSING.getId() && ResourceRegistry.RESOURCE_STORAGE.containsKey(entryID)) {
            ResourceRegistry.RESOURCE_STORAGE.remove(entryID);
        }
    }

    @Override
    public String describe() {
        if(!ResourceRegistry.RESOURCE_STORAGE.containsKey(storage.getId())) {
            return String.format("A Resource Panda doesn't exists with the ID %s", entryID);
        }
        return String.format("Removed Resource Panda entry with ID %s", entryID);
    }

    @Override
    public void undo() {
        if(!ResourceRegistry.RESOURCE_STORAGE.containsKey(entryID)) {
            ResourceRegistry.RESOURCE_STORAGE.put(entryID, storage);
        }
    }

    @Override
    public String describeUndo() {
        return String.format("Adding Resource Panda entry with id %s back", entryID);
    }
}
