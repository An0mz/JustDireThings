package com.direwolf20.justdirethings.common.network.data;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record InventoryHolderMoveItemsPayload(int moveType) {
    public static final ResourceLocation ID = new ResourceLocation(JustDireThings.MODID, "inventory_holder_move_items");

    public InventoryHolderMoveItemsPayload(final FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(moveType);
    }
}
