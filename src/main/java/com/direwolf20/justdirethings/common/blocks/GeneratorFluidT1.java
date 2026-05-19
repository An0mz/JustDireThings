package com.direwolf20.justdirethings.common.blocks;

import com.direwolf20.justdirethings.common.blockentities.GeneratorFluidT1BE;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.containers.GeneratorFluidT1Container;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class GeneratorFluidT1 extends BaseMachineBlock {
	public GeneratorFluidT1() {
		super(Properties.of().sound(SoundType.METAL).strength(2.0f).isRedstoneConductor(BaseMachineBlock::never));
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GeneratorFluidT1BE(pos, state);
	}

	@Override
	public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player,
			InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide)
			return InteractionResult.SUCCESS;
		BlockEntity te = level.getBlockEntity(blockPos);
		if (!(te instanceof GeneratorFluidT1BE))
			return InteractionResult.FAIL;
		NetworkHooks.openScreen((ServerPlayer) player,
				new SimpleMenuProvider((windowId, playerInventory,
						playerEntity) -> new GeneratorFluidT1Container(windowId, playerInventory, blockPos),
						Component.translatable("")),
				buf -> buf.writeBlockPos(blockPos));
		return InteractionResult.SUCCESS;
	}
}
