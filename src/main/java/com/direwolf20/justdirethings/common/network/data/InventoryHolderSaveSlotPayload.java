package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record InventoryHolderSaveSlotPayload(int slot) {
	public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "inventory_holder_save_slot");

	public InventoryHolderSaveSlotPayload(final FriendlyByteBuf buffer) {
		this(buffer.readInt());
	}

	public void write(FriendlyByteBuf buffer) {
		buffer.writeInt(slot);
	}
}
