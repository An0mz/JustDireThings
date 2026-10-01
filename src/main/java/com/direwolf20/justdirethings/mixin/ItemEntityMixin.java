package com.direwolf20.justdirethings.mixin;

import com.direwolf20.justdirethings.common.events.EntityEvents;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Stand-in for NeoForge's EntityTickEvent.Post, which Forge 1.20.1 lacks. Runs
 * fluid drop crafting only for item entities that actually tick, instead of
 * scanning every entity in every level each tick.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

	@Inject(method = "tick", at = @At("RETURN"))
	private void justdirethings$afterTick(CallbackInfo ci) {
		EntityEvents.handleFluidDrop((ItemEntity) (Object) this);
	}
}
