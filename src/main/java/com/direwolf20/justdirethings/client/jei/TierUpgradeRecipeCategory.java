package com.direwolf20.justdirethings.client.jei;

import com.direwolf20.justdirethings.datagen.recipes.TierUpgradeRecipe;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;

public class TierUpgradeRecipeCategory implements ISmithingCategoryExtension<TierUpgradeRecipe> {

	@Override
	public <T extends IIngredientAcceptor<T>> void setTemplate(TierUpgradeRecipe recipe, T ingredientAcceptor) {
		ingredientAcceptor.addIngredients(recipe.getTemplate());
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setBase(TierUpgradeRecipe recipe, T ingredientAcceptor) {
		ingredientAcceptor.addIngredients(recipe.getBase());
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setAddition(TierUpgradeRecipe recipe, T ingredientAcceptor) {
		ingredientAcceptor.addIngredients(recipe.getAddition());
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setOutput(TierUpgradeRecipe recipe, T ingredientAcceptor) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null)
			return;
		RegistryAccess registryAccess = level.registryAccess();
		ItemStack result = recipe.getResultItem(registryAccess);
		if (!result.isEmpty()) {
			ingredientAcceptor.addItemStack(result);
		}
	}
}
