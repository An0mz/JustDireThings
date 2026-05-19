package com.direwolf20.justdirethings.client.jei.ghostfilters;

import com.direwolf20.justdirethings.client.screens.basescreens.BaseScreen;
import com.direwolf20.justdirethings.common.containers.slots.FilterBasicSlot;
import com.direwolf20.justdirethings.common.network.data.GhostSlotPayload;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

import com.direwolf20.justdirethings.common.network.PacketHandler;
public class GhostFilterBasic implements IGhostIngredientHandler<BaseScreen> {
	@Override
	public <I> List<Target<I>> getTargetsTyped(BaseScreen gui, ITypedIngredient<I> ingredient, boolean doStart) {
		List<Target<I>> targets = new ArrayList<>();

		for (Slot slot : gui.getMenu().slots) {
			if (!(ingredient.getIngredient() instanceof ItemStack) || !(slot instanceof FilterBasicSlot)) {
				continue;
			}
			if (slot.x < 0 || slot.y < 0) {
				continue;
			}

			Rect2i bounds = new Rect2i(gui.getGuiLeft() + slot.x, gui.getGuiTop() + slot.y, 16, 16);
			targets.add(new Target<>() {
				@Override
				public Rect2i getArea() {
					return bounds;
				}

				@Override
				public void accept(I ingredient) {
					slot.set((ItemStack) ingredient);
					PacketHandler.CHANNEL.sendToServer(new GhostSlotPayload(slot.index, (ItemStack) ingredient,
							((ItemStack) ingredient).getCount(), -1));
				}
			});
		}
		return targets;
	}

	@Override
	public void onComplete() {
		// NO OP
	}
}
