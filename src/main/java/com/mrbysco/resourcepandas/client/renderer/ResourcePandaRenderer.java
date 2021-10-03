package com.mrbysco.resourcepandas.client.renderer;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PandaRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Panda;

public class ResourcePandaRenderer extends PandaRenderer {
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/panda/resource_overlay.png");
    public ResourcePandaRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.addLayer(new ResourceLayer(this, OVERLAY_TEXTURE));
    }

    @Override
    protected boolean isShaking(Panda panda) {
        return !((ResourcePandaEntity)panda).isTransformed();
    }
}
