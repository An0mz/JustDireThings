package com.direwolf20.justdirethings.common.containers;

import com.direwolf20.justdirethings.common.blockentities.InventoryHolderBE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.containers.handlers.FilterBasicHandler;
import com.direwolf20.justdirethings.common.containers.slots.FilterBasicSlot;
import com.direwolf20.justdirethings.common.containers.slots.InventoryHolderSlot;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.ItemStackKey;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class InventoryHolderContainer extends BaseMachineContainer {
    public InventoryHolderBE inventoryHolderBE;

    public InventoryHolderContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, extraData.readBlockPos());
    }

    public InventoryHolderContainer(int windowId, Inventory playerInventory, BlockPos blockPos) {
        super(Registration.InventoryHolder_Container.get(), windowId, playerInventory, blockPos);
        if (baseMachineBE instanceof InventoryHolderBE be) {
            inventoryHolderBE = be;
        }
        // InventoryHolderBE doesn't implement FilterableBE, so add ghost filter slots manually
        if (inventoryHolderBE != null) {
            filterHandler = inventoryHolderBE.filterBasicHandler;
            FILTER_SLOTS = filterHandler.getSlots();
            for (int i = 0; i < FILTER_SLOTS; i++) {
                addSlot(new FilterBasicSlot(filterHandler, i, -100 - i, -100));
            }
        }
        addPlayerSlots(player.getInventory());
    }

    @Override
    public void addMachineSlots() {
        machineHandler = baseMachineBE.getMachineHandler();
        if (baseMachineBE instanceof InventoryHolderBE be) {
            // 3 rows of 9 (main inventory 0-26)
            addInventoryHolderSlotBox(machineHandler, be, 0, 8, 18, 9, 18, 3, 18);
            // Hotbar row (27-35)
            addInventoryHolderSlotRange(machineHandler, be, 27, 8, 72, 9, 18);
            // Armor (36-39)
            addInventoryHolderSlotRange(machineHandler, be, 36, 8, 90, 4, 18);
            // Offhand (40)
            addInventoryHolderSlotRange(machineHandler, be, 40, 80, 90, 1, 18);
        }
    }

    private int addInventoryHolderSlotRange(IItemHandler handler, InventoryHolderBE be, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new InventoryHolderSlot(handler, index, x, y, be));
            x += dx;
            index++;
        }
        return index;
    }

    private int addInventoryHolderSlotBox(IItemHandler handler, InventoryHolderBE be, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addInventoryHolderSlotRange(handler, be, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public void sendAllItemsToMachine() {
        int playerStart = MACHINE_SLOTS + FILTER_SLOTS;
        for (int i = playerStart; i < slots.size(); i++) {
            quickMoveStack(this.player, i);
        }
    }

    public void sendAllItemsToPlayer() {
        for (int i = 0; i < MACHINE_SLOTS; i++) {
            quickMoveStack(this.player, i);
        }
    }

    public void swapItems() {
        int playerStart = MACHINE_SLOTS + FILTER_SLOTS;
        for (int i = 0; i < MACHINE_SLOTS; i++) {
            int playerSlot = playerStart + i;
            if (playerSlot >= slots.size()) break;

            Slot machineSlot = this.slots.get(i);
            Slot playerSlotRef = this.slots.get(playerSlot);
            ItemStack machineStack = machineSlot.getItem();
            ItemStack playerStack = playerSlotRef.getItem();

            if (playerStack.isEmpty() && machineStack.isEmpty()) continue;

            ItemStack machineStackCopy = machineStack.copy();
            ItemStack playerStackCopy = playerStack.copy();

            if (!playerStack.isEmpty()) {
                machineSlot.set(ItemStack.EMPTY);
                if (moveItemStackTo(playerStack, i, i + 1, false) && playerStack.isEmpty()) {
                    playerSlotRef.set(machineStackCopy);
                    machineSlot.setChanged();
                    playerSlotRef.setChanged();
                } else {
                    machineSlot.set(machineStackCopy);
                    playerSlotRef.set(playerStackCopy);
                }
            } else {
                moveItemStackTo(machineStack, playerSlot, playerSlot + 1, false);
            }
        }
    }

    public boolean moveToFilteredSlot(ItemStack currentStack) {
        if (inventoryHolderBE == null) return false;
        ItemStackKey key = new ItemStackKey(currentStack, inventoryHolderBE.compareNBT);
        FilterBasicHandler filteredItems = inventoryHolderBE.filterBasicHandler;
        for (int i = 0; i < filteredItems.getSlots(); i++) {
            ItemStack stack = filteredItems.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            if (key.equals(new ItemStackKey(stack, inventoryHolderBE.compareNBT))) {
                if (this.moveItemStackTo(currentStack, i, i + 1, false) && currentStack.isEmpty())
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(ContainerLevelAccess.create(player.level(), pos), player, Registration.InventoryHolder.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack currentStack = slot.getItem();
            int playerStart = MACHINE_SLOTS + FILTER_SLOTS;
            if (index < MACHINE_SLOTS) {
                if (!this.moveItemStackTo(currentStack, playerStart, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= playerStart) {
                if (moveToFilteredSlot(currentStack)) {
                    // placed in filter-matched slot
                } else if (inventoryHolderBE != null && inventoryHolderBE.filtersOnly) {
                    return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(currentStack, 0, MACHINE_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (currentStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (currentStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, currentStack);
        }
        return itemstack;
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
        if (baseMachineBE != null)
            baseMachineBE.markDirtyClient();
    }
}
