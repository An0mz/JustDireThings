package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.containers.ToolSettingContainer;
import com.direwolf20.justdirethings.common.network.data.ToggleToolRefreshSlotsPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleToolRefreshSlotsPacket {
	public static void handle(final ToggleToolRefreshSlotsPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player != null && payload.slot() >= 0 && player.containerMenu instanceof ToolSettingContainer container) {
				container.refreshSlots(player.getInventory().getItem(payload.slot()));
				container.broadcastFullState();
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
