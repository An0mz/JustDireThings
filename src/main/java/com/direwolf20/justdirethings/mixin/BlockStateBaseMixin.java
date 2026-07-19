package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.util.PhaseCollisionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The one choke point every collision system flows through: vanilla's
 * BlockCollisions iterator, the server's move validation, pose-fit checks, and
 * optimization mods' replacement sweepers (Lithium/Radium overwrite
 * Entity.collideBoundingBox, but their sweeper still asks each block state for
 * its collision shape with the querying entity's context). Returning an empty
 * shape here makes walls non-solid for phasing players everywhere at once - and
 * avoids injecting into anything those mods replace.
 */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

	@Inject(method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", at = @At("RETURN"), cancellable = true)
	private void onGetCollisionShape(BlockGetter level, BlockPos pos, CollisionContext context,
			CallbackInfoReturnable<VoxelShape> cir) {
		VoxelShape shape = cir.getReturnValue();
		if (shape.isEmpty() || !(context instanceof EntityCollisionContext entityContext))
			return;
		Entity entity = entityContext.getEntity();
		if (!(entity instanceof Player player) || !PhaseCollisionHelper.shouldPassThroughWalls(player))
			return;
		if (!PhaseCollisionHelper.isPhaseSolid(level, pos, (BlockState) (Object) this, shape, entity))
			cir.setReturnValue(Shapes.empty());
	}
}
