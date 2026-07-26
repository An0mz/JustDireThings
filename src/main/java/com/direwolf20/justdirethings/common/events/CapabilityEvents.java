package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.capabilities.EnergyStorageItemStackNoReceive;
import com.direwolf20.justdirethings.common.capabilities.EnergyStorageItemstack;
import com.direwolf20.justdirethings.common.capabilities.FluidHandlerItemStack;
import com.direwolf20.justdirethings.common.items.PocketGenerator;
import com.direwolf20.justdirethings.common.items.interfaces.FluidContainingItem;
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
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("removal")
public class CapabilityEvents {

	private static final ResourceLocation ENERGY_CAP_KEY = new ResourceLocation(JustDireThings.MODID, "energy");
	private static final ResourceLocation FLUID_CAP_KEY = new ResourceLocation(JustDireThings.MODID, "fluid");

	@SubscribeEvent
	public static void onAttachItemCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
		ItemStack stack = event.getObject();

		if (stack.getItem() instanceof PoweredItem poweredItem) {
			int capacity = poweredItem.getMaxEnergy();
			EnergyStorageItemstack storage = stack.getItem() instanceof PocketGenerator
					? new EnergyStorageItemStackNoReceive(stack, capacity)
					: new EnergyStorageItemstack(stack, capacity);
			LazyOptional<IEnergyStorage> holder = LazyOptional.of(() -> storage);
			event.addCapability(ENERGY_CAP_KEY, new ICapabilityProvider() {
				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
					return cap == ForgeCapabilities.ENERGY ? holder.cast() : LazyOptional.empty();
				}
			});
			event.addListener(holder::invalidate);
		}

		if (stack.getItem() instanceof FluidContainingItem fluidContainingItem) {
			int capacity = fluidContainingItem.getMaxMB();
			FluidHandlerItemStack storage = new FluidHandlerItemStack(stack, capacity);
			LazyOptional<IFluidHandlerItem> holder = LazyOptional.of(() -> storage);
			event.addCapability(FLUID_CAP_KEY, new ICapabilityProvider() {
				@Nonnull
				@Override
				public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
					return cap == ForgeCapabilities.FLUID_HANDLER_ITEM ? holder.cast() : LazyOptional.empty();
				}
			});
			event.addListener(holder::invalidate);
		}
	}
}
