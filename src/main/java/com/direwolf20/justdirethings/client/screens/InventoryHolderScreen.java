package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.client.screens.basescreens.BaseMachineScreen;
import com.direwolf20.justdirethings.client.screens.standardbuttons.ToggleButtonFactory;
import com.direwolf20.justdirethings.client.screens.widgets.GrayscaleButton;
import com.direwolf20.justdirethings.common.blockentities.InventoryHolderBE;
import com.direwolf20.justdirethings.common.containers.InventoryHolderContainer;
import com.direwolf20.justdirethings.common.containers.slots.InventoryHolderSlot;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.InventoryHolderMoveItemsPayload;
import com.direwolf20.justdirethings.common.network.data.InventoryHolderPayload;
import com.direwolf20.justdirethings.common.network.data.InventoryHolderSaveSlotPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InventoryHolderScreen extends BaseMachineScreen<InventoryHolderContainer> {
    private InventoryHolderBE inventoryHolderBE;
    private boolean compareNBT;
    private boolean filtersOnly;
    private boolean compareCounts;
    private boolean automatedFiltersOnly;
    private boolean automatedCompareCounts;
    private boolean renderPlayer;
    private int renderedSlot;

    public InventoryHolderScreen(InventoryHolderContainer container, Inventory inv, Component name) {
        super(container, inv, name);
        if (container.baseMachineBE instanceof InventoryHolderBE be) {
            this.inventoryHolderBE = be;
            this.compareNBT = be.compareNBT;
            this.filtersOnly = be.filtersOnly;
            this.compareCounts = be.compareCounts;
            this.automatedFiltersOnly = be.automatedFiltersOnly;
            this.automatedCompareCounts = be.automatedCompareCounts;
            this.renderPlayer = be.renderPlayer;
            this.renderedSlot = be.renderedSlot;
        }
    }

    @Override
    public void addTickSpeedButton() {
        // No tick speed for InventoryHolder
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(ToggleButtonFactory.FILTERONLYBUTTON(getGuiLeft() + 134, topSectionTop + 22, filtersOnly, b -> {
            filtersOnly = !filtersOnly;
            ((GrayscaleButton) b).toggleActive();
            saveSettings();
        }));
        addRenderableWidget(ToggleButtonFactory.COMPARENBTBUTTON(getGuiLeft() + 152, topSectionTop + 22, compareNBT, b -> {
            compareNBT = !compareNBT;
            ((GrayscaleButton) b).toggleActive();
            saveSettings();
        }));
        addRenderableWidget(ToggleButtonFactory.COMPARECOUNTSBUTTON(getGuiLeft() + 134, topSectionTop + 4, compareCounts, b -> {
            compareCounts = !compareCounts;
            ((GrayscaleButton) b).toggleActive();
            saveSettings();
        }));
        addRenderableWidget(ToggleButtonFactory.SEND_INV_BUTTON(getGuiLeft() + 134, topSectionTop + 129, b -> {
            PacketHandler.CHANNEL.sendToServer(new InventoryHolderMoveItemsPayload(0));
        }));
        addRenderableWidget(ToggleButtonFactory.PULL_INV_BUTTON(getGuiLeft() + 26, topSectionTop + 129, b -> {
            PacketHandler.CHANNEL.sendToServer(new InventoryHolderMoveItemsPayload(1));
        }));
        addRenderableWidget(ToggleButtonFactory.SWAP_INV_BUTTON(getGuiLeft() + 152, topSectionTop + 129, b -> {
            PacketHandler.CHANNEL.sendToServer(new InventoryHolderMoveItemsPayload(2));
        }));
        addRenderableWidget(ToggleButtonFactory.FILTERONLYBUTTON(getGuiLeft() + 26, topSectionTop + 22, automatedFiltersOnly, b -> {
            automatedFiltersOnly = !automatedFiltersOnly;
            ((GrayscaleButton) b).toggleActive();
            saveSettings();
        }));
        addRenderableWidget(ToggleButtonFactory.COMPARECOUNTSBUTTON(getGuiLeft() + 26, topSectionTop + 4, automatedCompareCounts, b -> {
            automatedCompareCounts = !automatedCompareCounts;
            ((GrayscaleButton) b).toggleActive();
            saveSettings();
        }));
        addRenderableWidget(ToggleButtonFactory.SHOWFAKEPLAYERBUTTON(getGuiLeft() + 8, topSectionTop + 4, renderPlayer, b -> {
            renderPlayer = !renderPlayer;
            ((GrayscaleButton) b).toggleActive();
            saveSettings();
        }));
    }

    @Override
    public void setTopSection() {
        extraWidth = 0;
        extraHeight = 24;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
        if (inventoryHolderBE == null) return;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (Slot slot : container.slots) {
            if (!(slot instanceof InventoryHolderSlot)) continue;
            int slotIdx = slot.getSlotIndex();
            if (slotIdx < 0 || slotIdx >= inventoryHolderBE.filterBasicHandler.getSlots()) continue;
            ItemStack filterStack = inventoryHolderBE.filterBasicHandler.getStackInSlot(slotIdx);
            if (!filterStack.isEmpty() && slot.getItem().isEmpty()) {
                guiGraphics.renderFakeItem(filterStack, leftPos + slot.x, topPos + slot.y);
                guiGraphics.fill(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, 0x80888888);
            }
            if (slotIdx == renderedSlot) {
                guiGraphics.fill(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 1, 0xFFFF0000);
                guiGraphics.fill(leftPos + slot.x, topPos + slot.y + 15, leftPos + slot.x + 16, topPos + slot.y + 16, 0xFFFF0000);
                guiGraphics.fill(leftPos + slot.x, topPos + slot.y, leftPos + slot.x + 1, topPos + slot.y + 16, 0xFFFF0000);
                guiGraphics.fill(leftPos + slot.x + 15, topPos + slot.y, leftPos + slot.x + 16, topPos + slot.y + 16, 0xFFFF0000);
            }
        }
        RenderSystem.disableBlend();
    }

    @Override
    protected void renderInventoryBackground(GuiGraphics guiGraphics, int relX, int relY) {
        // Inventory Holder has an extra armor/offhand row in the lower section, so it needs
        // a taller contour than the standard player inventory background texture provides.
        blitNineSlice(guiGraphics, relX, relY + 74, this.imageWidth, 111);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        Slot slot = this.hoveredSlot;
        if (slot instanceof InventoryHolderSlot && slot.getItem().isEmpty() && inventoryHolderBE != null &&
                !inventoryHolderBE.filterBasicHandler.getStackInSlot(slot.getSlotIndex()).isEmpty()) {
            ItemStack itemstack = inventoryHolderBE.filterBasicHandler.getStackInSlot(slot.getSlotIndex());
            guiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), x, y);
        } else {
            super.renderTooltip(guiGraphics, x, y);
        }
    }

    @Override
    public void saveSettings() {
        super.saveSettings();
        PacketHandler.CHANNEL.sendToServer(new InventoryHolderPayload(compareNBT, filtersOnly, compareCounts,
                automatedFiltersOnly, automatedCompareCounts, renderPlayer, renderedSlot));
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (btn == 0 && Screen.hasControlDown() && hoveredSlot instanceof InventoryHolderSlot) {
            if (Screen.hasShiftDown()) {
                if (hoveredSlot.getSlotIndex() >= 27 && hoveredSlot.getSlotIndex() <= 35) {
                    renderedSlot = hoveredSlot.getSlotIndex();
                    saveSettings();
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                    return true;
                }
            } else {
                PacketHandler.CHANNEL.sendToServer(new InventoryHolderSaveSlotPayload(hoveredSlot.getSlotIndex()));
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        return super.mouseClicked(x, y, btn);
    }
}
