package com.direwolf20.justdirethings.common.network;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.network.data.*;
import com.direwolf20.justdirethings.common.network.handler.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String PROTOCOL = "1";
	public static SimpleChannel CHANNEL;
	private static int packetId = 0;

	private static int nextId() {
		return packetId++;
	}

	public static void registerNetworking(final FMLCommonSetupEvent event) {
		CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(JustDireThings.MODID, "main"), () -> PROTOCOL,
				PROTOCOL::equals, PROTOCOL::equals);

		// Server-bound packets
		CHANNEL.registerMessage(nextId(), AreaAffectingPayload.class, AreaAffectingPayload::write,
				AreaAffectingPayload::new, AreaAffectingPacket::handle);
		CHANNEL.registerMessage(nextId(), BlockStateFilterPayload.class, BlockStateFilterPayload::write,
				BlockStateFilterPayload::new, BlockStateFilterPacket::handle);
		CHANNEL.registerMessage(nextId(), ClickerPayload.class, ClickerPayload::write, ClickerPayload::new,
				ClickerPacket::handle);
		CHANNEL.registerMessage(nextId(), DirectionSettingPayload.class, DirectionSettingPayload::write,
				DirectionSettingPayload::new, DirectionSettingPacket::handle);
		CHANNEL.registerMessage(nextId(), DropperSettingPayload.class, DropperSettingPayload::write,
				DropperSettingPayload::new, DropperSettingPacket::handle);
		CHANNEL.registerMessage(nextId(), EnergyTransmitterSettingPayload.class, EnergyTransmitterSettingPayload::write,
				EnergyTransmitterSettingPayload::new, EnergyTransmitterPacket::handle);
		CHANNEL.registerMessage(nextId(), FilterSettingPayload.class, FilterSettingPayload::write,
				FilterSettingPayload::new, FilterSettingPacket::handle);
		CHANNEL.registerMessage(nextId(), GhostSlotPayload.class, GhostSlotPayload::write, GhostSlotPayload::new,
				GhostSlotPacket::handle);
		CHANNEL.registerMessage(nextId(), LeftClickPayload.class, LeftClickPayload::write, LeftClickPayload::new,
				LeftClickPacket::handle);
		CHANNEL.registerMessage(nextId(), PortalGunLeftClickPayload.class, PortalGunLeftClickPayload::write,
				PortalGunLeftClickPayload::new, PortalGunLeftClickPacket::handle);
		CHANNEL.registerMessage(nextId(), PortalGunFavoritePayload.class, PortalGunFavoritePayload::write,
				PortalGunFavoritePayload::new, PortalGunFavoritePacket::handle);
		CHANNEL.registerMessage(nextId(), PortalGunFavoriteChangePayload.class, PortalGunFavoriteChangePayload::write,
				PortalGunFavoriteChangePayload::new, PortalGunFavoriteChangePacket::handle);
		CHANNEL.registerMessage(nextId(), PlayerAccessorPayload.class, PlayerAccessorPayload::write,
				PlayerAccessorPayload::new, PlayerAccessorPacket::handle);
		CHANNEL.registerMessage(nextId(), RedstoneSettingPayload.class, RedstoneSettingPayload::write,
				RedstoneSettingPayload::new, RedstoneSettingPacket::handle);
		CHANNEL.registerMessage(nextId(), SensorPayload.class, SensorPayload::write, SensorPayload::new,
				SensorPacket::handle);
		CHANNEL.registerMessage(nextId(), SwapperPayload.class, SwapperPayload::write, SwapperPayload::new,
				SwapperPacket::handle);
		CHANNEL.registerMessage(nextId(), TickSpeedPayload.class, TickSpeedPayload::write, TickSpeedPayload::new,
				TickSpeedPacket::handle);
		CHANNEL.registerMessage(nextId(), ToggleToolLeftRightClickPayload.class, ToggleToolLeftRightClickPayload::write,
				ToggleToolLeftRightClickPayload::new, ToggleToolLeftRightClickPacket::handle);
		CHANNEL.registerMessage(nextId(), ToggleToolPayload.class, ToggleToolPayload::write, ToggleToolPayload::new,
				ToggleToolPacket::handle);
		CHANNEL.registerMessage(nextId(), ToggleToolSlotPayload.class, ToggleToolSlotPayload::write,
				ToggleToolSlotPayload::new, ToggleToolSlotPacket::handle);
		CHANNEL.registerMessage(nextId(), ExperienceHolderPayload.class, ExperienceHolderPayload::write,
				ExperienceHolderPayload::new, ExperienceHolderPacket::handle);
		CHANNEL.registerMessage(nextId(), ExperienceHolderSettingsPayload.class, ExperienceHolderSettingsPayload::write,
				ExperienceHolderSettingsPayload::new, ExperienceHolderSettingsPacket::handle);
		CHANNEL.registerMessage(nextId(), InventoryHolderPayload.class, InventoryHolderPayload::write,
				InventoryHolderPayload::new, InventoryHolderPacket::handle);
		CHANNEL.registerMessage(nextId(), InventoryHolderMoveItemsPayload.class, InventoryHolderMoveItemsPayload::write,
				InventoryHolderMoveItemsPayload::new, InventoryHolderMoveItemsPacket::handle);
		CHANNEL.registerMessage(nextId(), InventoryHolderSaveSlotPayload.class, InventoryHolderSaveSlotPayload::write,
				InventoryHolderSaveSlotPayload::new, InventoryHolderSaveSlotPacket::handle);
		CHANNEL.registerMessage(nextId(), ParadoxMachinePayload.class, ParadoxMachinePayload::write,
				ParadoxMachinePayload::new, ParadoxMachinePacket::handle);

		CHANNEL.registerMessage(nextId(), CopyMachineSettingsPayload.class, CopyMachineSettingsPayload::write,
				CopyMachineSettingsPayload::new, CopyMachineSettingsPacket::handle);

		// Client-bound packets
		CHANNEL.registerMessage(nextId(), ClientSoundPayload.class, ClientSoundPayload::write, ClientSoundPayload::new,
				ClientSoundPacket::handle);
		CHANNEL.registerMessage(nextId(), ParadoxSyncPayload.class, ParadoxSyncPayload::write, ParadoxSyncPayload::new,
				ParadoxSyncPacket::handle);
	}
}
