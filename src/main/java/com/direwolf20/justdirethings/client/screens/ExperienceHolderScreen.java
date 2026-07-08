package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.client.screens.basescreens.BaseMachineScreen;
import com.direwolf20.justdirethings.client.screens.standardbuttons.ToggleButtonFactory;
import com.direwolf20.justdirethings.client.screens.widgets.GrayscaleButton;
import com.direwolf20.justdirethings.client.screens.widgets.NumberButton;
import com.direwolf20.justdirethings.client.screens.widgets.ToggleButton;
import com.direwolf20.justdirethings.common.blockentities.ExperienceHolderBE;
import com.direwolf20.justdirethings.common.containers.ExperienceHolderContainer;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.ExperienceHolderPayload;
import com.direwolf20.justdirethings.common.network.data.ExperienceHolderSettingsPayload;
import com.direwolf20.justdirethings.util.ExperienceUtils;
import com.direwolf20.justdirethings.util.MiscHelpers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@SuppressWarnings("removal")
public class ExperienceHolderScreen extends BaseMachineScreen<ExperienceHolderContainer> {

	protected ExperienceHolderBE experienceHolderBE;
	private int targetExp;
	private boolean ownerOnly;
	private boolean collectExp;
	private boolean showParticles = true;

	private static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");

	public ExperienceHolderScreen(ExperienceHolderContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		if (baseMachineBE instanceof ExperienceHolderBE be) {
			this.experienceHolderBE = be;
			this.targetExp = be.targetExp;
			this.ownerOnly = be.ownerOnly;
			this.collectExp = be.collectExp;
			this.showParticles = be.showParticles;
		}
	}

	@Override
	public void init() {
		super.init();
		addRenderableWidget(ToggleButtonFactory.STOREEXPBUTTON(topSectionLeft + (topSectionWidth / 2) + 15,
				topSectionTop + 62, true, b -> {
					int amt = 1;
					if (Screen.hasControlDown())
						amt = -1;
					else if (Screen.hasShiftDown())
						amt = amt * 10;
					PacketHandler.CHANNEL.sendToServer(new ExperienceHolderPayload(true, amt));
				}));
		addRenderableWidget(ToggleButtonFactory.EXTRACTEXPBUTTON(topSectionLeft + (topSectionWidth / 2) - 15 - 18,
				topSectionTop + 62, true, b -> {
					int amt = 1;
					if (Screen.hasControlDown())
						amt = -1;
					else if (Screen.hasShiftDown())
						amt = amt * 10;
					PacketHandler.CHANNEL.sendToServer(new ExperienceHolderPayload(false, amt));
				}));
		addRenderableWidget(ToggleButtonFactory.TARGETEXPBUTTON(topSectionLeft + (topSectionWidth / 2) - 15 - 42,
				topSectionTop + 64, targetExp, b -> {
					targetExp = ((NumberButton) b).getValue();
					saveExperienceSettings();
				}));
		addRenderableWidget(ToggleButtonFactory.OWNERONLYBUTTON(topSectionLeft + (topSectionWidth / 2) - 15 - 60,
				topSectionTop + 62, ownerOnly, b -> {
					ownerOnly = !ownerOnly;
					((GrayscaleButton) b).toggleActive();
					saveExperienceSettings();
				}));
		addRenderableWidget(ToggleButtonFactory.COLLECTEXPBUTTON(topSectionLeft + (topSectionWidth / 2) + 15,
				topSectionTop + 42, collectExp, b -> {
					collectExp = !collectExp;
					((GrayscaleButton) b).toggleActive();
					saveExperienceSettings();
				}));
		addRenderableWidget(ToggleButtonFactory.SHOWPARTICLESBUTTON(topSectionLeft + (topSectionWidth / 2) + 31,
				topSectionTop + 42, showParticles, b -> {
					showParticles = !showParticles;
					((GrayscaleButton) b).toggleActive();
					saveExperienceSettings();
				}));
	}

	@Override
	public void addRedstoneButtons() {
		addRenderableWidget(ToggleButtonFactory.REDSTONEBUTTON(topSectionLeft + (topSectionWidth / 2) - 15,
				topSectionTop + 42, redstoneMode.ordinal(), b -> {
					redstoneMode = MiscHelpers.RedstoneMode.values()[((ToggleButton) b).getTexturePosition()];
					saveExperienceSettings();
				}));
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		super.renderBg(guiGraphics, partialTicks, mouseX, mouseY);
		if (experienceHolderBE == null)
			return;
		renderXPBar(guiGraphics);
	}

	private void renderXPBar(GuiGraphics guiGraphics) {
		int barX = topSectionLeft + (topSectionWidth / 2) - (182 / 2);
		int barY = topSectionTop + topSectionHeight - 15;

		guiGraphics.blit(ICONS, barX, barY, 0, 64, 182, 5);
		int partialAmount = (int) (ExperienceUtils.getProgressToNextLevel(experienceHolderBE.exp) * 183.0F);
		if (partialAmount > 0) {
			guiGraphics.blit(ICONS, barX, barY, 0, 69, partialAmount, 5);
		}

		String levelStr = String.valueOf(ExperienceUtils.getLevelFromTotalExperience(experienceHolderBE.exp));
		int centerX = topSectionLeft + (topSectionWidth / 2);
		int j = centerX - font.width(levelStr) / 2;
		int k = topSectionTop + 62 + 9 - font.lineHeight / 2;
		guiGraphics.drawString(font, levelStr, j + 1, k, 0, false);
		guiGraphics.drawString(font, levelStr, j - 1, k, 0, false);
		guiGraphics.drawString(font, levelStr, j, k + 1, 0, false);
		guiGraphics.drawString(font, levelStr, j, k - 1, 0, false);
		guiGraphics.drawString(font, levelStr, j, k, 8453920, false);
	}

	public void saveExperienceSettings() {
		saveSettings();
		PacketHandler.CHANNEL
				.sendToServer(new ExperienceHolderSettingsPayload(targetExp, ownerOnly, collectExp, showParticles));
	}
}
