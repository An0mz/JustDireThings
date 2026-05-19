package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.common.items.PolymorphicWand;
import com.direwolf20.justdirethings.common.items.PolymorphicWandV2;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.AbilityMethods;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredTool;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool.getInstantRFCost;
import static com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool.getToolValue;

public class PlayerEvents {

	// Tracks which players have flight granted specifically by JDT, so we don't
	// revoke mayfly granted by other mods (e.g. AttributeLib/Apotheosis).
	private static final Set<UUID> jdtFlightGranted = new HashSet<>();

	@SubscribeEvent
	public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		jdtFlightGranted.remove(event.getEntity().getUUID());
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide())
			return;
		Player player = event.player;
		ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
		boolean hasFlight = chestplate.getItem() instanceof ToggleableTool toggleableTool
				&& toggleableTool.canUseAbilityAndDurability(chestplate, Ability.FLIGHT);
		if (hasFlight) {
			if (!player.getAbilities().mayfly) {
				player.getAbilities().mayfly = true;
				player.onUpdateAbilities();
			}
			jdtFlightGranted.add(player.getUUID());
		} else if (!player.isCreative() && !player.isSpectator()) {
			// Only revoke mayfly if JDT was the one that granted it; leave other mods'
			// flight alone
			if (jdtFlightGranted.remove(player.getUUID())) {
				player.getAbilities().mayfly = false;
				player.getAbilities().flying = false;
				player.onUpdateAbilities();
			}
		}
		if (chestplate.getItem() instanceof ToggleableTool lavaToggleable
				&& lavaToggleable.canUseAbilityAndDurability(chestplate, Ability.LAVAIMMUNITY) && player.isOnFire()) {
			player.clearFire();
		}
	}

	@SubscribeEvent
	public static void BreakSpeed(PlayerEvent.BreakSpeed event) {
		Player player = event.getEntity();
		ItemStack stack = player.getMainHandItem(); // Assuming the tool is in the main hand
		int rfCost = 0;
		if (stack.getItem() instanceof ToggleableTool toggleableTool && stack.isCorrectToolForDrops(event.getState())) {
			if (stack.getItem() instanceof PoweredTool poweredTool) {
				if (poweredTool.getAvailableEnergy(stack) < poweredTool.getBlockBreakFECost()) {
					event.setNewSpeed(0.1f);
					return;
				}
			}
			Level level = player.level();
			BlockPos originalPos = event.getPosition().get();
			BlockState originalState = level.getBlockState(event.getPosition().get());
			float originalDestroySpeed = originalState.getDestroySpeed(level, originalPos);
			float targetSpeed = event.getOriginalSpeed();
			float cumulativeDestroy = 0;
			if (originalDestroySpeed <= 0)
				return;
			Set<BlockPos> breakBlockPositions = toggleableTool.getBreakBlockPositions(stack, level, originalPos, player,
					originalState);
			if (!breakBlockPositions.isEmpty()) { // Avoid potential divide by zero
				int radius = toggleableTool.canUseAbility(stack, Ability.HAMMER)
						? getToolValue(stack, Ability.HAMMER.getName())
						: 1;
				for (BlockPos pos : breakBlockPositions) {
					BlockState blockState = level.getBlockState(pos);
					float destroySpeedTarget = blockState.getDestroySpeed(level, pos);
					cumulativeDestroy = cumulativeDestroy + destroySpeedTarget;
				}
				rfCost = getInstantRFCost(cumulativeDestroy);
				float modifier = ((float) breakBlockPositions.size() / radius) < 1
						? 1
						: ((float) breakBlockPositions.size() / radius);
				cumulativeDestroy = (cumulativeDestroy / breakBlockPositions.size()) * modifier; // Up to 3 times slower
				float relative = originalDestroySpeed / cumulativeDestroy;
				targetSpeed = event.getOriginalSpeed() * relative;
			}
			if (toggleableTool.canUseAbility(stack, Ability.INSTABREAK)
					&& stack.getItem() instanceof PoweredTool poweredTool
					&& poweredTool.getAvailableEnergy(stack) >= rfCost) {
				targetSpeed = 10000f;
			}
			if (targetSpeed != event.getOriginalSpeed())
				event.setNewSpeed(targetSpeed);
		}
	}

	@SubscribeEvent
	public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		ItemStack stack = event.getItemStack();
		if (!(event.getTarget() instanceof Mob mob))
			return;
		Player player = event.getEntity();

		if (stack.getItem() instanceof PolymorphicWandV2) {
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
			if (player.isShiftKeyDown()) {
				if (!player.level().isClientSide())
					PolymorphicWandV2.savePolymorphTarget(stack, player, mob);
			} else if (!player.level().isClientSide()) {
				CompoundTag tag = stack.getOrCreateTag();
				if (tag.contains("polymorphTargetType"))
					AbilityMethods.polymorphTarget(player.level(), player, stack, mob);
				else
					AbilityMethods.polymorphRandom(player.level(), player, stack, mob);
			}
		} else if (stack.getItem() instanceof PolymorphicWand) {
			event.setCanceled(true);
			event.setCancellationResult(InteractionResult.SUCCESS);
			if (!player.level().isClientSide())
				AbilityMethods.polymorphRandom(player.level(), player, stack, mob);
		}
	}
}
