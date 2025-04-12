//package com.mrbysco.resourcepandas.compat.jei;
//
//import com.mrbysco.resourcepandas.Reference;
//import com.mrbysco.resourcepandas.compat.jei.panda.PandaCategory;
//import com.mrbysco.resourcepandas.recipe.PandaRecipe;
//import com.mrbysco.resourcepandas.recipe.PandaRecipes;
//import com.mrbysco.resourcepandas.registry.PandaRegistry;
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.helpers.IGuiHelper;
//import mezz.jei.api.helpers.IJeiHelpers;
//import mezz.jei.api.recipe.RecipeType;
//import mezz.jei.api.recipe.category.IRecipeCategory;
//import mezz.jei.api.registration.IRecipeCatalystRegistration;
//import mezz.jei.api.registration.IRecipeCategoryRegistration;
//import mezz.jei.api.registration.IRecipeRegistration;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.multiplayer.ClientLevel;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.crafting.Recipe;
//import net.minecraft.world.item.crafting.RecipeHolder;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Objects;
//
//@JeiPlugin
//public class JEIPlugin implements IModPlugin {
//	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(Reference.MOD_ID, "jei_plugin");
//
//	public static final RecipeType<RecipeHolder<PandaRecipe>> PANDA_RECIPE_TYPE = createPandaType();
//
//	@Nullable
//	private IRecipeCategory<RecipeHolder<PandaRecipe>> pandaCategory;
//
//	@Override
//	public ResourceLocation getPluginUid() {
//		return UID;
//	}
//
//	@Override
//	public void registerCategories(IRecipeCategoryRegistration registration) {
//		IJeiHelpers jeiHelpers = registration.getJeiHelpers();
//		IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
//		registration.addRecipeCategories(
//				pandaCategory = new PandaCategory(guiHelper)
//		);
//	}
//
//	@Override
//	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
//		registration.addRecipeCatalyst(new ItemStack(PandaRegistry.RESOURCE_PANDA_SPAWN_EGG.get()), PANDA_RECIPE_TYPE);
//	}
//
//	@Override
//	public void registerRecipes(IRecipeRegistration registration) {
//		ClientLevel world = Objects.requireNonNull(Minecraft.getInstance().level);
//		registration.addRecipes(PANDA_RECIPE_TYPE, world.getRecipeManager().getAllRecipesFor(PandaRecipes.PANDA_RECIPE_TYPE.get()));
//	}
//
//	public static <R extends Recipe<?>> RecipeType<RecipeHolder<R>> createPandaType() {
//		@SuppressWarnings({"unchecked", "RedundantCast"})
//		Class<? extends RecipeHolder<R>> holderClass = (Class<? extends RecipeHolder<R>>) (Object) RecipeHolder.class;
//		return new RecipeType<>(PandaRecipes.PANDA_RECIPE_TYPE.getId(), holderClass);
//	}
//}
