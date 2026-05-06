package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.client.screens.basescreens.BaseMachineScreen;
import com.direwolf20.justdirethings.client.screens.standardbuttons.ToggleButtonFactory;
import com.direwolf20.justdirethings.client.screens.widgets.ToggleButton;
import com.direwolf20.justdirethings.common.blockentities.GeneratorFluidT1BE;
import com.direwolf20.justdirethings.common.blockentities.basebe.PoweredMachineBE;
import com.direwolf20.justdirethings.common.containers.GeneratorFluidT1Container;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.MiscTools;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.Arrays;

public class GeneratorFluidT1Screen extends BaseMachineScreen<GeneratorFluidT1Container> {
    protected GeneratorFluidT1Container generatorContainer;
    protected GeneratorFluidT1BE generatorBE;

    public GeneratorFluidT1Screen(GeneratorFluidT1Container container, Inventory inv, Component name) {
        super(container, inv, name);
        this.generatorContainer = container;
        if (container.baseMachineBE instanceof GeneratorFluidT1BE be) {
            this.generatorBE = be;
        }
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void setTopSection() {
        extraWidth = 0;
        extraHeight = 0;
    }

    @Override
    public void addTickSpeedButton() {
        // Generator has no tick speed control
    }

    @Override
    public void addRedstoneButtons() {
        addRenderableWidget(ToggleButtonFactory.REDSTONEBUTTON(getGuiLeft() + 104, topSectionTop + 38, redstoneMode.ordinal(), b -> {
            redstoneMode = MiscHelpers.RedstoneMode.values()[((ToggleButton) b).getTexturePosition()];
            saveSettings();
        }));
    }

    @Override
    public void powerBarTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        if (baseMachineBE instanceof PoweredMachineBE poweredMachineBE && generatorBE != null) {
            if (MiscTools.inBounds(topSectionLeft + 5, topSectionTop + 5, 18, 72, pX, pY)) {
                if (hasShiftDown())
                    pGuiGraphics.renderTooltip(font, Language.getInstance().getVisualOrder(Arrays.asList(
                            Component.translatable("justdirethings.screen.energy", MagicHelpers.formatted(this.generatorContainer.getEnergy()), MagicHelpers.formatted(poweredMachineBE.getMaxEnergy())),
                            Component.translatable("justdirethings.screen.fepertick", MagicHelpers.formatted(generatorBE.getFePerFuelTick()))
                    )), pX, pY);
                else
                    pGuiGraphics.renderTooltip(font, Language.getInstance().getVisualOrder(Arrays.asList(
                            Component.translatable("justdirethings.screen.energy", MagicHelpers.withSuffix(this.generatorContainer.getEnergy()), MagicHelpers.withSuffix(poweredMachineBE.getMaxEnergy())),
                            Component.translatable("justdirethings.screen.fepertick", MagicHelpers.formatted(generatorBE.getFePerFuelTick()))
                    )), pX, pY);
            }
        }
    }
}
