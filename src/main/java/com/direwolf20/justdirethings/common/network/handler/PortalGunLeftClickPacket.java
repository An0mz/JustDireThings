package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.PortalGun;
import com.direwolf20.justdirethings.common.network.data.PortalGunLeftClickPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PortalGunLeftClickPacket {
	public static void handle(final PortalGunLeftClickPayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;

			ItemStack stack = sender.getMainHandItem();
			if (!(stack.getItem() instanceof PortalGun)) {
				stack = sender.getOffhandItem();
			}
			if (!(stack.getItem() instanceof PortalGun))
				return;

			PortalGun.firePortal(sender.level(), sender, stack, true);
		});
		ctx.get().setPacketHandled(true);
	}
}
