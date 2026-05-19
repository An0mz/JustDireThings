package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record PortalGunFavoritePayload(int favorite, boolean staysOpen) {
	public PortalGunFavoritePayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readBoolean());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(favorite);
		buffer.writeBoolean(staysOpen);
	}
}
