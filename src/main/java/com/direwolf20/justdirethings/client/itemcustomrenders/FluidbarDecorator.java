package com.direwolf20.justdirethings.client.itemcustomrenders;

import com.direwolf20.justdirethings.common.items.interfaces.FluidContainingItem;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class FluidbarDecorator implements IItemDecorator {
	public static final FluidbarDecorator INSTANCE = new FluidbarDecorator();

	@Override
	public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
		if (!(stack.getItem() instanceof FluidContainingItem fluidContainingItem))
			return false;

		boolean isPowerBarVisible = false;
		if (stack.getItem() instanceof PoweredItem poweredItem)
			isPowerBarVisible = poweredItem.isPowerBarVisible(stack);
		if (stack.isBarVisible())
			isPowerBarVisible = true;

		if (stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null) == null)
			return false;

		int fluidBarY = isPowerBarVisible ? yOffset + 11 : yOffset + 13;
		int fluidBarWidth = fluidContainingItem.getFluidBarWidth(stack);
		int fluidBarColor = fluidContainingItem.getFluidBarColor(stack);

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0, 0, 200);
		renderBar(guiGraphics, xOffset + 2, fluidBarY, fluidBarWidth, fluidBarColor);
		guiGraphics.pose().popPose();
		return false;
	}

	private void renderBar(GuiGraphics guiGraphics, int x, int y, int width, int color) {
		guiGraphics.fill(x, y, x + 13, y + 2, 0xFF303030);
		guiGraphics.fill(x, y, x + width, y + 1, color | 0xFF000000);
	}
}
