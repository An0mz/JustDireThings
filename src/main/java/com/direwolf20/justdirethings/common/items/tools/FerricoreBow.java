package com.direwolf20.justdirethings.common.items.tools;

import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.common.items.tools.basetools.BaseBow;
import net.minecraft.world.item.Item;

public class FerricoreBow extends BaseBow {
    public FerricoreBow() {
        super(new Item.Properties().durability(250));
        registerAbility(Ability.POTIONARROW);
    }

    public float getMaxDraw() {
        return 20;
    }
}


