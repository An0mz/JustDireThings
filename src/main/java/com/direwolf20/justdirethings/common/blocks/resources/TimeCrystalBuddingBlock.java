package com.direwolf20.justdirethings.common.blocks.resources;

import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;

import java.util.List;
import java.util.Random;

public class TimeCrystalBuddingBlock extends BuddingAmethystBlock {
    private final static Random rand = new Random();
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);

    public TimeCrystalBuddingBlock() {
        super(Properties.of()
                .sound(SoundType.AMETHYST)
                .randomTicks()
                .strength(1.5F));
    }

    public int canAdvanceToCustom(Level level, BlockState state) {
        int stage = state.getValue(STAGE);
        if (stage == 0) {
            List<? extends String> allowedDims = Config.TIME_CRYSTAL_STAGE1_DIMENSIONS.get();
            if (allowedDims.contains(level.dimension().location().toString()))
                return 1;
        }
        if (stage == 1) {
            List<? extends String> allowedDims = Config.TIME_CRYSTAL_STAGE2_DIMENSIONS.get();
            if (allowedDims.contains(level.dimension().location().toString()))
                return 2;
        }
        if (stage == 2) {
            List<? extends String> allowedDims = Config.TIME_CRYSTAL_STAGE3_DIMENSIONS.get();
            if (allowedDims.contains(level.dimension().location().toString()))
                return 3;
        }
        return -1;
    }

    public int canAdvanceTo(Level level, BlockState state) {
        int stage = state.getValue(STAGE);
        if (stage == 0 && (level.dimension() != Level.NETHER && level.dimension() != Level.END))
            return 1;
        if (stage == 1 && level.dimension() == Level.NETHER)
            return 2;
        if (stage == 2 && level.dimension() == Level.END)
            return 3;
        return -1;
    }

    public void advance(Level level, BlockState state, BlockPos pos, int advanceTo) {
        level.setBlockAndUpdate(pos, state.setValue(STAGE, advanceTo));
        level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0F, 0.25F);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int stage = state.getValue(STAGE);
        int advanceTo = Config.TIME_CRYSTAL_CUSTOM_DIMENSIONS.get() ? canAdvanceToCustom(level, state) : canAdvanceTo(level, state);
        if (advanceTo != -1) {
            advance(level, state, pos, advanceTo);
        }
        if (stage != 3) return;
        if (random.nextInt(5) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos blockpos = pos.relative(direction);
            BlockState blockstate = level.getBlockState(blockpos);
            Block block = null;
            if (canClusterGrowAtState(blockstate)) {
                block = Registration.TimeCrystalCluster_Small.get();
            } else if (blockstate.is(Registration.TimeCrystalCluster_Small.get()) && blockstate.getValue(AmethystClusterBlock.FACING) == direction) {
                block = Registration.TimeCrystalCluster_Medium.get();
            } else if (blockstate.is(Registration.TimeCrystalCluster_Medium.get()) && blockstate.getValue(AmethystClusterBlock.FACING) == direction) {
                block = Registration.TimeCrystalCluster_Large.get();
            } else if (blockstate.is(Registration.TimeCrystalCluster_Large.get()) && blockstate.getValue(AmethystClusterBlock.FACING) == direction) {
                block = Registration.TimeCrystalCluster.get();
            }

            if (block != null) {
                BlockState blockstate1 = block.defaultBlockState()
                        .setValue(TimeCrystalCluster.FACING, direction)
                        .setValue(TimeCrystalCluster.WATERLOGGED, Boolean.valueOf(blockstate.getFluidState().getType() == Fluids.WATER));
                level.setBlockAndUpdate(blockpos, blockstate1);

                if (state.getValue(STAGE) == 3 && level.random.nextFloat() < 0.05f) {
                    level.setBlockAndUpdate(pos, state.setValue(STAGE, 0));
                    level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.BLOCKS, 1.0F, 0.25F);
                }
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(STAGE, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(STAGE) == 3) return;
        double d0 = (double) pos.getX() + 0.5;
        double d1 = (double) pos.getY() + 0.5;
        double d2 = (double) pos.getZ() + 0.5;

        int advanceTo = Config.TIME_CRYSTAL_CUSTOM_DIMENSIONS.get() ? canAdvanceToCustom(level, state) : canAdvanceTo(level, state);
        if (advanceTo == -1) return;

        ParticleOptions particle;
        if (advanceTo == 1) {
            particle = ParticleTypes.ENCHANT;
        } else if (advanceTo == 2) {
            particle = ParticleTypes.FLAME;
        } else {
            particle = ParticleTypes.DRAGON_BREATH;
        }

        for (int i = 0; i < 3; i++) {
            double offsetX = random.nextBoolean() ? -0.5 + random.nextDouble() * 0.5 : 1.0 + random.nextDouble() * 0.5;
            double offsetY = random.nextBoolean() ? -0.5 + random.nextDouble() * 0.5 : 1.0 + random.nextDouble() * 0.5;
            double offsetZ = random.nextBoolean() ? -0.5 + random.nextDouble() * 0.5 : 1.0 + random.nextDouble() * 0.5;

            double startX = (double) pos.getX() + offsetX;
            double startY = (double) pos.getY() + offsetY;
            double startZ = (double) pos.getZ() + offsetZ;

            level.addParticle(particle, startX, startY, startZ, 0.025, 0.025, 0.025);
        }
    }
}
