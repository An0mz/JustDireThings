package com.direwolf20.justdirethings.common.blocks;

import com.direwolf20.justdirethings.common.blockentities.ExperienceHolderBE;
import com.direwolf20.justdirethings.common.blocks.baseblocks.BaseMachineBlock;
import com.direwolf20.justdirethings.common.containers.ExperienceHolderContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ExperienceHolder extends BaseMachineBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    protected static final VoxelShape[] shapes = new VoxelShape[]{
            Stream.of(
                    Block.box(4, 4, 6, 6, 10, 10),
                    Block.box(4, 0, 4, 12, 1, 12),
                    Block.box(3, 1, 3, 13, 2, 13),
                    Block.box(6, 4, 10, 10, 10, 12),
                    Block.box(6, 4, 4, 10, 10, 6),
                    Block.box(10, 4, 6, 12, 10, 10),
                    Block.box(5, 2, 5, 11, 9, 11),
                    Block.box(6, 9, 6, 10, 11, 10),
                    Block.box(4, 3, 4, 12, 4, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), // UP
            Stream.of(
                    Block.box(4, 6, 6, 6, 12, 10),
                    Block.box(4, 15, 4, 12, 16, 12),
                    Block.box(3, 14, 3, 13, 15, 13),
                    Block.box(6, 6, 10, 10, 12, 12),
                    Block.box(6, 6, 4, 10, 12, 6),
                    Block.box(10, 6, 6, 12, 12, 10),
                    Block.box(5, 7, 5, 11, 14, 11),
                    Block.box(6, 5, 6, 10, 7, 10),
                    Block.box(4, 12, 4, 12, 13, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), // DOWN
            Stream.of(
                    Block.box(4, 6, 4, 6, 10, 10),
                    Block.box(4, 4, 0, 12, 12, 1),
                    Block.box(3, 3, 1, 13, 13, 2),
                    Block.box(6, 4, 4, 10, 6, 10),
                    Block.box(6, 10, 4, 10, 12, 10),
                    Block.box(10, 6, 4, 12, 10, 10),
                    Block.box(5, 5, 2, 11, 11, 9),
                    Block.box(6, 6, 9, 10, 10, 11),
                    Block.box(4, 4, 3, 12, 12, 4)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), // SOUTH
            Stream.of(
                    Block.box(10, 6, 6, 12, 10, 12),
                    Block.box(4, 4, 15, 12, 12, 16),
                    Block.box(3, 3, 14, 13, 13, 15),
                    Block.box(6, 4, 6, 10, 6, 12),
                    Block.box(6, 10, 6, 10, 12, 12),
                    Block.box(4, 6, 6, 6, 10, 12),
                    Block.box(5, 5, 7, 11, 11, 14),
                    Block.box(6, 6, 5, 10, 10, 7),
                    Block.box(4, 4, 12, 12, 12, 13)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), // NORTH
            Stream.of(
                    Block.box(4, 6, 10, 10, 10, 12),
                    Block.box(0, 4, 4, 1, 12, 12),
                    Block.box(1, 3, 3, 2, 13, 13),
                    Block.box(4, 4, 6, 10, 6, 10),
                    Block.box(4, 10, 6, 10, 12, 10),
                    Block.box(4, 6, 4, 10, 10, 6),
                    Block.box(2, 5, 5, 9, 11, 11),
                    Block.box(9, 6, 6, 11, 10, 10),
                    Block.box(3, 4, 4, 4, 12, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get(), // EAST
            Stream.of(
                    Block.box(6, 6, 4, 12, 10, 6),
                    Block.box(15, 4, 4, 16, 12, 12),
                    Block.box(14, 3, 3, 15, 13, 13),
                    Block.box(6, 4, 6, 12, 6, 10),
                    Block.box(6, 10, 6, 12, 12, 10),
                    Block.box(6, 6, 10, 12, 10, 12),
                    Block.box(7, 5, 5, 14, 11, 11),
                    Block.box(5, 6, 6, 7, 10, 10),
                    Block.box(12, 4, 4, 13, 12, 12)
            ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get() // WEST
    };

    public ExperienceHolder() {
        super(Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0f)
                .noOcclusion()
                .isRedstoneConductor(BaseMachineBlock::never)
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExperienceHolderBE(pos, state);
    }

    @Override
    public InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockEntity te = level.getBlockEntity(blockPos);
        if (!(te instanceof ExperienceHolderBE)) return InteractionResult.FAIL;
        NetworkHooks.openScreen((ServerPlayer) player, new SimpleMenuProvider(
                (windowId, playerInventory, playerEntity) -> new ExperienceHolderContainer(windowId, playerInventory, blockPos),
                Component.translatable("")), buf -> buf.writeBlockPos(blockPos));
        return InteractionResult.SUCCESS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return shapes[state.getValue(FACING).get3DDataValue()];
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return shapes[state.getValue(FACING).get3DDataValue()];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter getter, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }
}

