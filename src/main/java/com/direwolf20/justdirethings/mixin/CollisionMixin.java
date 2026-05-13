package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.datagen.JustDireBlockTags;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockCollisions;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Mixin(CollisionGetter.class)
public interface CollisionMixin {

    @Unique Logger JDT_LOGGER = LogUtils.getLogger();

    @Inject(method = "collidesWithSuffocatingBlock(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z", at = @At("HEAD"), cancellable = true)
    default void collidesWithSuffocatingBlock(Entity entity, AABB box, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player player && jdt$shouldPassThroughWalls(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getBlockCollisions", at = @At("HEAD"), cancellable = true)
    default void onGetBlockCollisions(Entity entity, AABB collisionBox, CallbackInfoReturnable<Iterable<VoxelShape>> cir) {
        if (entity instanceof Player player && jdt$shouldPassThroughWalls(player)) {
            Iterable<VoxelShape> originalBlockCollisions = jdt$getOriginalBlockCollisions(entity, collisionBox);
            List<VoxelShape> filteredBlockCollisions = StreamSupport.stream(originalBlockCollisions.spliterator(), false)
                    .filter(shape -> jdt$isVerticalCollision(shape, collisionBox, player))
                    .collect(Collectors.toList());
            cir.setReturnValue(filteredBlockCollisions);
        }
    }

    @Unique
    default Iterable<VoxelShape> jdt$getOriginalBlockCollisions(Entity entity, AABB collisionBox) {
        return () -> new BlockCollisions<>((CollisionGetter) this, entity, collisionBox, false, (p_286215_, p_286216_) -> p_286216_);
    }

    @Unique
    default boolean jdt$isVerticalCollision(VoxelShape shape, AABB collisionBox, Player player) {
        if (shape.isEmpty()) return false;
        Level level = player.level();
        BlockPos blockPos = new BlockPos((int) shape.min(Direction.Axis.X), (int) shape.min(Direction.Axis.Y), (int) shape.min(Direction.Axis.Z));
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.getDestroySpeed(level, blockPos) < 0 || blockState.is(JustDireBlockTags.PHASEDENY))
            return true;
        double maxY = shape.max(Direction.Axis.Y);
        double minY = collisionBox.minY;
        return Math.abs(maxY - minY) < 0.75;
    }

    @Unique
    default boolean jdt$shouldPassThroughWalls(Player player) {
        return player.getAttributeValue(Registration.PHASE.get()) > 0;
    }
}
