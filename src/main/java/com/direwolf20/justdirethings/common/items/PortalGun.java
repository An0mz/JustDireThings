package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.entities.PortalProjectile;
import com.direwolf20.justdirethings.common.entities.PortalEntity;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import com.direwolf20.justdirethings.setup.Config;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import static com.direwolf20.justdirethings.util.TooltipHelpers.appendFEText;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class PortalGun extends BasePoweredItem implements PoweredItem {

	private static final String UUID_MOST = "GunUUIDMost";
	private static final String UUID_LEAST = "GunUUIDLeast";

	public PortalGun() {
		super(new Properties().stacksTo(1));
	}

	@Override
	public int getMaxEnergy() {
		return Config.PORTAL_GUN_ORIGINAL_MAX_FE.get();
	}

	public static UUID getOrCreateGunUUID(ItemStack stack) {
		CompoundTag tag = stack.getOrCreateTag();
		if (tag.contains(UUID_MOST)) {
			return new UUID(tag.getLong(UUID_MOST), tag.getLong(UUID_LEAST));
		}
		UUID uuid = UUID.randomUUID();
		tag.putLong(UUID_MOST, uuid.getMostSignificantBits());
		tag.putLong(UUID_LEAST, uuid.getLeastSignificantBits());
		return uuid;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (level.isClientSide)
			return InteractionResultHolder.pass(stack);

		if (player.isShiftKeyDown()) {
			closeMyPortals((ServerLevel) level, getOrCreateGunUUID(stack));
			return InteractionResultHolder.success(stack);
		}

		return firePortal(level, player, stack, false);
	}

	// Shared shoot logic for right-click use and left-click packet path.
	public static InteractionResultHolder<ItemStack> firePortal(Level level, Player player, ItemStack stack,
			boolean isPrimaryType) {
		if (level.isClientSide)
			return InteractionResultHolder.pass(stack);

		if (!PoweredItem.hasEnoughEnergy(stack, Config.PORTAL_GUN_ORIGINAL_FE_COST.get())) {
			player.displayClientMessage(Component.translatable("justdirethings.lowenergy"), true);
			return InteractionResultHolder.fail(stack);
		}

		UUID gunUUID = getOrCreateGunUUID(stack);
		int lifespan = Config.PORTAL_GUN_ORIGINAL_LIFESPAN.get();
		PortalProjectile projectile = new PortalProjectile(level, player, gunUUID, isPrimaryType, false, null,
				lifespan);
		projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 0.0F);
		level.addFreshEntity(projectile);

		PoweredItem.consumeEnergy(stack, Config.PORTAL_GUN_ORIGINAL_FE_COST.get());

		return InteractionResultHolder.success(stack);
	}

	public static InteractionResultHolder<ItemStack> firePortal(Level level, Player player, ItemStack stack) {
		return firePortal(level, player, stack, false);
	}

	private static void closeMyPortals(ServerLevel level, UUID gunUUID) {
		for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
			for (net.minecraft.world.entity.Entity entity : serverLevel.getAllEntities()) {
				if (entity instanceof PortalEntity portal && portal.getGunUUID().equals(gunUUID)) {
					portal.discard();
				}
			}
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, level, tooltip, flag);
		if (level == null)
			return;
		appendFEText(stack, tooltip);
	}
}
