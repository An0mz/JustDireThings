package com.direwolf20.justdirethings.util;

import com.direwolf20.justdirethings.datagen.JustDireBlockTags;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared wall-vs-floor collision logic for the Phase ability mixins. Must NOT
 * live in the mixin package: classes there can't be referenced from
 * mixin-transformed code at runtime.
 */
public final class PhaseCollisionHelper {
	private PhaseCollisionHelper() {
	}

	public static boolean shouldPassThroughWalls(Player player) {
		return player.getAttributeValue(Registration.PHASE.get()) > 0;
	}

	public static Iterable<VoxelShape> filterBlockCollisions(BlockGetter level, Entity entity,
			Iterable<VoxelShape> original) {
		double feetY = entity.getBoundingBox().minY;
		List<VoxelShape> filtered = new ArrayList<>();
		for (VoxelShape shape : original) {
			if (isVerticalCollision(level, shape, entity, feetY))
				filtered.add(shape);
		}
		return filtered;
	}

	public static boolean isVerticalCollision(BlockGetter level, VoxelShape shape, Entity entity, double feetY) {
		double maxY = shape.max(Direction.Axis.Y);
		if (maxY <= feetY + 0.01)
			return true;
		if (entity.onGround() && maxY - feetY < 0.75)
			return true;

		BlockPos blockPos = new BlockPos(Mth.floor(shape.min(Direction.Axis.X)), Mth.floor(shape.min(Direction.Axis.Y)),
				Mth.floor(shape.min(Direction.Axis.Z)));
		BlockState blockState = level.getBlockState(blockPos);
		return blockState.getDestroySpeed(level, blockPos) < 0 || blockState.is(JustDireBlockTags.PHASEDENY);
	}
	
	public static boolean filteredNoCollision(Level level, Entity entity, AABB box) {
		if (!(entity instanceof Player player) || !shouldPassThroughWalls(player))
			return level.noCollision(entity, box);

		if (!level.getEntityCollisions(entity, box).isEmpty())
			return false;

		for (VoxelShape shape : level.getBlockCollisions(entity, box)) {
			if (!shape.isEmpty() && isVerticalCollision(level, shape, entity, box.minY))
				return false;
		}
		return true;
	}
}
