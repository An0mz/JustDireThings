package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.containers.PotionCanisterContainer;
import com.direwolf20.justdirethings.common.items.PotionCanister;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

import java.awt.*;

public class PotionCanisterScreen extends AbstractContainerScreen<PotionCanisterContainer> {
	private static final ResourceLocation GUI = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/fuelcanister.png");

	protected final PotionCanisterContainer container;
	private final ItemStack potionCanister;

	public PotionCanisterScreen(PotionCanisterContainer container, Inventory inv, Component name) {
		super(container, inv, name);
		this.container = container;
		this.potionCanister = container.potionCanister;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.renderTooltip(guiGraphics, mouseX, mouseY);

		Potion potion = PotionCanister.getStoredPotion(potionCanister);
		int amt = PotionCanister.getPotionAmount(potionCanister);

		if (potion != Potions.EMPTY) {
			int color = PotionUtils.getColor(potion);
			MutableComponent potionName = Component.literal(potion.getName("item.minecraft.potion.effect."))
					.withStyle(s -> s.withColor(color));
			guiGraphics.drawString(font, potionName,
					this.getGuiLeft() + this.imageWidth / 2 - font.width(potionName) / 2, getGuiTop() + 5,
					Color.DARK_GRAY.getRGB(), false);
		}

		MutableComponent amtMsg = Component
				.literal(MagicHelpers.formatted(amt) + "/" + MagicHelpers.formatted(PotionCanister.getMaxMB()) + " mB");
		guiGraphics.drawString(font, amtMsg, this.getGuiLeft() + this.imageWidth / 2 - font.width(amtMsg) / 2,
				getGuiTop() + 15, Color.DARK_GRAY.getRGB(), false);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, GUI);
		int relX = (this.width - this.imageWidth) / 2;
		int relY = (this.height - this.imageHeight) / 2;
		guiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int key, int scanCode, int modifiers) {
		InputConstants.Key mouseKey = InputConstants.getKey(key, scanCode);
		if (key == 256 || minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
			onClose();
			return true;
		}
		return super.keyPressed(key, scanCode, modifiers);
	}

	private static MutableComponent getTrans(String key, Object... args) {
		return Component.translatable(JustDireThings.MODID + "." + key, args);
	}
}
