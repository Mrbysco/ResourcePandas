package com.mrbysco.resourcepandas.compat.rei.category;

import com.mrbysco.resourcepandas.client.ClientHelper;
import com.mrbysco.resourcepandas.compat.rei.REIPlugin;
import com.mrbysco.resourcepandas.compat.rei.display.PandaDisplay;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import com.mrbysco.resourcepandas.util.RenderHelper;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class PandaCategory implements DisplayCategory<PandaDisplay> {
	@Override
	public CategoryIdentifier<? extends PandaDisplay> getCategoryIdentifier() {
		return REIPlugin.PANDAS;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("resourcepandas.pandas.title");
	}

	@Override
	public Renderer getIcon() {
		return EntryStacks.of(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get());
	}

	@Override
	public List<Widget> setupDisplay(PandaDisplay display, Rectangle bounds) {
		Point centerPoint = new Point(bounds.getCenterX(), bounds.getCenterY());
		List<Widget> widgets = new ArrayList<>();
		widgets.add(Widgets.createRecipeBase(bounds));
		widgets.add(Widgets.withTranslate(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, v) -> {
			ResourcePandaEntity panda = ClientHelper.getResourcePanda(display.getRecipeHolder());
			if (panda != null) {
				// Draw entity
				RenderHelper.renderEntity(guiGraphics, 46, 52, 20.0F, 38 - mouseX, 80 - mouseY, panda);
			}
		}), bounds.x, bounds.y - 2, 0));

		widgets.add(Widgets.createSlot(new Point(bounds.getMinX() + 5, centerPoint.y - 8)).entries(display.getInputEntries().get(0)).markInput());
		widgets.add(Widgets.createSlot(new Point(bounds.getMaxX() - 21, centerPoint.y - 8)).entries(display.getOutputEntries().get(0)).markOutput());

		return widgets;
	}

	@Override
	public int getDisplayWidth(PandaDisplay display) {
		return 92;
	}

	@Override
	public int getDisplayHeight() {
		return 62;
	}
}
