package com.direwolf20.justdirethings;

import com.direwolf20.justdirethings.common.entities.DecoyEntity;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.setup.ClientSetup;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;

@Mod(JustDireThings.MODID)
public class JustDireThings {
	public static final String MODID = "justdirethings";
	private static final Logger LOGGER = LogUtils.getLogger();

	public JustDireThings() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		Registration.init(modEventBus);
		Config.register();

		modEventBus.addListener(ModSetup::init);
		ModSetup.CREATIVE_MODE_TABS.register(modEventBus);
		modEventBus.addListener(PacketHandler::registerNetworking);
		modEventBus.addListener(JustDireThings::registerEntityAttributes);
		modEventBus.addListener(JustDireThings::registerEntityTypeAttributes);
		if (FMLLoader.getDist().isClient()) {
			modEventBus.addListener(ClientSetup::init);
		}
	}

	private static void registerEntityAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, Registration.PHASE.get());
	}

	private static void registerEntityTypeAttributes(EntityAttributeCreationEvent event) {
		event.put(Registration.DecoyEntity.get(), DecoyEntity.createAttributes().build());
	}
}
