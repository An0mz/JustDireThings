package com.direwolf20.justdirethings.common.containers;

import com.direwolf20.justdirethings.common.containers.basecontainers.BaseContainer;
import com.direwolf20.justdirethings.common.items.PotionCanister;
import com.direwolf20.justdirethings.common.items.tools.basetools.BaseBow;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("removal")
public class ToolSettingContainer extends BaseContainer {
	public Player playerEntity;
	public final List<Slot> dynamicSlots = new ArrayList<>();
	public ItemStackHandler currentCanisterHandler;

	public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET = new ResourceLocation("item/empty_armor_slot_helmet");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE = new ResourceLocation(
			"item/empty_armor_slot_chestplate");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS = new ResourceLocation(
			"item/empty_armor_slot_leggings");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD = new ResourceLocation("item/empty_armor_slot_shield");
	static final ResourceLocation[] TEXTURE_EMPTY_SLOTS = new ResourceLocation[]{EMPTY_ARMOR_SLOT_BOOTS,
			EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET};
	private static final EquipmentSlot[] SLOT_IDS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST,
			EquipmentSlot.LEGS, EquipmentSlot.FEET};

	public ToolSettingContainer(int windowId, Inventory playerInventory, Player player, FriendlyByteBuf extraData) {
		this(windowId, playerInventory, player);
	}

	public ToolSettingContainer(int windowId, Inventory playerInventory, Player player) {
		super(Registration.Tool_Settings_Container.get(), windowId);
		playerEntity = player;
		for (int k = 0; k < 4; ++k) {
			final EquipmentSlot equipmentslot = SLOT_IDS[k];
			this.addSlot(new Slot(playerInventory, 39 - k, 44 + k * 18, 66) {
				@Override
				public void set(ItemStack newItem) {
					ItemStack oldItem = this.getItem();
					onEquipItem(playerEntity, equipmentslot, newItem, oldItem);
					super.set(newItem);
				}

				@Override
				public int getMaxStackSize() {
					return 1;
				}

				@Override
				public boolean mayPlace(ItemStack p_39746_) {
					return p_39746_.canEquip(equipmentslot, playerEntity);
				}

				@Override
				public boolean mayPickup(Player p_39744_) {
					ItemStack itemstack = this.getItem();
					return !itemstack.isEmpty() && !p_39744_.isCreative()
							&& EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.mayPickup(p_39744_);
				}

				@Override
				public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
					return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[equipmentslot.getIndex()]);
				}
			});
		}

		this.addSlot(new Slot(playerInventory, 40, 44 + 4 * 18, 66) {
			@Override
			public void set(ItemStack newItem) {
				ItemStack oldItem = this.getItem();
				onEquipItem(playerEntity, EquipmentSlot.OFFHAND, newItem, oldItem);
				super.set(newItem);
			}

			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_ARMOR_SLOT_SHIELD);
			}
		});

		addPlayerSlots(playerInventory, 8, 84);

		refreshSlots(player.getMainHandItem());
	}

	private void addSelectedItemSlots() {
		for (int i = 0; i < currentCanisterHandler.getSlots(); i++) {
			int x = 134 + (i % 2) * 18;
			int y = 66 - (i / 2) * 18;
			Slot slot = new SlotItemHandler(currentCanisterHandler, i, x, y) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.getItem() instanceof PotionCanister;
				}
			};
			this.addSlot(slot);
			dynamicSlots.add(slot);
		}
	}

	public void refreshSlots(ItemStack selectedStack) {
		// In 1.20.1 Forge, slot removal after construction is not safely supported
		// (remoteSlots is private and there is no NeoForge sync API).
		// Dynamic slots are populated once at construction; this is a no-op after that.
		if (!dynamicSlots.isEmpty() || currentCanisterHandler != null)
			return;
		currentCanisterHandler = getItemHandler(selectedStack);
		if (currentCanisterHandler != null)
			addSelectedItemSlots();
	}

	public ItemStackHandler getItemHandler(ItemStack itemStack) {
		if (!(itemStack.getItem() instanceof BaseBow))
			return null;
		final ItemStack liveBow = itemStack;
		ItemStackHandler handler = new ItemStackHandler(BaseBow.CANISTER_SLOTS) {
			@Override
			protected void onContentsChanged(int slot) {
				BaseBow.setPotionCanister(liveBow, getStackInSlot(slot), slot);
			}

			@Override
			public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
				return stack.isEmpty() || stack.getItem() instanceof PotionCanister;
			}
		};
		for (int i = 0; i < BaseBow.CANISTER_SLOTS; i++) {
			ItemStack existing = BaseBow.getPotionCanister(itemStack, i);
			if (!existing.isEmpty())
				handler.setStackInSlot(i, existing);
		}
		return handler;
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		if (dynamicSlots.isEmpty())
			return ItemStack.EMPTY;
		int playerInvStart = 5; // armor(4) + offhand(1) = 5 fixed slots
		int playerInvEnd = playerInvStart + Inventory.INVENTORY_SIZE; // 5-40
		int dynamicStart = playerInvEnd; // 41+
		int dynamicEnd = dynamicStart + dynamicSlots.size();
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack currentStack = slot.getItem();
			if (index >= dynamicStart && index < dynamicEnd) { // Dynamic → player inventory
				if (!this.moveItemStackTo(currentStack, playerInvStart, playerInvEnd, true))
					return ItemStack.EMPTY;
			} else if (index >= playerInvStart && index < playerInvEnd) { // Player inventory → dynamic
				if (!this.moveItemStackTo(currentStack, dynamicStart, dynamicEnd, false))
					return ItemStack.EMPTY;
			}
			if (currentStack.isEmpty())
				slot.set(ItemStack.EMPTY);
			else
				slot.setChanged();
			slot.onTake(playerIn, currentStack);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return true;
	}

	static void onEquipItem(Player pPlayer, EquipmentSlot pSlot, ItemStack pNewItem, ItemStack pOldItem) {
		pPlayer.onEquipItem(pSlot, pOldItem, pNewItem);
	}
}
