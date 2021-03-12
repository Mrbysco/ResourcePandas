package com.mrbysco.resourcepandas.compat.ct;


import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.mrbysco.resourcepandas.resource.ResourceEntry;
import org.openzen.zencode.java.ZenCodeType.Constructor;
import org.openzen.zencode.java.ZenCodeType.Name;

@ZenRegister
@Name("mods.resourcepandas.PandaResource")
public class CTPandaResource {
    private final ResourceEntry internal;

    public CTPandaResource(ResourceEntry data) {
        this.internal = data;
    }

    @Constructor
    public CTPandaResource(String uniqueID, String[] inputs, String output, String hexColor, float chance) {
        this(new ResourceEntry(uniqueID.toLowerCase(), inputs, output, hexColor, chance));
    }

    @Constructor
    public CTPandaResource(String uniqueID, String name, String[] inputs, String output, String hexColor, float chance) {
        this(new ResourceEntry(uniqueID.toLowerCase(), name, inputs, output, hexColor, chance));
    }

    @Constructor
    public CTPandaResource(String uniqueID, String input, String output, String hexColor, float chance) {
        this(new ResourceEntry(uniqueID.toLowerCase(), new String[]{input}, output, hexColor, chance));
    }

    @Constructor
    public CTPandaResource(String uniqueID, String name, String input, String output, String hexColor, float chance) {
        this(new ResourceEntry(uniqueID.toLowerCase(), name, new String[]{input}, output, hexColor, chance));
    }

    public ResourceEntry getInternal() {
        return this.internal;
    }
}
