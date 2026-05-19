package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.containers.PotionCanisterContainer;
import com.direwolf20.justdirethings.common.containers.handlers.PotionCanisterHandler;
import com.direwolf20.justdirethings.util.MagicHelpers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class PotionCanister extends Item {
	private static final String POTION_ID_KEY = "StoredPotionId";
	private static final String POTION_AMT_KEY = "PotionAmt";

	public PotionCanister() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (level.isClientSide())
			return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);

		NetworkHooks
				.openScreen((ServerPlayer) player,
						new SimpleMenuProvider((windowId, playerInventory, playerEntity) -> new PotionCanisterContainer(
								windowId, playerInventory, player, itemstack), Component.translatable("")),
						buf -> buf.writeItem(itemstack));

		return new InteractionResultHolder<>(InteractionResult.PASS, itemstack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, level, tooltip, flagIn);
		if (level == null)
			return;
		int potionAmt = getPotionAmount(stack);
		Potion potion = getStoredPotion(stack);
		if (potionAmt == 0 || potion == Potions.EMPTY)
			return;
		tooltip.add(Component.literal(MagicHelpers.formatted(potionAmt) + "/" + MagicHelpers.formatted(getMaxMB())));
		PotionUtils.addPotionTooltip(potion.getEffects(), tooltip, 1.0F);
	}

	public static int getMaxMB() {
		return 1000;
	}

	public static Potion getStoredPotion(ItemStack itemStack) {
		var tag = itemStack.getTag();
		if (tag == null || !tag.contains(POTION_ID_KEY))
			return Potions.EMPTY;
		String id = tag.getString(POTION_ID_KEY);
		if (id.isEmpty())
			return Potions.EMPTY;
		Potion potion = BuiltInRegistries.POTION.get(new ResourceLocation(id));
		return potion != null ? potion : Potions.EMPTY;
	}

	public static void setStoredPotion(ItemStack itemStack, Potion potion) {
		if (potion == Potions.EMPTY) {
			var tag = itemStack.getTag();
			if (tag != null)
				tag.remove(POTION_ID_KEY);
		} else {
			ResourceLocation key = BuiltInRegistries.POTION.getKey(potion);
			if (key != null)
				itemStack.getOrCreateTag().putString(POTION_ID_KEY, key.toString());
		}
	}

	public static void attemptFill(ItemStack canister, PotionCanisterHandler handler) {
		if (!(canister.getItem() instanceof PotionCanister))
			return;
		ItemStack potionStack = handler.getStackInSlot(0);
		if (potionStack.isEmpty() || !(potionStack.getItem() instanceof PotionItem))
			return;

		Potion current = getStoredPotion(canister);
		Potion incoming = PotionUtils.getPotion(potionStack);

		if (incoming == Potions.EMPTY)
			return;
		if (current != Potions.EMPTY && current != incoming)
			return;

		int currentAmt = getPotionAmount(canister);
		if (currentAmt + 250 <= getMaxMB()) {
			setStoredPotion(canister, incoming);
			addPotionAmount(canister, 250);
			handler.setStackInSlot(0, new ItemStack(Items.GLASS_BOTTLE));
		}
	}

	public static int getPotionAmount(ItemStack itemStack) {
		var tag = itemStack.getTag();
		return tag != null && tag.contains(POTION_AMT_KEY) ? tag.getInt(POTION_AMT_KEY) : 0;
	}

	public static void addPotionAmount(ItemStack itemStack, int amt) {
		setPotionAmount(itemStack, getPotionAmount(itemStack) + amt);
	}

	public static void setPotionAmount(ItemStack itemStack, int amt) {
		int clamped = Math.max(0, Math.min(getMaxMB(), amt));
		itemStack.getOrCreateTag().putInt(POTION_AMT_KEY, clamped);
		if (clamped == 0)
			setStoredPotion(itemStack, Potions.EMPTY);
	}

	public static void reducePotionAmount(ItemStack itemStack, int amt) {
		setPotionAmount(itemStack, getPotionAmount(itemStack) - amt);
	}

	public static int getFullness(ItemStack itemStack) {
		int amt = getPotionAmount(itemStack);
		if (amt == 0)
			return 0;
		if (amt <= 250)
			return 1;
		if (amt <= 500)
			return 2;
		if (amt <= 750)
			return 3;
		return 4;
	}

	public static int getPotionColor(ItemStack itemStack) {
		Potion potion = getStoredPotion(itemStack);
		if (potion == Potions.EMPTY)
			return -1;
		return PotionUtils.getColor(potion);
	}
}
