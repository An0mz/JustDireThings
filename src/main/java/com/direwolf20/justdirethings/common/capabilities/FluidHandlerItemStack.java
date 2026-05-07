package com.direwolf20.justdirethings.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

public class FluidHandlerItemStack implements IFluidHandlerItem {
    public static final String FLUID_NBT_KEY = "Fluid";

    private final ItemStack container;
    private final int capacity;

    public FluidHandlerItemStack(ItemStack container, int capacity) {
        this.container = container;
        this.capacity = capacity;
    }

    @Override
    public @NotNull ItemStack getContainer() {
        return container;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        CompoundTag tag = container.getTag();
        if (tag == null || !tag.contains(FLUID_NBT_KEY)) return FluidStack.EMPTY;
        return FluidStack.loadFluidStackFromNBT(tag.getCompound(FLUID_NBT_KEY));
    }

    @Override
    public int getTankCapacity(int tank) {
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack fluid) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return 0;
        FluidStack existing = getFluidInTank(0);
        if (!existing.isEmpty() && !existing.isFluidEqual(resource)) return 0;
        int space = capacity - existing.getAmount();
        int toFill = Math.min(space, resource.getAmount());
        if (toFill <= 0) return 0;
        if (action.execute()) {
            FluidStack newFluid = new FluidStack(resource, existing.getAmount() + toFill);
            CompoundTag fluidTag = new CompoundTag();
            newFluid.writeToNBT(fluidTag);
            container.getOrCreateTag().put(FLUID_NBT_KEY, fluidTag);
        }
        return toFill;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        FluidStack existing = getFluidInTank(0);
        if (existing.isEmpty() || !existing.isFluidEqual(resource)) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        FluidStack existing = getFluidInTank(0);
        if (existing.isEmpty()) return FluidStack.EMPTY;
        int toDrain = Math.min(existing.getAmount(), maxDrain);
        FluidStack drained = new FluidStack(existing, toDrain);
        if (action.execute()) {
            int remaining = existing.getAmount() - toDrain;
            if (remaining <= 0) {
                container.getOrCreateTag().remove(FLUID_NBT_KEY);
            } else {
                FluidStack newFluid = new FluidStack(existing, remaining);
                CompoundTag fluidTag = new CompoundTag();
                newFluid.writeToNBT(fluidTag);
                container.getOrCreateTag().put(FLUID_NBT_KEY, fluidTag);
            }
        }
        return drained;
    }
}
