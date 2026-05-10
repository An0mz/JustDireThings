package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class PortalProjectile extends ThrowableItemProjectile {

    private UUID gunUUID = UUID.randomUUID();
    private boolean stayOpen;

    public PortalProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public PortalProjectile(Level level, LivingEntity shooter, UUID gunUUID, boolean stayOpen) {
        super(Registration.PortalProjectile.get(), shooter, level);
        this.gunUUID = gunUUID;
        this.stayOpen = stayOpen;
    }

    @Override
    protected Item getDefaultItem() {
        return Registration.PortalGunV2.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        if (level().isClientSide) return;

        HitResult.Type type = hitResult.getType();
        if (type != HitResult.Type.BLOCK) return;

        Direction facing = hitResult.getDirection();
        if (facing == Direction.UP || facing == Direction.DOWN) {
            this.discard();
            return;
        }

        Vec3 hitLoc = hitResult.getLocation();
        double px;
        double py = hitResult.getBlockPos().getY();
        double pz;

        if (facing.getAxis() == Direction.Axis.Z) {
            px = hitResult.getBlockPos().getX() + 0.5;
            pz = hitLoc.z;
        } else {
            px = hitLoc.x;
            pz = hitResult.getBlockPos().getZ() + 0.5;
        }

        Vec3 portalPos = new Vec3(px, py, pz);

        net.minecraft.server.level.ServerLevel serverLevel = (net.minecraft.server.level.ServerLevel) level();
        List<PortalEntity> existing = new java.util.ArrayList<>();
        for (net.minecraft.world.entity.Entity entity : serverLevel.getAllEntities()) {
            if (entity instanceof PortalEntity p && p.getGunUUID().equals(gunUUID)) {
                existing.add(p);
            }
        }
        if (existing.size() >= 2) {
            existing.get(0).discard();
        }

        PortalEntity portal = new PortalEntity(level(), portalPos, facing, gunUUID, stayOpen);
        level().addFreshEntity(portal);

        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        this.discard();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putLong("GunUUIDMost", gunUUID.getMostSignificantBits());
        compound.putLong("GunUUIDLeast", gunUUID.getLeastSignificantBits());
        compound.putBoolean("StayOpen", stayOpen);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("GunUUIDMost")) {
            gunUUID = new UUID(compound.getLong("GunUUIDMost"), compound.getLong("GunUUIDLeast"));
        }
        stayOpen = compound.getBoolean("StayOpen");
    }
}
