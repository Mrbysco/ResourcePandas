package com.mrbysco.resourcepandas.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class RenderHelper {
	public static final Vector3f TRANSLATION = new Vector3f();
	public static final Quaternionf ANGLE = new Quaternionf().rotationXYZ(0.0F, 0.0F, (float) Math.PI);

	public static void renderEntity(GuiGraphics guiGraphics, int x, int y, double scale, double yaw, double pitch, LivingEntity livingEntity) {
		if (livingEntity.level() == null) return;

		int startX = x - 50;
		int startY = y - 50;
		int endX = x + 50;
		int endY = y + 50;

		Matrix3x2fStack poseStack = guiGraphics.pose();
		poseStack.pushMatrix();
		poseStack.scale((float) scale, (float) scale);

		livingEntity.yBodyRot = ((float) -(yaw / 40.F) * 20.0F) + 180F;
		livingEntity.setYRot(((float) -(yaw / 40.F) * 20.0F));
		livingEntity.yHeadRot = livingEntity.getYRot() + 180F;
		livingEntity.yHeadRotO = livingEntity.getYRot() + 180F;
		livingEntity.setXRot((float) -(pitch / 5.F));

		poseStack.translate(0.0F, (float)livingEntity.getVehicleAttachmentPoint(livingEntity).y());

		InventoryScreen.renderEntityInInventory(guiGraphics, startX, startY, endX, endY,
				(float) scale, TRANSLATION, ANGLE, (Quaternionf) null, livingEntity);

		poseStack.popMatrix();
	}
}
