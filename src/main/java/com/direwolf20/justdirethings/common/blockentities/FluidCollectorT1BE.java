package com.direwolf20.justdirethings.common.blockentities;

import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.FluidContainerData;
import com.direwolf20.justdirethings.common.blockentities.basebe.FluidMachineBE;
import com.direwolf20.justdirethings.common.blockentities.basebe.RedstoneControlledBE;
import com.direwolf20.justdirethings.common.capabilities.JustDireFluidTank;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.interfacehelpers.RedstoneControlData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.LiquidBlock.LEVEL;

public class FluidCollectorT1BE extends BaseMachineBE implements RedstoneControlledBE, FluidMachineBE {
	public RedstoneControlData redstoneControlData = new RedstoneControlData();
	public final FluidContainerData fluidContainerData;
	protected final JustDireFluidTank fluidTank;
	List<BlockPos> positionsToPlace = new ArrayList<>();

	public FluidCollectorT1BE(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
		super(pType, pPos, pBlockState);
		MACHINE_SLOTS = 1;
		fluidTank = new JustDireFluidTank(getMaxMB());
		fluidContainerData = new FluidContainerData(this);
	}

	public FluidCollectorT1BE(BlockPos pPos, BlockState pBlockState) {
		this(Registration.FluidCollectorT1BE.get(), pPos, pBlockState);
	}

	@Override
	public ItemStackHandler getMachineHandler() {
		if (machineHandler == null) {
			machineHandler = new ItemStackHandler(MACHINE_SLOTS) {
				@Override
				protected void onContentsChanged(int slot) {
					setChanged();
				}

				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
					return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).isPresent();
				}
			};
		}
		return machineHandler;
	}

	@Override
	public int getMaxMB() {
		return 8000;
	}

	@Override
	public ContainerData getFluidContainerData() {
		return fluidContainerData;
	}

	@Override
	public JustDireFluidTank getFluidTank() {
		return fluidTank;
	}

	@Override
	public RedstoneControlData getRedstoneControlData() {
		return redstoneControlData;
	}

	@Override
	public BlockEntity getBlockEntity() {
		return this;
	}

	@Override
	public void tickClient() {
	}

	@Override
	public void tickServer() {
		super.tickServer();
		handleItemStack();
		doFluidCollect();
	}

	public void handleItemStack() {
		FluidStack fluidStack = getFluidStack();
		if (fluidStack.isEmpty())
			return;
		ItemStack itemStack = getItemStack();
		if (!isStackValidForDraining(itemStack, fluidStack))
			return;
		IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null)
				.orElse(null);
		if (fluidHandlerItem == null)
			return;
		int insertAmt = fluidHandlerItem.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE);
		if (insertAmt > 0) {
			FluidStack extractedStack = getFluidTank().drain(Math.min(insertAmt, 1000),
					IFluidHandler.FluidAction.EXECUTE);
			fluidHandlerItem.fill(extractedStack, IFluidHandler.FluidAction.EXECUTE);
			if (itemStack.getItem() instanceof BucketItem)
				getMachineHandler().setStackInSlot(0, fluidHandlerItem.getContainer());
		}
	}

	public ItemStack getItemStack() {
		return getMachineHandler().getStackInSlot(0);
	}

	public boolean isStackValidForDraining(ItemStack itemStack, FluidStack fluidStack) {
		if (itemStack.isEmpty())
			return false;
		if (fluidStack.isEmpty())
			return false;
		IFluidHandlerItem fluidHandlerItem = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null)
				.orElse(null);
		if (fluidHandlerItem == null)
			return false;
		return fluidHandlerItem.fill(fluidStack, IFluidHandler.FluidAction.SIMULATE) > 0;
	}

	public boolean isStackValid(FluidStack fluidStack) {
		if (fluidStack.isEmpty())
			return false;
		return fluidStack.getAmount() >= 1000;
	}

	public boolean canCollect() {
		return true;
	}

	public boolean clearTrackerIfNeeded() {
		if (positionsToPlace.isEmpty())
			return false;
		if (!canCollect())
			return true;
		if (!isActiveRedstone() && !redstoneControlData.redstoneMode.equals(MiscHelpers.RedstoneMode.PULSE))
			return true;
		return false;
	}

	public void doFluidCollect() {
		if (clearTrackerIfNeeded()) {
			positionsToPlace.clear();
			return;
		}
		if (!canCollect())
			return;
		FakePlayer fakePlayer = getFakePlayer((ServerLevel) level);
		if (isActiveRedstone() && canRun() && positionsToPlace.isEmpty())
			positionsToPlace = findSpotsToCollect(fakePlayer);
		if (positionsToPlace.isEmpty())
			return;
		if (canRun()) {
			BlockPos blockPos = positionsToPlace.remove(0);
			collectFluid(blockPos);
		}
	}

	public LiquidBlock getLiquidBlockAt(BlockPos blockPos) {
		if (level.getBlockState(blockPos).getBlock() instanceof LiquidBlock liquidBlock)
			return liquidBlock;
		return null;
	}

	public boolean collectFluid(BlockPos blockPos) {
		LiquidBlock liquidBlock = getLiquidBlockAt(blockPos);
		if (liquidBlock == null)
			return false;
		FluidStack testFluid = new FluidStack(level.getFluidState(blockPos).getType(), 1000);
		if (!isBlockValidForTank(testFluid))
			return false;
		if (getFluidTank().fill(testFluid, IFluidHandler.FluidAction.SIMULATE) < 1000)
			return false;
		if (level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3)) {
			getFluidTank().fill(testFluid, IFluidHandler.FluidAction.EXECUTE);
			level.playSound(null, blockPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1F, 1.0F);
			return true;
		}
		return false;
	}

	public boolean isBlockValidForTank(FluidStack fluidStack) {
		if (!getFluidStack().isEmpty() && !getFluidStack().getFluid().isSame(fluidStack.getFluid()))
			return false;
		return true;
	}

	public boolean isBlockPosValid(BlockPos blockPos, FakePlayer fakePlayer) {
		BlockState blockState = level.getBlockState(blockPos);
		if (!(blockState.getBlock() instanceof LiquidBlock))
			return false;
		if (blockState.getValue(LEVEL) != 0)
			return false;
		FluidStack testFluid = new FluidStack(level.getFluidState(blockPos).getType(), 1000);
		if (!isBlockValidForTank(testFluid))
			return false;
		if (!canPlaceAt(level, blockPos, fakePlayer))
			return false;
		return true;
	}

	public List<BlockPos> findSpotsToCollect(FakePlayer fakePlayer) {
		List<BlockPos> returnList = new ArrayList<>();
		BlockPos blockPos = getBlockPos().relative(getBlockState().getValue(BlockStateProperties.FACING));
		if (isBlockPosValid(blockPos, fakePlayer))
			returnList.add(blockPos);
		return returnList;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("fluidTank", fluidTank.serializeToNBT());
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("fluidTank"))
			fluidTank.deserializeFromNBT(tag.getCompound("fluidTank"));
	}
}
