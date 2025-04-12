//package com.mrbysco.resourcepandas.compat.jei.panda;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mrbysco.resourcepandas.Reference;
//import com.mrbysco.resourcepandas.client.ClientHelper;
//import com.mrbysco.resourcepandas.compat.jei.JEIPlugin;
//import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
//import com.mrbysco.resourcepandas.recipe.PandaRecipe;
//import com.mrbysco.resourcepandas.util.RenderHelper;
//import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
//import mezz.jei.api.gui.builder.ITooltipBuilder;
//import mezz.jei.api.gui.drawable.IDrawable;
//import mezz.jei.api.gui.drawable.IDrawableStatic;
//import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
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
//import net.minecraft.world.item.crafting.RecipeHolder;
//import org.jetbrains.annotations.NotNull;
//
//public class PandaCategory implements IRecipeCategory<RecipeHolder<PandaRecipe>> {
//	protected static final int X_FIRST_ITEM = 0;
//	protected static final int X_OUTPUT_ITEM = 105;
//	protected static final int Y_ITEM_DISTANCE = 23;
//	private final IDrawableStatic background;
//	private final IDrawableStatic icon;
//
//	public PandaCategory(IGuiHelper guiHelper) {
//		ResourceLocation location = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/pandas.png");
//		this.background = guiHelper.drawableBuilder(location, 0, 0, 72, 62).addPadding(1, 0, 0, 50).build();
//
//		ResourceLocation iconLocation = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "textures/gui/panda_icon.png");
//		this.icon = guiHelper.createDrawable(iconLocation, 0, 0, 16, 16);
//	}
//
//	@Override
//	public RecipeType<RecipeHolder<PandaRecipe>> getRecipeType() {
//		return JEIPlugin.PANDA_RECIPE_TYPE;
//	}
//
//	@Override
//	public Component getTitle() {
//		return Component.translatable("resourcepandas.pandas.title");
//	}
//
//	@Override
//	public int getWidth() {
//		return 72;
//	}
//
//	@Override
//	public int getHeight() {
//		return 62;
//	}
//
//	@Override
//	public IDrawable getIcon() {
//		return icon;
//	}
//
//	@Override
//	public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<PandaRecipe> recipeHolder, IFocusGroup focuses) {
//		PandaRecipe recipe = recipeHolder.value();
//		Minecraft minecraft = Minecraft.getInstance();
//		ClientLevel level = minecraft.level;
//		if (level == null) {
//			throw new NullPointerException("level must not be null.");
//		}
//		RegistryAccess registryAccess = level.registryAccess();
//
//		builder.addSlot(RecipeIngredientRole.INPUT, X_FIRST_ITEM, Y_ITEM_DISTANCE)
//				.addIngredients(recipe.getIngredients().getFirst());
//		builder.addSlot(RecipeIngredientRole.OUTPUT, X_OUTPUT_ITEM, Y_ITEM_DISTANCE)
//				.addItemStack(recipe.getResultItem(registryAccess))
//				.addRichTooltipCallback(new OutputTooltip(recipe));
//	}
//
//	@Override
//	public void draw(RecipeHolder<PandaRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
//		this.background.draw(guiGraphics);
//		PandaRecipe recipe = recipeHolder.value();
//
//		// Draw entity
//		ResourcePandaEntity resourcePanda = ClientHelper.getResourcePanda(recipeHolder);
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
//	public static class OutputTooltip implements IRecipeSlotRichTooltipCallback {
//		private final PandaRecipe recipe;
//
//		public OutputTooltip(PandaRecipe recipe) {
//			this.recipe = recipe;
//		}
//
//		@Override
//		public void onRichTooltip(@NotNull IRecipeSlotView recipeSlotView, ITooltipBuilder tooltip) {
//			tooltip.add(Component.translatable("resourcepandas.gui.jei.pandas.tooltip", (int) (100 * recipe.getChance())).withStyle(ChatFormatting.YELLOW));
//		}
//	}
//}
