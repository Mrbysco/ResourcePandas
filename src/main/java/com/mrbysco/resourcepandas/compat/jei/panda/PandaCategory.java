//package com.mrbysco.resourcepandas.compat.jei.panda;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mrbysco.resourcepandas.Reference;
//import com.mrbysco.resourcepandas.compat.jei.JEIPlugin;
//import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
//import com.mrbysco.resourcepandas.recipe.PandaRecipe;
//import com.mrbysco.resourcepandas.util.RenderHelper;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.gui.drawable.IDrawableStatic;
//import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
//import mezz.jei.api.gui.ingredient.IRecipeSlotView;
//import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.recipe.IFocusGroup;
//import mezz.jei.api.recipe.RecipeIngredientRole;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import net.minecraft.ChatFormatting;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Font;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.core.RegistryAccess;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//
//import java.util.List;
//
//public class PandaCategory implements IRecipeCategory<PandaRecipe> {
//	protected static final int X_FIRST_ITEM = 0;
//	protected static final int X_OUTPUT_ITEM = 105;
//	protected static final int Y_ITEM_DISTANCE = 23;
//	private final IDrawableStatic background;
//	private final IDrawableStatic icon;
//	private final IDrawableStatic slotDrawable;
//
//	public PandaCategory(IGuiHelper guiHelper) {
//		ResourceLocation location = new ResourceLocation(Reference.MOD_ID, "textures/gui/pandas.png");
//		this.background = guiHelper.drawableBuilder(location, 0, 0, 72, 62).addPadding(1, 0, 0, 50).build();
//
//		ResourceLocation iconLocation = new ResourceLocation(Reference.MOD_ID, "textures/gui/panda_icon.png");
//		this.icon = guiHelper.createDrawable(iconLocation, 0, 0, 16, 16);
//
//		this.slotDrawable = guiHelper.getSlotDrawable();
//	}
//
//	@Override
//	public RecipeType<PandaRecipe> getRecipeType() {
//		return JEIPlugin.PANDA_RECIPE_TYPE;
//	}
//
//	@Override
//	public Component getTitle() {
//		return Component.translatable("resourcepandas.pandas.title");
//	}
//
//	@Override
//	public IDrawable getBackground() {
//		return background;
//	}
//
//	@Override
//	public IDrawable getIcon() {
//		return icon;
//	}
//
//	@Override
//	public void setRecipe(IRecipeLayoutBuilder builder, PandaRecipe recipe, IFocusGroup focuses) {
//		Minecraft minecraft = Minecraft.getInstance();
//		ClientLevel level = minecraft.level;
//		if (level == null) {
//			throw new NullPointerException("level must not be null.");
//		}
//		RegistryAccess registryAccess = level.registryAccess();
//
//		builder.addSlot(RecipeIngredientRole.INPUT, X_FIRST_ITEM, Y_ITEM_DISTANCE).addIngredients(recipe.getIngredients().get(0));
//		builder.addSlot(RecipeIngredientRole.OUTPUT, X_OUTPUT_ITEM, Y_ITEM_DISTANCE).addItemStack(recipe.getResultItem(registryAccess)).addTooltipCallback(new OutputTooltip(recipe));
//	}
//
//	@Override
//	public void draw(PandaRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
//		IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
//
//		// Draw entity
//		ResourcePandaEntity resourcePanda = recipe.getResourcePanda();
//		if (resourcePanda != null) {
//			RenderHelper.renderEntity(guiGraphics, 60, 52, 20.0F, 38 - mouseX, 80 - mouseY, resourcePanda);
//		}
//
//		// Draw entity name
//		final PoseStack poseStack = guiGraphics.pose();
//		poseStack.translate(1, 0, 0);
//		Font font = Minecraft.getInstance().font;
//		String text = recipe.getName();
//		if (font.width(text) > 122) {
//			poseStack.scale(0.75F, 0.75F, 0.75F);
//		}
//		guiGraphics.drawString(font, text, 0, 0, 8, false);
//		poseStack.popPose();
//	}
//
//	public static class OutputTooltip implements IRecipeSlotTooltipCallback {
//		private final PandaRecipe recipe;
//
//		public OutputTooltip(PandaRecipe recipe) {
//			this.recipe = recipe;
//		}
//
//		@Override
//		public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
//			tooltip.add(Component.translatable("resourcepandas.gui.jei.pandas.tooltip", (int) (100 * recipe.getChance())).withStyle(ChatFormatting.YELLOW));
//		}
//	}
//}
