package com.mrbysco.resourcepandas.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mrbysco.resourcepandas.client.state.ResourcePandaRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;

public class ResourceLayer<S extends PandaRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
	private final ResourceLocation overlayLocation;
	private final PandaModel model;

	public ResourceLayer(RenderLayerParent<S, M> entityRendererIn, ResourceLocation overlay, EntityModelSet modelSet) {
		super(entityRendererIn);
		this.model = new PandaModel(modelSet.bakeLayer(ModelLayers.PANDA));
		this.overlayLocation = overlay;
	}

	@Override
	public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, S renderState, float yRot, float xRot) {
		if (renderState instanceof ResourcePandaRenderState resourceState) {
			VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.overlayLocation));
			this.model.setupAnim(renderState);

			int color;
			int alpha = resourceState.alpha;
			if (renderState.customName != null && "jeb_".equals(renderState.customName.getString())) {
				int k = Mth.floor(renderState.ageInTicks);
				int l = k / 25 + resourceState.id;
				int i1 = DyeColor.values().length;
				int j1 = l % i1;
				int k1 = (l + 1) % i1;
				float f = ((float)(k % 25) + Mth.frac(renderState.ageInTicks)) / 25.0F;
				int l1 = Sheep.getColor(DyeColor.byId(j1));
				int i2 = Sheep.getColor(DyeColor.byId(k1));
				color = ARGB.lerp(f, l1, i2);
			} else {
				color = color(resourceState.hexColor);
			}

			this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), ARGB.color(alpha, color));
		}
	}

	public int color(String hex) {
		int red = Integer.valueOf(hex.substring(1, 3), 16);
		int green = Integer.valueOf(hex.substring(3, 5), 16);
		int blue = Integer.valueOf(hex.substring(5, 7), 16);
		return ARGB.color(red, green, blue);
	}
}
