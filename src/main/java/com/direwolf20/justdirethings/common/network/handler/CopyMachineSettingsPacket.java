package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.MachineSettingsCopier;
import com.direwolf20.justdirethings.common.network.data.CopyMachineSettingsPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CopyMachineSettingsPacket {
	public static void handle(final CopyMachineSettingsPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			if (player == null)
				return;
			ItemStack heldItem = player.getMainHandItem();
			if (heldItem.getItem() instanceof MachineSettingsCopier) {
				MachineSettingsCopier.setSettings(heldItem, payload.area(), payload.offset(), payload.filter(),
						payload.redstone());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
