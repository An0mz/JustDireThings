package com.direwolf20.justdirethings.common.blockentities;

import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

public class GooSoilBE extends BlockEntity {
	private NBTHelpers.BoundInventory boundInventory;

	public GooSoilBE(BlockPos pos, BlockState state) {
		super(Registration.GooSoilBE.get(), pos, state);
	}

	public void bindInventory(NBTHelpers.BoundInventory boundInventory) {
		this.boundInventory = boundInventory;
		this.setChanged();
	}

	public IItemHandler getAttachedInventory(ServerLevel serverLevel) {
		if (boundInventory == null)
			return null;
		ServerLevel boundLevel = serverLevel.getServer().getLevel(boundInventory.globalPos().dimension());
		if (boundLevel == null)
			return null;
		BlockEntity be = boundLevel.getBlockEntity(boundInventory.globalPos().pos());
		if (be == null)
			return null;
		return be.getCapability(ForgeCapabilities.ITEM_HANDLER, boundInventory.direction()).orElse(null);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		if (boundInventory != null) {
			tag.put("boundinventory", NBTHelpers.BoundInventory.toNBT(boundInventory));
		}
	}

	@Override
	public void load(CompoundTag tag) {
		if (tag.contains("boundinventory"))
			boundInventory = NBTHelpers.BoundInventory.fromNBT(tag.getCompound("boundinventory"));
		super.load(tag);
	}
}
