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
 * Matches 1.21.1's EntityMixin: while phasing, a player's bounding box
 * genuinely overlaps solid block shapes (that's the point), so isInWall must
 * be suppressed or vanilla's suffocation logic would damage them for
 * standing inside a wall.
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
	 * canEnterPose asks "does this pose's bounding box fit here" via an
	 * unfiltered Level.noCollision - inside a wall every pose box overlaps
	 * wall shapes, so it reports the pose doesn't fit. Player.updatePlayerPose
	 * and LocalPlayer's crouching flag both gate on it, so a phasing player
	 * who crouch-walks into a wall silently loses the crouch state (and its
	 * slowdown) the moment they enter. Filtering the query keeps crouching
	 * (and any other pose) working inside walls; non-phasing entities fall
	 * through to the vanilla check inside filteredNoCollision.
	 */
	@Redirect(method = "canEnterPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z"))
	private boolean onCanEnterPoseNoCollision(Level level, Entity entity, AABB box) {
		return PhaseCollisionHelper.filteredNoCollision(level, entity, box);
	}
}
