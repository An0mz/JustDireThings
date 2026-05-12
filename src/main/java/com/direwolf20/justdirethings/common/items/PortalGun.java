package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.entities.PortalProjectile;
import com.direwolf20.justdirethings.common.items.interfaces.FluidContainingItem;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.MagicHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class PortalGun extends Item implements FluidContainingItem {

    private static final String UUID_MOST = "GunUUIDMost";
    private static final String UUID_LEAST = "GunUUIDLeast";

    public PortalGun() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getMaxMB() {
        return Config.PORTAL_GUN_ORIGINAL_MAX_FLUID.get();
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

        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            if (FluidContainingItem.pickupFluid(level, player, stack, hitResult))
                return InteractionResultHolder.success(stack);
        }

        if (level.isClientSide) return InteractionResultHolder.pass(stack);

        IFluidHandlerItem fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (fluidHandler == null) return InteractionResultHolder.fail(stack);

        FluidStack fluidStack = fluidHandler.getFluidInTank(0);
        boolean hasFluid = !fluidStack.isEmpty()
                && fluidStack.getFluid().isSame(Registration.UNSTABLE_PORTAL_FLUID_SOURCE.get())
                && fluidStack.getAmount() >= Config.PORTAL_GUN_ORIGINAL_FLUID_COST.get();

        if (!hasFluid) {
            player.displayClientMessage(Component.translatable("justdirethings.lowportalfluid"), true);
            return InteractionResultHolder.fail(stack);
        }

        UUID gunUUID = getOrCreateGunUUID(stack);
        boolean stayOpen = Config.PORTAL_GUN_ORIGINAL_LIFESPAN.get() < 0;

        PortalProjectile projectile = new PortalProjectile(level, player, gunUUID, stayOpen);
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 0.0F);
        level.addFreshEntity(projectile);

        fluidHandler.drain(Config.PORTAL_GUN_ORIGINAL_FLUID_COST.get(), IFluidHandler.FluidAction.EXECUTE);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (level == null) return;
        IFluidHandlerItem fh = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (fh != null) {
            tooltip.add(Component.translatable("justdirethings.portalfluidamt",
                    MagicHelpers.formatted(fh.getFluidInTank(0).getAmount()),
                    MagicHelpers.formatted(fh.getTankCapacity(0))).withStyle(ChatFormatting.GREEN));
        }
    }
}
