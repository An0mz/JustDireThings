package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.blockentities.ParadoxMachineBE;
import com.direwolf20.justdirethings.common.network.data.ParadoxSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ParadoxSyncPacket {
    public static void handle(final ParadoxSyncPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            handleClient(payload);
        });
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(ParadoxSyncPayload payload) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        BlockEntity be = mc.level.getBlockEntity(payload.pos());
        if (be instanceof ParadoxMachineBE paradoxMachineBE) {
            paradoxMachineBE.receiveRunTime(payload.timeRunning());
        }
    }
}

