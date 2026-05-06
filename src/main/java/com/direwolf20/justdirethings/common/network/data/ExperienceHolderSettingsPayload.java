package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ExperienceHolderSettingsPayload(
        int targetExp,
        boolean ownerOnly,
        boolean collectExp,
        boolean showParticles
) {
    public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "experience_holder_settings_packet");

    public ExperienceHolderSettingsPayload(final FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(targetExp);
        buffer.writeBoolean(ownerOnly);
        buffer.writeBoolean(collectExp);
        buffer.writeBoolean(showParticles);
    }
}
