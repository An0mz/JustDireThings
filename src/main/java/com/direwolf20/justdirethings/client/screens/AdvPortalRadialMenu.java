package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.KeyBindings;
import com.direwolf20.justdirethings.client.screens.widgets.BaseButton;
import com.direwolf20.justdirethings.client.screens.widgets.GrayscaleButton;
import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.PortalGunFavoriteChangePayload;
import com.direwolf20.justdirethings.common.network.data.PortalGunFavoritePayload;
import com.direwolf20.justdirethings.util.NBTHelpers;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class AdvPortalRadialMenu extends Screen {
	private static final int SEGMENTS = PortalGunV2.MAX_FAVORITES;

	private static final ResourceLocation ADD_TEX = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/buttons/add.png");
	private static final ResourceLocation REMOVE_TEX = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/buttons/remove.png");
	private static final ResourceLocation EDIT_TEX = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/buttons/matchnbttrue.png");
	private static final ResourceLocation STAYOPEN_TEX = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/buttons/area.png");

	private int slotSelected;
	private ItemStack portalGun;
	private boolean staysOpen;

	public AdvPortalRadialMenu(ItemStack stack) {
		super(Component.literal(""));
		this.portalGun = stack;
		this.slotSelected = PortalGunV2.getFavoritePosition(stack);
		this.staysOpen = PortalGunV2.getStayOpen(stack);
	}

	@Override
	public void init() {
		int startX = this.width / 2 - 82;
		int startY = this.height / 2 - 52;

		for (int i = 0; i < SEGMENTS; i++) {
			int index = i;
			int col = i % 4;
			int row = i / 4;
			int x = startX + (col * 54);
			int y = startY + (row * 22);
			addRenderableWidget(Button.builder(getFavoriteLabel(index), button -> {
				slotSelected = index;
				saveFavorite();
			}).pos(x, y).size(50, 20).build());
		}

		addRenderableWidget(new GrayscaleButton(this.width / 2 - 110, this.height / 2 + 30, 16, 16, ADD_TEX,
				Component.translatable("justdirethings.screen.add_favorite"), true, button -> addFavorite()));

		addRenderableWidget(new GrayscaleButton(this.width / 2 - 86, this.height / 2 + 30, 16, 16, REMOVE_TEX,
				Component.translatable("justdirethings.screen.remove_favorite"), true, button -> removeFavorite()));

		addRenderableWidget(new GrayscaleButton(this.width / 2 - 62, this.height / 2 + 30, 16, 16, EDIT_TEX,
				Component.translatable("justdirethings.screen.edit_favorite"), true, button -> editFavorite()));

		addRenderableWidget(new GrayscaleButton(this.width / 2 - 38, this.height / 2 + 30, 16, 16, STAYOPEN_TEX,
				Component.translatable("justdirethings.screen.stay_open"), staysOpen, button -> {
					staysOpen = !staysOpen;
					saveFavorite();
					((GrayscaleButton) button).toggleActive();
				}));
	}

	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.portalGun = PortalGunV2.getPortalGunv2(Minecraft.getInstance().player);
		if (this.portalGun.isEmpty()) {
			onClose();
			return;
		}
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		guiGraphics.drawCenteredString(this.font, Component.translatable("justdirethings.screen.edit_favorite"),
				this.width / 2, this.height / 2 - 70, 0xFFFFFF);

		NBTHelpers.PortalDestination favorite = PortalGunV2.getFavorite(portalGun, slotSelected);
		if (favorite != null) {
			String dim = favorite.dimension().location().toString();
			String coords = String.format("(%.1f, %.1f, %.1f)", favorite.position().x, favorite.position().y,
					favorite.position().z);
			guiGraphics.drawCenteredString(this.font, favorite.name(), this.width / 2, this.height / 2 + 58, 0xFFFFFF);
			guiGraphics.drawCenteredString(this.font, dim, this.width / 2, this.height / 2 + 68, 0xBBBBBB);
			guiGraphics.drawCenteredString(this.font, coords, this.width / 2, this.height / 2 + 78, 0xBBBBBB);
		}

		renderTooltip(guiGraphics, mouseX, mouseY);
	}

	@Override
	public void tick() {
		if (!staysOpen && !InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),
				KeyBindings.toggleTool.getKey().getValue())) {
			onClose();
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (staysOpen && (keyCode == 256 || keyCode == KeyBindings.toggleTool.getKey().getValue())) {
			onClose();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
		for (Renderable renderable : this.renderables) {
			if (renderable instanceof BaseButton button && !button.getLocalization(x, y).equals(Component.empty())) {
				guiGraphics.renderTooltip(font, button.getLocalization(), x, y);
			}
		}
	}

	private void saveFavorite() {
		PacketHandler.CHANNEL.sendToServer(new PortalGunFavoritePayload(slotSelected, staysOpen));
	}

	private void addFavorite() {
		PacketHandler.CHANNEL
				.sendToServer(new PortalGunFavoriteChangePayload(slotSelected, true, "UNNAMED", false, Vec3.ZERO));
	}

	private void removeFavorite() {
		PacketHandler.CHANNEL
				.sendToServer(new PortalGunFavoriteChangePayload(slotSelected, false, "", false, Vec3.ZERO));
	}

	private void editFavorite() {
		Minecraft.getInstance().setScreen(new AdvPortalEditMenu(portalGun, slotSelected));
	}

	private Component getFavoriteLabel(int slot) {
		NBTHelpers.PortalDestination favorite = PortalGunV2.getFavorite(portalGun, slot);
		String name = favorite == null ? "Empty" : favorite.name();
		return Component.literal((slot + 1) + ": " + name);
	}
}
