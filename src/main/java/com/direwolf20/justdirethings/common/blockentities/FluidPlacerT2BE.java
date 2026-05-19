package com.direwolf20.justdirethings.common.blockentities;

import com.direwolf20.justdirethings.common.blockentities.basebe.AreaAffectingBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.FilterableBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.PoweredMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.PoweredMachineContainerData;
import com.direwolf20.justdirethings.common.capabilities.MachineEnergyStorage;
import com.direwolf20.justdirethings.common.containers.handlers.FilterBasicHandler;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.interfacehelpers.AreaAffectingData;
import com.direwolf20.justdirethings.util.interfacehelpers.FilterData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FluidPlacerT2BE extends FluidPlacerT1BE implements PoweredMachineBE, AreaAffectingBE, FilterableBE {
	public FilterData filterData = new FilterData();
	public AreaAffectingData areaAffectingData = new AreaAffectingData(
			getBlockState().getValue(BlockStateProperties.FACING));
	public final PoweredMachineContainerData poweredMachineData;
	private final MachineEnergyStorage energyStorage;
	private final FilterBasicHandler filterHandler;

	public FluidPlacerT2BE(BlockPos pPos, BlockState pBlockState) {
		super(Registration.FluidPlacerT2BE.get(), pPos, pBlockState);
		fluidTank.setCapacity(getMaxMB());
		energyStorage = new MachineEnergyStorage(getMaxEnergy());
		filterHandler = new FilterBasicHandler(9);
		poweredMachineData = new PoweredMachineContainerData(this);
	}

	@Override
	public int getMaxMB() {
		return 32000;
	}

	@Override
	public ContainerData getContainerData() {
		return poweredMachineData;
	}

	@Override
	public MachineEnergyStorage getEnergyStorage() {
		return energyStorage;
	}

	@Override
	public int getStandardEnergyCost() {
		return 500;
	}

	@Override
	public BlockEntity getBlockEntity() {
		return this;
	}

	@Override
	public AreaAffectingData getAreaAffectingData() {
		return areaAffectingData;
	}

	@Override
	public FilterBasicHandler getFilterHandler() {
		return filterHandler;
	}

	@Override
	public FilterData getFilterData() {
		return filterData;
	}

	@Override
	public boolean canPlace() {
		return hasEnoughPower(getStandardEnergyCost());
	}

	@Override
	public boolean placeFluid(FluidStack fluidStack, BlockPos blockPos) {
		boolean success = super.placeFluid(fluidStack, blockPos);
		if (success)
			extractEnergy(getStandardEnergyCost(), false);
		return success;
	}

	@Override
	public List<BlockPos> findSpotsToPlace(FakePlayer fakePlayer) {
		AABB area = getAABB(getBlockPos());
		return BlockPos
				.betweenClosedStream((int) area.minX, (int) area.minY, (int) area.minZ, (int) area.maxX - 1,
						(int) area.maxY - 1, (int) area.maxZ - 1)
				.filter(blockPos -> isBlockPosValid(blockPos, fakePlayer)).map(BlockPos::immutable)
				.sorted(Comparator.comparingDouble(x -> x.distSqr(getBlockPos()))).collect(Collectors.toList());
	}

	@Override
	public boolean isBlockPosValid(BlockPos blockPos, FakePlayer fakePlayer) {
		if (!super.isBlockPosValid(blockPos, fakePlayer))
			return false;
		ItemStack blockItemStack = level.getBlockState(blockPos.relative(getDirectionValue()))
				.getCloneItemStack(new net.minecraft.world.phys.BlockHitResult(net.minecraft.world.phys.Vec3.ZERO,
						getDirectionValue(), blockPos, false), level, blockPos, null);
		return isStackValidFilter(blockItemStack);
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("energy", energyStorage.getEnergyStored());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("energy"))
			energyStorage.setEnergy(tag.getInt("energy"));
	}
}
