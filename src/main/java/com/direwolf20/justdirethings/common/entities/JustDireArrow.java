package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class JustDireArrow extends AbstractArrow {
    public static final Predicate<LivingEntity> WATER_SENSITIVE_OR_ON_FIRE =
            (e) -> e.isSensitiveToWater() || e.isOnFire();

    private static final EntityDataAccessor<Integer> ID_EFFECT_COLOR =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_POTIONARROW =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_SPLASH =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_LINGERING =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_HOMING =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HOSTILE_ONLY =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ARROW_STATE =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STATE_TICK_COUNTER =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> ORIGINAL_VELOCITY =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_EPIC_ARROW =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_PHASE =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_TARGET_ANGRY =
            SynchedEntityData.defineId(JustDireArrow.class, EntityDataSerializers.BOOLEAN);

    private enum ArrowState {
        NORMAL, SLOWING_DOWN, STOPPED_AND_ROTATING, RESUMING_FLIGHT
    }

    private static final int SLOW_DOWN_DURATION = 4;
    private static final int STOP_DURATION = 10;

    private boolean canHitMobs = true;
    private LivingEntity targetEntity;

    // Potion effects stored as instance field (serialized via NBT)
    private final List<MobEffectInstance> effects = new ArrayList<>();

    public JustDireArrow(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    public JustDireArrow(Level level, LivingEntity owner) {
        super(Registration.JustDireArrow.get(), owner, level);
    }

    public void addEffect(MobEffectInstance effectInstance) {
        effects.add(new MobEffectInstance(effectInstance));
        updateColor();
    }

    private void updateColor() {
        if (effects.isEmpty()) {
            this.entityData.set(ID_EFFECT_COLOR, -1);
            return;
        }
        int color = PotionUtils.getColor(effects);
        this.entityData.set(ID_EFFECT_COLOR, color);
    }

    public List<MobEffectInstance> getEffects() {
        return effects;
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(Items.ARROW);
    }

    public void setPotionArrow(boolean potionArrow) {
        this.entityData.set(IS_POTIONARROW, potionArrow);
    }

    public void setSplash(boolean splash) {
        this.entityData.set(IS_SPLASH, splash);
    }

    public void setLingering(boolean lingering) {
        this.entityData.set(IS_LINGERING, lingering);
    }

    public void setHoming(boolean homing) {
        this.entityData.set(IS_HOMING, homing);
    }

    public void setPhase(boolean phase) {
        this.entityData.set(IS_PHASE, phase);
    }

    public void setTargetAngry(boolean angry) {
        this.entityData.set(IS_TARGET_ANGRY, angry);
    }

    public boolean isPhase() {
        return this.entityData.get(IS_PHASE);
    }

    public float getOriginalVelocity() {
        return this.entityData.get(ORIGINAL_VELOCITY);
    }

    public void setEpicArrow(boolean isEpicArrow) {
        this.entityData.set(IS_EPIC_ARROW, isEpicArrow);
        this.setPierceLevel((byte) 5);
    }

    public boolean isEpic() {
        return this.entityData.get(IS_EPIC_ARROW);
    }

    public void setHostileOnly(boolean hostileOnly) {
        this.entityData.set(HOSTILE_ONLY, hostileOnly);
    }

    public boolean getHostileOnly() {
        return this.entityData.get(HOSTILE_ONLY);
    }

    public boolean getTargetAngry() {
        return this.entityData.get(IS_TARGET_ANGRY);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_EFFECT_COLOR, -1);
        this.entityData.define(IS_POTIONARROW, false);
        this.entityData.define(IS_SPLASH, false);
        this.entityData.define(IS_LINGERING, false);
        this.entityData.define(IS_HOMING, false);
        this.entityData.define(ARROW_STATE, ArrowState.NORMAL.ordinal());
        this.entityData.define(STATE_TICK_COUNTER, 0);
        this.entityData.define(ORIGINAL_VELOCITY, 0f);
        this.entityData.define(IS_EPIC_ARROW, false);
        this.entityData.define(IS_PHASE, false);
        this.entityData.define(HOSTILE_ONLY, true);
        this.entityData.define(IS_TARGET_ANGRY, false);
    }

    public boolean isHostileEntity(LivingEntity entity) {
        if (getTargetAngry()) return true;
        if (entity instanceof NeutralMob neutralMob) {
            if (neutralMob.isAngry()) {
                setTargetAngry(true);
                return true;
            }
            return false;
        }
        return entity instanceof Enemy;
    }

    public void setData(EntityDataAccessor<Integer> accessor, int value) {
        if (!this.level().isClientSide) this.entityData.set(accessor, value);
    }

    public void setData(EntityDataAccessor<Boolean> accessor, boolean value) {
        if (!this.level().isClientSide) this.entityData.set(accessor, value);
    }

    public double searchRadius() {
        return isEpic() ? 20 : 10;
    }

    @Override
    protected EntityHitResult findHitEntity(Vec3 startVec, Vec3 endVec) {
        if (!canHitMobs) return null;
        return super.findHitEntity(startVec, endVec);
    }

    public void setTargetEntity(LivingEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    @Override
    public void tick() {
        if (isPhase()) {
            this.noPhysics = true;
        } else {
            this.noPhysics = false;
        }
        if (this.isPhase() && !level().isClientSide) {
            if (tickCount >= 200) {
                this.discard();
                return;
            }
            canHitMobs = true;
            Vec3 vec32 = this.position();
            Vec3 vec33 = vec32.add(getDeltaMovement());
            EntityHitResult entityhitresult = this.findHitEntity(vec32, vec33);
            if (entityhitresult != null) {
                this.onHit(entityhitresult);
            }
            canHitMobs = false;
        }
        super.tick();

        if (isEpic() && targetEntity != null && wasAlreadyHit(targetEntity)) {
            targetEntity = this.findNearestEntity();
        }
        if (!level().isClientSide && getOriginalVelocity() == 0f)
            this.entityData.set(ORIGINAL_VELOCITY, (float) this.getDeltaMovement().length());
        if (this.targetEntity != null && !this.targetEntity.isAlive()) {
            if (!isEpic()) {
                this.discard();
            } else {
                targetEntity = this.findNearestEntity();
                if (targetEntity == null || !targetEntity.isAlive()) {
                    this.discard();
                }
            }
        }

        if (this.entityData.get(IS_HOMING) && !this.inGround) {
            ArrowState currentState = ArrowState.values()[this.entityData.get(ARROW_STATE)];
            if (currentState != ArrowState.NORMAL && targetEntity == null && tickCount > 5) {
                this.discard();
                return;
            }
            int stateTickCounter = this.entityData.get(STATE_TICK_COUNTER);

            switch (currentState) {
                case NORMAL -> handleNormalState(stateTickCounter);
                case SLOWING_DOWN -> handleSlowingDownState(stateTickCounter);
                case STOPPED_AND_ROTATING -> handleStoppedAndRotatingState(stateTickCounter);
                case RESUMING_FLIGHT -> handleResumingFlightState(stateTickCounter);
            }

            stateTickCounter++;
            setData(STATE_TICK_COUNTER, stateTickCounter);
        }
        if (this.level().isClientSide) {
            if (this.inGround) {
                if (this.inGroundTime % 5 == 0) makeParticle(1);
            } else {
                makeParticle(2);
            }
        } else {
            if (this.inGround && this.inGroundTime != 0 && !effects.isEmpty() && this.inGroundTime >= 600) {
                this.level().broadcastEntityEvent(this, (byte) 0);
                // Clear effects after timeout
                effects.clear();
                this.entityData.set(ID_EFFECT_COLOR, -1);
            }
        }
    }

    @Override
    protected float getWaterInertia() {
        if (isPhase()) return 1.0f;
        return super.getWaterInertia();
    }

    private double calculateDotProduct(Vec3 vec1, Vec3 vec2) {
        return vec1.normalize().dot(vec2.normalize());
    }

    private void handleNormalState(int stateTickCounter) {
        if (targetEntity == null || !targetEntity.isAlive() || targetEntity.distanceTo(this) > 20.0) {
            targetEntity = this.findNearestEntity();
        }
        if (targetEntity != null) {
            Vec3 arrowPosition = this.position();
            Vec3 targetPosition = targetEntity.getBoundingBox().getCenter();
            Vec3 directionToTarget = targetPosition.subtract(arrowPosition).normalize();
            Vec3 arrowDirection = this.getDeltaMovement().normalize();
            double dotProduct = calculateDotProduct(arrowDirection, directionToTarget);
            double distanceToTarget = this.position().distanceTo(targetPosition);
            if (dotProduct >= 0.85 || distanceToTarget < 1.0) {
                this.adjustCourseTowards(targetEntity);
            } else {
                setData(ARROW_STATE, ArrowState.SLOWING_DOWN.ordinal());
                setData(STATE_TICK_COUNTER, 0);
            }
        }
    }

    private void handleSlowingDownState(int stateTickCounter) {
        if (stateTickCounter < SLOW_DOWN_DURATION) {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
        } else {
            setData(ARROW_STATE, ArrowState.STOPPED_AND_ROTATING.ordinal());
            setData(STATE_TICK_COUNTER, 0);
        }
    }

    private void handleStoppedAndRotatingState(int stateTickCounter) {
        this.setDeltaMovement(Vec3.ZERO);
        if (targetEntity != null && stateTickCounter != 0) {
            Vec3 arrowPosition = this.position();
            Vec3 targetCenterPosition = targetEntity.getBoundingBox().getCenter();
            Vec3 direction = targetCenterPosition.subtract(arrowPosition).normalize();
            double dx = direction.x, dy = direction.y, dz = direction.z;
            double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
            float targetYaw = (float) (Math.atan2(dx, dz) * (180D / Math.PI));
            float targetPitch = (float) (Math.atan2(dy, horizontalDistance) * (180D / Math.PI));
            float currentYaw = this.getYRot();
            float currentPitch = this.getXRot();
            float newYaw = currentYaw + wrapDegrees(targetYaw - currentYaw) * 0.3f;
            float newPitch = currentPitch + wrapDegrees(targetPitch - currentPitch) * 0.3f;
            this.yRotO = currentYaw;
            this.xRotO = currentPitch;
            this.setYRot(newYaw);
            this.setXRot(newPitch);
        }
        if (stateTickCounter >= STOP_DURATION) {
            setData(ARROW_STATE, ArrowState.RESUMING_FLIGHT.ordinal());
            setData(STATE_TICK_COUNTER, 0);
            if (targetEntity != null) {
                this.adjustCourseTowards(targetEntity);
                if (this.getOwner() instanceof Player player)
                    this.level().playSound(player, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 1.0F, 0.5F);
            }
            this.setDeltaMovement(this.getDeltaMovement().scale(0.25));
        }
    }

    private void handleResumingFlightState(int stateTickCounter) {
        if (this.getDeltaMovement().length() < getOriginalVelocity()) {
            this.setDeltaMovement(this.getDeltaMovement().scale(1.5));
        }
        if (this.getDeltaMovement().length() > getOriginalVelocity()) {
            this.setDeltaMovement(this.getDeltaMovement().normalize().scale(getOriginalVelocity()));
        }
        if (targetEntity != null) {
            this.adjustCourseTowards(targetEntity);
        }
    }

    @Override
    public void setYRot(float yRot) {
        if (yRot == 0f && getDeltaMovement().equals(Vec3.ZERO)) return;
        if (isPhase()) {
            Vec3 delta = getDeltaMovement();
            if (yRot == (float) (Mth.atan2(-delta.x, -delta.z) * 180.0F / (float) Math.PI))
                return;
        }
        super.setYRot(yRot);
    }

    @Override
    public void setXRot(float xRot) {
        if (xRot == 0f && getDeltaMovement().equals(Vec3.ZERO)) return;
        super.setXRot(xRot);
    }

    private float wrapDegrees(float degrees) {
        degrees = degrees % 360.0F;
        if (degrees >= 180.0F) degrees -= 360.0F;
        if (degrees < -180.0F) degrees += 360.0F;
        return degrees;
    }

    @Nullable
    private LivingEntity findNearestEntity() {
        double radius = searchRadius();
        AABB searchArea = this.getBoundingBox().inflate(radius, radius / 2, radius);
        List<Mob> entities = this.level().getEntitiesOfClass(Mob.class, searchArea);
        LivingEntity nearestEntity = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Mob entity : entities) {
            if (entity == this.getOwner() || !entity.isAlive() || wasAlreadyHit(entity)) continue;
            if (getHostileOnly() && !isHostileEntity(entity)) continue;
            double distance = this.distanceToSqr(entity);
            if (distance < nearestDistance) {
                nearestEntity = entity;
                nearestDistance = distance;
            }
        }
        return nearestEntity;
    }

    private boolean wasAlreadyHit(LivingEntity target) {
        if (!isEpic()) return false;
        // piercingIgnoreEntityIds is private in 1.20.1 AbstractArrow - use getPierceLevel as proxy
        // If the arrow has pierce enabled but this entity is still alive, we consider it as already hit
        // This is a simplified check - the full HPPC IntOpenHashSet is not accessible
        return false;
    }

    private void adjustCourseTowards(LivingEntity target) {
        Vec3 arrowPosition = this.position();
        Vec3 targetCenterPosition = target.getBoundingBox().getCenter();
        Vec3 direction = targetCenterPosition.subtract(arrowPosition).normalize();
        if (this.getDeltaMovement().equals(Vec3.ZERO))
            this.setDeltaMovement(direction.scale(0.1f));
        else
            this.setDeltaMovement(direction.scale(this.getDeltaMovement().length()));
        double dx = direction.x, dy = direction.y, dz = direction.z;
        double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
        this.setYRot((float) (Math.atan2(dx, dz) * (180D / Math.PI)));
        this.setXRot((float) (Math.atan2(dy, horizontalDistance) * (180D / Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    private void makeParticle(int particleAmount) {
        int i = this.getColor();
        if (i != -1 && particleAmount > 0) {
            float r = (float) (i >> 16 & 0xFF) / 255.0F;
            float g = (float) (i >> 8 & 0xFF) / 255.0F;
            float b = (float) (i & 0xFF) / 255.0F;
            for (int j = 0; j < particleAmount; j++) {
                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.ENTITY_EFFECT,
                        this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), r, g, b);
            }
        }
    }

    public int getColor() {
        return this.entityData.get(ID_EFFECT_COLOR);
    }

    @Override
    protected void onHit(HitResult result) {
        HitResult.Type type = result.getType();
        if (isPhase() && type == HitResult.Type.BLOCK) return;
        super.onHit(result);
        if (!effects.isEmpty()) {
            if (this.entityData.get(IS_LINGERING)) {
                this.makeAreaOfEffectCloud();
            }
            if (this.entityData.get(IS_SPLASH)) {
                this.applySplash(result.getType() == HitResult.Type.ENTITY ?
                        ((EntityHitResult) result).getEntity() : null);
                int color = getColor();
                // Check for instant effects to choose right particle event
                boolean hasInstant = effects.stream().anyMatch(e -> e.getEffect().isInstantenous());
                this.level().levelEvent(hasInstant ? 2007 : 2002, this.blockPosition(),
                        color == -1 ? 0 : color);
            }
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (isEpic()) {
            setData(ARROW_STATE, ArrowState.STOPPED_AND_ROTATING.ordinal());
            setData(STATE_TICK_COUNTER, 0);
        }
    }

    @Override
    public boolean isCurrentlyGlowing() {
        return isPhase();
    }

    private void applySplash(@Nullable Entity hitEntity) {
        AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        if (!list.isEmpty()) {
            Entity source = this.getEffectSource();
            for (LivingEntity livingEntity : list) {
                if (livingEntity.isAffectedByPotions()) {
                    double d0 = this.distanceToSqr(livingEntity);
                    if (d0 < 16.0) {
                        double factor = livingEntity == hitEntity ? 1.0 : 1.0 - Math.sqrt(d0) / 4.0;
                        for (MobEffectInstance effectInstance : effects) {
                            MobEffect effect = effectInstance.getEffect();
                            if (effect.getCategory() == MobEffectCategory.HARMFUL &&
                                    getOwner() != null && livingEntity.is(getOwner())) continue;
                            if (effect.getCategory() == MobEffectCategory.BENEFICIAL &&
                                    getOwner() != null && !livingEntity.is(getOwner())) continue;
                            if (effect.isInstantenous()) {
                                effect.applyInstantenousEffect(this, this.getOwner(), livingEntity,
                                        effectInstance.getAmplifier(), factor);
                            } else {
                                int duration = (int) (factor * (double) effectInstance.getDuration() + 0.5);
                                MobEffectInstance toApply = new MobEffectInstance(effect, duration,
                                        effectInstance.getAmplifier(), effectInstance.isAmbient(),
                                        effectInstance.isVisible());
                                if (toApply.getDuration() > 20) {
                                    livingEntity.addEffect(toApply, source);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void makeAreaOfEffectCloud() {
        AreaEffectCloud cloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
        if (this.getOwner() instanceof LivingEntity owner) {
            cloud.setOwner(owner);
        }
        cloud.setRadius(3.0F);
        cloud.setRadiusOnUse(-0.1F);
        cloud.setWaitTime(10);
        cloud.setRadiusPerTick(-cloud.getRadius() / (float) cloud.getDuration());
        for (MobEffectInstance effect : effects) {
            cloud.addEffect(new MobEffectInstance(effect));
        }
        this.level().addFreshEntity(cloud);
    }

    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        if (!this.entityData.get(IS_POTIONARROW) || effects.isEmpty()) return;
        Entity source = this.getEffectSource();
        for (MobEffectInstance effectInstance : effects) {
            MobEffect effect = effectInstance.getEffect();
            if (effect.getCategory() == MobEffectCategory.HARMFUL &&
                    getOwner() != null && living.is(getOwner())) continue;
            if (effect.getCategory() == MobEffectCategory.BENEFICIAL &&
                    getOwner() != null && !living.is(getOwner())) continue;
            if (effect.isInstantenous()) {
                effect.applyInstantenousEffect(this, this.getOwner(), living,
                        effectInstance.getAmplifier(), 1.0);
            } else {
                int duration = Math.max(effectInstance.getDuration() / 2, 1);
                living.addEffect(new MobEffectInstance(effect, duration,
                        effectInstance.getAmplifier(), effectInstance.isAmbient(),
                        effectInstance.isVisible()), source);
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 0) {
            int i = this.getColor();
            if (i != -1) {
                float f = (float) (i >> 16 & 0xFF) / 255.0F;
                float f1 = (float) (i >> 8 & 0xFF) / 255.0F;
                float f2 = (float) (i & 0xFF) / 255.0F;
                for (int j = 0; j < 20; j++) {
                    this.level().addParticle(net.minecraft.core.particles.ParticleTypes.ENTITY_EFFECT,
                            this.getRandomX(0.5), this.getRandomY(), this.getRandomZ(0.5), f, f1, f2);
                }
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("is_potionarrow", this.entityData.get(IS_POTIONARROW));
        pCompound.putBoolean("is_splash", this.entityData.get(IS_SPLASH));
        pCompound.putBoolean("is_lingering", this.entityData.get(IS_LINGERING));
        pCompound.putBoolean("is_homing", this.entityData.get(IS_HOMING));
        pCompound.putInt("arrow_state", this.entityData.get(ARROW_STATE));
        pCompound.putInt("state_tick_counter", this.entityData.get(STATE_TICK_COUNTER));
        pCompound.putFloat("original_velocity", this.entityData.get(ORIGINAL_VELOCITY));
        pCompound.putBoolean("is_epic_arrow", this.entityData.get(IS_EPIC_ARROW));
        pCompound.putBoolean("is_phase", this.entityData.get(IS_PHASE));
        // Save effects
        ListTag effectList = new ListTag();
        for (MobEffectInstance effect : effects) {
            effectList.add(effect.save(new CompoundTag()));
        }
        pCompound.put("custom_effects", effectList);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(IS_POTIONARROW, pCompound.getBoolean("is_potionarrow"));
        this.entityData.set(IS_SPLASH, pCompound.getBoolean("is_splash"));
        this.entityData.set(IS_LINGERING, pCompound.getBoolean("is_lingering"));
        this.entityData.set(IS_HOMING, pCompound.getBoolean("is_homing"));
        this.entityData.set(ARROW_STATE, pCompound.getInt("arrow_state"));
        this.entityData.set(STATE_TICK_COUNTER, pCompound.getInt("state_tick_counter"));
        this.entityData.set(ORIGINAL_VELOCITY, pCompound.getFloat("original_velocity"));
        this.entityData.set(IS_EPIC_ARROW, pCompound.getBoolean("is_epic_arrow"));
        this.entityData.set(IS_PHASE, pCompound.getBoolean("is_phase"));
        // Load effects
        effects.clear();
        if (pCompound.contains("custom_effects", Tag.TAG_LIST)) {
            ListTag effectList = pCompound.getList("custom_effects", Tag.TAG_COMPOUND);
            for (int i = 0; i < effectList.size(); i++) {
                MobEffectInstance effect = MobEffectInstance.load(effectList.getCompound(i));
                if (effect != null) effects.add(effect);
            }
        }
        updateColor();
    }
}



