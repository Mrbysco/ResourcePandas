package com.mrbysco.resourcepandas.compat.jei.panda;

import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.client.ClientHelper;
import com.mrbysco.resourcepandas.compat.jei.JEIPlugin;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.util.RenderHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.joml.Vector2f;

public class PandaCategory implements IRecipeCategory<RecipeHolder<PandaRecipe>> {
	protected static final int X_FIRST_ITEM = 0;
	protected static final int X_OUTPUT_ITEM = 96;
	protected static final int Y_ITEM_DISTANCE = 13;
	private final IDrawableStatic icon;

	public PandaCategory(IGuiHelper guiHelper) {
		Identifier iconLocation = Identifier.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/panda_icon.png");
		this.icon = guiHelper.createDrawable(iconLocation, 0, 0, 16, 16);
	}

	@Override
	public IRecipeType<RecipeHolder<PandaRecipe>> getRecipeType() {
		return JEIPlugin.PANDA_RECIPE_TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("resourcepandas.pandas.title");
	}

	@Override
	public int getWidth() {
		return 112;
	}

	@Override
	public int getHeight() {
		return 40;
	}

	@Override
	public IDrawable getIcon() {
		return icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<PandaRecipe> recipeHolder, IFocusGroup focuses) {
		PandaRecipe recipe = recipeHolder.value();

		builder.addSlot(RecipeIngredientRole.INPUT, X_FIRST_ITEM, Y_ITEM_DISTANCE)
				.add(recipe.getIngredient())
				.setStandardSlotBackground();
		builder.addSlot(RecipeIngredientRole.OUTPUT, X_OUTPUT_ITEM, Y_ITEM_DISTANCE)
				.add(recipe.getResult())
				.addRichTooltipCallback(new OutputTooltip(recipe))
				.setStandardSlotBackground();
	}

	@Override
	public void draw(RecipeHolder<PandaRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
		PandaRecipe recipe = recipeHolder.value();

		final Matrix3x2fStack poseStack = guiGraphics.pose();

		// Draw entity
		ResourcePandaEntity resourcePanda = ClientHelper.getResourcePanda(recipeHolder.id().identifier());
		if (resourcePanda != null) {
			Vector2f position = new Vector2f(56, 36);
			position = poseStack.transformPosition(position);
			int x = Math.round(position.x);
			int y = Math.round(position.y);
			RenderHelper.renderEntity(guiGraphics, x, y, 25.0F,
					mouseX + 12,
					mouseY - 12,
					resourcePanda);
		}

		// Draw entity name
		poseStack.pushMatrix();
		poseStack.translate(1, 0);
		Font font = Minecraft.getInstance().font;
		String text = recipe.getName();
		if (font.width(text) > 122) {
			poseStack.scale(0.75F, 0.75F);
		}
		guiGraphics.text(font, text, 0, 0, 8, false);
		poseStack.popMatrix();
	}

	public static class OutputTooltip implements IRecipeSlotRichTooltipCallback {
		private final PandaRecipe recipe;

		public OutputTooltip(PandaRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void onRichTooltip(@NotNull IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
			tooltip.add(Component.translatable("resourcepandas.gui.jei.pandas.tooltip", Math.round(100 * recipe.getChance())).withStyle(ChatFormatting.YELLOW));
		}
	}
}
