package com.mrbysco.resourcepandas.compat.jei.panda;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.ResourcePandas;
import com.mrbysco.resourcepandas.registry.PandaRegistry;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class PandaCategory<T extends IRecipeCategoryExtension> implements IRecipeCategory<PandaWrapper> {
	public static final ResourceLocation UID = new ResourceLocation(Reference.MOD_ID, "pandas");

	protected static final int X_FIRST_ITEM = 0;
	protected static final int X_OUTPUT_ITEM = 104;
	protected static final int Y_ITEM_DISTANCE = 22;
	private final IDrawableStatic background;
	private final IDrawableStatic icon;
	private final IDrawableStatic slotDrawable;

	public PandaCategory(IGuiHelper guiHelper) {
		ResourceLocation location = new ResourceLocation(Reference.MOD_ID, "textures/gui/pandas.png");
		this.background = guiHelper.drawableBuilder(location, 0, 0, 72, 62).addPadding(1, 0, 0, 50).build();

		ResourceLocation iconLocation = new ResourceLocation(Reference.MOD_ID, "textures/gui/panda_icon.png");
		this.icon = guiHelper.createDrawable(iconLocation, 0, 0, 16, 16);

		this.slotDrawable = guiHelper.getSlotDrawable();
	}

	@Override
	public ResourceLocation getUid() {
		return this.UID;
	}

	@Override
	public Class<? extends PandaWrapper> getRecipeClass() {
		return PandaWrapper.class;
	}

	@Override
	public String getTitle() {
		return I18n.get("resourcepandas.pandas.title");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setIngredients(PandaWrapper recipe, IIngredients ingredients) {
		recipe.setIngredients(ingredients);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PandaWrapper recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, X_FIRST_ITEM, Y_ITEM_DISTANCE);
		guiItemStacks.set(ingredients);
//		guiItemStacks.set(0, recipe.getInputs());
		guiItemStacks.init(1, false, X_OUTPUT_ITEM, Y_ITEM_DISTANCE);
		if(!recipe.getOutput().isEmpty()) {
			guiItemStacks.set(1, recipe.getOutput());
		} else {
			ResourcePandas.LOGGER.error("Output became empty of " + recipe.getResourceName() + ", Please report to the mod author");
		}
	}

	@Override
	public void draw(PandaWrapper recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		// Draw Drops
		this.slotDrawable.draw(matrixStack, X_FIRST_ITEM, Y_ITEM_DISTANCE);
		this.slotDrawable.draw(matrixStack, X_OUTPUT_ITEM, Y_ITEM_DISTANCE);

		// Draw entity
		recipe.drawInfo(getBackground().getWidth(), getBackground().getHeight(), matrixStack, mouseX, mouseY);
		// Draw entity name
		matrixStack.pushPose();
		matrixStack.translate(1, 0, 0);
		FontRenderer font = Minecraft.getInstance().font;
		String text = recipe.getResourceName();
		if(font.width(text) > 122) {
			matrixStack.scale(0.75F, 0.75F, 0.75F);
		}
		font.draw(matrixStack, text, 0, 0, 8);
		matrixStack.popPose();
	}
}
