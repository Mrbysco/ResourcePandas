package com.mrbysco.resourcepandas.compat.jei.panda;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrbysco.resourcepandas.Reference;
import com.mrbysco.resourcepandas.compat.jei.JEIPlugin;
import com.mrbysco.resourcepandas.entity.ResourcePandaEntity;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import com.mrbysco.resourcepandas.util.RenderHelper;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

public class PandaCategory implements IRecipeCategory<PandaRecipe> {
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
		return JEIPlugin.PANDAS;
	}

	@Override
	public Class<? extends PandaRecipe> getRecipeClass() {
		return PandaRecipe.class;
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
	public void setIngredients(PandaRecipe recipe, IIngredients ingredients) {
		ingredients.setInputIngredients(recipe.getIngredients());
		ingredients.setOutput(VanillaTypes.ITEM,recipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PandaRecipe recipe, IIngredients ingredients) {
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

		guiItemStacks.init(0, true, X_FIRST_ITEM, Y_ITEM_DISTANCE);
		guiItemStacks.init(1, false, X_OUTPUT_ITEM, Y_ITEM_DISTANCE);
		guiItemStacks.set(ingredients);

		recipeLayout.getItemStacks().addTooltipCallback(new ITooltipCallback<ItemStack>() {
			@OnlyIn(Dist.CLIENT)
			@Override
			public void onTooltip(int slot, boolean input, ItemStack stack, List<ITextComponent> list) {
				if(!input && slot == 1) {
					list.add(new StringTextComponent((int)(100 * recipe.getChance()) + "")
							.append(new TranslationTextComponent("resourcepandas.gui.jei.pandas.tooltip")).withStyle(TextFormatting.YELLOW));
				}
			}
		});
	}

	@Override
	public void draw(PandaRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
		// Draw Drops
		this.slotDrawable.draw(matrixStack, X_FIRST_ITEM, Y_ITEM_DISTANCE);
		this.slotDrawable.draw(matrixStack, X_OUTPUT_ITEM, Y_ITEM_DISTANCE);

		// Draw entity
		ResourcePandaEntity resourcePanda = recipe.getResourcePanda();
		if(resourcePanda != null) {
			RenderHelper.renderEntity(matrixStack, 60, 52, 20.0F,
					38 - mouseX,
					80 - mouseY,
					resourcePanda);
		}

		// Draw entity name
		matrixStack.pushPose();
		matrixStack.translate(1, 0, 0);
		FontRenderer font = Minecraft.getInstance().font;
		String text = recipe.getName();
		if(font.width(text) > 122) {
			matrixStack.scale(0.75F, 0.75F, 0.75F);
		}
		font.draw(matrixStack, text, 0, 0, 8);
		matrixStack.popPose();
	}
}
