package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.common.network.data.ToggleToolPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
public class ToggleToolPacket {
    public static final ToggleToolPacket INSTANCE = new ToggleToolPacket();

    public static ToggleToolPacket get() {
        return INSTANCE;
    }

    public static void handle(final ToggleToolPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;


            ItemStack toggleableItem = ToggleableItem.getToggleableItem(player);
            if (toggleableItem.getItem() instanceof ToggleableItem toggleableTool) {
                toggleableTool.toggleEnabled(toggleableItem);
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
