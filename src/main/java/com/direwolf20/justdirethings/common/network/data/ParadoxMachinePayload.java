package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * action: 0 = snapshot area, 1 = set render/target settings, 2 = set area offset only
 */
public record ParadoxMachinePayload(
        int action,
        boolean renderParadox,
        int targetType,
        double xRadius,
        double yRadius,
        double zRadius,
        int xOffset,
        int yOffset,
        int zOffset
) {
    public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "paradox_machine_packet");

    public ParadoxMachinePayload(final FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readBoolean(), buffer.readInt(),
                buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(action);
        buffer.writeBoolean(renderParadox);
        buffer.writeInt(targetType);
        buffer.writeDouble(xRadius);
        buffer.writeDouble(yRadius);
        buffer.writeDouble(zRadius);
        buffer.writeInt(xOffset);
        buffer.writeInt(yOffset);
        buffer.writeInt(zOffset);
    }
}

