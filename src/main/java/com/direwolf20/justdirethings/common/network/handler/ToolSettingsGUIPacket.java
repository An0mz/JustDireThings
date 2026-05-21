package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.containers.ToolSettingContainer;
import com.direwolf20.justdirethings.common.network.data.ToolSettingsGUIPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToolSettingsGUIPacket {
	public static void handle(ToolSettingsGUIPayload payload, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			ServerPlayer sender = context.getSender();
			if (sender == null)
				return;
			sender.openMenu(new SimpleMenuProvider(
					(windowId, playerInventory, player) -> new ToolSettingContainer(windowId, playerInventory, player),
					Component.empty()));
		});
		context.setPacketHandled(true);
	}
}
