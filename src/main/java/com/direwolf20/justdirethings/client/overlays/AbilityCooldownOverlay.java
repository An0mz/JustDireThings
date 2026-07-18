package com.direwolf20.justdirethings.client.overlays;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.AbilityParams;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

@SuppressWarnings("removal")
public class AbilityCooldownOverlay implements IGuiOverlay {
	public static final AbilityCooldownOverlay INSTANCE = new AbilityCooldownOverlay();
	private static final EquipmentSlot[] EQUIPMENT_ORDER = {EquipmentSlot.HEAD, EquipmentSlot.CHEST,
			EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND};

	@Override
	public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui)
			return;
		Player player = mc.player;
		if (player == null || mc.level == null)
			return;
		long gameTick = mc.level.getGameTime();
		int renderedIcons = 0;

		for (EquipmentSlot slot : EQUIPMENT_ORDER) {
			ItemStack itemStack = player.getItemBySlot(slot);
			if (!(itemStack.getItem() instanceof ToggleableTool toggleableTool))
				continue;
			for (ToggleableTool.CooldownEntry entry : ToggleableTool.getAllCooldowns(itemStack, gameTick)) {
				Ability ability = entry.ability();
				if (!ability.hasCooldownIcon())
					continue;
				ResourceLocation icon = ability.getCooldownIcon();
				AbilityParams abilityParams = toggleableTool.getAbilityParams(ability);
				int xPosition = screenWidth / 2 - 91 + ((renderedIcons % 7) * 11);
				int yPosition = screenHeight - gui.leftHeight - 30 - ((renderedIcons / 7) * 11);
				if (entry.active()) {
					int activeMax = abilityParams.activeCooldown;
					int iconHeight = activeMax > 0 ? ((entry.remaining() * 8) / activeMax) + 1 : 9;
					int blitYPosition = yPosition + (9 - iconHeight); // bottom of the intended icon segment
					int textureYOffset = 9 - iconHeight; // slice the texture from the bottom upwards
					guiGraphics.blit(icon, xPosition, blitYPosition, 0, textureYOffset, 9, iconHeight, 9, 9);
				} else {
					int cooldownMax = abilityParams.cooldown;
					int iconHeight = cooldownMax > 0 ? 9 - ((entry.remaining() * 9) / cooldownMax) : 0;
					int blitYPosition = yPosition + (9 - iconHeight);
					int textureYOffset = 9 - iconHeight;
					RenderSystem.setShaderColor(1f, 0.5f, 0.5f, 1.0f);
					guiGraphics.blit(icon, xPosition, blitYPosition, 0, textureYOffset, 9, iconHeight, 9, 9);
					RenderSystem.enableBlend();
					RenderSystem.defaultBlendFunc(); // standard alpha blending
					RenderSystem.setShaderColor(1f, 0.5f, 0.5f, 0.25f);
					guiGraphics.blit(icon, xPosition, yPosition, 0, 0, 9, 9, 9, 9);
					RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
				}
				renderedIcons++;
			}
		}
	}
}
