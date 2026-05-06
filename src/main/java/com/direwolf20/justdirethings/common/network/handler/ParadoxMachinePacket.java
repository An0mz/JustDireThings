package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.ParadoxMachineBE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.network.data.ParadoxMachinePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParadoxMachinePacket {
    public static void handle(final ParadoxMachinePayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;
            AbstractContainerMenu container = sender.containerMenu;
            if (container instanceof BaseMachineContainer baseMachineContainer &&
                    baseMachineContainer.baseMachineBE instanceof ParadoxMachineBE be) {
                switch (payload.action()) {
                    case 0 -> be.snapshotArea();
                    case 1 -> be.setRenderParadox(payload.renderParadox(), payload.targetType());
                    case 2 -> be.setAreaOnly(payload.xRadius(), payload.yRadius(), payload.zRadius());
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

