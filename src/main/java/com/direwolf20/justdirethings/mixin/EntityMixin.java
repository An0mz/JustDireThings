package com.direwolf20.justdirethings.mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Placeholder – phase-through-walls feature removed pending a reliable
 * implementation. The mixin must remain registered in
 * mixins.justdirethings.json because removing it while the LightTextureMixin
 * client entry still exists could cause issues; an empty mixin is harmless.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
}
