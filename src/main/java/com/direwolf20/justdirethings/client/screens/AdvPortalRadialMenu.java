/**
 * Adapted from code written by Vazkii for the PSI mod: https://github.com/Vazkii/Psi
 * Psi is Open Source and distributed under the Psi License: http://psi.vazkii.us/license.php
 */
package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.KeyBindings;
import com.direwolf20.justdirethings.client.renderers.OurRenderTypes;
import com.direwolf20.justdirethings.client.screens.widgets.GrayscaleButton;
import com.direwolf20.justdirethings.client.screens.widgets.BaseButton;
import com.direwolf20.justdirethings.common.items.PortalGunV2;
import com.direwolf20.justdirethings.common.network.PacketHandler;
import com.direwolf20.justdirethings.common.network.data.PortalGunFavoriteChangePayload;
import com.direwolf20.justdirethings.common.network.data.PortalGunFavoritePayload;
import com.direwolf20.justdirethings.util.NBTHelpers;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.awt.*;

@SuppressWarnings("removal")
public class AdvPortalRadialMenu extends Screen {

	private static final ResourceLocation ADD_TEX = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/buttons/add.png");
	private static final ResourceLocation REMOVE_TEX = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/buttons/remove.png");
	private static final ResourceLocation EDIT_TEX = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/buttons/matchnbttrue.png");
	private static final ResourceLocation STAYOPEN_TEX = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/buttons/area.png");

	private static final int SEGMENTS = PortalGunV2.MAX_FAVORITES;
	private static final int RADIUS_MIN = 26;
	private static final int RADIUS_MAX = 120;

	private int timeIn = 0;
	private int slotHovered = -1;
	private int slotSelected = 0;
	private ItemStack portalGun;
	private boolean staysOpen = false;

	public AdvPortalRadialMenu(ItemStack stack) {
		super(Component.literal(""));
		this.portalGun = stack;
		this.slotSelected = PortalGunV2.getFavoritePosition(portalGun);
		this.staysOpen = PortalGunV2.getStayOpen(portalGun);
	}

	private static float mouseAngle(int x, int y, int mx, int my) {
		Vector2f base = new Vector2f(1f, 0f);
		Vector2f mouse = new Vector2f(mx - x, my - y);
		float ang = (float) (Math.acos(base.dot(mouse) / (base.length() * mouse.length())) * (180f / Math.PI));
		return my < y ? 360f - ang : ang;
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics) {
	}

	@Override
	public void init() {
		addRenderableWidget(new GrayscaleButton(width / 2 - 150, height / 2 - 20, 16, 16, ADD_TEX,
				Component.translatable("justdirethings.screen.add_favorite"), true, b -> addFavorite()));
		addRenderableWidget(new GrayscaleButton(width / 2 + 140, height / 2 - 20, 16, 16, REMOVE_TEX,
				Component.translatable("justdirethings.screen.remove_favorite"), true, b -> removeFavorite()));
		addRenderableWidget(new GrayscaleButton(width / 2 - 150, height / 2 + 20, 16, 16, EDIT_TEX,
				Component.translatable("justdirethings.screen.edit_favorite"), true, b -> editFavorite()));
		addRenderableWidget(new GrayscaleButton(width / 2 + 140, height / 2 + 20, 16, 16, STAYOPEN_TEX,
				Component.translatable("justdirethings.screen.stay_open"), staysOpen, b -> {
					staysOpen = !staysOpen;
					saveFavorite();
					((GrayscaleButton) b).toggleActive();
				}));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mx, int my, float partialTicks) {
		portalGun = PortalGunV2.getPortalGunv2(Minecraft.getInstance().player);
		if (portalGun.isEmpty()) {
			onClose();
			return;
		}

		PoseStack matrices = guiGraphics.pose();
		float speedOfButtonGrowth = 5f;
		float fract = Math.min(speedOfButtonGrowth, this.timeIn + partialTicks) / speedOfButtonGrowth;
		int x = this.width / 2;
		int y = this.height / 2;

		boolean inRange = isInRange(mx, my);

		matrices.pushPose();
		matrices.translate((1 - fract) * x, (1 - fract) * y, 0);
		matrices.scale(fract, fract, fract);
		super.render(guiGraphics, mx, my, partialTicks);
		matrices.popPose();

		float angle = mouseAngle(x, y, mx, my);
		float totalDeg = 0;
		float degPer = 360f / SEGMENTS;

		for (int seg = 0; seg < SEGMENTS; seg++) {
			NBTHelpers.PortalDestination favorite = PortalGunV2.getFavorite(portalGun, seg);
			String favoriteName = favorite != null ? favorite.name() : "Empty";
			String dimension = favorite != null ? favorite.dimension().location().getPath() : "";
			String coordinates = favorite != null
					? String.format("(%d, %d, %d)", (int) favorite.position().x, (int) favorite.position().y,
							(int) favorite.position().z)
					: "";

			boolean mouseInSector = isCursorInSlice(angle, totalDeg, degPer, inRange);
			float radius = Math.max(0f,
					Math.min((this.timeIn + partialTicks - seg / (float) SEGMENTS) * 25f, RADIUS_MAX));

			float gs = seg % 2 == 0 ? 0.35f : 0.25f;
			float r = gs, g = gs, b = gs, a = 0.4f;
			if (mouseInSector) {
				this.slotHovered = seg;
				r = g = b = 1f;
			}
			if (seg == slotSelected) {
				r = 1f;
				g = 1f;
				b = 0f;
				a = 0.6f;
			}

			MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
			VertexConsumer buffer = bufferSource.getBuffer(OurRenderTypes.TRIANGLE_STRIP);
			Matrix4f pose = matrices.last().pose();

			for (float i = degPer; i >= 0; i--) {
				float rad = (float) ((i + totalDeg) / 180f * Math.PI);
				float xp = (float) (x + Math.cos(rad) * radius);
				float yp = (float) (y + Math.sin(rad) * radius);
				float xi = (float) (x + Math.cos(rad) * radius / 2.3f);
				float yi = (float) (y + Math.sin(rad) * radius / 2.3f);
				buffer.vertex(pose, xi, yi, 0).color(r, g, b, a).endVertex();
				buffer.vertex(pose, xp, yp, 0).color(r, g, b, a).endVertex();
			}

			bufferSource.endBatch(OurRenderTypes.TRIANGLE_STRIP);
			totalDeg += degPer;

			float nameAngle = (totalDeg - degPer / 2f) * (float) Math.PI / 180f;
			float nameX = x + (float) (Math.cos(nameAngle) * (RADIUS_MAX / 1.4));
			float nameY = y + (float) (Math.sin(nameAngle) * (RADIUS_MAX / 1.4));
			int textWidth = this.font.width(favoriteName);
			int dimWidth = this.font.width(dimension);
			int coordWidth = this.font.width(coordinates);

			matrices.pushPose();
			matrices.translate(nameX, nameY, 0);
			matrices.scale(0.85f, 0.85f, 0.85f);
			if (nameAngle > Math.PI / 2 && nameAngle < 3 * Math.PI / 2) {
				matrices.mulPose(Axis.ZP.rotation(nameAngle + (float) Math.PI));
			} else {
				matrices.mulPose(Axis.ZP.rotation(nameAngle));
			}
			guiGraphics.drawString(this.font, favoriteName, -textWidth / 2, -15, Color.WHITE.getRGB());
			matrices.popPose();

			matrices.pushPose();
			matrices.translate(nameX, nameY, 0);
			matrices.scale(0.7f, 0.7f, 0.7f);
			if (nameAngle > Math.PI / 2 && nameAngle < 3 * Math.PI / 2) {
				matrices.mulPose(Axis.ZP.rotation(nameAngle + (float) Math.PI));
			} else {
				matrices.mulPose(Axis.ZP.rotation(nameAngle));
			}
			guiGraphics.drawString(this.font, dimension, -dimWidth / 2, -5, Color.LIGHT_GRAY.getRGB());
			guiGraphics.drawString(this.font, coordinates, -coordWidth / 2, 10, Color.LIGHT_GRAY.getRGB());
			matrices.popPose();
		}

		renderTooltip(guiGraphics, mx, my);
	}

	private boolean isCursorInSlice(float angle, float totalDeg, float degPer, boolean inRange) {
		return inRange && angle > totalDeg && angle < totalDeg + degPer;
	}

	public boolean isInRange(double mouseX, double mouseY) {
		int x = this.width / 2;
		int y = this.height / 2;
		double dist = new Vec3(x, y, 0).distanceTo(new Vec3(mouseX, mouseY, 0));
		return dist > RADIUS_MIN && dist < RADIUS_MAX;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		if (isInRange(mouseX, mouseY))
			saveFavorite();
		return super.mouseClicked(mouseX, mouseY, mouseButton);
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
	public void tick() {
		if (!staysOpen && !InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(),
				KeyBindings.toggleTool.getKey().getValue())) {
			onClose();
		}
		this.timeIn++;
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
		slotSelected = slotHovered;
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

	private static class Vector2f {
		final float x, y;

		Vector2f(float x, float y) {
			this.x = x;
			this.y = y;
		}

		float dot(Vector2f v) {
			return this.x * v.x + this.y * v.y;
		}

		float length() {
			return (float) Math.sqrt(this.x * this.x + this.y * this.y);
		}
	}
}
