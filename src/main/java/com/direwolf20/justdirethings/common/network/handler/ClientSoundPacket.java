package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.client.OurSounds;
import com.direwolf20.justdirethings.common.network.data.ClientSoundPayload;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
public class ClientSoundPacket {
    public static final ClientSoundPacket INSTANCE = new ClientSoundPacket();

    public static ClientSoundPacket get() {
        return INSTANCE;
    }

    public static void handle(final ClientSoundPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.get(payload.soundEvent());
            if (soundEvent != null) {
                OurSounds.playSound(soundEvent, payload.pitch(), payload.volume());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
