package com.direwolf20.justdirethings.common.items.interfaces;

import com.direwolf20.justdirethings.common.blockentities.GooSoilBE;
import com.direwolf20.justdirethings.common.blocks.soil.GooSoilBase;
import com.direwolf20.justdirethings.common.containers.ToolSettingContainer;
import com.direwolf20.justdirethings.util.MiningCollect;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.direwolf20.justdirethings.common.items.interfaces.AbilityMethods.handleDrops;
import static com.direwolf20.justdirethings.common.items.interfaces.Helpers.*;

public interface ToggleableTool extends ToggleableItem {
	EnumSet<Ability> getAbilities();

	Map<Ability, AbilityParams> getAbilityParamsMap();

	default AbilityParams getAbilityParams(Ability toolAbility) {
		return getAbilityParamsMap().getOrDefault(toolAbility, new AbilityParams(-1, -1, -1));
	}

	default void registerAbility(Ability ability) {
		getAbilities().add(ability);
	}

	default void registerAbility(Ability ability, AbilityParams abilityParams) {
		getAbilities().add(ability);
		getAbilityParamsMap().put(ability, abilityParams);
	}

	default boolean hasAbility(Ability ability) {
		return getAbilities().contains(ability);
	}

	default List<Ability> getUseOnAbilities(ItemStack itemStack) {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if (ability.useType == Ability.UseType.USE_ON && canUseAbility(itemStack, ability))
				abilityList.add(ability);
		}
		return abilityList;
	}

	default List<Ability> getActiveAbilities(ItemStack itemStack) {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if ((ability.useType == Ability.UseType.USE || ability.useType == Ability.UseType.USE_COOLDOWN)
					&& canUseAbility(itemStack, ability))
				abilityList.add(ability);
		}
		return abilityList;
	}

	default List<Ability> getPassiveTickAbilities(ItemStack itemStack) {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if ((ability.useType == Ability.UseType.PASSIVE_TICK
					|| ability.useType == Ability.UseType.PASSIVE_TICK_COOLDOWN) && canUseAbility(itemStack, ability))
				abilityList.add(ability);
		}
		return abilityList;
	}

	default List<Ability> getCooldownAbilities() {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if (ability.useType == Ability.UseType.USE_COOLDOWN)
				abilityList.add(ability);
		}
		return abilityList;
	}

	/**
	 * Used for toggling on and off via hotkey
	 */
	default List<Ability> getAllPassiveAbilities() {
		List<Ability> abilityList = new ArrayList<>();
		for (Ability ability : getAbilities()) {
			if ((ability.useType == Ability.UseType.PASSIVE || ability.useType == Ability.UseType.PASSIVE_TICK))
				abilityList.add(ability);
		}
		return abilityList;
	}

	default boolean canUseAbility(ItemStack itemStack, Ability toolAbility) {
		return hasAbility(toolAbility) && hasUpgrade(itemStack, toolAbility) && getEnabled(itemStack)
				&& getSetting(itemStack, toolAbility.getName());
	}

	static boolean hasUpgrade(ItemStack stack, Ability ability) {
		if (!ability.requiresUpgrade())
			return true;
		CompoundTag tag = stack.getTag();
		if (tag == null)
			return false;
		return tag.getBoolean("upgrade_" + ability.getName());
	}

	default boolean canUseAbilityAndDurability(ItemStack itemStack, Ability toolAbility) {
		return canUseAbility(itemStack, toolAbility) && (testUseTool(itemStack, toolAbility) >= 0);
	}

	default boolean canUseAbilityAndDurability(ItemStack itemStack, Ability toolAbility, int multiplier) {
		return canUseAbility(itemStack, toolAbility) && (testUseTool(itemStack, toolAbility, multiplier) >= 0);
	}

	default void openSettings(Player player) {
		player.openMenu(
				new SimpleMenuProvider((windowId, playerInventory, playerEntity) -> new ToolSettingContainer(windowId,
						playerInventory, player), Component.translatable("")));

	}

	// Abilities
	default boolean hurtEnemyAbility(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
		damageTool(pStack, pAttacker);
		return true;
	}

	default Set<BlockPos> getBreakBlockPositions(ItemStack pStack, Level pLevel, BlockPos pPos,
			LivingEntity pEntityLiving, BlockState pState) {
		Set<BlockPos> breakBlockPositions = new HashSet<>();
		if (canUseAbility(pStack, Ability.OREMINER) && oreCondition.test(pState)
				&& pStack.isCorrectToolForDrops(pState)) {
			breakBlockPositions.addAll(findLikeBlocks(pLevel, pState, pPos, null, 64, 2)); // Todo: Balance and Config?
		}
		if (canUseAbility(pStack, Ability.TREEFELLER) && logCondition.test(pState)
				&& pStack.isCorrectToolForDrops(pState)) {
			breakBlockPositions.addAll(findLikeBlocks(pLevel, pState, pPos, null, 64, 2)); // Todo: Balance and Config?
		}
		if (canUseAbility(pStack, Ability.HAMMER)) {
			breakBlockPositions.addAll(MiningCollect.collect(pEntityLiving, pPos, getTargetLookDirection(pEntityLiving),
					pLevel, getToolValue(pStack, Ability.HAMMER.getName()), MiningCollect.SizeMode.AUTO, pStack));
		}
		if (pLevel.getBlockEntity(pPos) == null) {
			breakBlockPositions.add(pPos);
		}
		if (canUseAbility(pStack, Ability.SKYSWEEPER) && pStack.isCorrectToolForDrops(pState)) {
			Set<BlockPos> newPos = new HashSet<>();
			for (BlockPos blockPos : breakBlockPositions) {
				BlockPos abovePos = blockPos.above();
				BlockState blockStateAbove = pLevel.getBlockState(abovePos);
				if (fallingBlockCondition.test(blockStateAbove))
					newPos.addAll(findLikeBlocks(pLevel, blockStateAbove, abovePos, Direction.UP, 64, 2)); // Todo:
																											// Balance
																											// and
																											// Config?
			}
			breakBlockPositions.addAll(newPos);
		}
		return breakBlockPositions;
	}

	default boolean canInstaBreak(ItemStack pStack, Level pLevel, Set<BlockPos> breakBlockPositions) {
		boolean instaBreak = false;
		if (canUseAbility(pStack, Ability.INSTABREAK)) { // Only Instabreak if we can instabreak ALL blocks in the area!
			float cumulativeDestroy = 0;
			for (BlockPos pos : breakBlockPositions) {
				BlockState blockState = pLevel.getBlockState(pos);
				float destroySpeedTarget = blockState.getDestroySpeed(pLevel, pos);
				if (destroySpeedTarget < 0)
					continue; // Skip unbreakable blocks (e.g. bedrock)
				cumulativeDestroy = cumulativeDestroy + destroySpeedTarget;
			}
			int rfCostInstaBreak = getInstantRFCost(cumulativeDestroy);
			if (testUseTool(pStack, rfCostInstaBreak) > 0)
				instaBreak = true;
		}
		return instaBreak;
	}

	default void mineBlocksAbility(ItemStack pStack, Level pLevel, BlockPos pPos, LivingEntity pEntityLiving,
			BlockState originalPrimaryState) {
		List<ItemStack> drops = new ArrayList<>();
		int totalExp = 0;
		int fortuneLevel = pEntityLiving.getMainHandItem().getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
		int silkTouchLevel = pEntityLiving.getMainHandItem().getEnchantmentLevel(Enchantments.SILK_TOUCH);
		Set<BlockPos> breakBlockPositions = getBreakBlockPositions(pStack, pLevel, pPos, pEntityLiving,
				originalPrimaryState);
		boolean instaBreak = canInstaBreak(pStack, pLevel, breakBlockPositions);
		for (BlockPos breakPos : breakBlockPositions) {
			if (breakPos.equals(pPos)) {
				// The primary block is already destroyed before mineBlock() is called.
				// FakePlayers (machines) get no drops from vanilla — collect them here so
				// SMELTER and DROPTELEPORT can process them. Only collect when a relevant
				// ability is actually active to avoid scattering unwanted item entities.
				if (pEntityLiving instanceof FakePlayer && pLevel instanceof ServerLevel sl
						&& originalPrimaryState.getDestroySpeed(pLevel, pPos) >= 0
						&& (canUseAbility(pStack, Ability.SMELTER) || canUseAbility(pStack, Ability.DROPTELEPORT))) {
					totalExp += originalPrimaryState.getExpDrop(pLevel, pLevel.random, pPos, fortuneLevel,
							silkTouchLevel);
					Helpers.combineDrops(drops,
							Block.getDrops(originalPrimaryState, sl, pPos, null, pEntityLiving, pStack));
				}
				continue;
			}
			if (testUseTool(pStack) < 0)
				break;
			BlockState breakState = pLevel.getBlockState(breakPos);
			if (breakState.getDestroySpeed(pLevel, breakPos) < 0)
				continue; // Skip unbreakable blocks (e.g. bedrock)
			int exp = breakState.getExpDrop(pLevel, pLevel.random, pPos, fortuneLevel, silkTouchLevel);
			totalExp = totalExp + exp;
			Helpers.combineDrops(drops, breakBlocks(pLevel, breakPos, pEntityLiving, pStack, true, instaBreak));
		}
		if (!pLevel.isClientSide) {
			handleDrops(pStack, (ServerLevel) pLevel, pPos, pEntityLiving, breakBlockPositions, drops,
					originalPrimaryState, totalExp);
			// ServerLevel.destroyBlockProgress(-1) only broadcasts when the server tracked
			// the ID, which it never does for our custom IDs (animations are set purely
			// client-side). Send the reset packet directly to bypass that check.
			if (pEntityLiving instanceof ServerPlayer serverPlayer) {
				for (BlockPos breakPos : breakBlockPositions) {
					if (!breakPos.equals(pPos)) {
						int breakerId = serverPlayer.getId() + (31 * 31 * breakPos.getX()) + (31 * breakPos.getY())
								+ breakPos.getZ();
						serverPlayer.connection.send(new ClientboundBlockDestructionPacket(breakerId, breakPos, -1));
					}
				}
			}
		}
	}

	static int getInstantRFCost(float cumulativeDestroy) {
		return Math.max(Ability.INSTABREAK.getFeCost(), Ability.INSTABREAK.getFeCost() * (int) cumulativeDestroy);
	}

	static void smelterParticles(ServerLevel level, Set<BlockPos> oreBlocksList) {
		Random random = new Random();
		int iterations = oreBlocksList.size() > 10 ? 1 : 5;
		for (int i = 0; i < iterations; i++) {
			for (BlockPos pos : oreBlocksList) {
				double d0 = (double) pos.getX() + random.nextDouble();
				double d1 = (double) pos.getY() + random.nextDouble();
				double d2 = (double) pos.getZ() + random.nextDouble();
				level.sendParticles(ParticleTypes.FLAME, d0, d1, d2, 1, 0.0, 0.0, 0.0, 0);
			}
		}
	}

	static void smokerParticles(ServerLevel level, BlockPos itemPos, int stackSize) {
		Random random = new Random();
		int iterations = stackSize < 10 ? 1 : 5;
		for (int i = 0; i < iterations; i++) {
			double d0 = (double) itemPos.getX() + random.nextDouble();
			double d1 = (double) itemPos.getY() + random.nextDouble();
			double d2 = (double) itemPos.getZ() + random.nextDouble();
			level.sendParticles(ParticleTypes.LARGE_SMOKE, d0, d1, d2, 1, 0.0, 0.0, 0.0, 0);
		}
	}

	static void teleportParticles(ServerLevel level, Vec3 pos) {
		Random random = new Random();
		for (int i = 0; i < 5; i++) {
			double d0 = pos.x() + random.nextDouble();
			double d1 = pos.y() - 0.5d + random.nextDouble();
			double d2 = pos.z() + random.nextDouble();
			level.sendParticles(ParticleTypes.PORTAL, d0, d1, d2, 1, 0.0, 0.0, 0.0, 0);
		}
	}

	static void teleportParticles(ServerLevel level, BlockPos pos, int iterations) {
		Random random = new Random();
		/*
		 * // Generate random positions within the block double xOffset =
		 * random.nextDouble(); // Random offset within the block double yOffset =
		 * random.nextDouble(); // Random offset within the block double zOffset =
		 * random.nextDouble(); // Random offset within the block
		 *
		 * // Calculate the position to spawn each particle, adding random offset to the
		 * block's position double spawnX = pos.getX() + xOffset; double spawnY =
		 * pos.getY() + yOffset; double spawnZ = pos.getZ() + zOffset;
		 *
		 * // Calculate velocity to make the particle move towards the center of the
		 * block // Since we want them to collapse to the center, we calculate the
		 * difference from the center (0.5 offset) and use a negative multiplier double
		 * xVelocity = (0.5 - xOffset) * 0.2; // Adjust multiplier for speed double
		 * yVelocity = (0.5 - yOffset) * 0.2; // Adjust multiplier for speed double
		 * zVelocity = (0.5 - zOffset) * 0.2; // Adjust multiplier for speed
		 *
		 * // Spawn a particle with calculated velocity to move it towards the center of
		 * the block level.sendParticles(ParticleTypes.PORTAL, spawnX, spawnY, spawnZ,
		 * 1, xVelocity, yVelocity, zVelocity, 0.0);
		 */
		for (int i = 0; i < iterations; i++) {
			double d0 = (double) pos.getX() + random.nextDouble();
			double d1 = (double) pos.getY() - 0.5d + random.nextDouble();
			double d2 = (double) pos.getZ() + random.nextDouble();
			level.sendParticles(ParticleTypes.PORTAL, d0, d1, d2, 1, 0.0, 0.0, 0.0, 0);
		}
	}

	static void teleportParticles(ServerLevel level, Set<BlockPos> oreBlocksList) {
		int iterations = oreBlocksList.size() > 10 ? 1 : 5;
		for (BlockPos pos : oreBlocksList) {
			teleportParticles(level, pos, iterations);
		}
	}

	record CooldownEntry(Ability ability, int remaining, boolean active) {
	}

	static List<CooldownEntry> getAllCooldowns(ItemStack itemStack, long currentTick) {
		List<CooldownEntry> result = new ArrayList<>();
		CompoundTag tag = itemStack.getTag();
		if (tag == null || !tag.contains("cooldowns"))
			return result;
		ListTag cooldowns = tag.getList("cooldowns", Tag.TAG_COMPOUND);
		for (int i = 0; i < cooldowns.size(); i++) {
			CompoundTag entry = cooldowns.getCompound(i);
			long remaining = entry.getLong("end_tick") - currentTick;
			if (remaining <= 0)
				continue;
			Ability ability = Ability.valueOf(entry.getString("ability").toUpperCase(Locale.ROOT));
			result.add(new CooldownEntry(ability, (int) remaining, entry.getBoolean("active")));
		}
		return result;
	}

	static int getAnyCooldown(ItemStack itemStack, Ability ability, long currentTick) {
		CompoundTag tag = itemStack.getTag();
		if (tag == null || !tag.contains("cooldowns"))
			return -1;
		ListTag cooldowns = tag.getList("cooldowns", Tag.TAG_COMPOUND);
		for (int i = 0; i < cooldowns.size(); i++) {
			CompoundTag entry = cooldowns.getCompound(i);
			if (entry.getString("ability").equals(ability.getName())) {
				long remaining = entry.getLong("end_tick") - currentTick;
				return remaining > 0 ? (int) remaining : -1;
			}
		}
		return -1;
	}

	static int getCooldown(ItemStack itemStack, Ability ability, boolean active, long currentTick) {
		CompoundTag tag = itemStack.getTag();
		if (tag == null || !tag.contains("cooldowns"))
			return -1;
		ListTag cooldowns = tag.getList("cooldowns", Tag.TAG_COMPOUND);
		for (int i = 0; i < cooldowns.size(); i++) {
			CompoundTag abilityTag = cooldowns.getCompound(i);
			if (abilityTag.getString("ability").equals(ability.getName())
					&& abilityTag.getBoolean("active") == active) {
				long remaining = abilityTag.getLong("end_tick") - currentTick;
				return remaining > 0 ? (int) remaining : -1;
			}
		}
		return -1;
	}

	static void tickCooldowns(ItemStack itemStack, Player player) {
		CompoundTag tag = itemStack.getTag();
		if (tag == null || !tag.contains("cooldowns"))
			return;
		long currentTick = player.level().getGameTime();
		boolean changed = false;
		Set<Integer> cooldownsToRemove = new HashSet<>();
		ListTag cooldowns = tag.getList("cooldowns", Tag.TAG_COMPOUND);
		for (int i = 0; i < cooldowns.size(); i++) {
			CompoundTag compoundTag = cooldowns.getCompound(i);
			long endTick = compoundTag.getLong("end_tick");
			boolean active = compoundTag.getBoolean("active");
			if (currentTick >= endTick) {
				if (!active) {
					cooldownsToRemove.add(i);
					changed = true;
				} else {
					Ability ability = Ability.valueOf(compoundTag.getString("ability").toUpperCase(Locale.ROOT));
					if (itemStack.getItem() instanceof ToggleableTool toggleableTool) {
						AbilityParams abilityParams = toggleableTool.getAbilityParams(ability);
						compoundTag.putLong("end_tick", currentTick + abilityParams.cooldown);
						compoundTag.putInt("duration", abilityParams.cooldown);
						compoundTag.putBoolean("active", false);
						changed = true;
						player.playNotifySound(SoundEvents.CONDUIT_DEACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
					}
				}
			}
			// remaining > 0: nothing to write, NBT unchanged this tick
		}
		for (Integer value : cooldownsToRemove) {
			cooldowns.remove(value.intValue());
			player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F, 1.0F);
		}
		if (cooldowns.size() == 0)
			tag.remove("cooldowns");
		else if (changed)
			tag.put("cooldowns", cooldowns);
	}

	static void addCooldown(ItemStack itemStack, Ability ability, int cooldown, boolean active, long startTick) {
		CompoundTag tag = itemStack.getOrCreateTag();
		ListTag cooldowns;
		if (tag.contains("cooldowns"))
			cooldowns = tag.getList("cooldowns", Tag.TAG_COMPOUND);
		else
			cooldowns = new ListTag();
		CompoundTag newTag = new CompoundTag();
		newTag.putString("ability", ability.getName());
		newTag.putLong("end_tick", startTick + cooldown);
		newTag.putInt("duration", cooldown);
		newTag.putBoolean("active", active);
		cooldowns.add(newTag);
		tag.put("cooldowns", cooldowns);
	}

	static boolean isItemEquipped(ItemStack itemStack, Player player) {
		if (itemStack.getItem() instanceof com.direwolf20.justdirethings.common.items.armors.basearmors.BaseBoots)
			return ItemStack.isSameItemSameTags(itemStack,
					player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET));
		if (itemStack.getItem() instanceof com.direwolf20.justdirethings.common.items.armors.basearmors.BaseLeggings)
			return ItemStack.isSameItemSameTags(itemStack,
					player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.LEGS));
		if (itemStack
				.getItem() instanceof com.direwolf20.justdirethings.common.items.armors.basearmors.BaseChestplate)
			return ItemStack.isSameItemSameTags(itemStack,
					player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST));
		if (itemStack.getItem() instanceof com.direwolf20.justdirethings.common.items.armors.basearmors.BaseHelmet)
			return ItemStack.isSameItemSameTags(itemStack,
					player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD));
		return ItemStack.isSameItemSameTags(itemStack, player.getItemInHand(InteractionHand.MAIN_HAND))
				|| ItemStack.isSameItemSameTags(itemStack, player.getItemInHand(InteractionHand.OFF_HAND));
	}

	default boolean useAbility(Level level, Player player, ItemStack itemStack, int keyCode, boolean isMouse) {
		boolean anyRan = false;
		Set<Ability> customBindAbilities = new HashSet<>();
		if (itemStack.getItem() instanceof LeftClickableTool)
			customBindAbilities
					.addAll(LeftClickableTool.getCustomBindingListFor(itemStack, keyCode, isMouse, player));
		for (Ability ability : getActiveAbilities(itemStack)) {
			if (customBindAbilities.contains(ability)) {
				if (ability.action != null) {
					ability.action.execute(level, player, itemStack);
				}
			}
		}
		if (!level.isClientSide) {
			for (Ability ability : getAllPassiveAbilities()) {
				if (customBindAbilities.contains(ability)) {
					if (ability.settingType == Ability.SettingType.CYCLE)
						ToggleableTool.cycleSetting(itemStack, ability.getName());
					else
						ToggleableTool.toggleSetting(itemStack, ability.getName());
					player.displayClientMessage(Component.translatable("justdirethings.ability",
							Component.translatable(ability.getLocalization()),
							ToggleableTool.getSetting(itemStack, ability.getName())
									? Component.translatable("justdirethings.enabled")
									: Component.translatable("justdirethings.disabled")),
							true);
				}
			}
		}
		return anyRan;
	}

	default boolean useAbility(Level level, Player player, InteractionHand hand, boolean rightClick) {
		if (player.isShiftKeyDown())
			return false;
		ItemStack itemStack = player.getItemInHand(hand);
		boolean anyRan = false;
		Set<Ability> leftClickAbilities = new HashSet<>();
		if (itemStack.getItem() instanceof LeftClickableTool)
			leftClickAbilities.addAll(LeftClickableTool.getLeftClickList(itemStack));
		for (Ability ability : getActiveAbilities(itemStack)) {
			if ((rightClick && !leftClickAbilities.contains(ability))
					|| (!rightClick && leftClickAbilities.contains(ability))) {
				if (ability.action != null) {
					ability.action.execute(level, player, itemStack);
				}
			}
		}
		return anyRan;
	}

	default boolean armorTick(Level level, Player player, ItemStack itemStack) {
		boolean anyRan = false;
		for (Ability ability : getAbilities()) {
			if ((ability.useType == Ability.UseType.PASSIVE_TICK
					|| ability.useType == Ability.UseType.PASSIVE_TICK_COOLDOWN) && ability.action != null
					&& canUseAbility(itemStack, ability)) {
				if (ability.action.execute(level, player, itemStack))
					anyRan = true;
			}
		}
		tickCooldowns(itemStack, player);
		return anyRan;
	}

	default boolean useOnAbility(UseOnContext pContext, ItemStack itemStack, int keyCode, boolean isMouse) {
		if (pContext.getPlayer().isShiftKeyDown())
			return false;
		boolean anyRan = false;
		Set<Ability> customBindAbilities = new HashSet<>();
		if (itemStack.getItem() instanceof LeftClickableTool)
			customBindAbilities.addAll(LeftClickableTool.getCustomBindingListFor(itemStack, keyCode, isMouse,
					pContext.getPlayer()));
		for (Ability ability : getUseOnAbilities(itemStack)) {
			if (customBindAbilities.contains(ability)) {
				if (ability.useOnAction != null) {
					if (ability.useOnAction.execute(pContext))
						anyRan = true;
				}
			}
		}
		return anyRan;
	}

	default boolean useOnAbility(UseOnContext pContext, boolean rightClick) {
		if (pContext.getPlayer().isShiftKeyDown())
			return false;
		ItemStack itemStack = pContext.getItemInHand();
		boolean anyRan = false;
		Set<Ability> leftClickAbilities = new HashSet<>();
		if (itemStack.getItem() instanceof LeftClickableTool)
			leftClickAbilities.addAll(LeftClickableTool.getLeftClickList(itemStack));
		for (Ability ability : getUseOnAbilities(itemStack)) {
			if ((rightClick && !leftClickAbilities.contains(ability))
					|| (!rightClick && leftClickAbilities.contains(ability))) {
				if (ability.useOnAction != null) {
					if (ability.useOnAction.execute(pContext))
						anyRan = true;
				}
			}
		}
		return anyRan;
	}

	default boolean useAbility(Level level, Player player, InteractionHand hand) {
		return useAbility(level, player, hand, true);
	}

	default boolean useOnAbility(UseOnContext pContext) {
		return useOnAbility(pContext, true);
	}

	default boolean bindDrops(UseOnContext pContext) {
		Player player = pContext.getPlayer();
		if (player == null)
			return false;
		if (!player.isShiftKeyDown())
			return false;
		ItemStack pStack = pContext.getItemInHand();
		if (!(pStack.getItem() instanceof ToggleableTool toggleableTool))
			return false;
		if (!toggleableTool.hasAbility(Ability.DROPTELEPORT))
			return false;
		Level pLevel = pContext.getLevel();
		BlockPos pPos = pContext.getClickedPos();
		BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
		if (blockEntity == null)
			return false;
		IItemHandler handler = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, pContext.getClickedFace())
				.orElse(null);
		if (handler == null)
			return false;
		setBoundInventory(pStack,
				new NBTHelpers.BoundInventory(GlobalPos.of(pLevel.dimension(), pPos), pContext.getClickedFace()));
		pContext.getPlayer().displayClientMessage(Component.translatable("justdirethings.boundto",
				Component.translatable(pLevel.dimension().location().getPath()), "[" + pPos.toShortString() + "]"),
				true);
		player.playNotifySound(SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
		return true;
	}

	default boolean bindSoil(UseOnContext pContext) {
		boolean bindingSuccess = false;
		if (!pContext.getPlayer().level().isClientSide) {
			Level pLevel = pContext.getLevel();
			Player player = pContext.getPlayer();
			ItemStack heldItem = pContext.getItemInHand();
			BlockPos clickedPos = pContext.getClickedPos();
			BlockState blockState = pLevel.getBlockState(clickedPos);
			if (blockState.getBlock() instanceof GooSoilBase) {
				if (heldItem.getItem() instanceof ToggleableTool toggleableTool) {
					if (toggleableTool.canUseAbility(heldItem, Ability.DROPTELEPORT)) {
						if (Helpers.testUseTool(heldItem, Ability.DROPTELEPORT, 10) > 0) {
							NBTHelpers.BoundInventory boundInventory = ToggleableTool.getBoundInventory(heldItem);
							if (boundInventory != null) {
								BlockEntity blockEntity = pLevel.getBlockEntity(clickedPos);
								if (blockEntity instanceof GooSoilBE gooSoilBE) {
									gooSoilBE.bindInventory(boundInventory);
									pContext.getPlayer().displayClientMessage(
											Component.translatable("justdirethings.boundto",
													Component.translatable(boundInventory.globalPos().dimension()
															.location().getPath()),
													"[" + boundInventory.globalPos().pos().toShortString() + "]"),
											true);
									player.playNotifySound(SoundEvents.ENDER_EYE_DEATH, SoundSource.PLAYERS, 1.0F,
											1.0F);
									Helpers.damageTool(heldItem, player, Ability.DROPTELEPORT, 10);
									bindingSuccess = true;
								}
							}
						}
						if (!bindingSuccess) {
							pContext.getPlayer().displayClientMessage(
									Component.translatable("justdirethings.bindfailed").withStyle(ChatFormatting.RED),
									true);
							player.playNotifySound(SoundEvents.ANVIL_BREAK, SoundSource.PLAYERS, 1.0F, 1.0F);
						}
					}
				}
			}
		}
		return bindingSuccess;
	}

	// Thanks Soaryn!
	@NotNull
	static Direction getTargetLookDirection(LivingEntity livingEntity) {
		var playerLook = new Vec3(livingEntity.getX(), livingEntity.getY() + livingEntity.getEyeHeight(),
				livingEntity.getZ());
		var lookVec = livingEntity.getViewVector(1.0F);
		var reach = livingEntity instanceof Player player ? player.getBlockReach() : 1; // Todo check if this is good
		var endLook = playerLook.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
		var hitResult = livingEntity.level().clip(
				new ClipContext(playerLook, endLook, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, livingEntity));
		return hitResult.getDirection().getOpposite();
	}

	static ItemStack getToggleableTool(Player player) {
		ItemStack mainHand = player.getMainHandItem();
		if (mainHand.getItem() instanceof ToggleableTool)
			return mainHand;
		ItemStack offHand = player.getOffhandItem();
		if (offHand.getItem() instanceof ToggleableTool)
			return offHand;
		return ItemStack.EMPTY;
	}

	static boolean toggleSetting(ItemStack stack, String setting) {
		CompoundTag tagCompound = stack.getOrCreateTag();
		tagCompound.putBoolean(setting, !getSetting(stack, setting));
		return tagCompound.getBoolean(setting);
	}

	/**
	 * Cycle will move through each possible value of a tool ability, incrementing
	 * by the increment value. When it reaches the end, it'll next disable the
	 * ability entirely. Next cycle it will re-enable it at the min value
	 */
	static boolean cycleSetting(ItemStack stack, String setting) {
		Ability toolAbility = Ability.valueOf(setting.toUpperCase(Locale.ROOT));
		AbilityParams abilityParams = ((ToggleableTool) stack.getItem()).getAbilityParams(toolAbility);
		CompoundTag tagCompound = stack.getOrCreateTag();
		int currentValue = getToolValue(stack, setting);
		int nextValue = Math.min(abilityParams.maxSlider, currentValue + abilityParams.increment);
		if (nextValue == currentValue && getSetting(stack, setting)) { // If the next value is equal to the current one,
																		// its because we max'd out, so toggle it off
			setSetting(stack, setting, false);
			nextValue = abilityParams.minSlider;
		} else if (currentValue == abilityParams.minSlider && !getSetting(stack, setting)) {
			nextValue = abilityParams.minSlider;
			setSetting(stack, setting, true);
		}
		setToolValue(stack, setting, nextValue);
		return tagCompound.getBoolean(setting);
	}

	static void setBoundInventory(ItemStack stack, NBTHelpers.BoundInventory boundInventory) {
		CompoundTag tagCompound = stack.getOrCreateTag();
		tagCompound.put("boundinventory", NBTHelpers.BoundInventory.toNBT(boundInventory));
	}

	static NBTHelpers.BoundInventory getBoundInventory(ItemStack stack) {
		CompoundTag tagCompound = stack.getOrCreateTag();
		if (tagCompound.contains("boundinventory"))
			return NBTHelpers.BoundInventory.fromNBT(tagCompound.getCompound("boundinventory"));
		return null;
	}

	static IItemHandler getBoundHandler(ServerLevel serverLevel, ItemStack stack) {
		NBTHelpers.BoundInventory boundInventory = getBoundInventory(stack);
		if (boundInventory != null)
			return MiscHelpers.getAttachedInventory(
					serverLevel.getServer().getLevel(boundInventory.globalPos().dimension()),
					boundInventory.globalPos().pos(), boundInventory.direction());
		return null;
	}

	static boolean setSetting(ItemStack stack, String setting, boolean value) {
		CompoundTag tagCompound = stack.getOrCreateTag();
		tagCompound.putBoolean(setting, value);
		return tagCompound.getBoolean(setting);
	}

	static boolean getSetting(ItemStack stack, String setting) {
		CompoundTag tagCompound = stack.getTag();
		return tagCompound == null || !tagCompound.contains(setting) || tagCompound.getBoolean(setting);
	}

	@Override
	default boolean getEnabled(ItemStack stack) {
		return getSetting(stack, "enabled");
	}

	static void setToolValue(ItemStack stack, String valueName, int value) {
		Ability toolAbility = Ability.valueOf(valueName.toUpperCase(Locale.ROOT));
		AbilityParams abilityParams = ((ToggleableTool) stack.getItem()).getAbilityParams(toolAbility);
		int min = abilityParams.minSlider;
		int max = abilityParams.maxSlider;
		int setValue = Math.max(min, Math.min(max, value));
		stack.getOrCreateTag().putInt(valueName + "_value", setValue);
	}

	static int getToolValue(ItemStack stack, String valueName) {
		Ability toolAbility = Ability.valueOf(valueName.toUpperCase(Locale.ROOT));
		AbilityParams abilityParams = ((ToggleableTool) stack.getItem()).getAbilityParams(toolAbility);
		int min = abilityParams.minSlider;
		int max = abilityParams.maxSlider;
		if (stack.getOrCreateTag().contains(valueName + "_value"))
			return Math.max(min, Math.min(max, stack.getOrCreateTag().getInt(valueName + "_value")));
		return abilityParams.defaultValue;
	}

	static int getCustomSetting(ItemStack stack, String setting) {
		String key = setting + "_custom";
		if (stack.getOrCreateTag().contains(key))
			return stack.getOrCreateTag().getInt(key);
		return 0;
	}

	static void setCustomSetting(ItemStack stack, String setting, int value) {
		stack.getOrCreateTag().putInt(setting + "_custom", value);
	}
}
