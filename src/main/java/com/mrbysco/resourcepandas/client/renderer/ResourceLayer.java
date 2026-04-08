package com.mrbysco.resourcepandas.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrbysco.resourcepandas.client.state.ResourcePandaRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.panda.PandaModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

public class ResourceLayer<S extends PandaRenderState, M extends EntityModel<? super S>> extends RenderLayer<S, M> {
	private final Identifier overlayLocation;
	private final PandaModel model;

	public ResourceLayer(RenderLayerParent<S, M> entityRendererIn, Identifier overlay, EntityModelSet modelSet) {
		super(entityRendererIn);
		this.model = new PandaModel(modelSet.bakeLayer(ModelLayers.PANDA));
		this.overlayLocation = overlay;
	}

	@Override
	public void submit(PoseStack poseStack, SubmitNodeCollector nodeCollector, int packedLight, PandaRenderState renderState, float xRoy, float yRot) {
		if (renderState instanceof ResourcePandaRenderState resourceState && resourceState.isConverted) {
			int color = ARGB.color(resourceState.alpha, resourceState.getColor());
			this.model.setupAnim(renderState);
			if (renderState.isInvisible) {
				if (renderState.appearsGlowing()) {
					nodeCollector.submitModel(
							this.model,
							renderState,
							poseStack,
							RenderTypes.outline(this.overlayLocation),
							packedLight,
							renderState.lightCoords,
							-1,
							null,
							renderState.outlineColor,
							null
					);
				}
			} else {
				coloredCutoutModelCopyLayerRender(this.model, this.overlayLocation, poseStack, nodeCollector, packedLight, renderState, color, 1);
			}
		}
	}
}
