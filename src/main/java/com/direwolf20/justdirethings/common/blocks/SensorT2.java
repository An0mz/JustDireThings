package com.direwolf20.justdirethings.common.blocks;

import com.direwolf20.justdirethings.common.blockentities.SensorT1BE;
import com.direwolf20.justdirethings.common.blockentities.SensorT2BE;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.containers.SensorT2Container;
import com.direwolf20.justdirethings.common.items.FerricoreWrench;
import com.direwolf20.justdirethings.common.items.MachineSettingsCopier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.Direction;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

public class SensorT2 extends BaseMachineBlock {
	public SensorT2() {
		super(Properties.of().sound(SoundType.METAL).strength(2.0f).isRedstoneConductor(BaseMachineBlock::never));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SensorT2BE(pos, state);
	}

	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide)
			return InteractionResult.SUCCESS;

		ItemStack playerHolding = player.getItemInHand(hand);
		if (playerHolding.getItem() instanceof FerricoreWrench
				|| playerHolding.getItem() instanceof MachineSettingsCopier)
			return InteractionResult.PASS;

		BlockEntity te = level.getBlockEntity(blockPos);
		if (!(te instanceof SensorT2BE))
			return InteractionResult.FAIL;

		NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
				(windowId, playerInventory, playerEntity) -> new SensorT2Container(windowId, playerInventory, blockPos),
				Component.translatable("")), (buf -> {
					buf.writeBlockPos(blockPos);
				}));
		return InteractionResult.SUCCESS;
	}

	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
			@Nullable Direction direction) {
		return true;
	}

	@Override
	public boolean isSignalSource(BlockState pState) {
		return true;
	}

	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		BlockEntity blockEntity = blockAccess.getBlockEntity(pos);
		if (blockEntity instanceof SensorT1BE sensorT1BE) {
			return sensorT1BE.emitRedstone ? 15 : 0; // Emit full power if true, no power if false
		}
		return 0;
	}

	@Override
	public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		BlockEntity blockEntity = blockAccess.getBlockEntity(pos);
		if (blockEntity instanceof SensorT1BE sensorT1BE && sensorT1BE.strongSignal) {
			return getSignal(blockState, blockAccess, pos, side);
		}
		return 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return this.defaultBlockState().setValue(BlockStateProperties.FACING,
				context.getNearestLookingDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.FACING);
	}
}
