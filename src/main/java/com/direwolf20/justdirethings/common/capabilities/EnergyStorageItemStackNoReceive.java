package com.direwolf20.justdirethings.common.capabilities;

import net.minecraft.world.item.ItemStack;

/**
 * Pocket Generators need to be able to power themselves from their own burnt
 * fuel, but must not receive energy pushed in externally (chargers, pipes,
 * etc). receiveEnergy() is blocked entirely; forceReceiveEnergy() bypasses that
 * for the item's own internal self-charging logic.
 */
public class EnergyStorageItemStackNoReceive extends EnergyStorageItemstack {
	public EnergyStorageItemStackNoReceive(ItemStack stack, int capacity) {
		super(stack, capacity);
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public boolean canReceive() {
		return false;
	}

	public int forceReceiveEnergy(int maxReceive, boolean simulate) {
		return super.receiveEnergy(maxReceive, simulate);
	}
}
