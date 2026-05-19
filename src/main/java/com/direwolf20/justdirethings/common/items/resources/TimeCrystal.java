package com.direwolf20.justdirethings.common.items.resources;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.Helpers;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TimeCrystal extends Item {
	public TimeCrystal() {
		super(new Item.Properties());
	}

	@Override
	public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int itemSlot, boolean isSelected) {
		if (world.isClientSide)
			return;
		if (entity instanceof Player player) {
			ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
			if (chestplate.getItem() instanceof ToggleableTool tt
					&& tt.canUseAbilityAndDurability(chestplate, Ability.TIMEPROTECTION)) {
				Helpers.damageTool(chestplate, player, Ability.TIMEPROTECTION);
				return;
			}
		}
		if (entity instanceof LivingEntity livingEntity) {
			if (world.random.nextFloat() < 0.005f) {
				boolean applySlowness = world.random.nextBoolean();
				if (applySlowness) {
					if (!livingEntity.hasEffect(MobEffects.MOVEMENT_SPEED)) {
						livingEntity
								.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 5, false, false));
					}
				} else {
					if (!livingEntity.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
						livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 5, false, false));
					}
				}
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null)
			return;
		long currentTime = System.currentTimeMillis();
		boolean showFirstTooltip = (currentTime / 10000) % 2 == 0;
		if (showFirstTooltip) {
			tooltip.add(
					Component.translatable("justdirethings.timecrystaltooltip").withStyle(ChatFormatting.DARK_AQUA));
		} else {
			tooltip.add(
					Component.translatable("justdirethings.timecrystaltooltiptwo").withStyle(ChatFormatting.DARK_AQUA));
		}
	}
}
