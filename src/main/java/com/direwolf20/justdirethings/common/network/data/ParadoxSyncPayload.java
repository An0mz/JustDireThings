package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record ParadoxSyncPayload(BlockPos pos, int timeRunning) {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "paradox_sync_packet");

	public ParadoxSyncPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBlockPos(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeInt(timeRunning);
	}
}
