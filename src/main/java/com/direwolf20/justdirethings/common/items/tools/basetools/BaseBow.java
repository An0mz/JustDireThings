package com.direwolf20.justdirethings.common.items.tools.basetools;

import com.direwolf20.justdirethings.common.entities.JustDireArrow;
import com.direwolf20.justdirethings.common.items.interfaces.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.direwolf20.justdirethings.util.TooltipHelpers.*;

public class BaseBow extends BowItem implements ToggleableTool, LeftClickableTool {
	protected final EnumSet<Ability> abilities = EnumSet.noneOf(Ability.class);
	protected final Map<Ability, AbilityParams> abilityParams = new EnumMap<>(Ability.class);

	public BaseBow(Properties properties) {
		super(properties);
	}

	public float getMaxDraw() {
		return 20;
	}

	@Override
	public EnumSet<Ability> getAbilities() {
		return abilities;
	}

	@Override
	public Map<Ability, AbilityParams> getAbilityParamsMap() {
		return abilityParams;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!level.isClientSide && player.isShiftKeyDown()) {
			openSettings(player);
			return InteractionResultHolder.success(stack);
		}
		if (stack.getItem() instanceof PoweredTool poweredTool
				&& poweredTool.getAvailableEnergy(stack) < poweredTool.getBlockBreakFECost()) {
			return InteractionResultHolder.fail(stack);
		}
		return super.use(level, player, hand);
	}

	@Override
	public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving, int pTimeCharged) {
		if (!(pEntityLiving instanceof Player player))
			return;

		boolean creative = player.getAbilities().instabuild
				|| EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, pStack) > 0;
		ItemStack arrowStack = player.getProjectile(pStack);

		if (arrowStack.isEmpty() && !creative)
			return;
		if (arrowStack.isEmpty())
			arrowStack = new ItemStack(Items.ARROW);

		float power = getPowerForTime(this.getUseDuration(pStack) - pTimeCharged);
		if ((double) power < 0.1D)
			return;

		boolean isCrit = power == 1.0F;

		if (!pLevel.isClientSide) {
			JustDireArrow arrow = new JustDireArrow(pLevel, player);
			arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
			if (isCrit)
				arrow.setCritArrow(true);

			int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, pStack);
			if (powerLevel > 0)
				arrow.setBaseDamage(arrow.getBaseDamage() + powerLevel * 0.5 + 0.5);
			int punch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, pStack);
			if (punch > 0)
				arrow.setKnockback(punch);
			if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, pStack) > 0)
				arrow.setSecondsOnFire(100);

			pStack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));

			// Set ability properties
			setupArrowAbilities(pStack, arrow, player);

			if (!creative) {
				arrow.pickup = AbstractArrow.Pickup.ALLOWED;
			}
			pLevel.addFreshEntity(arrow);
		}

		pLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
				net.minecraft.sounds.SoundEvents.ARROW_SHOOT, net.minecraft.sounds.SoundSource.PLAYERS, 1.0F,
				1.0F / (pLevel.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);

		if (!creative && !player.getAbilities().instabuild) {
			arrowStack.shrink(1);
			if (arrowStack.isEmpty())
				player.getInventory().removeItem(arrowStack);
		}
		player.awardStat(Stats.ITEM_USED.get(this));
	}

	protected void setupArrowAbilities(ItemStack bowStack, JustDireArrow arrow, Player player) {
		if (!getEnabled(bowStack))
			return;

		if (canUseAbilityAndDurability(bowStack, Ability.PHASE)) {
			arrow.setPhase(true);
			Helpers.damageTool(bowStack, player, Ability.PHASE);
		}

		if (canUseAbilityAndDurability(bowStack, Ability.HOMING)) {
			arrow.setHoming(true);
			Helpers.damageTool(bowStack, player, Ability.HOMING);
			boolean hostileOnly = ToggleableTool.getToolValue(bowStack, Ability.HOMING.getName()) == 0;
			LivingEntity target = findAimedAtEntity(player, hostileOnly, arrow);
			if (target != null)
				arrow.setTargetEntity(target);
			arrow.setHostileOnly(hostileOnly);
		}
	}

	@Nullable
	public LivingEntity findAimedAtEntity(LivingEntity shooter, boolean onlyHostile, JustDireArrow arrow) {
		double range = 50;
		Vec3 startVec = shooter.getEyePosition(1.0F);
		Vec3 lookVec = shooter.getViewVector(1.0F);
		Vec3 endVec = startVec.add(lookVec.scale(range));

		HitResult hitResult = shooter.level()
				.clip(new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter));
		if (hitResult.getType() != HitResult.Type.MISS) {
			endVec = hitResult.getLocation();
		}

		AABB boundingBox = new AABB(startVec, endVec).inflate(1.0D);
		List<Entity> entities = shooter.level().getEntities(shooter, boundingBox,
				EntitySelector.LIVING_ENTITY_STILL_ALIVE);

		LivingEntity closestEntity = null;
		double closestDistance = range * range;

		for (Entity entity : entities) {
			if (entity instanceof LivingEntity living) {
				if (onlyHostile && !arrow.isHostileEntity(living))
					continue;
				AABB entityBox = entity.getBoundingBox().inflate(entity.getPickRadius());
				Vec3 hitVec = entityBox.clip(startVec, endVec).orElse(null);
				if (entityBox.contains(startVec)) {
					if (closestDistance >= 0.0D) {
						closestEntity = living;
						closestDistance = 0.0D;
					}
				} else if (hitVec != null) {
					double dist = startVec.distanceToSqr(hitVec);
					if (dist < closestDistance) {
						closestEntity = living;
						closestDistance = dist;
					}
				}
			}
		}
		return closestEntity;
	}

	@Override
	public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
		if ((!getPassiveTickAbilities(pStack).isEmpty() || !getCooldownAbilities().isEmpty())
				&& pEntity instanceof Player player) {
			if (player.isUsingItem() && player.getUseItem() == pStack)
				return;
			ToggleableTool.tickCooldowns(pStack, player);
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		Minecraft mc = Minecraft.getInstance();
		if (level == null || mc.player == null)
			return;
		boolean sneakPressed = Screen.hasShiftDown();
		appendFEText(stack, tooltip);
		if (sneakPressed) {
			appendToolEnabled(stack, tooltip);
			appendAbilityList(stack, tooltip);
		} else {
			appendToolEnabled(stack, tooltip);
			appendShiftForInfo(stack, tooltip);
		}
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		if (stack.getItem() instanceof PoweredTool) {
			IEnergyStorage energyStorage = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
			if (energyStorage == null)
				return amount;
			int unbreakingLevel = stack.getEnchantmentLevel(Enchantments.UNBREAKING);
			double reductionFactor = Math.min(1.0, unbreakingLevel * 0.1);
			int finalEnergyCost = (int) Math.max(0, amount - (amount * reductionFactor));
			energyStorage.extractEnergy(finalEnergyCost, false);
			return 0;
		}
		return amount;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		if (stack.getItem() instanceof PoweredTool)
			return super.isBookEnchantable(stack, book) && canAcceptEnchantments(book);
		return super.isBookEnchantable(stack, book);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack,
			net.minecraft.world.item.enchantment.Enchantment enchantment) {
		if (stack.getItem() instanceof PoweredTool)
			return super.canApplyAtEnchantingTable(stack, enchantment) && canAcceptEnchantments(enchantment);
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	private boolean canAcceptEnchantments(ItemStack book) {
		return !EnchantmentHelper.getEnchantments(book).containsKey(Enchantments.MENDING);
	}

	private boolean canAcceptEnchantments(net.minecraft.world.item.enchantment.Enchantment enchantment) {
		return enchantment != Enchantments.MENDING;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}
}
