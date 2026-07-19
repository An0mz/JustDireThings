package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.PhaseCollisionHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * While phasing the player's box genuinely overlaps solid blocks, so
 * suppress isInWall (suffocation damage) and filter the pose-fit check.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

	@Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
	private void onIsInWall(CallbackInfoReturnable<Boolean> cir) {
		if ((Object) this instanceof Player player && PhaseCollisionHelper.shouldPassThroughWalls(player)) {
			cir.setReturnValue(false);
		}
	}

	/**
	 * Pose selection and the client's crouching flag both gate on this fit
	 * check; unfiltered, it would silently drop crouch (and its slowdown)
	 * the moment a phasing player enters a wall.
	 */
	@Redirect(method = "canEnterPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z"))
	private boolean onCanEnterPoseNoCollision(Level level, Entity entity, AABB box) {
		return PhaseCollisionHelper.filteredNoCollision(level, entity, box);
	}
}
