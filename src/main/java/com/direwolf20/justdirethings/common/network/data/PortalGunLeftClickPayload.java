package com.direwolf20.justdirethings.common.network.data;

import net.minecraft.network.FriendlyByteBuf;

public record PortalGunLeftClickPayload() {
    public PortalGunLeftClickPayload(final FriendlyByteBuf buffer) {
        this();
    }

    public void write(FriendlyByteBuf buffer) {
    }
}

