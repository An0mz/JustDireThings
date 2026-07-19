package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.PhaseCollisionHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * In 1.21.1 the CollisionGetter interface mixin filters every collision
 * query at once, so the server's move-packet validation automatically agrees
 * with movement resolution. Our 1.20.1 CollisionMixin only covers the
 * Entity.collideBoundingBox call site, so handleMovePlayer/handleMoveVehicle
 * would still validate against unfiltered collisions and rubber-band a
 * phasing player. Two paths need covering:
 * <p>
 * 1. ServerLevel.noCollision(entity, box) - consulted when the "moved
 * wrongly" heuristic flags a move as suspicious.
 * <p>
 * 2. isPlayerCollidingWithAnythingNew(...), which gates ordinary movement.
 * Its internal old-vs-new shape comparison (Shapes.joinIsNotEmpty) sees a
 * wall the player is stepping into as a "new" collision and rejects the
 * move, so filtering its inputs isn't enough - the whole call is replaced
 * with a direct "would the new position collide with anything that isn't a
 * wall" check for phasing players. Non-phasing players still go through the
 * original vanilla method via the @Shadow below.
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class PlayerMovementMixin {

	@Shadow
	private ServerPlayer player;

	@Shadow
	private boolean isPlayerCollidingWithAnythingNew(LevelReader reader, AABB oldPositionBox, double x, double y, double z) {
		throw new UnsupportedOperationException();
	}

	@Redirect(method = {"handleMovePlayer", "handleMoveVehicle"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z"))
	private boolean onNoCollision(ServerLevel level, Entity entity, AABB box) {
		return PhaseCollisionHelper.filteredNoCollision(level, entity, box);
	}

	@Redirect(method = {"handleMovePlayer", "handleMoveVehicle"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;isPlayerCollidingWithAnythingNew(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/phys/AABB;DDD)Z"))
	private boolean onIsPlayerCollidingWithAnythingNew(ServerGamePacketListenerImpl self, LevelReader reader, AABB oldPositionBox, double x, double y, double z) {
		if (!PhaseCollisionHelper.shouldPassThroughWalls(player))
			return isPlayerCollidingWithAnythingNew(reader, oldPositionBox, x, y, z);

		AABB newBox = player.getBoundingBox().move(x - player.getX(), y - player.getY(), z - player.getZ());
		return !PhaseCollisionHelper.filteredNoCollision(player.level(), player, newBox);
	}
}
