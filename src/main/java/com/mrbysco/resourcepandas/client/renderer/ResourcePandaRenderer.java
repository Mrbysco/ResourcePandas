package com.mrbysco.resourcepandas.client.renderer;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.ResourceLocation;

public class ResourcePandaRenderer extends PandaRenderer {
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/panda/resource_overlay.png");
    public ResourcePandaRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.addLayer(new ResourceLayer(this, OVERLAY_TEXTURE));
    }

    @Override
    protected boolean isShaking(PandaEntity panda) {
        return !((ResourcePandaEntity)panda).isTransformed();
    }
}
