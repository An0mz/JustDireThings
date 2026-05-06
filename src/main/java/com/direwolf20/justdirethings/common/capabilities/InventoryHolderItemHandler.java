package com.direwolf20.justdirethings.common.capabilities;

import com.direwolf20.justdirethings.common.blockentities.InventoryHolderBE;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class InventoryHolderItemHandler extends ItemStackHandler {
    private final InventoryHolderBE be;
    private final ItemStackHandler delegate;

    public InventoryHolderItemHandler(InventoryHolderBE be, ItemStackHandler delegate) {
        this.be = be;
        this.delegate = delegate;
    }

    @Override
    public int getSlots() {
        return delegate.getSlots();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return delegate.getStackInSlot(slot);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        delegate.setStackInSlot(slot, stack);
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!be.isStackValidFilter(stack, slot)) return stack;
        return delegate.insertItem(slot, stack, simulate);
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        int allowedAmt = be.allowedExtractAmount(slot, amount);
        if (allowedAmt <= 0) return ItemStack.EMPTY;
        return delegate.extractItem(slot, allowedAmt, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        int limit = be.getSlotLimit(slot);
        return limit == -1 ? delegate.getSlotLimit(slot) : limit;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return be.isStackValidFilter(stack, slot);
    }
}

