package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.PhaseCollisionHelper;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * aiStep force-pushes the player out of any solid block they overlap
 * (moveTowardsClosestSpace sets velocity away from the block, overriding
 * input), which would slowly eject a phasing player from walls. Cancelling it
 * here is 1.20.1's stand-in for 1.21.1's collidesWithSuffocatingBlock override,
 * which has no injectable bytecode on Mixin 0.8.5.
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
