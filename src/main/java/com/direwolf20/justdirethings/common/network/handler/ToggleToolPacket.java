package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.common.network.data.ToggleToolPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
				boolean enabled = toggleableTool.getEnabled(toggleableItem);
				player.displayClientMessage(
						Component.translatable("justdirethings.ability", toggleableItem.getHoverName(),
								Component.translatable(enabled ? "justdirethings.enabled" : "justdirethings.disabled")),
						true);
				player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5F,
						enabled ? 2.0F : 0.5F);
			}

		});
		ctx.get().setPacketHandled(true);
	}
}
