package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.direwolf20.justdirethings.common.network.data.ToggleToolSlotPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
public class ToggleToolSlotPacket {
    public static final ToggleToolSlotPacket INSTANCE = new ToggleToolSlotPacket();

    public static ToggleToolSlotPacket get() {
        return INSTANCE;
    }

    public static void handle(final ToggleToolSlotPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;


            ItemStack stack = player.getInventory().getItem(payload.slot());
            if (stack.getItem() instanceof ToggleableTool) {
                if (payload.type() == 0) //Toggle
                    ToggleableTool.toggleSetting(stack, payload.settingName());
                else if (payload.type() == 1) //Cycle
                    ToggleableTool.cycleSetting(stack, payload.settingName());
                else if (payload.type() == 2) //Slider
                    ToggleableTool.setToolValue(stack, payload.settingName(), payload.value());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
