package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public record ToggleToolPayload(String settingName) {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "toggle_tool_setting");

	public ToggleToolPayload(final FriendlyByteBuf buffer) {
		this(buffer.readUtf());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeUtf(settingName);
	}

}
