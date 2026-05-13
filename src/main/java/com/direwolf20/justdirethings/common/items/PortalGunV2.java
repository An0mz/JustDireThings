package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.entities.PortalProjectile;
import com.direwolf20.justdirethings.common.entities.PortalEntity;
import com.direwolf20.justdirethings.common.items.interfaces.FluidContainingItem;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import static com.direwolf20.justdirethings.util.TooltipHelpers.appendFEText;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PortalGunV2 extends BasePoweredItem implements FluidContainingItem {

    public static final int MAX_FAVORITES = 12;

    private static final String UUID_MOST = "GunUUIDMost";
    private static final String UUID_LEAST = "GunUUIDLeast";
    private static final String FAVORITES = "PortalFavorites";
    private static final String FAVORITE_POS = "PortalFavoritePos";
    private static final String PREVIOUS = "PortalPrevious";
    private static final String STAY_OPEN = "PortalStayOpen";

    public PortalGunV2() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getMaxEnergy() {
        return Config.PORTAL_GUN_MAX_FE.get();
    }

    @Override
    public int getMaxMB() {
        return Config.PORTAL_GUN_MAX_FLUID.get();
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
            if (isPortalFluid(level, hitResult)) {
                IFluidHandlerItem fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
                if (fluidHandler != null) {
                    FluidStack inTank = fluidHandler.getFluidInTank(0);
                    if (!inTank.isEmpty() && !isPortalFluid(inTank)) {
                        // Recover from previously-filled wrong fluids by clearing the tank before refill.
                        fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
                if (FluidContainingItem.pickupFluid(level, player, stack, hitResult)) {
                    return InteractionResultHolder.success(stack);
                }
            }
        }

        if (level.isClientSide) return InteractionResultHolder.pass(stack);

        if (player.isShiftKeyDown()) {
            closeMyPortals((ServerLevel) level, getOrCreateGunUUID(stack));
            return InteractionResultHolder.success(stack);
        }

        return spawnProjectile(level, player, stack, true);
    }

    public static InteractionResultHolder<ItemStack> spawnProjectile(Level level, Player player, ItemStack stack, boolean isPrimaryType) {
        NBTHelpers.PortalDestination destination = player.isShiftKeyDown() ? getPrevious(stack) : getSelectedFavorite(stack);
        if (destination == null) {
            return InteractionResultHolder.fail(stack);
        }

        if (!PoweredItem.hasEnoughEnergy(stack, Config.PORTAL_GUN_FE_COST.get())) {
            player.displayClientMessage(Component.translatable("justdirethings.lowenergy"), true);
            return InteractionResultHolder.fail(stack);
        }

        IFluidHandlerItem fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (fluidHandler == null) return InteractionResultHolder.fail(stack);

        int fluidCost = calculateFluidCost((ServerLevel) level, player, destination);
        FluidStack fluidStack = fluidHandler.getFluidInTank(0);
        boolean hasPortalFluid = !fluidStack.isEmpty()
                && (fluidStack.getFluid().isSame(Registration.PORTAL_FLUID_SOURCE.get())
                || fluidStack.getFluid().isSame(Registration.PORTAL_FLUID_FLOWING.get()))
                && fluidStack.getAmount() >= fluidCost;

        if (!hasPortalFluid) {
            player.displayClientMessage(Component.translatable("justdirethings.lowportalfluid"), true);
            return InteractionResultHolder.fail(stack);
        }

        UUID gunUUID = getOrCreateGunUUID(stack);
        int lifespan = getStayOpen(stack) ? -1 : Config.PORTAL_GUN_LIFESPAN.get();

        PortalProjectile projectile = new PortalProjectile(level, player, gunUUID, isPrimaryType, true, destination, lifespan);
        projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.0F, 1.0F);
        level.addFreshEntity(projectile);

        PoweredItem.consumeEnergy(stack, Config.PORTAL_GUN_FE_COST.get());
        fluidHandler.drain(fluidCost, IFluidHandler.FluidAction.EXECUTE);
        setPrevious(player, stack);

        return InteractionResultHolder.success(stack);
    }

    public static int calculateFluidCost(ServerLevel sourceLevel, Player player, NBTHelpers.PortalDestination destination) {
        if (player.isCreative()) return 0;

        ServerLevel targetLevel = sourceLevel.getServer().getLevel(destination.dimension());
        if (targetLevel == null || !targetLevel.equals(sourceLevel)) {
            return 100;
        }

        HitResult result = player.pick(5, 0f, false);
        double distance = destination.position().distanceTo(result.getLocation());
        return Math.max(Config.PORTAL_GUN_FLUID_COST.get(), Math.min((int) Math.ceil(distance * 0.25), 100));
    }

    public static NBTHelpers.PortalDestination getSelectedFavorite(ItemStack stack) {
        List<NBTHelpers.PortalDestination> favorites = getFavorites(stack);
        int index = Math.max(0, Math.min(getFavoritePosition(stack), favorites.size() - 1));
        return favorites.get(index);
    }

    public static NBTHelpers.PortalDestination getFavorite(ItemStack stack, int slot) {
        List<NBTHelpers.PortalDestination> favorites = getFavorites(stack);
        if (slot < 0 || slot >= favorites.size()) return null;
        return favorites.get(slot);
    }

    public static int getFavoritePosition(ItemStack stack) {
        return Math.max(0, Math.min(stack.getOrCreateTag().getInt(FAVORITE_POS), MAX_FAVORITES - 1));
    }

    public static void setFavoritePosition(ItemStack stack, int favorite) {
        stack.getOrCreateTag().putInt(FAVORITE_POS, Math.max(0, Math.min(favorite, MAX_FAVORITES - 1)));
    }

    public static List<NBTHelpers.PortalDestination> getFavorites(ItemStack stack) {
        List<NBTHelpers.PortalDestination> out = new ArrayList<>(MAX_FAVORITES);
        ListTag listTag = stack.getOrCreateTag().getList(FAVORITES, Tag.TAG_COMPOUND);
        for (int i = 0; i < MAX_FAVORITES; i++) {
            if (i < listTag.size()) {
                NBTHelpers.PortalDestination destination = NBTHelpers.PortalDestination.fromNBT(listTag.getCompound(i));
                out.add(destination);
            } else {
                out.add(null);
            }
        }
        return out;
    }

    public static void setFavorites(ItemStack stack, List<NBTHelpers.PortalDestination> favorites) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < MAX_FAVORITES; i++) {
            NBTHelpers.PortalDestination destination = i < favorites.size() ? favorites.get(i) : null;
            listTag.add(destination == null ? new CompoundTag() : destination.toNBT());
        }
        stack.getOrCreateTag().put(FAVORITES, listTag);
    }

    public static void setPrevious(Player player, ItemStack stack) {
        Vec3 position = player.position();
        net.minecraft.core.Direction facing = MiscHelpers.getFacingDirection(player);
        if (facing == net.minecraft.core.Direction.DOWN) facing = net.minecraft.core.Direction.NORTH;
        ResourceKey<Level> dimension = player.level().dimension();
        NBTHelpers.PortalDestination destination = new NBTHelpers.PortalDestination(dimension, position, facing, "previous");
        stack.getOrCreateTag().put(PREVIOUS, destination.toNBT());
    }

    public static NBTHelpers.PortalDestination getPrevious(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(PREVIOUS, Tag.TAG_COMPOUND)) return null;
        return NBTHelpers.PortalDestination.fromNBT(tag.getCompound(PREVIOUS));
    }

    public static void addFavorite(ItemStack stack, int position, NBTHelpers.PortalDestination destination) {
        List<NBTHelpers.PortalDestination> favorites = getFavorites(stack);
        favorites.set(Math.max(0, Math.min(position, MAX_FAVORITES - 1)), destination);
        setFavorites(stack, favorites);
    }

    public static void removeFavorite(ItemStack stack, int position) {
        List<NBTHelpers.PortalDestination> favorites = getFavorites(stack);
        favorites.set(Math.max(0, Math.min(position, MAX_FAVORITES - 1)), null);
        setFavorites(stack, favorites);
    }

    public static boolean getStayOpen(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean(STAY_OPEN);
    }

    public static void setStayOpen(ItemStack stack, boolean stayOpen) {
        stack.getOrCreateTag().putBoolean(STAY_OPEN, stayOpen);
    }

    public static ItemStack getPortalGunv2(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.getItem() instanceof PortalGunV2) return mainHand;
        ItemStack offHand = player.getOffhandItem();
        if (offHand.getItem() instanceof PortalGunV2) return offHand;
        return ItemStack.EMPTY;
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

    private static boolean isPortalFluid(Level level, BlockHitResult hitResult) {
        net.minecraft.world.level.material.Fluid fluid = level.getFluidState(hitResult.getBlockPos()).getType();
        return fluid.isSame(Registration.PORTAL_FLUID_SOURCE.get())
                || fluid.isSame(Registration.PORTAL_FLUID_FLOWING.get());
    }

    private static boolean isPortalFluid(FluidStack stack) {
        return stack.getFluid().isSame(Registration.PORTAL_FLUID_SOURCE.get())
                || stack.getFluid().isSame(Registration.PORTAL_FLUID_FLOWING.get());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (level == null) return;
        appendFEText(stack, tooltip);
        IFluidHandlerItem fh = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (fh != null) {
            tooltip.add(Component.translatable("justdirethings.portalfluidamt",
                    MagicHelpers.formatted(fh.getFluidInTank(0).getAmount()),
                    MagicHelpers.formatted(fh.getTankCapacity(0))).withStyle(ChatFormatting.GREEN));
        }
    }
}
