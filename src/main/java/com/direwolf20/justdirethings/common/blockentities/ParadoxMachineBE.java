package com.direwolf20.justdirethings.common.blockentities;

import com.direwolf20.justdirethings.client.particles.glitterparticle.GlitterParticleData;
import com.direwolf20.justdirethings.common.blockentities.basebe.*;
import com.direwolf20.justdirethings.common.capabilities.JustDireFluidTank;
import com.direwolf20.justdirethings.common.capabilities.MachineEnergyStorage;
import com.direwolf20.justdirethings.common.entities.ParadoxEntity;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.ParadoxSyncPayload;
import com.direwolf20.justdirethings.datagen.JustDireBlockTags;
import com.direwolf20.justdirethings.datagen.JustDireEntityTags;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.MiscTools;
import com.direwolf20.justdirethings.util.NBTHelpers;
import com.direwolf20.justdirethings.util.UsefulFakePlayer;
import com.direwolf20.justdirethings.util.interfacehelpers.AreaAffectingData;
import com.direwolf20.justdirethings.util.interfacehelpers.RedstoneControlData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class ParadoxMachineBE extends BaseMachineBE
		implements
			PoweredMachineBE,
			AreaAffectingBE,
			RedstoneControlledBE,
			FluidMachineBE {
	public RedstoneControlData redstoneControlData = getDefaultRedstoneData();
	public final FluidContainerData fluidContainerData;
	public AreaAffectingData areaAffectingData = new AreaAffectingData(
			getBlockState().getValue(BlockStateProperties.FACING));
	public final PoweredMachineContainerData poweredMachineData;
	private final MachineEnergyStorage energyStorage;
	private final JustDireFluidTank fluidTank;
	public CompoundTag snapshotData = new CompoundTag();
	public int savedBlocks = -1;
	public int savedEntities = -1;
	public int numBlocks = -1;
	public int numEntities = -1;
	public boolean renderParadox = false;
	public int targetType = 0;
	public boolean isRunning = false;
	public int timeRunning = 0;
	public int fePerTick = 0;
	public int fluidPerTick = 0;
	public float paradoxEnergy = 0;
	public Map<BlockPos, BlockState> restoringBlocks = new HashMap<>();
	public List<Vec3> restoringEntites = new ArrayList<>();
	private static final Random random = new Random();

	public ParadoxMachineBE(BlockPos pPos, BlockState pBlockState) {
		super(Registration.ParadoxMachineBE.get(), pPos, pBlockState);
		energyStorage = new MachineEnergyStorage(getMaxEnergy());
		fluidTank = new JustDireFluidTank(getMaxMB());
		poweredMachineData = new PoweredMachineContainerData(this);
		fluidContainerData = new FluidContainerData(this);
	}

	@Override
	public void tickClient() {
		if (!isRunning || level == null)
			return;
		timeRunning++;
		for (Map.Entry<BlockPos, BlockState> entry : restoringBlocks.entrySet()) {
			drawRestoringParticles(entry.getKey().getCenter());
		}
		for (Vec3 vec3 : restoringEntites) {
			drawRestoringParticles(vec3);
		}
	}

	public void drawRestoringParticles(Vec3 vec3) {
		double d0 = vec3.x;
		double d1 = vec3.y;
		double d2 = vec3.z;

		double offsetX = random.nextBoolean() ? -0.5 + random.nextDouble() * 0.5 : 1.0 + random.nextDouble() * 0.5;
		double offsetY = random.nextBoolean() ? -0.5 + random.nextDouble() * 0.5 : 1.0 + random.nextDouble() * 0.5;
		double offsetZ = random.nextBoolean() ? -0.5 + random.nextDouble() * 0.5 : 1.0 + random.nextDouble() * 0.5;

		double startX = d0 - 0.5 + offsetX;
		double startY = d1 - 0.5 + offsetY;
		double startZ = d2 - 0.5 + offsetZ;

		float size = 0.05f + (0.025f - 0.05f) * random.nextFloat();
		GlitterParticleData data = new GlitterParticleData(d0, d1, d2, size, 0.4f, 1.0f, 0.39f, 1.0f, 120f);
		level.addParticle(data, startX, startY, startZ, 0.00025, 0.00025, 0.00025);
	}

	@Override
	public void tickServer() {
		super.tickServer();
		doParadox();
		if (paradoxEnergy >= getMaxParadoxEnergy())
			spawnParadox();
	}

	public int getRunTime() {
		if (!isRunning)
			return 300;
		return (restoringBlocks.size() + restoringEntites.size()) * 10;
	}

	public void receiveRunTime(int runtime) {
		this.timeRunning = runtime;
	}

	public float getParadoxEnergyPerBlock() {
		return Config.PARADOX_ENERGY_PER_BLOCK.get().floatValue();
	}
	public float getParadoxEnergyPerEntity() {
		return Config.PARADOX_ENERGY_PER_ENTITY.get().floatValue();
	}
	public float getMaxParadoxEnergy() {
		return Config.PARADOX_ENERGY_MAX.get().floatValue();
	}

	public void addParadoxEnergy(float amt) {
		this.paradoxEnergy = Math.min(getMaxParadoxEnergy(), paradoxEnergy + amt);
		markDirtyClient();
	}

	public void resetParadoxEnergy() {
		this.paradoxEnergy = 0;
		markDirtyClient();
	}

	public void spawnParadox() {
		if (level == null)
			return;
		ParadoxEntity entity = new ParadoxEntity(level, getStartingPoint());
		level.addFreshEntity(entity);
		resetParadoxEnergy();
	}

	public boolean paradoxExists() {
		if (level == null)
			return true;
		return !level.getEntitiesOfClass(ParadoxEntity.class, new AABB(getStartingPoint())).isEmpty();
	}

	public void startParadox() {
		if (!(isActiveRedstone() && canRun()))
			return;
		if (!canParadox())
			return;
		if (paradoxExists())
			return;
		if (!isRunning) {
			if (isEmptyScanOnCooldown())
				return;
			UsefulFakePlayer fakePlayer = getUsefulFakePlayer((ServerLevel) level);
			restoringBlocks = testRestoreBlocks(fakePlayer);
			restoringEntites = new ArrayList<>(getEntitiesFromNBT().keySet());
			if (restoringBlocks.isEmpty() && restoringEntites.isEmpty()) {
				// Nothing restorable right now (blocked target area, entity UUID
				// conflicts, etc.) - back off instead of retrying every tick.
				setEmptyScanCooldown(20);
				return;
			}
			isRunning = true;
			fePerTick = getEnergyCostPerTick(getEnergyCost(restoringBlocks.size(), restoringEntites.size()));
			fluidPerTick = getFluidCostPerTick(getFluidCost(restoringBlocks.size(), restoringEntites.size()));
			level.playSound(null, getBlockPos(), SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, 0.25F);
			markDirtyClient();
		}
	}

	public void doParadox() {
		if (level == null)
			return;
		startParadox();
		if (!isRunning)
			return;
		if (timeRunning % 20 == 0 && !(timeRunning >= getRunTime())) {
			final int tr = timeRunning;
			final BlockPos bp = getBlockPos();
			for (Player player : level.players()) {
				if (player instanceof ServerPlayer sp) {
					PacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp), new ParadoxSyncPayload(bp, tr));
				}
			}
		}
		if (timeRunning % 100 == 0 && !(timeRunning >= getRunTime())) {
			level.playSound(null, getBlockPos(), SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5F, 0.25F);
		}
		if (extractFluid(fluidPerTick) == fluidPerTick && extractEnergy(fePerTick, false) == fePerTick) {
			timeRunning++;
		} else {
			stopRunning(false);
			return;
		}
		if (timeRunning >= getRunTime())
			stopRunning(true);
	}

	public void stopRunning(boolean success) {
		if (success) {
			int finalFluidCost = getFluidCost(restoringBlocks.size(), restoringEntites.size())
					- (fluidPerTick * getRunTime());
			int finalEnergyCost = getEnergyCost(restoringBlocks.size(), restoringEntites.size())
					- (fePerTick * getRunTime());
			if (extractFluid(finalFluidCost) == finalFluidCost
					&& extractEnergy(finalEnergyCost, false) == finalEnergyCost) {
				UsefulFakePlayer fakePlayer = getUsefulFakePlayer((ServerLevel) level);
				restoreBlocks(fakePlayer);
				addParadoxEnergy(getParadoxEnergyPerBlock() * restoringBlocks.size());
				restoreEntities(fakePlayer);
				addParadoxEnergy(getParadoxEnergyPerEntity() * restoringEntites.size());
				postRun();
				level.playSound(null, getBlockPos(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.BLOCKS, 0.5F,
						0.25F);
			} else {
				level.playSound(null, getBlockPos(), SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 0.5F, 0.25F);
			}
		} else {
			level.playSound(null, getBlockPos(), SoundEvents.CONDUIT_DEACTIVATE, SoundSource.BLOCKS, 0.5F, 0.25F);
		}
		timeRunning = 0;
		isRunning = false;
		restoringBlocks.clear();
		restoringEntites.clear();
		fePerTick = 0;
		fluidPerTick = 0;
		markDirtyClient();
	}

	public boolean canPlace(FakePlayer fakePlayer, BlockPos blockPos) {
		if (!level.mayInteract(fakePlayer, blockPos))
			return false;
		if (!level.getBlockState(blockPos).canBeReplaced())
			return false;
		if (!canPlaceAt(level, blockPos, fakePlayer))
			return false;
		return true;
	}

	private Map<BlockPos, BlockState> testRestoreBlocks(FakePlayer fakePlayer) {
		Map<BlockPos, BlockState> blocksToRestore = getBlocksFromNBT();
		Map<BlockPos, BlockState> returnMap = new HashMap<>();
		for (Map.Entry<BlockPos, BlockState> entry : blocksToRestore.entrySet()) {
			BlockPos blockPos = entry.getKey();
			BlockState blockState = entry.getValue();
			if (!canPlace(fakePlayer, blockPos))
				continue;
			returnMap.put(blockPos, blockState);
		}
		return returnMap;
	}

	public boolean canPlace(BlockPos blockPos) {
		return level.getBlockState(blockPos).canBeReplaced();
	}

	public Map<BlockPos, BlockState> testRestoreBlocks() {
		Map<BlockPos, BlockState> blocksToRestore = getBlocksFromNBT();
		Map<BlockPos, BlockState> returnMap = new HashMap<>();
		for (Map.Entry<BlockPos, BlockState> entry : blocksToRestore.entrySet()) {
			BlockPos blockPos = entry.getKey();
			BlockState blockState = entry.getValue();
			if (!canPlace(blockPos))
				continue;
			returnMap.put(blockPos, blockState);
		}
		return returnMap;
	}

	private void restoreBlocks(FakePlayer fakePlayer) {
		int restoredCount = 0;
		for (Map.Entry<BlockPos, BlockState> entry : restoringBlocks.entrySet()) {
			BlockPos blockPos = entry.getKey();
			BlockState blockState = entry.getValue();
			if (!canPlace(fakePlayer, blockPos))
				continue;
			if (level.setBlock(blockPos, blockState, 3))
				restoredCount++;
		}
		numBlocks = restoredCount;
	}

	private void restoreEntities(FakePlayer fakePlayer) {
		Map<Vec3, LivingEntity> entitiesToRestore = getEntitiesFromNBT();
		int restoredCount = 0;
		for (Map.Entry<Vec3, LivingEntity> entry : entitiesToRestore.entrySet()) {
			Vec3 entityPos = entry.getKey();
			if (!restoringEntites.contains(entityPos))
				continue;
			LivingEntity entity = entry.getValue();
			entity.moveTo(entityPos.x, entityPos.y, entityPos.z, entity.getYRot(), entity.getXRot());
			if (level.addFreshEntity(entity))
				restoredCount++;
		}
		numEntities = restoredCount;
	}

	public boolean hasSnapshotData() {
		return !snapshotData.isEmpty();
	}

	@Override
	public int getMaxMB() {
		return Config.PARADOX_TOTAL_FLUID_CAPACITY.get();
	}

	@Override
	public JustDireFluidTank getFluidTank() {
		return fluidTank;
	}

	@Override
	public ContainerData getFluidContainerData() {
		return fluidContainerData;
	}

	@Override
	public RedstoneControlData getRedstoneControlData() {
		return redstoneControlData;
	}

	@Override
	public BlockEntity getBlockEntity() {
		return this;
	}

	@Override
	public RedstoneControlData getDefaultRedstoneData() {
		return new RedstoneControlData(MiscHelpers.RedstoneMode.PULSE);
	}

	@Override
	public ContainerData getContainerData() {
		return poweredMachineData;
	}

	@Override
	public MachineEnergyStorage getEnergyStorage() {
		return energyStorage;
	}

	@Override
	public int getMaxEnergy() {
		return Config.PARADOX_TOTAL_RF_CAPACITY.get();
	}

	@Override
	public int getStandardEnergyCost() {
		return getBlockEnergyCost();
	}

	public int getBlockEnergyCost() {
		return Config.PARADOX_RF_PER_BLOCK.get();
	}
	public int getEntityEnergyCost() {
		return Config.PARADOX_RF_PER_ENTITY.get();
	}

	public int getEnergyCostPerTick(int cost) {
		return (int) Math.floor((double) cost / getRunTime());
	}

	public int getEnergyCost(int blocks, int entities) {
		return blocks * getBlockEnergyCost() + entities * getEntityEnergyCost();
	}

	@Override
	public AreaAffectingData getAreaAffectingData() {
		return areaAffectingData;
	}

	public void setAreaOnly(double x, double y, double z) {
		getAreaAffectingData().xRadius = Math.max(0, Math.min(x, maxRadius));
		getAreaAffectingData().yRadius = Math.max(0, Math.min(y, maxRadius));
		getAreaAffectingData().zRadius = Math.max(0, Math.min(z, maxRadius));
		getAreaAffectingData().area = null;
		markDirtyClient();
	}

	public boolean canParadox() {
		return hasSnapshotData() && !getFluidTank().isEmpty();
	}

	public int extractFluid(int fluidCost) {
		return getFluidTank().drain(fluidCost, IFluidHandler.FluidAction.EXECUTE).getAmount();
	}

	public int getFluidCostPerTick(int totalFluidCost) {
		return (int) Math.floor((double) totalFluidCost / getRunTime());
	}

	public int getFluidCost(int blocks, int entities) {
		return blocks * getBlockFluidCost() + entities * getEntityFluidCost();
	}

	public int getBlockFluidCost() {
		return Config.PARADOX_FLUID_PER_BLOCK.get();
	}
	public int getEntityFluidCost() {
		return Config.PARADOX_FLUID_PER_ENTITY.get();
	}

	public void postRun() {
		if (numBlocks == -1 || numEntities == -1)
			return;
		numBlocks = -1;
		numEntities = -1;
	}

	public BlockPos getStartingPoint() {
		return getBlockPos().offset(getAreaAffectingData().xOffset, getAreaAffectingData().yOffset,
				getAreaAffectingData().zOffset);
	}

	private static boolean isParadoxBlockBlacklisted(BlockState blockState) {
		ResourceLocation key = ForgeRegistries.BLOCKS.getKey(blockState.getBlock());
		if (key == null)
			return false;
		return MiscTools.matchesRegexBlacklist(Config.PARADOX_BLOCK_BLACKLIST.get(), key.toString());
	}

	public boolean isBlockPosValid(ServerLevel serverLevel, BlockPos blockPos) {
		BlockState blockState = serverLevel.getBlockState(blockPos);
		if (!blockState.is(JustDireBlockTags.PARADOX_ALLOW))
			return false;
		return !isParadoxBlockBlacklisted(blockState);
	}

	public Map<BlockPos, BlockState> getBlocksFromNBT() {
		BlockPos machinePos = getBlockPos();
		Map<BlockPos, BlockState> blockMap = new HashMap<>();
		if (targetType == 2 || !snapshotData.contains("blocks"))
			return blockMap;
		ListTag blockDataList = snapshotData.getList("blocks", 10);
		for (int i = 0; i < blockDataList.size(); i++) {
			CompoundTag blockTag = blockDataList.getCompound(i);
			BlockPos relativePos = NbtUtils.readBlockPos(blockTag.getCompound("pos"));
			BlockPos worldPos = relativePos.offset(machinePos);
			BlockState blockState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(),
					blockTag.getCompound("state"));
			blockMap.put(worldPos, blockState);
		}
		return blockMap;
	}

	public Map<Vec3, LivingEntity> getEntitiesFromNBT() {
		BlockPos machinePos = getBlockPos();
		Map<Vec3, LivingEntity> entityMap = new HashMap<>();
		if (targetType == 1 || !snapshotData.contains("entities"))
			return entityMap;
		ListTag entityDataList = snapshotData.getList("entities", 10);
		for (int i = 0; i < entityDataList.size(); i++) {
			CompoundTag entityTag = entityDataList.getCompound(i);
			Vec3 relativePos = NBTHelpers.nbtToVec3(entityTag.getCompound("relativePos"));
			Vec3 worldPos = relativePos.add(Vec3.atCenterOf(machinePos));
			CompoundTag entityData = entityTag.getCompound("data");
			LivingEntity entity = restoreEntityFromNBT(entityData, worldPos);
			if (entity != null)
				entityMap.put(worldPos, entity);
		}
		return entityMap;
	}

	private LivingEntity restoreEntityFromNBT(CompoundTag entityData, Vec3 worldPos) {
		if (entityData == null || !entityData.contains("id"))
			return null;
		EntityType<?> entityType = EntityType.byString(entityData.getString("id")).orElse(null);
		if (entityType == null)
			return null;
		if (entityData.contains("UUID")) {
			UUID entityUUID = entityData.getUUID("UUID");
			if (level instanceof ServerLevel serverLevel) {
				if (serverLevel.getEntity(entityUUID) != null)
					return null;
			}
		}
		Entity entity = entityType.create(level);
		if (!(entity instanceof LivingEntity))
			return null;
		CompoundTag sanitizedData = Config.PARADOX_RESTRICTED_MOBS.get()
				? sanitizeEntityData(entityData)
				: sanitizeEntityDataDeny(entityData);
		entity.load(sanitizedData);
		if (entity instanceof Mob mob && level instanceof ServerLevel serverLevel) {
			ForgeEventFactory.onFinalizeSpawn(mob, serverLevel, level.getCurrentDifficultyAt(getBlockPos()),
					MobSpawnType.SPAWNER, null, null);
		}
		entity.moveTo(worldPos.x, worldPos.y, worldPos.z, entity.getYRot(), entity.getXRot());
		return (LivingEntity) entity;
	}

	public CompoundTag sanitizeEntityData(CompoundTag entityData) {
		CompoundTag compoundTag = new CompoundTag();
		String[] fieldsToCopy = {"IsBaby", "UUID", "Health", "Motion", "Rotation", "Fire", "CustomName", "NoAI",
				"PersistenceRequired", "Silent", "Color", "Sheared", "Variant", "FromBucket", "Age", "VillagerData",
				"Xp", "LastRestock", "RestocksToday", "Offers", "EggLayTime"};
		for (String field : fieldsToCopy) {
			if (entityData.contains(field))
				compoundTag.put(field, entityData.get(field));
		}
		return compoundTag;
	}

	public CompoundTag sanitizeEntityDataDeny(CompoundTag entityData) {
		CompoundTag compoundTag = new CompoundTag();
		String[] fieldsToRemove = {"ArmorItems", "HandItems", "Items", "SaddleItem", "Inventory"};
		for (String key : entityData.getAllKeys()) {
			boolean shouldRemove = false;
			for (String field : fieldsToRemove) {
				if (key.equals(field)) {
					shouldRemove = true;
					break;
				}
			}
			if (!shouldRemove && entityData.get(key) != null)
				compoundTag.put(key, entityData.get(key));
		}
		return compoundTag;
	}

	public void setRenderParadox(boolean render, int targetType) {
		this.renderParadox = render;
		this.targetType = targetType;
		markDirtyClient();
	}

	public void snapshotArea() {
		if (level == null)
			return;
		BlockPos machinePos = getBlockPos();
		AABB area = getAABB(machinePos);
		List<CompoundTag> blockDataList = new ArrayList<>();
		List<CompoundTag> entityDataList = new ArrayList<>();

		for (BlockPos pos : findBlocksToSave()) {
			BlockState blockState = level.getBlockState(pos);
			if (!blockState.isAir()) {
				CompoundTag blockTag = new CompoundTag();
				blockTag.put("pos", NbtUtils.writeBlockPos(pos.subtract(machinePos)));
				blockTag.put("state", NbtUtils.writeBlockState(blockState));
				blockDataList.add(blockTag);
			}
		}

		List<LivingEntity> entities = findEntitiesToSave(area);
		for (LivingEntity entity : entities) {
			CompoundTag entityTag = new CompoundTag();
			CompoundTag entityData = new CompoundTag();
			entity.save(entityData);
			Vec3 entityRelativePos = entity.position().subtract(Vec3.atCenterOf(machinePos));
			entityTag.put("relativePos", NBTHelpers.vec3ToNBT(entityRelativePos));
			entityTag.put("data", entityData);
			entityDataList.add(entityTag);
		}

		snapshotData = new CompoundTag();
		ListTag bList = new ListTag();
		bList.addAll(blockDataList);
		ListTag eList = new ListTag();
		eList.addAll(entityDataList);
		snapshotData.put("blocks", bList);
		snapshotData.put("entities", eList);
		clearSnapshotCache();
		markDirtyClient();
	}

	public void clearSnapshotCache() {
		this.savedBlocks = -1;
		this.savedEntities = -1;
	}

	public int getSavedBlocksCount() {
		if (savedBlocks == -1)
			savedBlocks = getBlocksFromNBT().size();
		return savedBlocks;
	}

	public int getSavedEntitiesCount() {
		if (savedEntities == -1)
			savedEntities = getEntitiesFromNBT().size();
		return savedEntities;
	}

	public List<BlockPos> findBlocksToSave() {
		AABB area = getAABB(getBlockPos());
		return BlockPos
				.betweenClosedStream((int) area.minX, (int) area.minY, (int) area.minZ, (int) area.maxX - 1,
						(int) area.maxY - 1, (int) area.maxZ - 1)
				.filter(blockPos -> isBlockPosValid((ServerLevel) level, blockPos)).map(BlockPos::immutable)
				.collect(Collectors.toList());
	}

	public List<LivingEntity> findEntitiesToSave(AABB aabb) {
		return new ArrayList<>(level.getEntitiesOfClass(LivingEntity.class, aabb, this::isValidEntity));
	}

	public boolean isValidEntity(Entity entity) {
		if (entity.isMultipartEntity())
			return false;
		if (entity instanceof PartEntity<?>)
			return false;
		if (entity.getType().is(JustDireEntityTags.PARADOX_DENY))
			return false;
		if (entity instanceof Player)
			return false;
		return !isParadoxEntityBlacklisted(entity);
	}

	private static boolean isParadoxEntityBlacklisted(Entity entity) {
		ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		if (key == null)
			return false;
		return MiscTools.matchesRegexBlacklist(Config.PARADOX_ENTITY_BLACKLIST.get(), key.toString());
	}

	@Override
	public void handleTicks() {
	} // NoOp

	@Override
	public boolean canRun() {
		return true;
	}

	@Override
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putInt("energy", energyStorage.getEnergyStored());
		tag.put("fluidTank", fluidTank.serializeToNBT());
		tag.put("snapshotData", snapshotData);
		tag.putBoolean("renderParadox", renderParadox);
		tag.putInt("targetType", targetType);
		tag.putBoolean("isRunning", isRunning);
		tag.putInt("timeRunning", timeRunning);
		tag.putInt("fePerTick", fePerTick);
		tag.putInt("fluidPerTick", fluidPerTick);
		tag.putFloat("paradoxEnergy", paradoxEnergy);

		ListTag restoringBlocksList = new ListTag();
		for (Map.Entry<BlockPos, BlockState> entry : restoringBlocks.entrySet()) {
			CompoundTag blockData = new CompoundTag();
			blockData.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
			blockData.put("state", NbtUtils.writeBlockState(entry.getValue()));
			restoringBlocksList.add(blockData);
		}
		tag.put("restoringBlocks", restoringBlocksList);

		ListTag restoringEntitiesList = new ListTag();
		for (Vec3 vec : restoringEntites) {
			restoringEntitiesList.add(NBTHelpers.vec3ToNBT(vec));
		}
		tag.put("restoringEntities", restoringEntitiesList);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		if (tag.contains("energy"))
			energyStorage.setEnergy(tag.getInt("energy"));
		if (tag.contains("fluidTank"))
			fluidTank.deserializeFromNBT(tag.getCompound("fluidTank"));
		if (tag.contains("snapshotData"))
			snapshotData = tag.getCompound("snapshotData");
		if (tag.contains("renderParadox"))
			renderParadox = tag.getBoolean("renderParadox");
		if (tag.contains("targetType"))
			targetType = tag.getInt("targetType");
		if (tag.contains("isRunning"))
			isRunning = tag.getBoolean("isRunning");
		if (tag.contains("timeRunning"))
			timeRunning = tag.getInt("timeRunning");
		if (tag.contains("fePerTick"))
			fePerTick = tag.getInt("fePerTick");
		if (tag.contains("fluidPerTick"))
			fluidPerTick = tag.getInt("fluidPerTick");
		if (tag.contains("paradoxEnergy"))
			paradoxEnergy = tag.getFloat("paradoxEnergy");

		restoringBlocks.clear();
		if (tag.contains("restoringBlocks")) {
			ListTag list = tag.getList("restoringBlocks", 10);
			for (int i = 0; i < list.size(); i++) {
				CompoundTag blockData = list.getCompound(i);
				BlockPos pos = NbtUtils.readBlockPos(blockData.getCompound("pos"));
				BlockState state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(),
						blockData.getCompound("state"));
				restoringBlocks.put(pos, state);
			}
		}

		restoringEntites.clear();
		if (tag.contains("restoringEntities")) {
			ListTag list = tag.getList("restoringEntities", 10);
			for (int i = 0; i < list.size(); i++) {
				restoringEntites.add(NBTHelpers.nbtToVec3(list.getCompound(i)));
			}
		}
	}
}
