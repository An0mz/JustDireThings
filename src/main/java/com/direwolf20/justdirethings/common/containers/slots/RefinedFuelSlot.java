package com.direwolf20.justdirethings.common.containers.slots;

import com.direwolf20.justdirethings.common.fluids.basefluids.RefinedFuel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class RefinedFuelSlot extends SlotItemHandler {
	public RefinedFuelSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public boolean mayPlace(@Nonnull ItemStack itemStack) {
		if (itemStack.isEmpty())
			return false;
		IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null)
				.orElse(null);
		if (fluidHandlerItem == null)
			return false;
		FluidStack fluidStack = fluidHandlerItem.drain(1000, IFluidHandler.FluidAction.SIMULATE);
		if (fluidStack.getAmount() == 0)
			return false;
		return fluidStack.getFluid() instanceof RefinedFuel;
	}
}
