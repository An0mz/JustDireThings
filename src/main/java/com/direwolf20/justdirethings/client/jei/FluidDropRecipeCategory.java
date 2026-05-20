package com.direwolf20.justdirethings.client.jei;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.datagen.recipes.FluidDropRecipe;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidStack;

public class FluidDropRecipeCategory implements IRecipeCategory<FluidDropRecipe> {

	public static final RecipeType<FluidDropRecipe> TYPE = RecipeType.create(JustDireThings.MODID, "fluid_drop_recipe",
			FluidDropRecipe.class);

	public static final int width = 120;
	public static final int height = 40;

	private final IDrawable background;
	private final IDrawable icon;
	private final Component localizedName;
	private final IDrawableStatic arrow;

	public FluidDropRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createBlankDrawable(width, height);
		icon = guiHelper.createDrawableItemStack(new ItemStack(Registration.PolymorphicCatalyst.get()));
		localizedName = Component.translatable("justdirethings.fluiddroprecipe.title");
		arrow = guiHelper.getRecipeArrow();
	}

	@Override
	public RecipeType<FluidDropRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return localizedName;
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
	public void draw(FluidDropRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics gui, double mouseX,
			double mouseY) {
		RenderSystem.enableBlend();
		arrow.draw(gui, 34, 20);
		RenderSystem.disableBlend();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, FluidDropRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.CATALYST, 9, 0).addItemStack(new ItemStack(recipe.getCatalyst()));

		BlockState input = recipe.getInput();
		IRecipeSlotBuilder inputSlotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, 9, 20);
		FluidState inputFluidState = input.getFluidState();
		if (!inputFluidState.isEmpty()) {
			inputSlotBuilder.addIngredient(ForgeTypes.FLUID_STACK, new FluidStack(inputFluidState.getType(), 1000));
		}

		BlockState output = recipe.getOutput();
		FluidState outputFluidState = output.getFluidState();
		if (!outputFluidState.isEmpty()) {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 68, 20).addIngredient(ForgeTypes.FLUID_STACK,
					new FluidStack(outputFluidState.getType(), 1000));
		} else {
			builder.addSlot(RecipeIngredientRole.OUTPUT, 68, 20).addItemStack(new ItemStack(output.getBlock()));
		}
	}
}
