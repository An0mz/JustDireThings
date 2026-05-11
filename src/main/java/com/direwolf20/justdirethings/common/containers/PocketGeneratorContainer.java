package com.direwolf20.justdirethings.common.containers;

import com.direwolf20.justdirethings.common.containers.basecontainers.BaseContainer;
import com.direwolf20.justdirethings.common.containers.slots.FuelSlot;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class PocketGeneratorContainer extends BaseContainer {
    public static final int SLOTS = 1;
    public ItemStackHandler handler;
    public ItemStack pocketGeneratorItemStack;
    public Player playerEntity;
    private boolean syncing = false;

    public PocketGeneratorContainer(int windowId, Inventory playerInventory, Player player, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, player, extraData.readItem());
    }

    public PocketGeneratorContainer(int windowId, Inventory playerInventory, Player player, ItemStack pocketGenerator) {
        super(Registration.PocketGenerator_Container.get(), windowId);
        playerEntity = player;
        this.pocketGeneratorItemStack = pocketGenerator;

        // Read handler from NBT tag on the item
        handler = new ItemStackHandler(SLOTS) {
            @Override
            protected void onContentsChanged(int slot) {
                if (syncing) return;
                CompoundTag tag = pocketGeneratorItemStack.getOrCreateTag();
                tag.put("FuelInventory", serializeNBT());
            }
        };

        // Load existing contents if any
        CompoundTag tag = pocketGenerator.getTag();
        if (tag != null && tag.contains("FuelInventory")) {
            handler.deserializeNBT(tag.getCompound("FuelInventory"));
        }

        addGeneratorSlots(handler, 0, 80, 35, 1, 18);
        addPlayerSlots(playerInventory, 8, 84);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return playerIn.getMainHandItem().equals(pocketGeneratorItemStack);
    }

    protected int addGeneratorSlots(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new FuelSlot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack currentStack = slot.getItem();
            if (index < SLOTS) { //Slot to Player Inventory
                if (!this.moveItemStackTo(currentStack, SLOTS, Inventory.INVENTORY_SIZE + SLOTS, true)) {
                    return ItemStack.EMPTY;
                }
            }
            if (index >= SLOTS) { //Player Inventory to Slots
                if (!this.moveItemStackTo(currentStack, 0, SLOTS, false)) {
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
    public void broadcastChanges() {
        // Sync handler from item NBT in case inventoryTick consumed fuel externally
        CompoundTag tag = pocketGeneratorItemStack.getTag();
        ItemStack nbtFuel = ItemStack.EMPTY;
        if (tag != null && tag.contains("FuelInventory")) {
            ItemStackHandler temp = new ItemStackHandler(SLOTS);
            temp.deserializeNBT(tag.getCompound("FuelInventory"));
            nbtFuel = temp.getStackInSlot(0);
        }
        if (!ItemStack.matches(nbtFuel, handler.getStackInSlot(0))) {
            syncing = true;
            handler.setStackInSlot(0, nbtFuel.copy());
            syncing = false;
        }
        super.broadcastChanges();
    }

    @Override
    public void removed(Player playerIn) {
        super.removed(playerIn);
    }
}
