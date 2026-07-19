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
 * The server validates move packets against unfiltered collisions, which
 * would rubber-band a phasing player. Covers both validation paths:
 * ServerLevel.noCollision (suspicious moves) and
 * isPlayerCollidingWithAnythingNew (ordinary moves) - the latter is replaced
 * wholesale while phasing because its old-vs-new shape comparison rejects
 * any wall being stepped into even when the inputs are filtered.
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
