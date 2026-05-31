package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public record ToggleToolSlotPayload(String settingName, int slot, int type, int value) {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "toggle_tool_slot_setting");

	public ToggleToolSlotPayload(final FriendlyByteBuf buffer) {
		this(buffer.readUtf(), buffer.readInt(), buffer.readInt(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(settingName);
		buffer.writeInt(slot);
		buffer.writeInt(type);
		buffer.writeInt(value);
	}

}
