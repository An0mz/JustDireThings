package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public record EnergyTransmitterSettingPayload(boolean showParticles) {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "energy_transmitter_packet");

	public EnergyTransmitterSettingPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(showParticles);
	}

}
