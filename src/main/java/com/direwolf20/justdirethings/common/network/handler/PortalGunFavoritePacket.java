package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.network.data.PortalGunFavoritePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PortalGunFavoritePacket {
    public static void handle(final PortalGunFavoritePayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;

            ItemStack stack = PortalGunV2.getPortalGunv2(sender);
            if (stack.isEmpty()) return;

            PortalGunV2.setFavoritePosition(stack, payload.favorite());
            PortalGunV2.setStayOpen(stack, payload.staysOpen());
        });
        ctx.get().setPacketHandled(true);
    }
}

