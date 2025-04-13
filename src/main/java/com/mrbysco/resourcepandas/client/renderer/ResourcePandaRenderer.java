package com.mrbysco.resourcepandas.client.renderer;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.client.state.ResourcePandaRenderState;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.client.renderer.entity.state.PandaRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Panda;

public class ResourcePandaRenderer extends PandaRenderer {
	private static final ResourceLocation OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/entity/panda/resource_overlay.png");

	@SuppressWarnings({"rawtypes", "unchecked"})
	public ResourcePandaRenderer(EntityRendererProvider.Context context) {
		super(context);
		this.addLayer(new ResourceLayer(this, OVERLAY_TEXTURE, context.getModelSet()));
	}

	@Override
	public PandaRenderState createRenderState() {
		return new ResourcePandaRenderState();
	}

	@Override
	public void extractRenderState(Panda panda, PandaRenderState renderState, float partialTick) {
		super.extractRenderState(panda, renderState, partialTick);
		if (panda instanceof ResourcePandaEntity resourcePanda && renderState instanceof ResourcePandaRenderState resourceState) {
			resourceState.isConverting = !resourcePanda.isTransformed();
			resourceState.isConverted = resourcePanda.isTransformed();
			resourceState.hasResourceVariant = resourcePanda.hasResourceVariant();
			resourceState.id = resourcePanda.getId();
			resourceState.alpha = (int) (resourcePanda.getAlpha() * 255);
			resourceState.hexColor = resourcePanda.getHexColor();
		}
	}

	@Override
	protected boolean isShaking(PandaRenderState renderState) {
		if (renderState instanceof ResourcePandaRenderState resourceState)
			return renderState.isFullyFrozen || resourceState.isConverting;
		else
			return false;
	}
}
