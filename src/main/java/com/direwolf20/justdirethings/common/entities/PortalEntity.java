package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
	private static final EntityDataAccessor<Integer> ALIGNMENT_ID = SynchedEntityData.defineId(PortalEntity.class,
			EntityDataSerializers.INT);

	private UUID gunUUID = UUID.randomUUID();
	private UUID partnerUUID;
	private ResourceKey<Level> partnerDimension;
	private ChunkPos partnerChunkPos;
	private int lifespanTicks = -1;
	private int age;
	private final Map<UUID, Integer> teleportCooldowns = new HashMap<>();
	private final Map<UUID, Integer> entityVelocityCooldowns = new HashMap<>();
	private final Map<UUID, Vec3> entityLastPosition = new HashMap<>();
	private final Map<UUID, Vec3> entityLastLastPosition = new HashMap<>();
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
		this(level, position, facing, Direction.Axis.Z, gunUUID, primaryType, lifespanTicks);
	}

	public PortalEntity(Level level, Vec3 position, Direction facing, Direction.Axis alignment, UUID gunUUID,
			boolean primaryType, int lifespanTicks) {
		this(Registration.PortalEntity.get(), level);
		this.setPos(position.x, position.y, position.z);
		this.entityData.set(FACING_ID, facing.ordinal());
		this.entityData.set(ALIGNMENT_ID, alignment.ordinal());
		this.entityData.set(PORTAL_COLOR, primaryType ? 0x44B5FF : 0xFF9A2E);
		this.entityData.set(PRIMARY_TYPE, primaryType);
		this.gunUUID = gunUUID;
		this.lifespanTicks = lifespanTicks;
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(FACING_ID, Direction.NORTH.ordinal());
		this.entityData.define(ALIGNMENT_ID, Direction.Axis.Z.ordinal());
		this.entityData.define(PORTAL_COLOR, 0x00DD00);
		this.entityData.define(PRIMARY_TYPE, false);
	}

	public Direction getFacing() {
		int ordinal = entityData.get(FACING_ID);
		Direction[] dirs = Direction.values();
		return (ordinal >= 0 && ordinal < dirs.length) ? dirs[ordinal] : Direction.NORTH;
	}

	public Direction.Axis getAlignment() {
		int ordinal = entityData.get(ALIGNMENT_ID);
		Direction.Axis[] axes = Direction.Axis.values();
		return (ordinal >= 0 && ordinal < axes.length) ? axes[ordinal] : Direction.Axis.Z;
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
			captureVelocity();
			checkForEntities();
		}
	}

	private void tickCooldowns() {
		teleportCooldowns.entrySet().removeIf(e -> {
			e.setValue(e.getValue() - 1);
			return e.getValue() <= 0;
		});
		entityVelocityCooldowns.entrySet().removeIf(entry -> {
			if (entry.getValue() <= 0) {
				entityLastPosition.remove(entry.getKey());
				entityLastLastPosition.remove(entry.getKey());
				return true;
			}
			entry.setValue(entry.getValue() - 1);
			return false;
		});
	}

	// Records position history in a widened box around the portal so we still have
	// approach velocity by the time an entity reaches the actual teleport AABB
	// (collision response can zero out delta movement right at the surface).
	private void captureVelocity() {
		AABB velocityBox = getVelocityBoundingBox();
		List<Entity> entities = level().getEntities(this, velocityBox, e -> !e.isSpectator() && e.isAlive());
		for (Entity entity : entities) {
			UUID entityUUID = entity.getUUID();
			Vec3 currentPos = entity.position();
			if (entityLastPosition.containsKey(entityUUID))
				entityLastLastPosition.put(entityUUID, entityLastPosition.get(entityUUID));
			entityLastPosition.put(entityUUID, currentPos);
			entityVelocityCooldowns.put(entityUUID, 10);
		}
	}

	private AABB getVelocityBoundingBox() {
		Direction facing = getFacing();
		return getPortalAABB().expandTowards(facing.getStepX() * 2.5, facing.getStepY() * 2.5, facing.getStepZ() * 2.5);
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

		ServerLevel destinationLevel = (ServerLevel) partner.level();

		if (!destinationLevel.equals(serverLevel) && !entity.canChangeDimensions()) {
			return;
		}

		teleportCooldowns.put(entity.getUUID(), TELEPORT_COOLDOWN);
		partner.teleportCooldowns.put(entity.getUUID(), TELEPORT_COOLDOWN);

		Vec3 teleportTo = getTeleportTo(entity, partner);
		Vec2 newLookAngle = transformLookAngle(entity, partner);
		Vec3 newMotion = calculateVelocity(entity, partner);

		entity.resetFallDistance();
		boolean success = entity.teleportTo(destinationLevel, teleportTo.x, teleportTo.y, teleportTo.z, new HashSet<>(),
				newLookAngle.y, newLookAngle.x);

		if (success) {
			entity.resetFallDistance();
			if (!newMotion.equals(Vec3.ZERO)) {
				entity.setDeltaMovement(newMotion);
				entity.hasImpulse = true;
				if (entity instanceof ServerPlayer sp) {
					sp.connection.send(new ClientboundSetEntityMotionPacket(entity));
				} else {
					destinationLevel.getChunkSource().broadcast(entity, new ClientboundSetEntityMotionPacket(entity));
				}
			}
			entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
		}
	}

	// Where an entity crossing this portal should appear on the linked partner
	// portal: the entry-position fraction across this portal's in-plane axis is
	// mapped onto the same axis on the partner, so walking speed/line carries
	// through instead of snapping to the portal's center.
	private Vec3 getTeleportTo(Entity entity, PortalEntity partner) {
		Vec3 teleportTo;
		double entityFraction;
		AABB entityBB = entity.getBoundingBox();
		AABB portalBB = getPortalAABB();
		if (getFacing().getAxis() == Direction.Axis.Y) {
			if (getAlignment() == Direction.Axis.X) {
				entityFraction = Math
						.abs((((entityBB.maxX + entityBB.minX) / 2) - portalBB.minX) / portalBB.getXsize());
			} else {
				entityFraction = Math
						.abs((((entityBB.maxZ + entityBB.minZ) / 2) - portalBB.minZ) / portalBB.getZsize());
			}
		} else {
			entityFraction = (entityBB.minY - portalBB.minY) / portalBB.getYsize();
		}
		entityFraction = Mth.clamp(entityFraction, 0.0, 1.0);

		AABB partnerBB = partner.getPortalAABB();
		Direction partnerFacing = partner.getFacing();
		if (partnerFacing.getAxis() == Direction.Axis.Y) {
			if (partner.getAlignment() == Direction.Axis.X) {
				double offset = entityFraction * partnerBB.getXsize();
				double buffer = entityBB.getXsize() / 2 + Shapes.EPSILON;
				offset = Mth.clamp(offset, buffer, partnerBB.getXsize() - buffer);
				teleportTo = new Vec3(partnerBB.minX + offset, partner.getY(), partner.getZ());
			} else {
				double offset = entityFraction * partnerBB.getZsize();
				double buffer = entityBB.getZsize() / 2 + Shapes.EPSILON;
				offset = Mth.clamp(offset, buffer, partnerBB.getZsize() - buffer);
				teleportTo = new Vec3(partner.getX(), partner.getY(), partnerBB.minZ + offset);
			}
		} else {
			teleportTo = new Vec3(partner.getX(), partnerBB.minY + entityFraction * partnerBB.getYsize(),
					partner.getZ());
		}

		// Move to the 'far' side of the exit portal so the entity doesn't
		// immediately re-collide, except when exiting upward (that would add
		// unwanted extra velocity) or downward (handled by dropping below instead).
		if (partnerFacing == Direction.DOWN) {
			teleportTo = teleportTo.relative(Direction.DOWN, entityBB.getYsize());
		} else if (partnerFacing != Direction.UP) {
			if (partnerFacing.getAxis() == Direction.Axis.X) {
				teleportTo = teleportTo.relative(partnerFacing, partnerBB.getXsize() / 2 + entityBB.getXsize() / 2);
			} else if (partnerFacing.getAxis() == Direction.Axis.Z) {
				teleportTo = teleportTo.relative(partnerFacing, partnerBB.getZsize() / 2 + entityBB.getZsize() / 2);
			}
		}

		teleportTo = teleportTo.relative(partnerFacing, Shapes.EPSILON);
		return teleportTo;
	}

	private Vec3 calculateVelocity(Entity entity, PortalEntity partner) {
		Vec3 newMotion = Vec3.ZERO;
		UUID entityUUID = entity.getUUID();
		if (entityLastPosition.containsKey(entityUUID)) {
			double threshold = 0.2;
			Vec3 previousPos = entityLastPosition.get(entityUUID);
			Vec3 currentPos = entity.position();
			Vec3 thisVelocity = currentPos.subtract(previousPos);
			Vec3 lastVelocity = Vec3.ZERO;
			if (entityLastLastPosition.containsKey(entityUUID)) {
				Vec3 lastLastPos = entityLastLastPosition.get(entityUUID);
				Vec3 lastPos = entityLastPosition.get(entityUUID);
				lastVelocity = lastPos.subtract(lastLastPos);
			}
			Vec3 velocity = lastVelocity.equals(Vec3.ZERO) ? thisVelocity : lastVelocity;
			if (Math.abs(velocity.x) > threshold || Math.abs(velocity.y) > threshold || Math.abs(velocity.z) > threshold
					|| velocity.y > 0) {
				newMotion = transformMotion(velocity, getFacing(), partner.getFacing().getOpposite());
			}
			entityLastPosition.remove(entityUUID);
			entityLastLastPosition.remove(entityUUID);
		}
		return newMotion;
	}

	// Rotates a look direction / velocity vector from this portal's facing into
	// the equivalent orientation relative to another facing (so momentum and view
	// direction carry through a portal that's angled differently from this one).
	public static Vec3 transformMotion(Vec3 motion, Direction from, Direction to) {
		Quaternionf fromRotation = from.getRotation();
		Quaternionf toRotation = to.getRotation();
		fromRotation.invert();

		Vector3f motionVec = new Vector3f((float) motion.x, (float) motion.y, (float) motion.z);
		fromRotation.transform(motionVec);
		toRotation.transform(motionVec);

		return new Vec3(motionVec.x(), motionVec.y(), motionVec.z());
	}

	private Vec2 transformLookAngle(Entity entity, PortalEntity destination) {
		Vec3 newLook = transformMotion(entity.getLookAngle(), getFacing(), destination.getFacing().getOpposite());
		return toDegrees(newLook);
	}

	private static Vec2 toDegrees(Vec3 vector) {
		double x = vector.x;
		double y = vector.y;
		double z = vector.z;
		double hyp = Math.sqrt(x * x + z * z);
		float xrot = Mth.wrapDegrees((float) (-(Mth.atan2(y, hyp) * 180.0F / (float) Math.PI)));
		float yrot = Mth.wrapDegrees((float) (Mth.atan2(z, x) * 180.0F / (float) Math.PI) - 90.0F);
		return new Vec2(xrot, yrot);
	}

	// Mutually records each portal's partner (UUID + last-known dimension/chunk),
	// so findPartner can jump straight to it and force-load its chunk on demand
	// instead of depending on both chunks happening to already be loaded.
	public void linkPartner(PortalEntity partner) {
		this.partnerUUID = partner.getUUID();
		this.partnerDimension = partner.level().dimension();
		this.partnerChunkPos = partner.chunkPosition();
		partner.partnerUUID = this.getUUID();
		partner.partnerDimension = this.level().dimension();
		partner.partnerChunkPos = this.chunkPosition();
	}

	private PortalEntity findPartner(ServerLevel level) {
		MinecraftServer server = level.getServer();
		if (partnerUUID != null && partnerDimension != null) {
			ServerLevel partnerLevel = server.getLevel(partnerDimension);
			if (partnerLevel != null) {
				if (partnerChunkPos != null)
					partnerLevel.getChunk(partnerChunkPos.x, partnerChunkPos.z); // Blocking force-load if unloaded
				if (partnerLevel.getEntity(partnerUUID) instanceof PortalEntity p)
					return p;
			}
		}
		// Fallback for portals placed before partner-linking existed, or if the
		// stored partner reference no longer resolves (e.g. re-linked elsewhere).
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
		double hl = PORTAL_HEIGHT / 2;
		if (facing.getAxis() == Direction.Axis.Y) {
			if (getAlignment() == Direction.Axis.X) {
				return new AABB(x - hl, y - hd, z - hw, x + hl, y + hd, z + hw);
			} else {
				return new AABB(x - hw, y - hd, z - hl, x + hw, y + hd, z + hl);
			}
		} else if (facing.getAxis() == Direction.Axis.Z) {
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
		if (partnerUUID != null) {
			compound.putLong("PartnerUUIDMost", partnerUUID.getMostSignificantBits());
			compound.putLong("PartnerUUIDLeast", partnerUUID.getLeastSignificantBits());
			compound.putString("PartnerDimension", partnerDimension.location().toString());
			compound.putInt("PartnerChunkX", partnerChunkPos.x);
			compound.putInt("PartnerChunkZ", partnerChunkPos.z);
		}
		compound.putInt("Facing", entityData.get(FACING_ID));
		compound.putInt("Alignment", entityData.get(ALIGNMENT_ID));
		compound.putInt("Color", entityData.get(PORTAL_COLOR));
		compound.putBoolean("PrimaryType", entityData.get(PRIMARY_TYPE));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		age = compound.getInt("Age");
		lifespanTicks = compound.getInt("LifespanTicks");
		gunUUID = new UUID(compound.getLong("GunUUIDMost"), compound.getLong("GunUUIDLeast"));
		if (compound.contains("PartnerUUIDMost")) {
			partnerUUID = new UUID(compound.getLong("PartnerUUIDMost"), compound.getLong("PartnerUUIDLeast"));
			partnerDimension = ResourceKey.create(Registries.DIMENSION,
					new ResourceLocation(compound.getString("PartnerDimension")));
			partnerChunkPos = new ChunkPos(compound.getInt("PartnerChunkX"), compound.getInt("PartnerChunkZ"));
		}
		entityData.set(FACING_ID, compound.getInt("Facing"));
		if (compound.contains("Alignment"))
			entityData.set(ALIGNMENT_ID, compound.getInt("Alignment"));
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
