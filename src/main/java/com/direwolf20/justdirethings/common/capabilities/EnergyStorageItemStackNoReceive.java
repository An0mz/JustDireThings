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
		// Can't call receiveEnergy()/super.receiveEnergy() here: EnergyStorage's
		// implementation gates on canReceive(), which we override to false above.
		// That check is virtual, so even a super call would still resolve to our
		// override and always return 0. Replicate the insertion math directly instead.
		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate && energyReceived > 0) {
			energy += energyReceived;
			save();
		}
		return energyReceived;
	}
}
