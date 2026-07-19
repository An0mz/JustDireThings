package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.PhaseCollisionHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Entity.collideBoundingBox(Entity, Vec3, AABB, Level, List) is a real,
 * concrete static method declared on Entity itself (verified via javap) that
 * calls Level.getBlockCollisions(entity, box) to gather the shapes movement
 * gets swept against. Redirecting that call site avoids two dead ends:
 * mixing into the CollisionGetter interface directly throws
 * InvalidInterfaceMixinException for any cancellable @Inject on Mixin 0.8.5,
 * and @Inject-ing into Level.getBlockCollisions silently no-ops because
 * Level's own class file has no bytecode for that method - it's only ever
 * present as a CollisionGetter default, and Mixin 0.8.5 does not synthesize
 * an override to attach the injection to.
 * <p>
 * This covers both client and server movement resolution. The server's
 * packet-validation checks re-query collisions through paths this redirect
 * can't reach - see PlayerMovementMixin. The client's "push the player out
 * of solid blocks" behavior is likewise separate - see LocalPlayerMixin.
 */
@Mixin(Entity.class)
public abstract class CollisionMixin {

	@Redirect(method = "collideBoundingBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockCollisions(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/lang/Iterable;"))
	private static Iterable<VoxelShape> onGetBlockCollisions(Level level, Entity entity, AABB collisionBox) {
		Iterable<VoxelShape> original = level.getBlockCollisions(entity, collisionBox);
		if (!(entity instanceof Player player) || !PhaseCollisionHelper.shouldPassThroughWalls(player))
			return original;
		return PhaseCollisionHelper.filterBlockCollisions(level, entity, original);
	}
}
