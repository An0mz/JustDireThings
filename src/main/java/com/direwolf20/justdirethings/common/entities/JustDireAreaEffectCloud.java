package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JustDireAreaEffectCloud extends Entity implements TraceableEntity {
	private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData
			.defineId(JustDireAreaEffectCloud.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData
			.defineId(JustDireAreaEffectCloud.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> DATA_WAITING = SynchedEntityData
			.defineId(JustDireAreaEffectCloud.class, EntityDataSerializers.BOOLEAN);
	private static final int REAPPLICATION_DELAY = 20;

	private final List<MobEffectInstance> effects = new ArrayList<>();
	private final Map<Entity, Integer> victims = new HashMap<>();
	private int duration = 600;
	private int waitTime = 20;
	private float radiusOnUse;
	private float radiusPerTick;
	@Nullable
	private LivingEntity owner;
	@Nullable
	private UUID ownerUUID;

	public JustDireAreaEffectCloud(EntityType<? extends JustDireAreaEffectCloud> entityType, Level level) {
		super(entityType, level);
		this.noPhysics = true;
	}

	public JustDireAreaEffectCloud(Level level, double x, double y, double z) {
		this(Registration.JustDireAreaEffectCloud.get(), level);
		this.setPos(x, y, z);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_RADIUS, 3.0F);
		this.entityData.define(DATA_COLOR, 0);
		this.entityData.define(DATA_WAITING, false);
	}

	public void setRadius(float radius) {
		if (!this.level().isClientSide)
			this.entityData.set(DATA_RADIUS, Mth.clamp(radius, 0.0F, 32.0F));
	}

	public float getRadius() {
		return this.entityData.get(DATA_RADIUS);
	}

	@Override
	public void refreshDimensions() {
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		super.refreshDimensions();
		this.setPos(x, y, z);
	}

	public void addEffect(MobEffectInstance effectInstance) {
		this.effects.add(effectInstance);
		this.entityData.set(DATA_COLOR, PotionUtils.getColor(this.effects));
	}

	public int getColor() {
		return this.entityData.get(DATA_COLOR);
	}

	public int getDuration() {
		return this.duration;
	}

	public void setRadiusOnUse(float radiusOnUse) {
		this.radiusOnUse = radiusOnUse;
	}

	public void setRadiusPerTick(float radiusPerTick) {
		this.radiusPerTick = radiusPerTick;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public void setOwner(@Nullable LivingEntity owner) {
		this.owner = owner;
		this.ownerUUID = owner == null ? null : owner.getUUID();
	}

	@Nullable
	@Override
	public LivingEntity getOwner() {
		if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel serverLevel
				&& serverLevel.getEntity(this.ownerUUID) instanceof LivingEntity livingEntity)
			this.owner = livingEntity;
		return this.owner;
	}

	@Override
	public void tick() {
		super.tick();
		boolean waiting = this.entityData.get(DATA_WAITING);
		float radius = this.getRadius();
		if (this.level().isClientSide) {
			tickParticles(waiting, radius);
			return;
		}
		if (this.tickCount >= this.waitTime + this.duration) {
			this.discard();
			return;
		}
		boolean stillWaiting = this.tickCount < this.waitTime;
		if (waiting != stillWaiting)
			this.entityData.set(DATA_WAITING, stillWaiting);
		if (stillWaiting)
			return;
		if (this.radiusPerTick != 0.0F) {
			radius += this.radiusPerTick;
			if (radius < 0.5F) {
				this.discard();
				return;
			}
			this.setRadius(radius);
		}
		if (this.tickCount % 5 != 0)
			return;
		this.victims.entrySet().removeIf(entry -> this.tickCount >= entry.getValue());
		if (this.effects.isEmpty()) {
			this.victims.clear();
			return;
		}
		LivingEntity cloudOwner = this.getOwner();
		for (LivingEntity living : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox())) {
			if (this.victims.containsKey(living) || !living.isAffectedByPotions())
				continue;
			double dx = living.getX() - this.getX();
			double dz = living.getZ() - this.getZ();
			if (dx * dx + dz * dz > radius * radius)
				continue;
			this.victims.put(living, this.tickCount + REAPPLICATION_DELAY);
			boolean affectedAnything = false;
			for (MobEffectInstance effect : this.effects) {
				MobEffectCategory category = effect.getEffect().getCategory();
				boolean isOwner = living == cloudOwner;
				if (cloudOwner != null && (category == MobEffectCategory.HARMFUL && isOwner
						|| category == MobEffectCategory.BENEFICIAL && !isOwner))
					continue;
				if (effect.getEffect().isInstantenous())
					effect.getEffect().applyInstantenousEffect(this, cloudOwner, living, effect.getAmplifier(), 0.5D);
				else
					living.addEffect(new MobEffectInstance(effect), this);
				affectedAnything = true;
			}
			if (this.radiusOnUse != 0.0F && affectedAnything) {
				radius += this.radiusOnUse;
				if (radius < 0.5F) {
					this.discard();
					return;
				}
				this.setRadius(radius);
			}
		}
	}

	private void tickParticles(boolean waiting, float radius) {
		if (waiting && this.random.nextBoolean())
			return;
		int count = waiting ? 2 : Mth.ceil((float) Math.PI * radius * radius);
		float spread = waiting ? 0.2F : radius;
		for (int i = 0; i < count; i++) {
			float angle = this.random.nextFloat() * (float) (Math.PI * 2);
			float distance = Mth.sqrt(this.random.nextFloat()) * spread;
			int color = waiting && this.random.nextBoolean() ? 0xFFFFFF : this.getColor();
			this.level().addAlwaysVisibleParticle(ParticleTypes.ENTITY_EFFECT,
					this.getX() + Mth.cos(angle) * distance, this.getY(), this.getZ() + Mth.sin(angle) * distance,
					(color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F, (color & 255) / 255.0F);
		}
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		this.tickCount = tag.getInt("Age");
		this.duration = tag.getInt("Duration");
		this.waitTime = tag.getInt("WaitTime");
		this.radiusOnUse = tag.getFloat("RadiusOnUse");
		this.radiusPerTick = tag.getFloat("RadiusPerTick");
		this.setRadius(tag.getFloat("Radius"));
		if (tag.hasUUID("Owner"))
			this.ownerUUID = tag.getUUID("Owner");
		for (Tag effectTag : tag.getList("Effects", Tag.TAG_COMPOUND)) {
			MobEffectInstance effect = MobEffectInstance.load((CompoundTag) effectTag);
			if (effect != null)
				this.addEffect(effect);
		}
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("Age", this.tickCount);
		tag.putInt("Duration", this.duration);
		tag.putInt("WaitTime", this.waitTime);
		tag.putFloat("RadiusOnUse", this.radiusOnUse);
		tag.putFloat("RadiusPerTick", this.radiusPerTick);
		tag.putFloat("Radius", this.getRadius());
		if (this.ownerUUID != null)
			tag.putUUID("Owner", this.ownerUUID);
		ListTag effectList = new ListTag();
		for (MobEffectInstance effect : this.effects)
			effectList.add(effect.save(new CompoundTag()));
		tag.put("Effects", effectList);
	}

	@Override
	public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
		if (DATA_RADIUS.equals(key))
			this.refreshDimensions();
		super.onSyncedDataUpdated(key);
	}

	@Override
	public PushReaction getPistonPushReaction() {
		return PushReaction.IGNORE;
	}

	@Override
	public EntityDimensions getDimensions(Pose pose) {
		return EntityDimensions.scalable(this.getRadius() * 2.0F, 0.5F);
	}
}
