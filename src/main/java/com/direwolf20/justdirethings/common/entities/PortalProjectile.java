package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class PortalProjectile extends ThrowableItemProjectile {

	private static final EntityDataAccessor<Boolean> IS_PRIMARY = SynchedEntityData.defineId(PortalProjectile.class,
			EntityDataSerializers.BOOLEAN);

	private UUID gunUUID = UUID.randomUUID();
	private boolean isPrimaryType;
	private boolean isAdvanced;
	private NBTHelpers.PortalDestination portalDestination;
	private int lifespanTicks;

	public PortalProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
		super(type, level);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(IS_PRIMARY, true);
	}

	public boolean isPrimaryType() {
		return this.entityData.get(IS_PRIMARY);
	}

	public PortalProjectile(Level level, LivingEntity shooter, UUID gunUUID, boolean isPrimaryType, boolean isAdvanced,
			NBTHelpers.PortalDestination portalDestination, int lifespanTicks) {
		super(Registration.PortalProjectile.get(), shooter, level);
		this.gunUUID = gunUUID;
		this.isPrimaryType = isPrimaryType;
		this.entityData.set(IS_PRIMARY, isPrimaryType);
		this.isAdvanced = isAdvanced;
		this.portalDestination = portalDestination;
		this.lifespanTicks = lifespanTicks;
	}

	@Override
	protected Item getDefaultItem() {
		return Registration.PortalGunV2.get();
	}

	@Override
	protected void onHitBlock(BlockHitResult hitResult) {
		if (level().isClientSide)
			return;

		HitResult.Type type = hitResult.getType();
		if (type != HitResult.Type.BLOCK)
			return;

		Direction facing = hitResult.getDirection();

		Vec3 hitLoc = hitResult.getLocation();
		double px;
		double py;
		double pz;
		Direction.Axis alignment = Direction.Axis.Z;

		if (facing.getAxis() == Direction.Axis.Y) {
			Vec3 velocity = getDeltaMovement();
			alignment = Math.abs(velocity.x) > Math.abs(velocity.z) ? Direction.Axis.X : Direction.Axis.Z;
			// Shift the center by half a block, in the direction of travel, so the
			// 2-block-long portal spans the hit block plus its neighbor in that
			// direction, grid-aligned, rather than straddling the hit block alone.
			if (alignment == Direction.Axis.X) {
				double dirSign = velocity.x >= 0 ? 0.5 : -0.5;
				px = hitResult.getBlockPos().getX() + 0.5 + dirSign;
				pz = hitResult.getBlockPos().getZ() + 0.5;
			} else {
				double dirSign = velocity.z >= 0 ? 0.5 : -0.5;
				px = hitResult.getBlockPos().getX() + 0.5;
				pz = hitResult.getBlockPos().getZ() + 0.5 + dirSign;
			}
			py = hitLoc.y;
		} else if (facing.getAxis() == Direction.Axis.Z) {
			px = hitResult.getBlockPos().getX() + 0.5;
			py = hitResult.getBlockPos().getY();
			pz = hitLoc.z;
		} else {
			px = hitLoc.x;
			py = hitResult.getBlockPos().getY();
			pz = hitResult.getBlockPos().getZ() + 0.5;
		}

		Vec3 portalPos = new Vec3(px, py, pz);
		if (isAdvanced && portalDestination != null) {
			spawnAdvancedPortals(portalPos, facing, alignment);
		} else {
			spawnPortal((ServerLevel) level(), portalPos, facing, alignment, isPrimaryType);
			this.discard();
		}
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
		compound.putBoolean("Primary", isPrimaryType);
		compound.putBoolean("Advanced", isAdvanced);
		compound.putInt("LifespanTicks", lifespanTicks);
		if (portalDestination != null) {
			compound.put("PortalDestination", portalDestination.toNBT());
		}
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("GunUUIDMost")) {
			gunUUID = new UUID(compound.getLong("GunUUIDMost"), compound.getLong("GunUUIDLeast"));
		}
		isPrimaryType = compound.getBoolean("Primary");
		this.entityData.set(IS_PRIMARY, isPrimaryType);
		isAdvanced = compound.getBoolean("Advanced");
		lifespanTicks = compound.getInt("LifespanTicks");
		if (compound.contains("PortalDestination")) {
			portalDestination = NBTHelpers.PortalDestination.fromNBT(compound.getCompound("PortalDestination"));
		}
	}

	private void spawnAdvancedPortals(Vec3 sourcePos, Direction sourceFacing, Direction.Axis sourceAlignment) {
		if (!(level() instanceof ServerLevel sourceLevel)) {
			this.discard();
			return;
		}
		MinecraftServer server = sourceLevel.getServer();
		ServerLevel destinationLevel = server.getLevel(portalDestination.dimension());
		if (destinationLevel == null) {
			this.discard();
			return;
		}

		PortalEntity sourcePortal = new PortalEntity(sourceLevel, sourcePos, sourceFacing, sourceAlignment, gunUUID,
				isPrimaryType, lifespanTicks);
		PortalEntity destinationPortal = new PortalEntity(destinationLevel, portalDestination.position(),
				portalDestination.facing(), gunUUID, !isPrimaryType, lifespanTicks);

		if (overlapsOtherPortal(sourceLevel, sourcePortal, true)
				|| overlapsOtherPortal(destinationLevel, destinationPortal, true)) {
			this.discard();
			return;
		}

		clearMyPortals(server);
		sourceLevel.addFreshEntity(sourcePortal);
		destinationLevel.addFreshEntity(destinationPortal);
		this.discard();
	}

	private void spawnPortal(ServerLevel serverLevel, Vec3 portalPos, Direction facing, Direction.Axis alignment,
			boolean primaryType) {
		PortalEntity portal = new PortalEntity(serverLevel, portalPos, facing, alignment, gunUUID, primaryType,
				lifespanTicks);

		if (overlapsOtherPortal(serverLevel, portal, false))
			return;

		clearMatchingPortal(serverLevel.getServer(), primaryType);
		serverLevel.addFreshEntity(portal);
	}

	// Blocks placement on top of another existing portal. When excludeAllOwnPortals
	// is true (the advanced/linked pair path, which clears both ends of this gun's
	// portals via clearMyPortals before placing), any portal sharing this gunUUID is
	// excluded. Otherwise (the basic gun path, which only clears the matching
	// primary/secondary slot via clearMatchingPortal) only that same slot is
	// excluded, so the gun's other, still-standing portal is still checked.
	private static boolean overlapsOtherPortal(ServerLevel level, PortalEntity candidate,
			boolean excludeAllOwnPortals) {
		AABB candidateBox = candidate.getPortalAABB().inflate(-0.1);
		List<? extends PortalEntity> existing = level.getEntities(Registration.PortalEntity.get(), portal -> {
			boolean sameGun = portal.getGunUUID().equals(candidate.getGunUUID());
			if (!sameGun)
				return true;
			return excludeAllOwnPortals ? false : portal.isPrimaryType() != candidate.isPrimaryType();
		});
		for (PortalEntity existingPortal : existing) {
			if (existingPortal.getPortalAABB().intersects(candidateBox))
				return true;
		}
		return false;
	}

	private void clearMatchingPortal(MinecraftServer server, boolean primaryType) {
		for (ServerLevel serverLevel : server.getAllLevels()) {
			List<? extends PortalEntity> existing = serverLevel.getEntities(Registration.PortalEntity.get(),
					portal -> portal.getGunUUID().equals(gunUUID) && portal.isPrimaryType() == primaryType);
			for (PortalEntity portal : existing) {
				portal.discard();
			}
		}
	}

	private void clearMyPortals(MinecraftServer server) {
		for (ServerLevel serverLevel : server.getAllLevels()) {
			List<? extends PortalEntity> existing = serverLevel.getEntities(Registration.PortalEntity.get(),
					portal -> portal.getGunUUID().equals(gunUUID));
			for (PortalEntity portal : existing) {
				portal.discard();
			}
		}
	}
}
