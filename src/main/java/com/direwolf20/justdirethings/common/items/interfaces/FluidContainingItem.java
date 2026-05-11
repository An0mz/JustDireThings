package com.direwolf20.justdirethings.common.items.interfaces;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public interface FluidContainingItem {
    default int getMaxMB() {
        return 8000;
    }

    static int getAvailableFluid(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (handler == null) return -1;
        return handler.getFluidInTank(0).getAmount();
    }

    default boolean isFluidBarVisible(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null) != null;
    }

    default int getFluidBarWidth(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (handler == null) return 13;
        return Math.min(Math.round((float) handler.getFluidInTank(0).getAmount() * 13.0F / (float) handler.getTankCapacity(0)), 13);
    }

    default int getFluidBarColor(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (handler == null) return -1;
        return Mth.hsvToRgb(0.55F, 1.0F, 1.0F);
    }

    static boolean hasEnoughFluid(ItemStack itemStack, int amt) {
        IFluidHandlerItem handler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (handler == null) return false;
        return handler.getFluidInTank(0).getAmount() >= amt;
    }

    static void consumeFluid(ItemStack itemStack, int amt) {
        IFluidHandlerItem handler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (handler == null) return;
        handler.drain(amt, IFluidHandler.FluidAction.EXECUTE);
    }

    static LiquidBlock getLiquidBlockAt(Level level, BlockPos blockPos) {
        if (level.getBlockState(blockPos).getBlock() instanceof LiquidBlock liquidBlock)
            return liquidBlock;
        return null;
    }

    static boolean pickupFluid(Level level, Player player, ItemStack itemStack, BlockHitResult blockhitresult) {
        BlockPos blockpos = blockhitresult.getBlockPos();
        BlockState blockstate1 = level.getBlockState(blockpos);
        LiquidBlock liquidBlock = getLiquidBlockAt(player.level(), blockpos);
        if (liquidBlock == null) return false;
        IFluidHandlerItem fluidHandler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (fluidHandler == null) return false;
        Fluid fluid = blockstate1.getFluidState().getType();
        int filledAmt = fluidHandler.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.SIMULATE);
        if (filledAmt == 1000) {
            ItemStack itemstack2 = liquidBlock.pickupBlock(level, blockpos, blockstate1);
            fluidHandler.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
            liquidBlock.getPickupSound().ifPresent(sound -> player.playSound(sound, 1.0F, 1.0F));
            if (!level.isClientSide) {
                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemstack2);
            }
            return true;
        }
        return false;
    }
}
