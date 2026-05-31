package com.direwolf20.justdirethings.common.containers;

import com.direwolf20.justdirethings.common.blockentities.InventoryHolderBE;
import com.direwolf20.justdirethings.common.containers.basecontainers.BaseMachineContainer;
import com.direwolf20.justdirethings.common.containers.handlers.FilterBasicHandler;
import com.direwolf20.justdirethings.common.containers.slots.FilterBasicSlot;
import com.direwolf20.justdirethings.common.containers.slots.InventoryHolderSlot;
import com.mojang.datafixers.util.Pair;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.ItemStackKey;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

@SuppressWarnings("removal")
public class InventoryHolderContainer extends BaseMachineContainer {
	public InventoryHolderBE inventoryHolderBE;
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

	public InventoryHolderContainer(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
		this(windowId, playerInventory, extraData.readBlockPos());
	}

	public InventoryHolderContainer(int windowId, Inventory playerInventory, BlockPos blockPos) {
		super(Registration.InventoryHolder_Container.get(), windowId, playerInventory, blockPos);
		if (baseMachineBE instanceof InventoryHolderBE be) {
			inventoryHolderBE = be;
		}
		// InventoryHolderBE doesn't implement FilterableBE, so add ghost filter slots
		// manually
		if (inventoryHolderBE != null) {
			filterHandler = inventoryHolderBE.filterBasicHandler;
			FILTER_SLOTS = filterHandler.getSlots();
			for (int i = 0; i < FILTER_SLOTS; i++) {
				addSlot(new FilterBasicSlot(filterHandler, i, -100 - i, -100));
			}
		}
		addInventoryHolderPlayerSlots(playerInventory);
	}

	@Override
	public void addMachineSlots() {
		machineHandler = baseMachineBE.getMachineHandler();
		if (baseMachineBE instanceof InventoryHolderBE be) {
			// Main inventory rows in the top section.
			addInventoryHolderSlotBox(machineHandler, be, 0, 8, -8, 9, 18, 3, 18);
			// Machine hotbar row.
			addInventoryHolderSlotRange(machineHandler, be, 27, 8, 50, 9, 18);
			// Armor + offhand strip with vanilla empty-slot icons.
			addArmorAndOffhandSlots(machineHandler, be);
		}
	}

	private void addArmorAndOffhandSlots(IItemHandler handler, InventoryHolderBE be) {
		for (int k = 0; k < 4; ++k) {
			final EquipmentSlot equipmentSlot = SLOT_IDS[k];
			this.addSlot(new InventoryHolderSlot(handler, 36 + k, 44 + k * 18, -28, be) {
				@Override
				public int getMaxStackSize() {
					return 1;
				}

				@Override
				public int getMaxStackSize(ItemStack stack) {
					return 1;
				}

				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.canEquip(equipmentSlot, player) && super.mayPlace(stack);
				}

				@Override
				public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
					return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[equipmentSlot.getIndex()]);
				}
			});
		}

		this.addSlot(new InventoryHolderSlot(handler, 40, 44 + 4 * 18, -28, be) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.canEquip(EquipmentSlot.OFFHAND, player) && super.mayPlace(stack);
			}

			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_ARMOR_SLOT_SHIELD);
			}
		});
	}

	private void addInventoryHolderPlayerSlots(Inventory playerInventory) {
		// Keep menu slot order aligned with machine slots: main inventory, hotbar,
		// armor, then offhand.
		InvWrapper playerWrapper = new InvWrapper(playerInventory);
		addSlotBox(playerWrapper, 9, 8, 97, 9, 18, 3, 18);
		addSlotRange(playerWrapper, 0, 8, 155, 9, 18);

		for (int k = 0; k < 4; ++k) {
			final EquipmentSlot equipmentSlot = SLOT_IDS[k];
			this.addSlot(new Slot(playerInventory, 39 - k, 44 + k * 18, 79) {
				@Override
				public void set(ItemStack newItem) {
					ItemStack oldItem = this.getItem();
					player.onEquipItem(equipmentSlot, oldItem, newItem);
					super.set(newItem);
				}

				@Override
				public int getMaxStackSize() {
					return 1;
				}

				@Override
				public boolean mayPlace(ItemStack stack) {
					return stack.canEquip(equipmentSlot, player);
				}

				@Override
				public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
					return Pair.of(InventoryMenu.BLOCK_ATLAS, TEXTURE_EMPTY_SLOTS[equipmentSlot.getIndex()]);
				}
			});
		}

		this.addSlot(new Slot(playerInventory, 40, 44 + 4 * 18, 79) {
			@Override
			public void set(ItemStack newItem) {
				ItemStack oldItem = this.getItem();
				player.onEquipItem(EquipmentSlot.OFFHAND, oldItem, newItem);
				super.set(newItem);
			}

			@Override
			public boolean mayPlace(ItemStack stack) {
				return stack.canEquip(EquipmentSlot.OFFHAND, player);
			}

			@Override
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_ARMOR_SLOT_SHIELD);
			}
		});
	}

	private int addInventoryHolderSlotRange(IItemHandler handler, InventoryHolderBE be, int index, int x, int y,
			int amount, int dx) {
		for (int i = 0; i < amount; i++) {
			addSlot(new InventoryHolderSlot(handler, index, x, y, be));
			x += dx;
			index++;
		}
		return index;
	}

	private int addInventoryHolderSlotBox(IItemHandler handler, InventoryHolderBE be, int index, int x, int y,
			int horAmount, int dx, int verAmount, int dy) {
		for (int j = 0; j < verAmount; j++) {
			index = addInventoryHolderSlotRange(handler, be, index, x, y, horAmount, dx);
			y += dy;
		}
		return index;
	}

	public void sendAllItemsToMachine() {
		int playerStart = MACHINE_SLOTS + FILTER_SLOTS;
		for (int i = 0; i < MACHINE_SLOTS; i++) {
			moveExactSlot(playerStart + i, i);
		}
	}

	public void sendAllItemsToPlayer() {
		for (int i = 0; i < MACHINE_SLOTS; i++) {
			moveExactSlot(i, MACHINE_SLOTS + FILTER_SLOTS + i);
		}
	}

	public void swapItems() {
		int playerStart = MACHINE_SLOTS + FILTER_SLOTS;
		for (int i = 0; i < MACHINE_SLOTS; i++) {
			swapExactSlots(i, playerStart + i);
		}
	}

	private void moveExactSlot(int sourceIndex, int targetIndex) {
		if (sourceIndex < 0 || targetIndex < 0 || sourceIndex >= this.slots.size()
				|| targetIndex >= this.slots.size()) {
			return;
		}

		Slot sourceSlot = this.slots.get(sourceIndex);
		Slot targetSlot = this.slots.get(targetIndex);
		if (!sourceSlot.hasItem()) {
			return;
		}

		ItemStack sourceStack = sourceSlot.getItem();
		ItemStack originalStack = sourceStack.copy();
		if (!targetSlot.mayPlace(sourceStack)) {
			return;
		}

		if (!this.moveItemStackTo(sourceStack, targetIndex, targetIndex + 1, false)) {
			return;
		}

		if (sourceStack.isEmpty()) {
			sourceSlot.set(ItemStack.EMPTY);
		} else {
			sourceSlot.setChanged();
		}

		if (sourceStack.getCount() != originalStack.getCount()) {
			sourceSlot.onTake(this.player, sourceStack);
		}
	}

	private void swapExactSlots(int firstIndex, int secondIndex) {
		if (firstIndex < 0 || secondIndex < 0 || firstIndex >= this.slots.size() || secondIndex >= this.slots.size()) {
			return;
		}

		Slot firstSlot = this.slots.get(firstIndex);
		Slot secondSlot = this.slots.get(secondIndex);
		ItemStack firstStack = firstSlot.getItem();
		ItemStack secondStack = secondSlot.getItem();

		if (firstStack.isEmpty() && secondStack.isEmpty()) {
			return;
		}

		if (firstStack.isEmpty()) {
			moveExactSlot(secondIndex, firstIndex);
			return;
		}

		if (secondStack.isEmpty()) {
			moveExactSlot(firstIndex, secondIndex);
			return;
		}

		if (!firstSlot.mayPlace(secondStack) || !secondSlot.mayPlace(firstStack)) {
			return;
		}

		ItemStack firstCopy = firstStack.copy();
		ItemStack secondCopy = secondStack.copy();
		firstSlot.set(secondCopy);
		secondSlot.set(firstCopy);
		firstSlot.setChanged();
		secondSlot.setChanged();
	}

	public boolean moveToFilteredSlot(ItemStack currentStack) {
		if (inventoryHolderBE == null)
			return false;
		ItemStackKey key = new ItemStackKey(currentStack, inventoryHolderBE.compareNBT);
		FilterBasicHandler filteredItems = inventoryHolderBE.filterBasicHandler;
		for (int i = 0; i < filteredItems.getSlots(); i++) {
			ItemStack stack = filteredItems.getStackInSlot(i);
			if (stack.isEmpty())
				continue;
			if (key.equals(new ItemStackKey(stack, inventoryHolderBE.compareNBT))) {
				if (this.moveItemStackTo(currentStack, i, i + 1, false) && currentStack.isEmpty())
					return true;
			}
		}
		return false;
	}

	private boolean movePlayerEquipmentToMatchingMachineSlot(ItemStack currentStack, int index) {
		int playerStart = MACHINE_SLOTS + FILTER_SLOTS;
		int playerArmorStart = playerStart + 36;
		int playerOffhandSlot = playerStart + 40;

		if (index >= playerArmorStart && index < playerArmorStart + 4) {
			int machineArmorSlot = 36 + (index - playerArmorStart);
			return this.moveItemStackTo(currentStack, machineArmorSlot, machineArmorSlot + 1, false);
		}

		if (index == playerOffhandSlot) {
			return this.moveItemStackTo(currentStack, 40, 41, false);
		}

		return false;
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(ContainerLevelAccess.create(player.level(), pos), player, Registration.InventoryHolder.get());
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot.hasItem()) {
			ItemStack currentStack = slot.getItem();
			int playerStart = MACHINE_SLOTS + FILTER_SLOTS;
			if (index < MACHINE_SLOTS) {
				if (!this.moveItemStackTo(currentStack, playerStart, slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= playerStart) {
				if (movePlayerEquipmentToMatchingMachineSlot(currentStack, index)) {
					// placed in corresponding armor/offhand slot
				} else if (moveToFilteredSlot(currentStack)) {
					// placed in filter-matched slot
				} else if (inventoryHolderBE != null && inventoryHolderBE.filtersOnly) {
					return ItemStack.EMPTY;
				} else if (!this.moveItemStackTo(currentStack, 0, MACHINE_SLOTS, false)) {
					return ItemStack.EMPTY;
				}
			}

			if (currentStack.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (currentStack.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(playerIn, currentStack);
		}
		return itemstack;
	}

	@Override
	public void removed(Player playerIn) {
		super.removed(playerIn);
		if (baseMachineBE != null)
			baseMachineBE.markDirtyClient();
	}
}
