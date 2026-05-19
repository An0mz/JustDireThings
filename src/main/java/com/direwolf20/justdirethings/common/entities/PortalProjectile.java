package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
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
	private boolean isPrimaryType;
	private boolean isAdvanced;
	private NBTHelpers.PortalDestination portalDestination;
	private int lifespanTicks;

	public PortalProjectile(EntityType<? extends ThrowableItemProjectile> type, Level level) {
		super(type, level);
	}

	public PortalProjectile(Level level, LivingEntity shooter, UUID gunUUID, boolean isPrimaryType, boolean isAdvanced,
			NBTHelpers.PortalDestination portalDestination, int lifespanTicks) {
		super(Registration.PortalProjectile.get(), shooter, level);
		this.gunUUID = gunUUID;
		this.isPrimaryType = isPrimaryType;
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
		if (isAdvanced && portalDestination != null) {
			spawnAdvancedPortals(portalPos, facing);
		} else {
			spawnPortal((ServerLevel) level(), portalPos, facing, isPrimaryType);
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
		isAdvanced = compound.getBoolean("Advanced");
		lifespanTicks = compound.getInt("LifespanTicks");
		if (compound.contains("PortalDestination")) {
			portalDestination = NBTHelpers.PortalDestination.fromNBT(compound.getCompound("PortalDestination"));
		}
	}

	private void spawnAdvancedPortals(Vec3 sourcePos, Direction sourceFacing) {
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

		clearMyPortals(server);

		PortalEntity sourcePortal = new PortalEntity(sourceLevel, sourcePos, sourceFacing, gunUUID, isPrimaryType,
				lifespanTicks);
		PortalEntity destinationPortal = new PortalEntity(destinationLevel, portalDestination.position(),
				portalDestination.facing(), gunUUID, !isPrimaryType, lifespanTicks);
		sourceLevel.addFreshEntity(sourcePortal);
		destinationLevel.addFreshEntity(destinationPortal);
		this.discard();
	}

	private void spawnPortal(ServerLevel serverLevel, Vec3 portalPos, Direction facing, boolean primaryType) {
		clearMatchingPortal(serverLevel.getServer(), primaryType);
		PortalEntity portal = new PortalEntity(serverLevel, portalPos, facing, gunUUID, primaryType, lifespanTicks);
		serverLevel.addFreshEntity(portal);
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
