package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.EnergyTransmitterBE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.network.data.EnergyTransmitterSettingPayload;
import net.minecraft.world.inventory.AbstractContainerMenu;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
public class EnergyTransmitterPacket {
	public static final EnergyTransmitterPacket INSTANCE = new EnergyTransmitterPacket();

	public static EnergyTransmitterPacket get() {
		return INSTANCE;
	}

	public static void handle(final EnergyTransmitterSettingPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			AbstractContainerMenu container = sender.containerMenu;

			if (container instanceof BaseMachineContainer baseMachineContainer
					&& baseMachineContainer.baseMachineBE instanceof EnergyTransmitterBE energyTransmitterBE) {
				energyTransmitterBE.setEnergyTransmitterSettings(payload.showParticles());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
