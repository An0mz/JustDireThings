package com.direwolf20.justdirethings.util;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandler;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CuriosCompat {
	public static IItemHandler getEquippedCurios(Player player) {
		return CuriosApi.getCuriosInventory(player).map(ICuriosItemHandler::getEquippedCurios).orElse(null);
	}
}
