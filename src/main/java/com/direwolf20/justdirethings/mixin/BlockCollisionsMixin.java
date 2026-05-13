package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.datagen.JustDireBlockTags;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockCollisions.class)
public abstract class BlockCollisionsMixin<T> {
    @Final @Shadow private CollisionContext context;
    @Final @Shadow private BlockPos.MutableBlockPos pos;

    @Inject(method = "computeNext", at = @At("RETURN"), cancellable = true)
    @SuppressWarnings("unchecked")
    private void jdt$filterPhaseableCollisions(CallbackInfoReturnable<T> cir) {
        T result = cir.getReturnValue();
        if (!(result instanceof VoxelShape shape)) return;

        Player player = jdt$getPhasingPlayer();
        if (player == null || jdt$shouldKeepCollision(player)) return;

        cir.setReturnValue((T) Shapes.empty());
    }

    @Unique
    private Player jdt$getPhasingPlayer() {
        if (!(this.context instanceof EntityCollisionContext ecc)) return null;
        if (!(ecc.getEntity() instanceof Player player)) return null;
        return player.getAttributeValue(Registration.PHASE.get()) > 0 ? player : null;
    }

    /**
     * Returns true if this block's collision should be kept (not phased through).
     * Keeps: unbreakable blocks, PHASEDENY-tagged blocks, and floor blocks
     * directly under the player's actual bounding box.
     */
    @Unique
    private boolean jdt$shouldKeepCollision(Player player) {
        Level level = player.level();
        BlockPos blockPos = this.pos.immutable();
        BlockState blockState = level.getBlockState(blockPos);

        // Always keep unbreakable blocks and any block tagged PHASEDENY.
        if (blockState.getDestroySpeed(level, blockPos) < 0 || blockState.is(JustDireBlockTags.PHASEDENY)) {
            return true;
        }

        // Keep floor blocks: block top is at or below player's feet,
        // AND the block horizontally overlaps the player's actual (non-swept) bounding box.
        AABB playerBB = player.getBoundingBox();
        double blockTopY = this.pos.getY() + 1.0D;

        if (blockTopY <= playerBB.minY + 0.6D) {
            double bMinX = this.pos.getX(), bMaxX = bMinX + 1.0D;
            double bMinZ = this.pos.getZ(), bMaxZ = bMinZ + 1.0D;
            if (bMaxX > playerBB.minX && bMinX < playerBB.maxX &&
                bMaxZ > playerBB.minZ && bMinZ < playerBB.maxZ) {
                return true; // floor block directly under player, keep it
            }
        }

        // Filter: player phases through this block
        return false;
    }
}
