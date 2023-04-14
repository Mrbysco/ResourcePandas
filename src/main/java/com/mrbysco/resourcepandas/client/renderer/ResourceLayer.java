package com.mrbysco.resourcepandas.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class ResourceLayer<T extends ResourcePandaEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
	private final ResourceLocation overlayLocation;

	public ResourceLayer(RenderLayerParent<T, M> entityRendererIn, ResourceLocation overlay) {
		super(entityRendererIn);
		this.overlayLocation = overlay;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLightIn, T resourcePanda, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (resourcePanda.isTransformed() && resourcePanda.hasResourceVariant()) {
			EntityModel<T> entityModel = this.getParentModel();
			entityModel.prepareMobModel(resourcePanda, limbSwing, limbSwingAmount, partialTicks);
			this.getParentModel().copyPropertiesTo(entityModel);
			VertexConsumer ivertexbuilder = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.overlayLocation));
			entityModel.setupAnim(resourcePanda, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			String hexColor = resourcePanda.getHexColor();

			float red;
			float green;
			float blue;
			if (resourcePanda.hasCustomName() && "jeb_".equals(resourcePanda.getName().getString())) {
				int i = resourcePanda.tickCount / 25 + resourcePanda.getId();
				int j = DyeColor.values().length;
				int k = i % j;
				int l = (i + 1) % j;
				float f3 = ((float) (resourcePanda.tickCount % 25) + partialTicks) / 25.0F;
				float[] afloat1 = Sheep.getColorArray(DyeColor.byId(k));
				float[] afloat2 = Sheep.getColorArray(DyeColor.byId(l));
				red = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
				green = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
				blue = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
			} else {
				red = getRed(hexColor);
				green = getGreen(hexColor);
				blue = getBlue(hexColor);
			}
			entityModel.renderToBuffer(poseStack, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, red, green, blue, resourcePanda.getAlpha());
		}
	}


	public float getRed(String hex) {
		return hex.isEmpty() ? 0.0F : (float) Integer.valueOf(hex.substring(1, 3), 16) / 255F;
	}

	public float getGreen(String hex) {
		return hex.isEmpty() ? 0.0F : (float) Integer.valueOf(hex.substring(3, 5), 16) / 255F;
	}

	public float getBlue(String hex) {
		return hex.isEmpty() ? 0.0F : (float) Integer.valueOf(hex.substring(5, 7), 16) / 255F;
	}
}
