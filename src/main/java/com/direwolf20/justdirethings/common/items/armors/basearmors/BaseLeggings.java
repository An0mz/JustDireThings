package com.direwolf20.justdirethings.common.items.armors.basearmors;

import com.direwolf20.justdirethings.common.items.interfaces.*;
import com.direwolf20.justdirethings.setup.Registration;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.direwolf20.justdirethings.util.TooltipHelpers.*;

public class BaseLeggings extends ArmorItem implements ToggleableTool, LeftClickableTool {
	protected final EnumSet<Ability> abilities = EnumSet.noneOf(Ability.class);
	protected final Map<Ability, AbilityParams> abilityParams = new EnumMap<>(Ability.class);

	public BaseLeggings(ArmorMaterial pMaterial, Properties pProperties) {
		super(pMaterial, Type.LEGGINGS, pProperties);
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
	public void appendHoverText(ItemStack stack, @javax.annotation.Nullable Level level, List<Component> tooltip,
			TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		Minecraft mc = Minecraft.getInstance();
		if (level == null || mc.player == null) {
			return;
		}

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
	public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level level, @NotNull Entity entity, int itemSlot,
			boolean isSelected) {
		if (itemSlot == EquipmentSlot.LEGS.getIndex() && entity instanceof Player player) {
			armorTick(level, player, itemStack);
		}
	}

	private static final UUID PHASE_UUID = UUID.fromString("3b4b2d28-8f5c-4c2e-9d3a-1a2b3c4d5e6f");

	@Override
	public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
		if (!(stack.getItem() instanceof PoweredTool poweredTool))
			return modifiers;

		if (slot == EquipmentSlot.LEGS) {
			Multimap<Attribute, AttributeModifier> result = HashMultimap.create(modifiers);
			if (canUseAbilityAndDurability(stack, Ability.PHASE)) {
				result.put(Registration.PHASE.get(),
						new AttributeModifier(PHASE_UUID, "Phase modifier", 1.0, AttributeModifier.Operation.ADDITION));
			}
			return result;
		}

		return poweredTool.getPoweredAttributeModifiers(slot, stack, modifiers);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		if (stack.getItem() instanceof PoweredTool poweredTool) {
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
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		if (stack.getItem() instanceof PoweredTool)
			return super.canApplyAtEnchantingTable(stack, enchantment) && canAcceptEnchantments(enchantment);
		return super.canApplyAtEnchantingTable(stack, enchantment);
	}

	private boolean canAcceptEnchantments(ItemStack book) {
		return !EnchantmentHelper.getEnchantments(book).containsKey(Enchantments.MENDING);
	}

	private boolean canAcceptEnchantments(Enchantment enchantment) {
		return enchantment != Enchantments.MENDING;
	}

	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}
}
