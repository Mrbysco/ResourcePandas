package com.mrbysco.resourcepandas.compat.ct;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import org.openzen.zencode.java.ZenCodeType.Method;
import org.openzen.zencode.java.ZenCodeType.Name;

@ZenRegister
@Name("mods.resourcepandas.Entries")
public class ResourcePandaCT {
    @Method
    public static void addEntry(CTPandaResource resource) {
        CraftTweakerAPI.apply(new ActionAddResource(resource));
    }

    @Method
    public static void replaceEntry(CTPandaResource resource) {
        CraftTweakerAPI.apply(new ActionChangeResource(resource));
    }

    @Method
    public static void removeEntry(String uniqueID) {
        CraftTweakerAPI.apply(new ActionRemoveResource(uniqueID.toLowerCase()));
    }
}
