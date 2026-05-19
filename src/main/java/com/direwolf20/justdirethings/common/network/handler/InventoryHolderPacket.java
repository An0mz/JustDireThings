package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.InventoryHolderBE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.network.data.InventoryHolderPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InventoryHolderPacket {
	public static void handle(final InventoryHolderPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;
			AbstractContainerMenu container = sender.containerMenu;
			if (container instanceof BaseMachineContainer baseMachineContainer
					&& baseMachineContainer.baseMachineBE instanceof InventoryHolderBE be) {
				be.saveSettings(payload.compareNBT(), payload.filtersOnly(), payload.compareCounts(),
						payload.automatedFiltersOnly(), payload.automatedCompareCounts(), payload.renderPlayer(),
						payload.renderedSlot());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
