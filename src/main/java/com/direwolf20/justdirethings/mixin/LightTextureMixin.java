package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightTexture.class)
public abstract class LightTextureMixin {

    @Redirect(
            method = "updateLightTexture",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;hasEffect(Lnet/minecraft/world/effect/MobEffect;)Z")
    )
    private boolean redirectHasEffect(LocalPlayer instance, MobEffect effect) {
        ItemStack helmet = instance.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.canUseAbilityAndDurability(helmet, Ability.NIGHTVISION)) {
            return true;
        }
        return instance.hasEffect(effect);
    }

    @Redirect(
            method = "updateLightTexture",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;getNightVisionScale(Lnet/minecraft/world/entity/LivingEntity;F)F")
    )
    private float redirectNightVisionScale(LivingEntity livingEntity, float nanoTime) {
        ItemStack helmet = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof ToggleableTool toggleableTool && toggleableTool.canUseAbilityAndDurability(helmet, Ability.NIGHTVISION)) {
            return 1f;
        }
        return GameRenderer.getNightVisionScale(livingEntity, nanoTime);
    }
}
