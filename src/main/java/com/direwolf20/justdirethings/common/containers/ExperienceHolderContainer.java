package com.direwolf20.justdirethings.common.containers;

import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.blockentities.ExperienceHolderBE;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ExperienceHolderContainer extends BaseMachineContainer {

	public ContainerData expData;

	public ExperienceHolderContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
		this(windowId, playerInventory, extraData.readBlockPos());
	}

	public ExperienceHolderContainer(int windowId, Inventory playerInventory, BlockPos blockPos) {
		super(Registration.ExperienceHolder_Container.get(), windowId, playerInventory, blockPos);
		BlockEntity be = player.level().getBlockEntity(pos);
		if (be instanceof ExperienceHolderBE expBE) {
			expData = new SimpleContainerData(2);
			expData.set(0, expBE.exp & 0xFFFF);
			expData.set(1, (expBE.exp >> 16) & 0xFFFF);
			addDataSlots(expData);
		}
		addPlayerSlots(player.getInventory());
	}

	public int getStoredExp() {
		if (expData == null)
			return 0;
		return (expData.get(1) << 16) | expData.get(0);
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(ContainerLevelAccess.create(player.level(), pos), player,
				Registration.ExperienceHolder.get());
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		return super.quickMoveStack(playerIn, index);
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
	}
}
