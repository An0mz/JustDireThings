package com.direwolf20.justdirethings;

import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.setup.ClientSetup;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(JustDireThings.MODID)
public class JustDireThings {
    public static final String MODID = "justdirethings";
    private static final Logger LOGGER = LogUtils.getLogger();

    public JustDireThings(IEventBus modEventBus) {
        Registration.init(modEventBus);
        Config.register();

        modEventBus.addListener(ModSetup::init);
        ModSetup.CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(PacketHandler::registerNetworking); // registers on FMLCommonSetupEvent
        if (FMLLoader.getDist().isClient()) {
            modEventBus.addListener(ClientSetup::init);
        }
    }
}
