package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PortalEntity extends Entity {
	private static final EntityDataAccessor<Integer> FACING_ID = SynchedEntityData.defineId(PortalEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> PORTAL_COLOR = SynchedEntityData.defineId(PortalEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> PRIMARY_TYPE = SynchedEntityData.defineId(PortalEntity.class,
			EntityDataSerializers.BOOLEAN);

	private UUID gunUUID = UUID.randomUUID();
	private int lifespanTicks = -1;
	private int age;
	private final Map<UUID, Integer> teleportCooldowns = new HashMap<>();
	private static final int TELEPORT_COOLDOWN = 40;
	private static final double PORTAL_WIDTH = 0.9;
	private static final double PORTAL_HEIGHT = 1.9;
	private static final double PORTAL_DEPTH = 0.2;

	public PortalEntity(EntityType<?> type, Level level) {
		super(type, level);
		this.noPhysics = true;
		this.setNoGravity(true);
	}

	public PortalEntity(Level level, Vec3 position, Direction facing, UUID gunUUID, boolean primaryType,
			int lifespanTicks) {
		this(Registration.PortalEntity.get(), level);
		this.setPos(position.x, position.y, position.z);
		this.entityData.set(FACING_ID, facing.ordinal());
		this.entityData.set(PORTAL_COLOR, primaryType ? 0x44B5FF : 0xFF9A2E);
		this.entityData.set(PRIMARY_TYPE, primaryType);
		this.gunUUID = gunUUID;
		this.lifespanTicks = lifespanTicks;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(FACING_ID, Direction.NORTH.ordinal());
		this.entityData.define(PORTAL_COLOR, 0x00DD00);
		this.entityData.define(PRIMARY_TYPE, false);
	}

	public Direction getFacing() {
		int ordinal = entityData.get(FACING_ID);
		Direction[] dirs = Direction.values();
		return (ordinal >= 0 && ordinal < dirs.length) ? dirs[ordinal] : Direction.NORTH;
	}

	public int getPortalColor() {
		return entityData.get(PORTAL_COLOR);
	}

	public UUID getGunUUID() {
		return gunUUID;
	}

	public boolean isPrimaryType() {
		return this.entityData.get(PRIMARY_TYPE);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide) {
			age++;
			if (lifespanTicks >= 0 && age > lifespanTicks) {
				this.discard();
				return;
			}
			tickCooldowns();
			checkForEntities();
		}
	}

	private void tickCooldowns() {
		teleportCooldowns.entrySet().removeIf(e -> {
			e.setValue(e.getValue() - 1);
			return e.getValue() <= 0;
		});
	}

	private void checkForEntities() {
		AABB portalAABB = getPortalAABB();
		List<Entity> entities = level().getEntities(this, portalAABB, e -> !e.isSpectator() && e.isAlive());
		for (Entity entity : entities) {
			if (teleportCooldowns.containsKey(entity.getUUID()))
				continue;
			tryTeleport(entity);
		}
	}

	private void tryTeleport(Entity entity) {
		if (!(level() instanceof ServerLevel serverLevel))
			return;

		PortalEntity partner = findPartner(serverLevel);
		if (partner == null)
			return;

		Vec3 dest = partner.position().add(0, 0, 0);
		ServerLevel destinationLevel = (ServerLevel) partner.level();

		if (!destinationLevel.equals(serverLevel) && !entity.canChangeDimensions()) {
			return;
		}

		teleportCooldowns.put(entity.getUUID(), TELEPORT_COOLDOWN);
		partner.teleportCooldowns.put(entity.getUUID(), TELEPORT_COOLDOWN);

		float yRot = entity.getYRot();
		float xRot = entity.getXRot();

		if (entity instanceof ServerPlayer sp) {
			sp.teleportTo(destinationLevel, dest.x, dest.y, dest.z, yRot, xRot);
		} else {
			entity.teleportTo(destinationLevel, dest.x, dest.y, dest.z, new HashSet<>(), yRot, xRot);
		}

		Vec3 velocity = entity.getDeltaMovement();
		entity.setDeltaMovement(velocity);
		entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
	}

	private PortalEntity findPartner(ServerLevel level) {
		MinecraftServer server = level.getServer();
		for (ServerLevel serverLevel : server.getAllLevels()) {
			for (net.minecraft.world.entity.Entity entity : serverLevel.getAllEntities()) {
				if (entity instanceof PortalEntity p && p != this && p.gunUUID.equals(this.gunUUID)
						&& p.isPrimaryType() != this.isPrimaryType()) {
					return p;
				}
			}
		}
		return null;
	}

	public AABB getPortalAABB() {
		Direction facing = getFacing();
		double x = getX();
		double y = getY();
		double z = getZ();
		double hw = PORTAL_WIDTH / 2;
		double hd = PORTAL_DEPTH / 2;
		if (facing.getAxis() == Direction.Axis.Z) {
			return new AABB(x - hw, y, z - hd, x + hw, y + PORTAL_HEIGHT, z + hd);
		} else {
			return new AABB(x - hd, y, z - hw, x + hd, y + PORTAL_HEIGHT, z + hw);
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		compound.putInt("Age", age);
		compound.putInt("LifespanTicks", lifespanTicks);
		compound.putLong("GunUUIDMost", gunUUID.getMostSignificantBits());
		compound.putLong("GunUUIDLeast", gunUUID.getLeastSignificantBits());
		compound.putInt("Facing", entityData.get(FACING_ID));
		compound.putInt("Color", entityData.get(PORTAL_COLOR));
		compound.putBoolean("PrimaryType", entityData.get(PRIMARY_TYPE));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		age = compound.getInt("Age");
		lifespanTicks = compound.getInt("LifespanTicks");
		gunUUID = new UUID(compound.getLong("GunUUIDMost"), compound.getLong("GunUUIDLeast"));
		entityData.set(FACING_ID, compound.getInt("Facing"));
		entityData.set(PORTAL_COLOR, compound.getInt("Color"));
		entityData.set(PRIMARY_TYPE, compound.getBoolean("PrimaryType"));
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	public static int colorFromUUID(UUID uuid) {
		int hash = uuid.hashCode();
		int r = (hash >> 16) & 0xFF;
		int g = (hash >> 8) & 0xFF;
		int b = hash & 0xFF;
		int maxC = Math.max(Math.max(r, g), b);
		if (maxC == 0)
			maxC = 1;
		r = (r * 255) / maxC;
		g = (g * 255) / maxC;
		b = (b * 255) / maxC;
		return (r << 16) | (g << 8) | b;
	}

	@Override
	public void onAddedToWorld() {
		super.onAddedToWorld();
		if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
			level().playSound(null, getX(), getY(), getZ(), Registration.PORTAL_GUN_OPEN.get(), SoundSource.NEUTRAL,
					0.75F, 0.4F);
			BlockPos pos = this.blockPosition();
			serverLevel.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, true);
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
			level().playSound(null, getX(), getY(), getZ(), Registration.PORTAL_GUN_CLOSE.get(), SoundSource.NEUTRAL,
					0.5F, 0.2F);
			BlockPos pos = this.blockPosition();
			serverLevel.setChunkForced(pos.getX() >> 4, pos.getZ() >> 4, false);
		}
		super.remove(reason);
	}
}
