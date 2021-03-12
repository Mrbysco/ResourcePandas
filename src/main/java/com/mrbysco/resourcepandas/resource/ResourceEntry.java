package com.mrbysco.resourcepandas.resource;

import com.google.gson.annotations.Expose;

public class ResourceEntry {
    @Expose
    private String id = "id";

    @Expose
    private String name = "name";

    @Expose
    private String[] inputs = new String[] { "mod:item:amount", "tag:tagname" };

    @Expose
    private String output = "mod:item:amount";

    @Expose
    private String hexColor = "#ffffff";

    @Expose
    private float chance = 1.0F;

    @Expose
    private float alpha = 1.0F;

    public ResourceEntry(String id, String name, String[] input, String output, String hex, float chance) {
        this.id = id;
        this.name = name;
        this.inputs = input;
        this.output = output;
        this.hexColor = hex;
        this.chance = chance;
    }

    public ResourceEntry(String id, String name, String input, String output, String hex, float chance) {
        this.id = id;
        this.name = name;
        this.inputs = new String[] {input};
        this.output = output;
        this.hexColor = hex;
        this.chance = chance;
    }

    public ResourceEntry(String id, String[] input, String output, String hex, float chance) {
        this.id = id;
        this.name = id;
        this.inputs = input;
        this.output = output;
        this.hexColor = hex;
        this.chance = chance;
    }

    public ResourceEntry(String id, String input, String output, String hex, float chance) {
        this.id = id;
        this.name = id;
        this.inputs = new String[] {input};
        this.output = output;
        this.hexColor = hex;
        this.chance = chance;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getInputs() {
        return inputs;
    }

    public void setInputs(String[] inputs) {
        this.inputs = inputs;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public float getRed() {
        return getHexColor().isEmpty() ? 0.0F : (float)Integer.valueOf(getHexColor().substring(1, 3), 16) / 255F;
    }

    public float getGreen() {
        return getHexColor().isEmpty() ? 0.0F : (float)Integer.valueOf(getHexColor().substring(3, 5), 16) / 255F;
    }

    public float getBlue() {
        return getHexColor().isEmpty() ? 0.0F : (float)Integer.valueOf(getHexColor().substring(5, 7), 16) / 255F;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getChance() {
        return chance;
    }

    public void setChance(float chance) {
        this.chance = chance;
    }
}
