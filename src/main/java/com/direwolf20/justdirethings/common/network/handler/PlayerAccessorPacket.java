package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.PlayerAccessorBE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.network.data.PlayerAccessorPayload;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;


import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
public class PlayerAccessorPacket {
    public static final PlayerAccessorPacket INSTANCE = new PlayerAccessorPacket();

    public static PlayerAccessorPacket get() {
        return INSTANCE;
    }

    public static void handle(final PlayerAccessorPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null)
                return;
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof BaseMachineContainer baseMachineContainer && baseMachineContainer.baseMachineBE instanceof PlayerAccessorBE playerAccessorBE) {
                playerAccessorBE.updateSidedInventory(Direction.values()[payload.direction()], payload.type());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
