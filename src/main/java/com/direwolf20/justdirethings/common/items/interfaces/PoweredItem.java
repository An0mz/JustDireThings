package com.direwolf20.justdirethings.common.items.interfaces;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

public interface PoweredItem {
    default int getAvailableEnergy(ItemStack stack) {
        var energy = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
        if (energy == null) {
            return -1;
        }
        return energy.getEnergyStored();
    }

    default boolean isPowerBarVisible(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ENERGY).orElse(null) != null;
    }

    default int getPowerBarWidth(ItemStack stack) {
        var energy = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
        if (energy == null) {
            return 13;
        }
        return Math.min(13 * energy.getEnergyStored() / energy.getMaxEnergyStored(), 13);
    }

    default int getPowerBarColor(ItemStack stack) {
        var energy = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
        if (energy == null) {
            return -1; //Tell caller to call super
        }
        return Mth.hsvToRgb(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored()) / 3.0F, 1.0F, 1.0F);
    }

    default int getMaxEnergy() {
        return 10000;
    }

    static boolean hasEnoughEnergy(ItemStack stack, int amount) {
        IEnergyStorage energy = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
        if (energy == null) return false;
        return energy.getEnergyStored() >= amount;
    }

    static void consumeEnergy(ItemStack stack, int amount) {
        IEnergyStorage energy = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
        if (energy == null) return;
        energy.extractEnergy(amount, false);
    }
}
