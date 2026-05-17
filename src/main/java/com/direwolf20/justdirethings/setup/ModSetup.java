package com.direwolf20.justdirethings.setup;

import com.direwolf20.justdirethings.common.events.BlockEvents;
import com.direwolf20.justdirethings.common.events.CapabilityEvents;
import com.direwolf20.justdirethings.common.events.EntityEvents;
import com.direwolf20.justdirethings.common.events.LivingEntityEvents;
import com.direwolf20.justdirethings.common.events.PlayerEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import static com.direwolf20.justdirethings.JustDireThings.MODID;

public class ModSetup {
    public static void init(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(BlockEvents.class);
        MinecraftForge.EVENT_BUS.register(LivingEntityEvents.class);
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
        MinecraftForge.EVENT_BUS.register(CapabilityEvents.class);
        MinecraftForge.EVENT_BUS.register(EntityEvents.class);
        MinecraftForge.EVENT_BUS.addListener(ModSetup::registerFurnaceFuels);
    }

    public static void registerFurnaceFuels(FurnaceFuelBurnTimeEvent event) {
        Item item = event.getItemStack().getItem();
        if (item == Registration.Coal_T1.get())           event.setBurnTime(4800);
        else if (item == Registration.CoalBlock_T1_ITEM.get()) event.setBurnTime(48000);
        else if (item == Registration.Coal_T2.get())      event.setBurnTime(14400);
        else if (item == Registration.CoalBlock_T2_ITEM.get()) event.setBurnTime(144000);
        else if (item == Registration.Coal_T3.get())      event.setBurnTime(43200);
        else if (item == Registration.CoalBlock_T3_ITEM.get()) event.setBurnTime(432000);
        else if (item == Registration.Coal_T4.get())      event.setBurnTime(129600);
        else if (item == Registration.CoalBlock_T4_ITEM.get()) event.setBurnTime(1296000);
        else if (item == Registration.CharcoalBlock_ITEM.get()) event.setBurnTime(16000);
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> TAB_JUSTDIRETHINGS = CREATIVE_MODE_TABS.register(MODID, () -> CreativeModeTab.builder()
            .title(Component.literal("Just Dire Things"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(Registration.Fuel_Canister.get()))
            .displayItems((parameters, output) -> {
                Registration.ITEMS.getEntries().forEach(e -> {
                    Item item = e.get();
                    output.accept(item);
                });
                Registration.TOOLS.getEntries().forEach(e -> {
                    Item item = e.get();
                    output.accept(item);
                });
                Registration.ARMORS.getEntries().forEach(e -> {
                    Item item = e.get();
                    output.accept(item);
                });
                Registration.BOWS.getEntries().forEach(e -> {
                    Item item = e.get();
                    output.accept(item);
                });
                Registration.UPGRADES.getEntries().forEach(e -> {
                    if (e.get() == Registration.UPGRADE_PHASE.get()) return;
                    Item item = e.get();
                    output.accept(item);
                });
            }).build());

}
