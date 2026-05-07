package com.direwolf20.justdirethings.common.containers;

import com.direwolf20.justdirethings.common.containers.basecontainers.BaseContainer;
import com.direwolf20.justdirethings.common.containers.handlers.PotionCanisterHandler;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class PotionCanisterContainer extends BaseContainer {
    public static final int SLOTS = 1;
    public PotionCanisterHandler handler;
    public ItemStack potionCanister;
    public Player playerEntity;

    public PotionCanisterContainer(int windowId, Inventory playerInventory, Player player, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, player, extraData.readItem());
    }

    public PotionCanisterContainer(int windowId, Inventory playerInventory, Player player, ItemStack potionCanister) {
        super(Registration.PotionCanister_Container.get(), windowId);
        playerEntity = player;
        this.potionCanister = potionCanister;
        this.handler = new PotionCanisterHandler(potionCanister);
        addItemSlots(handler, 80, 35, 1);
        addPlayerSlots(playerInventory, 8, 84);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return playerIn.getMainHandItem().equals(potionCanister)
                || playerIn.getOffhandItem().equals(potionCanister);
    }

    private void addItemSlots(IItemHandler h, int x, int y, int amount) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(h, i, x + i * 18, y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof PotionItem;
                }
            });
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack currentStack = slot.getItem();
            if (index < SLOTS) {
                if (!this.moveItemStackTo(currentStack, SLOTS, Inventory.INVENTORY_SIZE + SLOTS, true))
                    return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(currentStack, 0, SLOTS, false))
                    return ItemStack.EMPTY;
            }
            if (currentStack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
            if (currentStack.getCount() == itemstack.getCount()) return ItemStack.EMPTY;
            slot.onTake(playerIn, currentStack);
        }
        return itemstack;
    }
}
