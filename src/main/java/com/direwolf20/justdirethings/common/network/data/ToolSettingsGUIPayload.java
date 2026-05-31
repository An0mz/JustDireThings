package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public record ToolSettingsGUIPayload() {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "tool_settings_gui");

	public ToolSettingsGUIPayload(final FriendlyByteBuf buffer) {
		this();
	}

	public void write(FriendlyByteBuf buffer) {
	}
}
