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
 * Shared by CollisionMixin (client/server movement resolution) and
 * PlayerMovementMixin (the server's move-validation checks). In 1.21.1 a
 * single interface mixin on CollisionGetter.getBlockCollisions covers every
 * caller at once; Mixin 0.8.5 can't do that (interface mixins with
 * cancellable injects are rejected), so each call site is redirected
 * separately and they all share this logic.
 * <p>
 * Deliberately NOT in the mixin package - mixins.justdirethings.json marks
 * that whole package as mixin-only, and code transformed by a mixin in that
 * package (e.g. Entity's redirected method) is blocked at classload time
 * from referencing anything else living there.
 */
public final class PhaseCollisionHelper {
	private PhaseCollisionHelper() {
	}

	public static boolean shouldPassThroughWalls(Player player) {
		return player.getAttributeValue(Registration.PHASE.get()) > 0;
	}

	public static Iterable<VoxelShape> filterBlockCollisions(BlockGetter level, Entity entity, Iterable<VoxelShape> original) {
		// The swept box collideBoundingBox queries with is pre-expanded toward
		// the movement vector, so its minY sits below the actual feet while
		// falling - every floor top would read as "above the feet" and get
		// phased, dropping the player to bedrock. Measure from the entity's
		// real, unexpanded feet instead.
		double feetY = entity.getBoundingBox().minY;
		List<VoxelShape> filtered = new ArrayList<>();
		for (VoxelShape shape : original) {
			if (isVerticalCollision(level, shape, feetY))
				filtered.add(shape);
		}
		return filtered;
	}

	/**
	 * A shape stays solid only if its top surface sits at or below the
	 * entity's feet: the floor you stand on, and any ground you fall onto -
	 * including the floor beneath a wall column, so you don't drop through
	 * the ground while inside a wall. Anything rising above the feet is
	 * phased through. 1.21.1 instead keeps every surface within +/-0.75 of
	 * the feet, but the "up to 0.75 above" half of that band re-solidifies a
	 * wall's lower block the moment a jump lifts your feet into it, freezing
	 * you mid-air - the signed check lets you jump normally while inside a
	 * wall. The trade-off is that low steps (slabs, stairs) are also phased
	 * through at floor level instead of stepped onto, which is consistent
	 * with how the ability treats everything else. Unbreakable and
	 * PHASEDENY-tagged blocks always stay solid.
	 */
	public static boolean isVerticalCollision(BlockGetter level, VoxelShape shape, double feetY) {
		// Cheap geometric test first: shapes at or below the feet are kept
		// regardless of block type, so the common case (the floor beneath the
		// entity) never pays for a block-state lookup.
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

		// Here box IS the candidate position's unexpanded bounding box, so its
		// minY is the correct feet reference (unlike the swept box above).
		for (VoxelShape shape : level.getBlockCollisions(entity, box)) {
			if (!shape.isEmpty() && isVerticalCollision(level, shape, box.minY))
				return false;
		}
		return true;
	}
}
