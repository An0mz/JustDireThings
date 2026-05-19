package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.client.KeyBindings;
import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.PortalGunFavoriteChangePayload;
import com.direwolf20.justdirethings.util.MiscHelpers;
import com.direwolf20.justdirethings.util.NBTHelpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class AdvPortalEditMenu extends Screen {
	private final int slotSelected;
	private int ticksOpened;
	private final ItemStack portalGun;

	private EditBox nameField;
	private EditBox xPos;
	private EditBox yPos;
	private EditBox zPos;

	protected AdvPortalEditMenu(ItemStack itemStack, int favoritePosition) {
		super(Component.literal(""));
		this.portalGun = itemStack;
		this.slotSelected = favoritePosition;
	}

	@Override
	public void init() {
		super.init();

		this.nameField = new EditBox(this.font, width / 2 - 75, height / 2 - 34, 200, this.font.lineHeight + 3,
				Component.empty());
		this.xPos = new EditBox(this.font, width / 2 - 75, height / 2 - 20, 60, this.font.lineHeight + 3,
				Component.empty());
		this.yPos = new EditBox(this.font, width / 2 - 5, height / 2 - 20, 60, this.font.lineHeight + 3,
				Component.empty());
		this.zPos = new EditBox(this.font, width / 2 + 65, height / 2 - 20, 60, this.font.lineHeight + 3,
				Component.empty());

		updateFields();

		this.nameField.setMaxLength(15);
		addRenderableWidget(this.nameField);

		this.xPos.setEditable(false);
		this.yPos.setEditable(false);
		this.zPos.setEditable(false);
		addRenderableWidget(this.xPos);
		addRenderableWidget(this.yPos);
		addRenderableWidget(this.zPos);

		addRenderableWidget(
				Button.builder(Component.translatable("justdirethings.screen.save_close"), b -> saveFavorite())
						.pos(width / 2 - 75, height / 2).size(120, 16).build());

		addRenderableWidget(Button.builder(Component.translatable("justdirethings.screen.cancel"), b -> onClose())
				.pos(width / 2 + 60, height / 2).size(65, 16).build());
	}

	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.drawCenteredString(this.font, Component.translatable("justdirethings.screen.edit_favorite"),
				width / 2, height / 2 - 52, 0xFFFFFF);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (ticksOpened < 20 && keyCode == KeyBindings.toggleTool.getKey().getValue()) {
			return true;
		}
		if (keyCode == 256) {
			onClose();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void tick() {
		ticksOpened++;
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void saveFavorite() {
		NBTHelpers.PortalDestination destination = PortalGunV2.getFavorite(portalGun, slotSelected);
		Vec3 coords;
		if (destination == null) {
			Player player = Minecraft.getInstance().player;
			coords = player == null ? Vec3.ZERO : player.position();
		} else {
			coords = destination.position();
		}

		PacketHandler.CHANNEL.sendToServer(
				new PortalGunFavoriteChangePayload(slotSelected, true, nameField.getValue(), true, coords));
		onClose();
	}

	private void updateFields() {
		NBTHelpers.PortalDestination destination = PortalGunV2.getFavorite(portalGun, slotSelected);
		Player player = Minecraft.getInstance().player;

		if (destination == null && player != null) {
			Vec3 position = player.position();
			Direction facing = MiscHelpers.getFacingDirection(player);
			destination = new NBTHelpers.PortalDestination(player.level().dimension(), position, facing, "UNNAMED");
		}

		if (destination == null) {
			this.nameField.setValue("UNNAMED");
			this.xPos.setValue("0.00");
			this.yPos.setValue("0.00");
			this.zPos.setValue("0.00");
			return;
		}

		this.nameField.setValue(destination.name());
		this.xPos.setValue(String.format("%.2f", destination.position().x));
		this.yPos.setValue(String.format("%.2f", destination.position().y));
		this.zPos.setValue(String.format("%.2f", destination.position().z));
	}
}
