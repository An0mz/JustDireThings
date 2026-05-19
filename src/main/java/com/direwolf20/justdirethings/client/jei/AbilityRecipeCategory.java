package com.direwolf20.justdirethings.client.jei;

import com.direwolf20.justdirethings.datagen.recipes.AbilityRecipe;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class AbilityRecipeCategory implements ISmithingCategoryExtension<AbilityRecipe> {

	@Override
	public <T extends IIngredientAcceptor<T>> void setTemplate(AbilityRecipe recipe, T ingredientAcceptor) {
		ingredientAcceptor.addIngredients(recipe.getTemplate());
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setBase(AbilityRecipe recipe, T ingredientAcceptor) {
		ingredientAcceptor.addIngredients(recipe.getBase());
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setAddition(AbilityRecipe recipe, T ingredientAcceptor) {
		ingredientAcceptor.addIngredients(recipe.getAddition());
	}

	@Override
	public <T extends IIngredientAcceptor<T>> void setOutput(AbilityRecipe recipe, T ingredientAcceptor) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null)
			return;
		RegistryAccess registryAccess = level.registryAccess();
		ItemStack resultItem = recipe.getResultItem(registryAccess);
		if (!resultItem.isEmpty()) {
			ingredientAcceptor.addItemStack(resultItem);
		}
	}

	@Override
	public void onDisplayedIngredientsUpdate(AbilityRecipe recipe, IRecipeSlotDrawable templateSlot,
			IRecipeSlotDrawable baseSlot, IRecipeSlotDrawable additionSlot, IRecipeSlotDrawable outputSlot,
			IFocusGroup focuses) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientLevel level = minecraft.level;
		if (level == null)
			return;
		RegistryAccess registryAccess = level.registryAccess();

		SimpleContainer container = new SimpleContainer(3);
		container.setItem(0, templateSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY));
		container.setItem(1, baseSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY));
		container.setItem(2, additionSlot.getDisplayedItemStack().orElse(ItemStack.EMPTY));

		ItemStack result = recipe.assemble(container, registryAccess);
		if (!result.isEmpty()) {
			outputSlot.createDisplayOverrides().addItemStack(result);
		}
	}
}
