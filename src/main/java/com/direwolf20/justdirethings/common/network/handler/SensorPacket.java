package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.SensorT1BE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.network.data.SensorPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;


import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
public class SensorPacket {
    public static final SensorPacket INSTANCE = new SensorPacket();

    public static SensorPacket get() {
        return INSTANCE;
    }

    public static void handle(final SensorPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null)
                return;
            AbstractContainerMenu container = sender.containerMenu;

            if (container instanceof BaseMachineContainer baseMachineContainer && baseMachineContainer.baseMachineBE instanceof SensorT1BE sensor) {
                sensor.setSensorSettings(payload.senseTarget(), payload.strongSignal(), payload.senseCount(), payload.equality());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
