package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.PhaseCollisionHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This is 1.20.1's stand-in for the collidesWithSuffocatingBlock override in
 * 1.21.1's CollisionMixin. LocalPlayer.aiStep() calls
 * moveTowardsClosestSpace(...) for each corner of the player's bounding box
 * every tick; when suffocatesAt(pos) (backed by collidesWithSuffocatingBlock)
 * reports the corner is inside a solid block, it force-sets the player's
 * horizontal deltaMovement to 0.1 toward the nearest open side - overriding
 * WASD input entirely. For a phasing player deliberately standing inside a
 * wall, that shows up as being slowly and irresistibly walked back out of
 * the block, no matter what the server-side collision handling allows.
 * Since collidesWithSuffocatingBlock is only a CollisionGetter default
 * method (no bytecode to inject into on Mixin 0.8.5), cancel the push at its
 * only consumer instead.
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

	@Inject(method = "moveTowardsClosestSpace", at = @At("HEAD"), cancellable = true)
	private void onMoveTowardsClosestSpace(double x, double z, CallbackInfo ci) {
		if (PhaseCollisionHelper.shouldPassThroughWalls((Player) (Object) this)) {
			ci.cancel();
		}
	}
}
