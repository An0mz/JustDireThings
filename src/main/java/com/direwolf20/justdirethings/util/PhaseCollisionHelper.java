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
 * Shared wall-vs-floor collision logic for the Phase ability mixins.
 * Must NOT live in the mixin package: classes there can't be referenced
 * from mixin-transformed code at runtime.
 */
public final class PhaseCollisionHelper {
	private PhaseCollisionHelper() {
	}

	public static boolean shouldPassThroughWalls(Player player) {
		return player.getAttributeValue(Registration.PHASE.get()) > 0;
	}

	public static Iterable<VoxelShape> filterBlockCollisions(BlockGetter level, Entity entity, Iterable<VoxelShape> original) {
		// Measure from the entity's real feet, not the swept query box - its
		// bottom sits below the feet while falling, which would phase floors.
		double feetY = entity.getBoundingBox().minY;
		List<VoxelShape> filtered = new ArrayList<>();
		for (VoxelShape shape : original) {
			if (isVerticalCollision(level, shape, feetY))
				filtered.add(shape);
		}
		return filtered;
	}

	/**
	 * A shape stays solid only if its top sits at or below the feet (floors
	 * and landing surfaces); anything rising above them is phased through.
	 * Deliberately signed, unlike 1.21.1's +/-0.75 band, whose above-feet
	 * half re-solidifies a wall's lower block mid-jump and freezes the
	 * player. Trade-off: slabs/stairs are phased through instead of stepped
	 * onto. Unbreakable and PHASEDENY blocks always stay solid.
	 */
	public static boolean isVerticalCollision(BlockGetter level, VoxelShape shape, double feetY) {
		// Geometry first so floor shapes (the common case) skip the
		// block-state lookup.
		if (shape.max(Direction.Axis.Y) <= feetY + 0.01)
			return true;

		BlockPos blockPos = new BlockPos(Mth.floor(shape.min(Direction.Axis.X)), Mth.floor(shape.min(Direction.Axis.Y)),
				Mth.floor(shape.min(Direction.Axis.Z)));
		BlockState blockState = level.getBlockState(blockPos);
		return blockState.getDestroySpeed(level, blockPos) < 0 || blockState.is(JustDireBlockTags.PHASEDENY);
	}

	/** Mirrors CollisionGetter.noCollision(Entity, AABB) but with wall shapes filtered out while phasing. */
	public static boolean filteredNoCollision(Level level, Entity entity, AABB box) {
		if (!(entity instanceof Player player) || !shouldPassThroughWalls(player))
			return level.noCollision(entity, box);

		if (!level.getEntityCollisions(entity, box).isEmpty())
			return false;

		// box is the candidate position's unexpanded bounding box, so its minY
		// is the feet reference.
		for (VoxelShape shape : level.getBlockCollisions(entity, box)) {
			if (!shape.isEmpty() && isVerticalCollision(level, shape, box.minY))
				return false;
		}
		return true;
	}
}
