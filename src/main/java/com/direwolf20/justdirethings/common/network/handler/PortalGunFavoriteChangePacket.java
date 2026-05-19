package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.network.data.PortalGunFavoriteChangePayload;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PortalGunFavoriteChangePacket {
	public static void handle(final PortalGunFavoriteChangePayload payload, final Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer sender = ctx.get().getSender();
			if (sender == null)
				return;

			ItemStack stack = PortalGunV2.getPortalGunv2(sender);
			if (stack.isEmpty())
				return;

			Level level = sender.level();
			if (!payload.add()) {
				PortalGunV2.removeFavorite(stack, payload.favorite());
				return;
			}

			NBTHelpers.PortalDestination destination = PortalGunV2.getFavorite(stack, payload.favorite());
			if (!payload.editing()) {
				Vec3 position = sender.position();
				Direction facing = MiscHelpers.getFacingDirection(sender);
				destination = new NBTHelpers.PortalDestination(level.dimension(), position, facing, payload.name());
				PortalGunV2.addFavorite(stack, payload.favorite(), destination);
			} else {
				Vec3 position = payload.coordinates().equals(Vec3.ZERO) ? sender.position() : payload.coordinates();
				Direction facing = destination == null ? MiscHelpers.getFacingDirection(sender) : destination.facing();
				destination = new NBTHelpers.PortalDestination(
						destination == null ? level.dimension() : destination.dimension(), position, facing,
						payload.name());
				PortalGunV2.addFavorite(stack, payload.favorite(), destination);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
