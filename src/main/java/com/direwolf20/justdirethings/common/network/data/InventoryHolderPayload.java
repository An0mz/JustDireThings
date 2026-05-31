package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public record InventoryHolderPayload(boolean compareNBT, boolean filtersOnly, boolean compareCounts,
		boolean automatedFiltersOnly, boolean automatedCompareCounts, boolean renderPlayer, int renderedSlot) {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "inventory_holder_packet");

	public InventoryHolderPayload(final FriendlyByteBuf buffer) {
		this(buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(), buffer.readBoolean(),
				buffer.readBoolean(), buffer.readBoolean(), buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeBoolean(compareNBT);
		buffer.writeBoolean(filtersOnly);
		buffer.writeBoolean(compareCounts);
		buffer.writeBoolean(automatedFiltersOnly);
		buffer.writeBoolean(automatedCompareCounts);
		buffer.writeBoolean(renderPlayer);
		buffer.writeInt(renderedSlot);
	}
}
