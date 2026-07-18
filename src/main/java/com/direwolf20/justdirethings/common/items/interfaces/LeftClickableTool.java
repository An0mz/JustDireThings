package com.direwolf20.justdirethings.common.items.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public interface LeftClickableTool {

	record AbilityBinding(String abilityName, int key, boolean isMouse, boolean requireEquipped) {
		CompoundTag toTag() {
			CompoundTag tag = new CompoundTag();
			tag.putString("abilityName", abilityName);
			tag.putInt("key", key);
			tag.putBoolean("isMouse", isMouse);
			tag.putBoolean("requireEquipped", requireEquipped);
			return tag;
		}

		static AbilityBinding fromTag(CompoundTag tag) {
			return new AbilityBinding(tag.getString("abilityName"), tag.getInt("key"), tag.getBoolean("isMouse"),
					!tag.contains("requireEquipped") || tag.getBoolean("requireEquipped"));
		}
	}

	static void setBindingMode(ItemStack stack, Ability ability, int mode) {
		CompoundTag compoundTag = stack.getOrCreateTag();
		compoundTag.putInt("bindingMode_" + ability.getName(), mode);
	}

	static int getBindingMode(ItemStack stack, Ability ability) {
		CompoundTag compoundTag = stack.getOrCreateTag();
		return compoundTag.getInt("bindingMode_" + ability.getName());
	}

	static void removeFromLeftClickList(ItemStack stack, Ability ability) {
		Set<Ability> abilityList = getLeftClickList(stack);
		abilityList.remove(ability);
		setLeftClickList(stack, abilityList);
	}

	static void addToLeftClickList(ItemStack stack, Ability ability) {
		Set<Ability> abilityList = getLeftClickList(stack);
		abilityList.add(ability);
		setLeftClickList(stack, abilityList);
	}

	static void setLeftClickList(ItemStack stack, Set<Ability> abilityList) {
		CompoundTag compoundTag = stack.getOrCreateTag();
		ListTag abilityListTag = new ListTag();
		for (Ability ability : abilityList) {
			CompoundTag comp = new CompoundTag();
			comp.putString("abilityName", ability.getName());
			abilityListTag.add(comp);
		}
		compoundTag.put("leftClickAbilities", abilityListTag);
	}

	static Set<Ability> getLeftClickList(ItemStack stack) {
		Set<Ability> abilities = new HashSet<>();
		CompoundTag compoundTag = stack.getOrCreateTag();
		if (compoundTag.contains("leftClickAbilities")) {
			ListTag listTag = compoundTag.getList("leftClickAbilities", Tag.TAG_COMPOUND);
			for (int i = 0; i < listTag.size(); i++) {
				abilities
						.add(Ability.valueOf(listTag.getCompound(i).getString("abilityName").toUpperCase(Locale.ROOT)));
			}
		}
		return abilities;
	}

	static LeftClickableTool.AbilityBinding getAbilityBinding(ItemStack stack, Ability ability) {
		return getCustomBindingList(stack).stream()
				.filter(binding -> binding.abilityName().equalsIgnoreCase(ability.getName())).findFirst().orElse(null);
	}

	static void removeFromCustomBindingList(ItemStack stack, Ability ability) {
		List<LeftClickableTool.AbilityBinding> bindings = new ArrayList<>(getCustomBindingList(stack));
		bindings.removeIf(binding -> binding.abilityName().equalsIgnoreCase(ability.getName()));
		setCustomBindingList(stack, bindings);
	}

	static void addToCustomBindingList(ItemStack stack, LeftClickableTool.AbilityBinding binding) {
		removeFromCustomBindingList(stack, Ability.valueOf(binding.abilityName().toUpperCase(Locale.ROOT)));
		List<LeftClickableTool.AbilityBinding> bindings = getCustomBindingList(stack);
		bindings.add(binding);
		setCustomBindingList(stack, bindings);
	}

	static void setCustomBindingList(ItemStack stack, List<LeftClickableTool.AbilityBinding> bindings) {
		CompoundTag compoundTag = stack.getOrCreateTag();
		ListTag listTag = new ListTag();
		for (LeftClickableTool.AbilityBinding binding : bindings) {
			listTag.add(binding.toTag());
		}
		compoundTag.put("customBindingAbilities", listTag);
	}

	static List<LeftClickableTool.AbilityBinding> getCustomBindingList(ItemStack stack) {
		List<LeftClickableTool.AbilityBinding> bindings = new ArrayList<>();
		CompoundTag compoundTag = stack.getOrCreateTag();
		if (compoundTag.contains("customBindingAbilities")) {
			ListTag listTag = compoundTag.getList("customBindingAbilities", Tag.TAG_COMPOUND);
			for (int i = 0; i < listTag.size(); i++) {
				bindings.add(LeftClickableTool.AbilityBinding.fromTag(listTag.getCompound(i)));
			}
		}
		return bindings;
	}

	static List<Ability> getCustomBindingListFor(ItemStack stack, int key, boolean isMouse, Player player) {
		List<Ability> returnList = new ArrayList<>();
		boolean isEquipped = ToggleableTool.isItemEquipped(stack, player);
		for (LeftClickableTool.AbilityBinding binding : getCustomBindingList(stack)) {
			if (binding.isMouse() != isMouse || binding.key() != key)
				continue;
			Ability ability = Ability.valueOf(binding.abilityName().toUpperCase(Locale.ROOT));
			if (getBindingMode(stack, ability) != 2)
				continue;
			if (binding.requireEquipped() && !isEquipped)
				continue;
			returnList.add(ability);
		}
		return returnList;
	}

	static ItemStack getLeftClickableItem(Player player) {
		ItemStack mainHand = player.getMainHandItem();
		if (mainHand.getItem() instanceof LeftClickableTool)
			return mainHand;
		ItemStack offHand = player.getOffhandItem();
		if (offHand.getItem() instanceof LeftClickableTool)
			return offHand;
		return ItemStack.EMPTY;
	}
}
