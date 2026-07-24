package com.direwolf20.justdirethings.client.screens;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.client.screens.basescreens.BaseScreen;
import com.direwolf20.justdirethings.common.containers.PotionCanisterContainer;
import com.direwolf20.justdirethings.common.items.PotionCanister;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.direwolf20.justdirethings.util.MiscTools;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("removal")
public class PotionCanisterScreen extends BaseScreen<PotionCanisterContainer> {
	private static final ResourceLocation GUI = new ResourceLocation(JustDireThings.MODID,
			"textures/gui/fuelcanister.png");
	protected final ResourceLocation FLUIDBAR = new ResourceLocation(JustDireThings.MODID, "textures/gui/fluidbar.png");

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
	}

	@Override
	protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
		super.renderTooltip(pGuiGraphics, pX, pY);
		fluidBarTooltip(pGuiGraphics, pX, pY);
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderTexture(0, GUI);
		int relX = (this.width - this.imageWidth) / 2;
		int relY = (this.height - this.imageHeight) / 2;
		guiGraphics.blit(GUI, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

		int offset = 5;
		guiGraphics.blit(FLUIDBAR, getGuiLeft() + offset, getGuiTop() + 5, 0, 0, 18, 72, 36, 72);
		int maxMB = PotionCanister.getMaxMB(), height = 70;
		if (maxMB > 0) {
			int remaining = (PotionCanister.getPotionAmount(potionCanister) * height) / maxMB;
			renderFluid(guiGraphics, getGuiLeft() + offset + 1, getGuiTop() + 5 + 72 - 1, 16, remaining);
		}
		guiGraphics.blit(FLUIDBAR, getGuiLeft() + offset, getGuiTop() + 5, 18, 0, 18, 72, 36, 72);
	}

	public void renderFluid(GuiGraphics guiGraphics, int startX, int startY, int width, int height) {
		Potion potion = PotionCanister.getStoredPotion(potionCanister);
		if (potion == Potions.EMPTY || height <= 0)
			return;

		ResourceLocation fluidStill = IClientFluidTypeExtensions.of(Fluids.WATER).getStillTexture();
		TextureAtlasSprite fluidStillSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
				.apply(fluidStill);
		int fluidColor = PotionUtils.getColor(potion);

		float red = (float) (fluidColor >> 16 & 255) / 255.0F;
		float green = (float) (fluidColor >> 8 & 255) / 255.0F;
		float blue = (float) (fluidColor & 255) / 255.0F;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

		PoseStack poseStack = guiGraphics.pose();
		poseStack.pushPose();
		RenderSystem.setShaderColor(red, green, blue, 1.0f);

		int zLevel = 0;
		float uMin = fluidStillSprite.getU0();
		float uMax = fluidStillSprite.getU1();
		float vMin = fluidStillSprite.getV0();
		float vMax = fluidStillSprite.getV1();
		int textureWidth = fluidStillSprite.contents().width();
		int textureHeight = fluidStillSprite.contents().height();

		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder vertexBuffer = tesselator.getBuilder();
		vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

		int yOffset = 0;
		while (yOffset < height) {
			int drawHeight = Math.min(textureHeight, height - yOffset);
			int drawY = startY - yOffset - drawHeight; // Adjust for bottom-to-top drawing

			float vMaxAdjusted = vMin + (vMax - vMin) * ((float) drawHeight / textureHeight);

			int xOffset = 0;
			while (xOffset < width) {
				int drawWidth = Math.min(textureWidth, width - xOffset);

				float uMaxAdjusted = uMin + (uMax - uMin) * ((float) drawWidth / textureWidth);

				vertexBuffer.vertex(poseStack.last().pose(), startX + xOffset, drawY + drawHeight, zLevel)
						.uv(uMin, vMaxAdjusted).endVertex();
				vertexBuffer.vertex(poseStack.last().pose(), startX + xOffset + drawWidth, drawY + drawHeight, zLevel)
						.uv(uMaxAdjusted, vMaxAdjusted).endVertex();
				vertexBuffer.vertex(poseStack.last().pose(), startX + xOffset + drawWidth, drawY, zLevel)
						.uv(uMaxAdjusted, vMin).endVertex();
				vertexBuffer.vertex(poseStack.last().pose(), startX + xOffset, drawY, zLevel).uv(uMin, vMin)
						.endVertex();

				xOffset += drawWidth;
			}
			yOffset += drawHeight;
		}

		tesselator.end();
		poseStack.popPose();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void fluidBarTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
		Potion potion = PotionCanister.getStoredPotion(potionCanister);
		int potionAmt = PotionCanister.getPotionAmount(potionCanister);
		if (potionAmt == 0 || potion == Potions.EMPTY)
			return;
		if (MiscTools.inBounds(getGuiLeft() + 5, getGuiTop() + 5, 18, 72, pX, pY)) {
			List<Component> components = new ArrayList<>();
			components.add(Component.literal(
					MagicHelpers.formatted(potionAmt) + "/" + MagicHelpers.formatted(PotionCanister.getMaxMB())));
			PotionUtils.addPotionTooltip(potion.getEffects(), components, 1.0F);
			pGuiGraphics.renderTooltip(font, components, java.util.Optional.empty(), pX, pY);
		}
	}

	@Override
	protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
	}

	private static MutableComponent getTrans(String key, Object... args) {
		return Component.translatable(JustDireThings.MODID + "." + key, args);
	}
}
