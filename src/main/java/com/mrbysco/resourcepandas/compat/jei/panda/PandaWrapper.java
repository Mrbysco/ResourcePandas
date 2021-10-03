package com.mrbysco.resourcepandas.compat.jei.panda;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.resource.ResourceStorage;
import com.mrbysco.resourcepandas.util.RenderHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import java.util.List;

public class PandaWrapper implements IRecipeCategoryExtension {
	private final ResourceStorage entry;

	public PandaWrapper(ResourceStorage entry) {
		this.entry = entry;
	}

	public String getResourceName() {
		return entry.getName();
	}

	public List<Ingredient> getInputs() {
		return this.entry.getInputs();
	}

	public ItemStack getOutput() {
		return this.entry.getOutput();
	}

	@Override
	public void setIngredients(IIngredients ingredients) {
		ingredients.setInputIngredients(getInputs());
		ingredients.setOutput(VanillaTypes.ITEM, getOutput());
	}

	@Override
	public void drawInfo(int recipeWidth, int recipeHeight, MatrixStack matrixStack, double mouseX, double mouseY) {
		ResourcePandaEntity resourcePanda = this.entry.getResourcePanda();
		if(resourcePanda != null) {
			RenderHelper.renderEntity(matrixStack, 60, 52, 20.0F,
					38 - mouseX,
					80 - mouseY,
					resourcePanda);
		}
	}
}
