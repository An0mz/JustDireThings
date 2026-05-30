package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.items.interfaces.*;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.direwolf20.justdirethings.util.MiscTools;
import com.direwolf20.justdirethings.util.PolymorphicEntitySanitizer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.List;

public class PolymorphicWandV2 extends BaseToggleableTool
		implements
			LeftClickableTool,
			FluidContainingItem,
			PoweredItem {

	public PolymorphicWandV2() {
		super(new Properties().fireResistant().stacksTo(1));
		registerAbility(Ability.POLYMORPH_RANDOM);
		registerAbility(Ability.POLYMORPH_TARGET);
	}

	@Override
	public int getMaxMB() {
		return Config.POLYMORPHIC_WAND_V2_MAX_FLUID.get();
	}

	@Override
	public int getMaxEnergy() {
		return Config.POLYMORPHIC_WAND_V2_MAX_FE.get();
	}

	@Override
	public InteractionResult useOn(UseOnContext pContext) {
		ItemStack itemStack = pContext.getItemInHand();
		Player player = pContext.getPlayer();
		if (player == null || itemStack.isEmpty())
			return InteractionResult.FAIL;
		BlockHitResult blockhitresult = getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.SOURCE_ONLY);
		if (blockhitresult.getType() == HitResult.Type.BLOCK) {
			if (FluidContainingItem.pickupFluid(player.level(), player, itemStack, blockhitresult))
				return InteractionResult.SUCCESS;
		}
		return super.useOn(pContext);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		Level level = player.level();
		if (level.isClientSide)
			return true;
		if (entity instanceof Mob mob) {
			if (AbilityMethods.polymorphTarget(level, player, stack, mob))
				return true;
			if (AbilityMethods.polymorphRandom(level, player, stack, mob))
				return true;
		}
		return true;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target,
			InteractionHand hand) {
		if (hand != InteractionHand.MAIN_HAND || target.level().isClientSide())
			return InteractionResult.PASS;
		if (!(target instanceof Mob mob))
			return InteractionResult.PASS;
		if (player.isShiftKeyDown()) {
			savePolymorphTarget(stack, player, target);
			return InteractionResult.CONSUME;
		}
		if (AbilityMethods.polymorphTarget(target.level(), player, stack, mob))
			return InteractionResult.CONSUME;
		if (AbilityMethods.polymorphRandom(target.level(), player, stack, mob))
			return InteractionResult.CONSUME;
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (blockhitresult.getType() == HitResult.Type.BLOCK) {
			if (FluidContainingItem.pickupFluid(level, player, itemStack, blockhitresult))
				return InteractionResultHolder.fail(itemStack);
		}
		// Fallback for when the vanilla entity pick missed the mob's hitbox
		// (e.g. eye inside a large mob's AABB at close range). interactLivingEntity
		// is never called in that case, so we detect the target here instead.
		if (!level.isClientSide) {
			Entity entity = MiscTools.getEntityLookedAt(player, 5);
			if (player.isShiftKeyDown() && entity instanceof LivingEntity livingEntity) {
				savePolymorphTarget(itemStack, player, livingEntity);
				return InteractionResultHolder.success(itemStack);
			}
			if (!player.isShiftKeyDown() && entity instanceof Mob mob) {
				if (AbilityMethods.polymorphTarget(level, player, itemStack, mob))
					return InteractionResultHolder.success(itemStack);
				if (AbilityMethods.polymorphRandom(level, player, itemStack, mob))
					return InteractionResultHolder.success(itemStack);
			}
		}
		return super.use(level, player, hand);
	}

	public static void savePolymorphTarget(ItemStack stack, Player player, LivingEntity target) {
		if (target instanceof Mob mob) {
			CompoundTag tag = stack.getOrCreateTag();
			tag.putString("polymorphTargetType", EntityType.getKey(mob.getType()).toString());

			CompoundTag fullNbt = new CompoundTag();
			mob.save(fullNbt);
			tag.put("polymorphCosmeticData", PolymorphicEntitySanitizer.cosmeticOnly(fullNbt));

			player.displayClientMessage(
					Component.translatable("justdirethings.polymorphset", mob.getType().getDescription()), true);
		} else {
			player.displayClientMessage(Component.translatable("justdirethings.invalidpolymorphentity"), true);
		}
	}

	@Override
	public boolean showBarWhenFull() {
		return true;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return isPowerBarVisible(stack);
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		return getPowerBarWidth(stack);
	}

	@Override
	public int getBarColor(ItemStack stack) {
		int color = getPowerBarColor(stack);
		return color == -1 ? super.getBarColor(stack) : color;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null)
			return;
		IFluidHandlerItem fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
		if (fluidHandler == null)
			return;
		tooltip.add(
				Component
						.translatable("justdirethings.polymorphicfluidamt",
								MagicHelpers.formatted(fluidHandler.getFluidInTank(0).getAmount()),
								MagicHelpers.formatted(fluidHandler.getTankCapacity(0)))
						.withStyle(ChatFormatting.GREEN));

		CompoundTag tag = stack.getTag();
		if (tag != null && tag.contains("polymorphTargetType")) {
			EntityType<?> savedType = EntityType.byString(tag.getString("polymorphTargetType")).orElse(null);
			if (savedType != null) {
				tooltip.add(Component.translatable("justdirethings.polymorphset", savedType.getDescription())
						.withStyle(ChatFormatting.AQUA));
			}
		}
	}
}
