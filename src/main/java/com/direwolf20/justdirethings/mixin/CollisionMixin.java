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
 * Filters wall shapes out of movement collision for phasing players. Targets
 * the call site inside Entity.collideBoundingBox because Mixin 0.8.5 cannot
 * inject into CollisionGetter's default methods. Server move validation and the
 * client's push-out-of-blocks logic need their own hooks - see
 * PlayerMovementMixin and LocalPlayerMixin.
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
