package com.direwolf20.justdirethings.common.containers.handlers;

import com.direwolf20.justdirethings.common.items.PotionCanister;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class PotionCanisterHandler extends ItemStackHandler {
	public static final String NBT_KEY = "PotionSlot";
	private final ItemStack canister;

	public PotionCanisterHandler(ItemStack canister) {
		super(1);
		this.canister = canister;
		CompoundTag tag = canister.getTag();
		if (tag != null && tag.contains(NBT_KEY)) {
			deserializeNBT(tag.getCompound(NBT_KEY));
		}
	}

	@Override
	protected void onContentsChanged(int slot) {
		canister.getOrCreateTag().put(NBT_KEY, serializeNBT());
		ItemStack slotStack = getStackInSlot(slot);
		if (!slotStack.isEmpty() && slotStack.getItem() instanceof PotionItem) {
			PotionCanister.attemptFill(canister, this);
		}
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return stack.getItem() instanceof PotionItem;
	}
}
