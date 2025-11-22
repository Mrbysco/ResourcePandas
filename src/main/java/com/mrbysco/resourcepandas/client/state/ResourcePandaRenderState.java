package com.mrbysco.resourcepandas.client.state;

import net.minecraft.client.color.ColorLerper;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.util.ARGB;

public class ResourcePandaRenderState extends PandaRenderState {
	public boolean isConverted;
	public boolean isConverting;
	public boolean hasResourceVariant;
	public int id;
	public int alpha;
	public String hexColor;

	public int getColor() {
		return this.isJebSheep() ? ColorLerper.getLerpedColor(ColorLerper.Type.SHEEP, this.ageInTicks) : getNormalColor();
	}

	public boolean isJebSheep() {
		return this.nameTag != null && "jeb_".equals(this.nameTag.getString());
	}

	public int getNormalColor() {
		int red = Integer.valueOf(hexColor.substring(1, 3), 16);
		int green = Integer.valueOf(hexColor.substring(3, 5), 16);
		int blue = Integer.valueOf(hexColor.substring(5, 7), 16);
		return ARGB.color(red, green, blue);
	}
}
