package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.containers.InventoryHolderContainer;
import com.direwolf20.justdirethings.common.network.data.InventoryHolderMoveItemsPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InventoryHolderMoveItemsPacket {
    public static void handle(final InventoryHolderMoveItemsPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;
            AbstractContainerMenu container = sender.containerMenu;
            if (container instanceof InventoryHolderContainer inventoryHolderContainer) {
                switch (payload.moveType()) {
                    case 0 -> inventoryHolderContainer.sendAllItemsToMachine();
                    case 1 -> inventoryHolderContainer.sendAllItemsToPlayer();
                    case 2 -> inventoryHolderContainer.swapItems();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
