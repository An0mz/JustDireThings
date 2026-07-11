package com.direwolf20.justdirethings.common.blocks;

import com.direwolf20.justdirethings.common.blockentities.FluidPlacerT1BE;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.containers.FluidPlacerT1Container;
import com.direwolf20.justdirethings.common.items.FerricoreWrench;
import com.direwolf20.justdirethings.common.items.MachineSettingsCopier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class FluidPlacerT1 extends BaseMachineBlock {
	public FluidPlacerT1() {
		super(Properties.of().sound(SoundType.METAL).strength(2.0f).isRedstoneConductor(BaseMachineBlock::never));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new FluidPlacerT1BE(pos, state);
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
		if (!(te instanceof FluidPlacerT1BE))
			return InteractionResult.FAIL;
		NetworkHooks
				.openScreen((ServerPlayer) player,
						new SimpleMenuProvider((windowId, playerInventory,
								playerEntity) -> new FluidPlacerT1Container(windowId, playerInventory, blockPos),
								Component.translatable("")),
						buf -> buf.writeBlockPos(blockPos));
		return InteractionResult.SUCCESS;
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
