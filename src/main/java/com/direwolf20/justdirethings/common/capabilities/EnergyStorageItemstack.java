package com.direwolf20.justdirethings.common.capabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.EnergyStorage;

/**
 * An EnergyStorage implementation that reads/writes its value directly to the
 * ItemStack's NBT. This ensures energy survives serialization/deserialization
 * and is compatible with other mods that use the standard "Energy" NBT key
 * (e.g. filling via pipes/chargers).
 */
public class EnergyStorageItemstack extends EnergyStorage {
	private final ItemStack stack;
	private static final String NBT_KEY = "Energy";

	public EnergyStorageItemstack(ItemStack stack, int capacity) {
		super(capacity);
		this.stack = stack;
		// Load current energy from the item's NBT tag
		if (stack.hasTag() && stack.getTag().contains(NBT_KEY)) {
			energy = Math.min(stack.getTag().getInt(NBT_KEY), capacity);
		}
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		int result = super.receiveEnergy(maxReceive, simulate);
		if (!simulate && result > 0)
			save();
		return result;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		int result = super.extractEnergy(maxExtract, simulate);
		if (!simulate && result > 0)
			save();
		return result;
	}

	protected void save() {
		stack.getOrCreateTag().putInt(NBT_KEY, energy);
	}
}
