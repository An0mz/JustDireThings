package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public record PortalGunFavoriteChangePayload(int favorite, boolean add, String name, boolean editing,
		Vec3 coordinates) {
	public PortalGunFavoriteChangePayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt(), buffer.readBoolean(), buffer.readUtf(), buffer.readBoolean(),
				new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(favorite);
		buffer.writeBoolean(add);
		buffer.writeUtf(name);
		buffer.writeBoolean(editing);
		buffer.writeDouble(coordinates.x);
		buffer.writeDouble(coordinates.y);
		buffer.writeDouble(coordinates.z);
	}
}
