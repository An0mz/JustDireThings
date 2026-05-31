package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public record ExperienceHolderPayload(boolean add, int levels) {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "experience_holder_packet");

	public ExperienceHolderPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(add);
		buffer.writeInt(levels);
	}
}
