package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.capabilities.EnergyStorageItemstack;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Attaches the Forge Energy capability to all ItemStacks that implement {@link PoweredItem}.
 * Registered on the game (MinecraftForge) event bus in {@link com.direwolf20.justdirethings.setup.ModSetup}.
 */
public class CapabilityEvents {

    private static final ResourceLocation ENERGY_CAP_KEY = new ResourceLocation(JustDireThings.MODID, "energy");

    @SubscribeEvent
    public static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (!(stack.getItem() instanceof PoweredItem poweredItem)) return;

        int capacity = poweredItem.getMaxEnergy();
        EnergyStorageItemstack storage = new EnergyStorageItemstack(stack, capacity);
        LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> storage);

        event.addCapability(ENERGY_CAP_KEY, new ICapabilityProvider() {
            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                return cap == ForgeCapabilities.ENERGY ? holder.cast() : LazyOptional.empty();
            }
        });
        // Invalidate the LazyOptional when the capability provider is invalidated
        event.addListener(holder::invalidate);
    }
}

