package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.direwolf20.justdirethings.datagen.JustDireBlockTags;
import com.google.common.collect.ImmutableList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    private static Vec3 collideWithShapes(Vec3 movement, AABB collisionBox, List<VoxelShape> shapes) {
        throw new AssertionError();
    }

    @Inject(method = "isInWall()Z", at = @At("HEAD"), cancellable = true)
    private void onIsInWall(CallbackInfoReturnable<Boolean> cir) {
        Entity self = (Entity) (Object) this;
        if (self instanceof Player player && jdt$shouldPassThroughWalls(player)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    private static void jdt$collideBoundingBox(@Nullable Entity entity, Vec3 movement, AABB collisionBox, Level level, List<VoxelShape> entityCollisions, CallbackInfoReturnable<Vec3> cir) {
        if (!(entity instanceof Player player) || !jdt$shouldPassThroughWalls(player)) {
            return;
        }

        ImmutableList.Builder<VoxelShape> builder = ImmutableList.builderWithExpectedSize(entityCollisions.size() + 1);
        if (!entityCollisions.isEmpty()) {
            builder.addAll(entityCollisions);
        }

        WorldBorder worldBorder = level.getWorldBorder();
        if (worldBorder.isInsideCloseToBorder(player, collisionBox.expandTowards(movement))) {
            builder.add(worldBorder.getCollisionShape());
        }

        for (VoxelShape shape : level.getBlockCollisions(player, collisionBox.expandTowards(movement))) {
            if (jdt$isVerticalCollision(shape, collisionBox, player)) {
                builder.add(shape);
            }
        }

        cir.setReturnValue(collideWithShapes(movement, collisionBox, builder.build()));
    }

    @Unique
    private static boolean jdt$shouldPassThroughWalls(Player player) {
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        return leggings.getItem() instanceof ToggleableTool toggleableTool
                && toggleableTool.canUseAbilityAndDurability(leggings, Ability.PHASE);
    }

    @Unique
    private static boolean jdt$isVerticalCollision(VoxelShape shape, AABB collisionBox, Player player) {
        Level level = player.level();
        BlockPos blockPos = new BlockPos((int) shape.min(Direction.Axis.X), (int) shape.min(Direction.Axis.Y), (int) shape.min(Direction.Axis.Z));
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.getDestroySpeed(level, blockPos) < 0 || blockState.is(JustDireBlockTags.PHASEDENY)) {
            return true;
        }

        double maxY = shape.max(Direction.Axis.Y);
        double minY = collisionBox.minY;
        return Math.abs(maxY - minY) < 0.75;
    }
}
