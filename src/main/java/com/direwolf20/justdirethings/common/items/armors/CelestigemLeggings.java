package com.direwolf20.justdirethings.common.items.armors;

import com.direwolf20.justdirethings.common.items.armors.basearmors.BaseLeggings;
import com.direwolf20.justdirethings.common.items.armors.utils.ArmorTiers;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.interfaces.AbilityParams;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredTool;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CelestigemLeggings extends BaseLeggings implements PoweredTool {
    public CelestigemLeggings() {
        super(ArmorTiers.CELESTIGEM, new Item.Properties().fireResistant());
        registerAbility(Ability.RUNSPEED, new AbilityParams(1, 3, 1));
        registerAbility(Ability.WALKSPEED, new AbilityParams(1, 3, 1));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isPowerBarVisible(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return getPowerBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        int color = getPowerBarColor(stack);
        if (color == -1)
            return super.getBarColor(stack);
        return color;
    }
}

