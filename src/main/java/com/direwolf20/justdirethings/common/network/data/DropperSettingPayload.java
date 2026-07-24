package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public record DropperSettingPayload(int dropCount, int pickupDelay) {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "dropper_setting_packet");

	public DropperSettingPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(dropCount);
		buffer.writeInt(pickupDelay);
	}

}
