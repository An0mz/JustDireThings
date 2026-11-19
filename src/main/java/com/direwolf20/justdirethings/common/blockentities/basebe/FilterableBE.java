package com.direwolf20.justdirethings.common.blockentities.basebe;

import com.direwolf20.justdirethings.common.containers.handlers.FilterBasicHandler;
import com.direwolf20.justdirethings.common.entities.CreatureCatcherEntity;
import com.direwolf20.justdirethings.common.items.CreatureCatcher;
import com.direwolf20.justdirethings.util.ItemStackKey;
import com.direwolf20.justdirethings.util.interfacehelpers.FilterData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Set;

public interface FilterableBE {
	Set<String> IGNORED_ENTITY_TAGS = Set.of("AbsorptionAmount", "Age", "Air", "ArmorDropChances", "ArmorItems", "Attributes", "Brain", "CanPickUpLoot", "CustomName", "DeathTime", "Dimension", "FallDistance", "FallFlying", "Fire", "FromBucket", "Glowing", "HandDropChances", "HandItems", "HasVisualFire", "HurtByTimestamp", "HurtTime", "InLove", "Invulnerable", "LeftHanded", "Leash", "Leashed", "Motion", "NoAI", "OnGround", "Passengers", "PersistenceRequired", "PortalCooldown", "Pos", "Rotation", "Silent", "Tags", "UUID", "id");

	FilterBasicHandler getFilterHandler();

	FilterData getFilterData();

	BlockEntity getBlockEntity();

	default void saveFilterSettings(CompoundTag tag) {
		tag.putBoolean("allowlist", getFilterData().allowlist);
		tag.putBoolean("compareNBT", getFilterData().compareNBT);
		tag.putInt("blockitemfilter", getFilterData().blockItemFilter);
	}

	default void loadFilterSettings(CompoundTag tag) {
		getFilterData().allowlist = tag.getBoolean("allowlist");
		getFilterData().compareNBT = tag.getBoolean("compareNBT");
		int blockItemFilter = tag.getInt("blockitemfilter");
		if (blockItemFilter != -1 && getFilterData().blockItemFilter != -1)
			getFilterData().blockItemFilter = blockItemFilter;
		getFilterData().filterCache.clear();
		getFilterData().entityCache.clear();
	}

	default void setFilterSettings(FilterData filterData) {
		getFilterData().allowlist = filterData.allowlist;
		getFilterData().compareNBT = filterData.compareNBT;
		getFilterData().blockItemFilter = filterData.blockItemFilter;
		getFilterData().filterCache.clear();
		getFilterData().entityCache.clear();
		if (getBlockEntity() instanceof BaseMachineBE baseMachineBE)
			baseMachineBE.markDirtyClient();
	}

	default boolean isStackValidFilter(ItemStack testStack) {
		ItemStackKey key = new ItemStackKey(testStack, getFilterData().compareNBT);
		if (getFilterData().filterCache.containsKey(key))
			return getFilterData().filterCache.get(key);

		FilterBasicHandler filteredItems = getFilterHandler();
		for (int i = 0; i < filteredItems.getSlots(); i++) {
			ItemStack stack = filteredItems.getStackInSlot(i);
			if (stack.isEmpty())
				continue;
			if (key.equals(new ItemStackKey(stack, getFilterData().compareNBT))) {
				getFilterData().filterCache.put(key, getFilterData().allowlist);
				return getFilterData().allowlist;
			}
		}
		getFilterData().filterCache.put(key, !getFilterData().allowlist);
		return !getFilterData().allowlist;
	}

	default boolean isEntityValidFilter(Entity entity, Level level) {
		Boolean cached = getFilterData().entityCache.get(entity);
		if (cached != null)
			return cached;
		boolean allowlist = getFilterData().allowlist;
		FilterBasicHandler filteredItems = getFilterHandler();
		for (int i = 0; i < filteredItems.getSlots(); i++) {
			ItemStack stack = filteredItems.getStackInSlot(i);
			if (stack.isEmpty())
				continue;
			boolean match = false;
			if (stack.getItem() instanceof SpawnEggItem) {
				Item egg = SpawnEggItem.byId(entity.getType());
				match = egg != null && stack.is(egg);
			} else if (stack.getItem() instanceof CreatureCatcher) {
				Mob mob = CreatureCatcherEntity.getEntityFromItemStack(stack, level);
				match = mob != null && entity.getType().equals(mob.getType())
						&& (!getFilterData().compareNBT || getNormalizedTag(mob).equals(getNormalizedTag(entity)));
			}
			if (match) {
				getFilterData().entityCache.put(entity, allowlist);
				return allowlist;
			}
		}
		getFilterData().entityCache.put(entity, !allowlist);
		return !allowlist;
	}

	default CompoundTag getNormalizedTag(Entity entity) {
		CompoundTag tag = new CompoundTag();
		entity.save(tag);
		IGNORED_ENTITY_TAGS.forEach(tag::remove);
		return tag;
	}
}
