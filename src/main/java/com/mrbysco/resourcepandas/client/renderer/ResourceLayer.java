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
		if (renderState instanceof ResourcePandaRenderState resourceState && resourceState.isConverted) {
			VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(this.overlayLocation));
			this.model.setupAnim(renderState);

			int color = resourceState.getColor();
			int alpha = resourceState.alpha;
			this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, LivingEntityRenderer.getOverlayCoords(renderState, 0.0F), ARGB.color(alpha, color));
		}
	}
}
