package com.direwolf20.justdirethings.client.jei;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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
import net.minecraft.world.item.Items;

@SuppressWarnings("removal")
public class OreToResourceRecipeCategory implements IRecipeCategory<OreToResourceRecipe> {
	public static final RecipeType<OreToResourceRecipe> TYPE = RecipeType.create(JustDireThings.MODID,
			"ore_to_resource", OreToResourceRecipe.class);

	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable pickaxeIcon;
	private final IDrawableAnimated animatedArrow;

	public OreToResourceRecipeCategory(IGuiHelper guiHelper) {
		this.background = guiHelper.createBlankDrawable(120, 30);
		this.icon = guiHelper.createDrawableItemStack(new ItemStack(Registration.RawFerricoreOre.get()));
		IDrawableStatic arrowDrawable = guiHelper.getRecipeArrow();
		this.animatedArrow = guiHelper.createAnimatedDrawable(arrowDrawable, 40, IDrawableAnimated.StartDirection.LEFT,
				false);
		this.pickaxeIcon = guiHelper.createDrawableItemStack(new ItemStack(Items.IRON_PICKAXE));
	}

	@Override
	public RecipeType<OreToResourceRecipe> getRecipeType() {
		return TYPE;
	}

	@Override
	public Component getTitle() {
		return Component.translatable("justdirethings.oretoresource.title");
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
	public void draw(OreToResourceRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics gui, double mouseX,
			double mouseY) {
		RenderSystem.enableBlend();
		animatedArrow.draw(gui, 46, 10);
		background.draw(gui, 17, 0);
		pickaxeIcon.draw(gui, 50, -2);
		RenderSystem.disableBlend();
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, OreToResourceRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 20, 10).addItemStack(recipe.getOreBlock());
		builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 10).addItemStack(recipe.getOutput());
	}
}
