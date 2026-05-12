package com.direwolf20.justdirethings.setup;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.KeyBindings;
import com.direwolf20.justdirethings.client.blockentityrenders.*;
import com.direwolf20.justdirethings.client.blockentityrenders.gooblocks.GooBlockRender_Tier1;
import com.direwolf20.justdirethings.client.blockentityrenders.gooblocks.GooBlockRender_Tier2;
import com.direwolf20.justdirethings.client.blockentityrenders.gooblocks.GooBlockRender_Tier3;
import com.direwolf20.justdirethings.client.blockentityrenders.gooblocks.GooBlockRender_Tier4;
import com.direwolf20.justdirethings.client.entityrenders.CreatureCatcherEntityRender;
import com.direwolf20.justdirethings.client.entityrenders.TimeWandEntityRenderer;
import com.direwolf20.justdirethings.client.entityrenders.PortalEntityRenderer;
import com.direwolf20.justdirethings.client.itemcustomrenders.FluidbarDecorator;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import com.direwolf20.justdirethings.client.events.EventKeyInput;
import com.direwolf20.justdirethings.client.events.PlayerEvents;
import com.direwolf20.justdirethings.client.events.RenderHighlight;
import com.direwolf20.justdirethings.client.events.RenderLevelLast;
import com.direwolf20.justdirethings.client.overlays.AbilityCooldownOverlay;
import com.direwolf20.justdirethings.client.screens.*;
import com.direwolf20.justdirethings.common.items.PocketGenerator;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import com.direwolf20.justdirethings.client.particles.GlitterParticle;
import com.direwolf20.justdirethings.client.particles.ModParticles;
import com.direwolf20.justdirethings.client.particles.ParadoxParticle;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.energy.IEnergyStorage;

@Mod.EventBusSubscriber(modid = JustDireThings.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(KeyBindings::onClientInput);

        //Register our Render Events Class
        MinecraftForge.EVENT_BUS.register(RenderLevelLast.class);
        MinecraftForge.EVENT_BUS.register(EventKeyInput.class);
        MinecraftForge.EVENT_BUS.register(RenderHighlight.class);
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);

        //Register screens
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.FuelCanister_Container.get(), FuelCanisterScreen::new);
            MenuScreens.register(Registration.PocketGenerator_Container.get(), PocketGeneratorScreen::new);
            MenuScreens.register(Registration.Tool_Settings_Container.get(), ToolSettingScreen::new);
            MenuScreens.register(Registration.Item_Collector_Container.get(), ItemCollectorScreen::new);
            MenuScreens.register(Registration.BlockBreakerT1_Container.get(), BlockBreakerT1Screen::new);
            MenuScreens.register(Registration.BlockBreakerT2_Container.get(), BlockBreakerT2Screen::new);
            MenuScreens.register(Registration.BlockPlacerT1_Container.get(), BlockPlacerT1Screen::new);
            MenuScreens.register(Registration.BlockPlacerT2_Container.get(), BlockPlacerT2Screen::new);
            MenuScreens.register(Registration.ClickerT1_Container.get(), ClickerT1Screen::new);
            MenuScreens.register(Registration.ClickerT2_Container.get(), ClickerT2Screen::new);
            MenuScreens.register(Registration.SensorT1_Container.get(), SensorT1Screen::new);
            MenuScreens.register(Registration.SensorT2_Container.get(), SensorT2Screen::new);
            MenuScreens.register(Registration.DropperT1_Container.get(), DropperT1Screen::new);
            MenuScreens.register(Registration.DropperT2_Container.get(), DropperT2Screen::new);
            MenuScreens.register(Registration.GeneratorT1_Container.get(), GeneratorT1Screen::new);
            MenuScreens.register(Registration.EnergyTransmitter_Container.get(), EnergyTransmitterScreen::new);
            MenuScreens.register(Registration.BlockSwapperT1_Container.get(), BlockSwapperT1Screen::new);
            MenuScreens.register(Registration.BlockSwapperT2_Container.get(), BlockSwapperT2Screen::new);
            MenuScreens.register(Registration.PlayerAccessor_Container.get(), PlayerAccessorScreen::new);
            MenuScreens.register(Registration.ExperienceHolder_Container.get(), ExperienceHolderScreen::new);
            MenuScreens.register(Registration.FluidCollectorT1_Container.get(), FluidCollectorT1Screen::new);
            MenuScreens.register(Registration.FluidCollectorT2_Container.get(), FluidCollectorT2Screen::new);
            MenuScreens.register(Registration.FluidPlacerT1_Container.get(), FluidPlacerT1Screen::new);
            MenuScreens.register(Registration.FluidPlacerT2_Container.get(), FluidPlacerT2Screen::new);
            MenuScreens.register(Registration.GeneratorFluidT1_Container.get(), GeneratorFluidT1Screen::new);
            MenuScreens.register(Registration.InventoryHolder_Container.get(), InventoryHolderScreen::new);
            MenuScreens.register(Registration.ParadoxMachine_Container.get(), ParadoxMachineScreen::new);
            MenuScreens.register(Registration.PotionCanister_Container.get(), PotionCanisterScreen::new);

            //Fluid block render layers
            ItemBlockRenderTypes.setRenderLayer(Registration.REFINED_T2_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.REFINED_T3_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.REFINED_T4_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.UNREFINED_T2_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.UNREFINED_T3_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.UNREFINED_T4_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.PORTAL_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.UNSTABLE_PORTAL_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.TIME_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.XP_FLUID_BLOCK.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.POLYMORPHIC_FLUID_BLOCK.get(), RenderType.translucent());

            //Fluid render layers (for in-world fluid rendering transparency)
            ItemBlockRenderTypes.setRenderLayer(Registration.UNSTABLE_PORTAL_FLUID_SOURCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.UNSTABLE_PORTAL_FLUID_FLOWING.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.TIME_FLUID_SOURCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(Registration.TIME_FLUID_FLOWING.get(), RenderType.translucent());

            //Item Properties
            for (var tool : Registration.TOOLS.getEntries()) {
                registerEnabledToolTextures(tool.get());
            }
            registerEnabledToolTextures(Registration.Pocket_Generator.get());
        });
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(),
                "abilitycooldownoverlay",
                AbilityCooldownOverlay.INSTANCE);
    }

    public static void registerEnabledToolTextures(Item tool) {
        if (tool instanceof ToggleableItem toggleableItem) {
            ItemProperties.register(tool,
                    new ResourceLocation(JustDireThings.MODID, "enabled"), (stack, level, living, id) -> {
                        if (stack.getItem() instanceof PocketGenerator) {
                            if (!toggleableItem.getEnabled(stack)) return 0.0f;
                            IEnergyStorage energyStorage = stack.getCapability(ForgeCapabilities.ENERGY).orElse(null);
                            if (energyStorage == null) return 0.0f;
                            if (energyStorage.getEnergyStored() > 0) return 1.0f;
                            if (!(NBTHelpers.getIntValue(stack, PocketGenerator.COUNTER) > 0)) return 0.0f;
                            return 1.0f;
                        } else
                            return toggleableItem.getEnabled(stack) ? 1.0f : 0.0f;
                    });
        }
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        var bucketColors = new DynamicFluidContainerModel.Colors();
        event.register(bucketColors,
                Registration.REFINED_T2_FLUID_BUCKET.get(),
                Registration.REFINED_T3_FLUID_BUCKET.get(),
                Registration.REFINED_T4_FLUID_BUCKET.get(),
                Registration.UNREFINED_T2_FLUID_BUCKET.get(),
                Registration.UNREFINED_T3_FLUID_BUCKET.get(),
                Registration.UNREFINED_T4_FLUID_BUCKET.get(),
                Registration.PORTAL_FLUID_BUCKET.get(),
                Registration.UNSTABLE_PORTAL_FLUID_BUCKET.get(),
                Registration.TIME_FLUID_BUCKET.get(),
                Registration.XP_FLUID_BUCKET.get(),
                Registration.POLYMORPHIC_FLUID_BUCKET.get()
        );
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.GLITTER.get(), GlitterParticle.Provider::new);
        event.registerSpriteSet(ModParticles.PARADOX.get(), ParadoxParticle.Provider::new);
    }

    @SubscribeEvent
    public static void mrl(ModelEvent.RegisterAdditional e) {
        e.register(new ResourceLocation(JustDireThings.MODID, "item/creaturecatcher_base"));
    }

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(Registration.TimeWand.get(), FluidbarDecorator.INSTANCE);
        event.register(Registration.PortalGun.get(), FluidbarDecorator.INSTANCE);
        event.register(Registration.PortalGunV2.get(), FluidbarDecorator.INSTANCE);
        event.register(Registration.PolymorphicWandV2.get(), FluidbarDecorator.INSTANCE);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        //Register Block Entity Renders
        event.registerBlockEntityRenderer(Registration.GooBlockBE_Tier1.get(), GooBlockRender_Tier1::new);
        event.registerBlockEntityRenderer(Registration.GooBlockBE_Tier2.get(), GooBlockRender_Tier2::new);
        event.registerBlockEntityRenderer(Registration.GooBlockBE_Tier3.get(), GooBlockRender_Tier3::new);
        event.registerBlockEntityRenderer(Registration.GooBlockBE_Tier4.get(), GooBlockRender_Tier4::new);
        event.registerBlockEntityRenderer(Registration.ItemCollectorBE.get(), ItemCollectorRenderer::new);
        event.registerBlockEntityRenderer(Registration.BlockBreakerT2BE.get(), BlockBreakerT2BER::new);
        event.registerBlockEntityRenderer(Registration.BlockPlacerT2BE.get(), BlockPlacerT2BER::new);
        event.registerBlockEntityRenderer(Registration.ClickerT2BE.get(), ClickerT2BER::new);
        event.registerBlockEntityRenderer(Registration.SensorT2BE.get(), SensorT2BER::new);
        event.registerBlockEntityRenderer(Registration.DropperT2BE.get(), DropperT2BER::new);
        event.registerBlockEntityRenderer(Registration.EnergyTransmitterBE.get(), EnergyTransmitterRenderer::new);
        event.registerBlockEntityRenderer(Registration.BlockSwapperT2BE.get(), BlockSwapperT2BER::new);
        event.registerBlockEntityRenderer(Registration.EclipseGateBE.get(), EclipseGateRenderer::new);
        event.registerBlockEntityRenderer(Registration.FluidCollectorT2BE.get(), FluidCollectorT2BER::new);
        event.registerBlockEntityRenderer(Registration.FluidPlacerT2BE.get(), FluidPlacerT2BER::new);
        event.registerBlockEntityRenderer(Registration.ExperienceHolderBE.get(), ExperienceHolderBER::new);
        event.registerBlockEntityRenderer(Registration.InventoryHolderBE.get(), InventoryHolderBER::new);
        event.registerBlockEntityRenderer(Registration.ParadoxMachineBE.get(), ParadoxMachineBER::new);

        //Entities
        event.registerEntityRenderer(Registration.CreatureCatcherEntity.get(), CreatureCatcherEntityRender::new);
        event.registerEntityRenderer(Registration.TimeWandEntity.get(), TimeWandEntityRenderer::new);
        event.registerEntityRenderer(Registration.PortalEntity.get(), PortalEntityRenderer::new);
        event.registerEntityRenderer(Registration.PortalProjectile.get(), ThrownItemRenderer::new);
    }
}
