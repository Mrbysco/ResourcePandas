package com.mrbysco.resourcepandas.util;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3x2fStack;

public class RenderHelper {
	public static void renderEntity(GuiGraphicsExtractor guiGraphics, int x, int y, double scale, double mouseX, double mouseY, LivingEntity livingEntity) {
		if (livingEntity.level() == null) return;

		int startX = x - 50;
		int startY = y - 50;
		int endX = x + 50;
		int endY = y + 50;

		Matrix3x2fStack poseStack = guiGraphics.pose();
		poseStack.pushMatrix();
		poseStack.scale((float) scale, (float) scale);

		poseStack.translate(0.0F, (float) livingEntity.getVehicleAttachmentPoint(livingEntity).y());
		double mouseScale = scale / 4;
		int adjustedMouseX = Mth.ceil(mouseX * mouseScale);
		int adjustedMouseY = Mth.ceil(mouseY * mouseScale);

		InventoryScreen.extractEntityInInventoryFollowsMouse(
				guiGraphics, startX, startY, endX, endY, (int) 20, -1, adjustedMouseX, adjustedMouseY, livingEntity);

		poseStack.popMatrix();
	}
}
