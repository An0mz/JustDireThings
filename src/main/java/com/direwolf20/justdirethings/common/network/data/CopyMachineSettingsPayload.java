package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public record CopyMachineSettingsPayload(boolean area, boolean offset, boolean filter, boolean redstone) {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "copy_machine_settings");

	public CopyMachineSettingsPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(area);
		buffer.writeBoolean(offset);
		buffer.writeBoolean(filter);
		buffer.writeBoolean(redstone);
	}
}
