package com.direwolf20.justdirethings.common.entities;

import com.direwolf20.justdirethings.client.particles.paradoxparticle.ParadoxParticleData;
import com.direwolf20.justdirethings.datagen.JustDireBlockTags;
import com.direwolf20.justdirethings.datagen.JustDireEntityTags;
import com.direwolf20.justdirethings.datagen.JustDireItemTags;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ParadoxEntity extends Entity {

	private static final EntityDataAccessor<Integer> REQUIRED_CONSUMPTION = SynchedEntityData
			.defineId(ParadoxEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> CONSUMPTION = SynchedEntityData.defineId(ParadoxEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> RADIUS = SynchedEntityData.defineId(ParadoxEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> SHRINK_SCALE = SynchedEntityData.defineId(ParadoxEntity.class,
			EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Integer> TARGET_RADIUS = SynchedEntityData.defineId(ParadoxEntity.class,
			EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> GROWTH_TICKS = SynchedEntityData.defineId(ParadoxEntity.class,
			EntityDataSerializers.INT);

	public int growthDuration = 50;
	private final Map<BlockPos, Integer> blocksToAbsorb = new HashMap<>();
	private int maxRadius = 12;
	private double itemSuckSpeed = 0.5;
	private boolean collapsing = false;
	private int maxBlocksForPerf = 40;
	public int radiusGrowthTime = 1200;
	public int radiusGrowthTimer = 0;
	public int maxRadiusGrowthTimer;
	public int growthPerBlock = 10;
	public int growthPerItem = 10;

	public ParadoxEntity(EntityType<?> type, Level level) {
		super(type, level);
		this.noPhysics = true;
		this.noCulling = true;
		maxRadiusGrowthTimer = radiusGrowthTime * maxRadius + radiusGrowthTime;
	}

	public ParadoxEntity(Level level, BlockPos pos) {
		this(Registration.ParadoxEntity.get(), level);
		this.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(REQUIRED_CONSUMPTION, 100);
		this.entityData.define(CONSUMPTION, 0);
		this.entityData.define(RADIUS, 0);
		this.entityData.define(SHRINK_SCALE, 1.0f);
		this.entityData.define(TARGET_RADIUS, 0);
		this.entityData.define(GROWTH_TICKS, 0);
	}

	@Override
	public void tick() {
		super.tick();
		int currentRadius = getRadius();
		incRadiusGrowthTimer(1);

		if (level().isClientSide)
			return;

		if (collapsing) {
			float scale = getShrinkScale() - 0.02f;
			setShrinkScale(Math.max(scale, 0.0f));
			if (getShrinkScale() <= 0.01f) {
				this.discard();
			}
			return;
		}

		int calculatedTargetRadius = Math.min(maxRadius, Math.max(0, radiusGrowthTimer / radiusGrowthTime));
		int targetRadius = getTargetRadius();
		if (calculatedTargetRadius != targetRadius && getGrowthTicks() == 0) {
			setTargetRadius(calculatedTargetRadius);
		}

		if (currentRadius != getTargetRadius()) {
			int growthTicks = getGrowthTicks() + 1;
			setGrowthTicks(growthTicks);
			if (growthTicks >= growthDuration) {
				setRadius(getTargetRadius());
				setGrowthTicks(0);
			}
		}

		handleBlockAbsorption(currentRadius);
		handleItemAbsorption(currentRadius);
	}

	public void incRadiusGrowthTimer(int value) {
		radiusGrowthTimer = Math.min(maxRadiusGrowthTimer, radiusGrowthTimer + value);
	}

	public void decRadiusGrowthTimer(int value) {
		radiusGrowthTimer = Math.max(0, radiusGrowthTimer - value);
	}

	private void handleBlockAbsorption(int currentRadius) {
		if (currentRadius <= 0)
			return;
		BlockPos center = getOnPos();
		for (BlockPos pos : BlockPos.betweenClosed(center.offset(-currentRadius, -currentRadius, -currentRadius),
				center.offset(currentRadius, currentRadius, currentRadius))) {
			if (random.nextFloat() < 0.0125f && isBlockValid(pos)) {
				blocksToAbsorb.put(new BlockPos(pos), 40 + random.nextInt(41));
			}
		}

		ServerLevel serverLevel = (ServerLevel) level();
		Iterator<Map.Entry<BlockPos, Integer>> iterator = blocksToAbsorb.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<BlockPos, Integer> entry = iterator.next();
			BlockPos pos = entry.getKey();
			if (!isBlockWithinRadius(pos) || level().isEmptyBlock(pos)) {
				iterator.remove();
				continue;
			}
			int timeLeft = entry.getValue() - 1;

			Vec3 sourcePos = Vec3.atCenterOf(pos);
			BlockState blockState = level().getBlockState(pos);
			ItemStack blockStack = new ItemStack(blockState.getBlock());
			if (blockStack.isEmpty())
				blockStack = new ItemStack(Blocks.STONE);
			Vec3 target = position().add(0, 0.5, 0);
			ParadoxParticleData data = new ParadoxParticleData(blockStack, target.x, target.y, target.z);

			float spawnChance = 1;
			if (blocksToAbsorb.size() > maxBlocksForPerf * 2)
				spawnChance = 0.25f;
			else if (blocksToAbsorb.size() > maxBlocksForPerf)
				spawnChance = 0.50f;

			if (random.nextFloat() < spawnChance) {
				double px = sourcePos.x + (random.nextDouble() - 0.5) * 0.5;
				double py = sourcePos.y + (random.nextDouble() - 0.5) * 0.5;
				double pz = sourcePos.z + (random.nextDouble() - 0.5) * 0.5;
				serverLevel.sendParticles(data, px, py, pz, 1, 0, 0, 0, 0);
			}

			if (timeLeft <= 0) {
				int particles = 8;
				if (blocksToAbsorb.size() > maxBlocksForPerf * 2)
					particles = 3;
				else if (blocksToAbsorb.size() > maxBlocksForPerf)
					particles = 5;
				for (int i = 0; i < particles; i++) {
					double px = sourcePos.x + (random.nextDouble() - 0.5) * 0.5;
					double py = sourcePos.y + (random.nextDouble() - 0.5) * 0.5;
					double pz = sourcePos.z + (random.nextDouble() - 0.5) * 0.5;
					serverLevel.sendParticles(data, px, py, pz, 1, 0, 0, 0, 0);
				}
				level().removeBlock(pos, false);
				incRadiusGrowthTimer(growthPerBlock);
				iterator.remove();
			} else {
				entry.setValue(timeLeft);
			}
		}
	}

	public boolean isBlockWithinRadius(BlockPos pos) {
		BlockPos centerPos = getOnPos();
		int radius = getTargetRadius();
		AABB aabb = new AABB(centerPos.getX() - radius, centerPos.getY() - radius, centerPos.getZ() - radius,
				centerPos.getX() + radius + 1, centerPos.getY() + radius + 1, centerPos.getZ() + radius + 1);
		return aabb.contains(pos.getX(), pos.getY(), pos.getZ());
	}

	private boolean isValidItem(ItemEntity entity) {
		return !entity.getItem().is(JustDireItemTags.PARADOX_DENY);
	}

	private boolean isValidEntity(Entity entity) {
		if (entity.isMultipartEntity())
			return false;
		if (entity instanceof PartEntity<?>)
			return false;
		if (entity instanceof Player)
			return false;
		if (entity.getType().is(JustDireEntityTags.PARADOX_ABSORB_DENY))
			return false;
		return true;
	}

	private void handleItemAbsorption(int currentRadius) {
		List<ItemEntity> items = level().getEntitiesOfClass(ItemEntity.class,
				getBoundingBox().inflate(currentRadius + 0.25f));
		for (ItemEntity item : items) {
			if (!isValidItem(item))
				continue;
			if (collapsing)
				break;
			Vec3 direction = position().subtract(item.position()).normalize().scale(itemSuckSpeed);
			item.setNoGravity(true);
			item.setDeltaMovement(direction);
			if (position().closerThan(item.position(), 0.25)) {
				ItemStack itemStack = item.getItem();
				if (itemStack.is(Registration.TimeCrystal.get()))
					collapse();
				else
					incRadiusGrowthTimer(growthPerItem * itemStack.getCount());
				item.discard();
			}
		}
		if (collapsing)
			return;
		List<LivingEntity> livingEntities = level().getEntitiesOfClass(LivingEntity.class,
				getBoundingBox().inflate(currentRadius + 0.25f));
		for (LivingEntity livingEntity : livingEntities) {
			if (!isValidEntity(livingEntity))
				continue;
			Vec3 direction = position().subtract(livingEntity.position()).normalize().scale(itemSuckSpeed);
			livingEntity.setNoGravity(true);
			livingEntity.setDeltaMovement(direction);
			if (position().closerThan(livingEntity.position(), 0.25)) {
				livingEntity.discard();
			}
		}
	}

	private void collapse() {
		collapsing = true;
		level().playSound(null, getX(), getY(), getZ(), SoundEvents.WITHER_AMBIENT, SoundSource.HOSTILE, 1.0f, 0.25f);
	}

	public boolean isBlockValid(BlockPos blockPos) {
		BlockState blockState = level().getBlockState(blockPos);
		if (blockState.isAir())
			return false;
		if (blockState.is(JustDireBlockTags.PARADOX_ABSORB_DENY))
			return false;
		if (blocksToAbsorb.containsKey(blockPos))
			return false;
		if (blockState.getDestroySpeed(level(), blockPos) < 0)
			return false;
		if (blockState.getBlock() instanceof LiquidBlock) {
			return blockState.getFluidState().isSource();
		}
		return true;
	}

	public boolean isCollapsing() {
		return getShrinkScale() < 1.0f;
	}

	public int getRadius() {
		return this.entityData.get(RADIUS);
	}
	public void setRadius(int radius) {
		this.entityData.set(RADIUS, Math.min(radius, maxRadius));
	}
	public int getGrowthTicks() {
		return this.entityData.get(GROWTH_TICKS);
	}
	public void setGrowthTicks(int growthTicks) {
		this.entityData.set(GROWTH_TICKS, growthTicks);
	}
	public int getTargetRadius() {
		return this.entityData.get(TARGET_RADIUS);
	}
	public void setTargetRadius(int radius) {
		this.entityData.set(TARGET_RADIUS, radius);
		setGrowthTicks(0);
	}
	public float getShrinkScale() {
		return this.entityData.get(SHRINK_SCALE);
	}
	public void setShrinkScale(float scale) {
		this.entityData.set(SHRINK_SCALE, scale);
	}
	public int getRequiredConsumption() {
		return this.entityData.get(REQUIRED_CONSUMPTION);
	}
	public int getConsumed() {
		return this.entityData.get(CONSUMPTION);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		if (tag.contains("radius"))
			this.entityData.set(RADIUS, tag.getInt("radius"));
		if (tag.contains("targetRadius"))
			this.entityData.set(TARGET_RADIUS, tag.getInt("targetRadius"));
		if (tag.contains("radiusGrowthTimer"))
			this.radiusGrowthTimer = tag.getInt("radiusGrowthTimer");
		if (tag.contains("shrinkScale"))
			this.entityData.set(SHRINK_SCALE, tag.getFloat("shrinkScale"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		tag.putInt("radius", getRadius());
		tag.putInt("targetRadius", getTargetRadius());
		tag.putInt("radiusGrowthTimer", radiusGrowthTimer);
		tag.putFloat("shrinkScale", getShrinkScale());
	}

	@Override
	public boolean isPickable() {
		return false;
	}

	@Override
	public boolean isPushable() {
		return false;
	}
}
