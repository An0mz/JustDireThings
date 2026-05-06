package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.InventoryHolderBE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.network.data.InventoryHolderSaveSlotPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InventoryHolderSaveSlotPacket {
    public static void handle(final InventoryHolderSaveSlotPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;
            AbstractContainerMenu container = sender.containerMenu;
            if (container instanceof BaseMachineContainer baseMachineContainer &&
                    baseMachineContainer.baseMachineBE instanceof InventoryHolderBE inventoryHolderBE) {
                inventoryHolderBE.addSavedItem(payload.slot());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
