package com.direwolf20.justdirethings.common.items.abilityupgrades;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static com.direwolf20.justdirethings.util.TooltipHelpers.appendShiftForInfo;
import static com.direwolf20.justdirethings.util.TooltipHelpers.appendUpgradeDetails;

public class Upgrade extends Item {
    public Upgrade() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        if (level == null) return;
        if (Screen.hasShiftDown()) {
            appendUpgradeDetails(stack, tooltip);
        } else {
            appendShiftForInfo(stack, tooltip);
        }
    }
}
