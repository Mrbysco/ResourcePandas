package com.mrbysco.resourcepandas.compat.rei.display;

import com.mrbysco.resourcepandas.compat.rei.REIPlugin;
import com.mrbysco.resourcepandas.recipe.PandaRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class PandaDisplay implements Display {
    private final RecipeHolder<PandaRecipe> recipeHolder;
    private final List<EntryIngredient> inputEntries;
    private final List<EntryIngredient> outputEntries;

    public PandaDisplay(RecipeHolder<PandaRecipe> recipeHolder) {
        this.recipeHolder = recipeHolder;
        this.inputEntries = EntryIngredients.ofIngredients(recipeHolder.value().getIngredients());
        this.outputEntries = List.of(EntryIngredients.of(recipeHolder.value().getResultItem(null).copy()));
    }

    public RecipeHolder<PandaRecipe> getRecipeHolder() {
        return recipeHolder;
    }

    @Override
    public List<EntryIngredient> getInputEntries() {
        return this.inputEntries;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return this.outputEntries;
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return REIPlugin.PANDAS;
    }
}
