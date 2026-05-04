package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.DropperT1BE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.network.data.DropperSettingPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;


import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
public class DropperSettingPacket {
    public static final DropperSettingPacket INSTANCE = new DropperSettingPacket();

    public static DropperSettingPacket get() {
        return INSTANCE;
    }

    public static void handle(final DropperSettingPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null)
                return;
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof BaseMachineContainer baseMachineContainer && baseMachineContainer.baseMachineBE instanceof DropperT1BE dropperT1BE) {
                dropperT1BE.setDropperSettings(payload.dropCount());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
