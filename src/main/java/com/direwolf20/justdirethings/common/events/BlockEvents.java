package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.common.items.tools.BlazegoldHoe;
import com.direwolf20.justdirethings.common.items.tools.CelestigemHoe;
import com.direwolf20.justdirethings.common.items.tools.EclipseAlloyHoe;
import com.direwolf20.justdirethings.common.items.tools.FerricoreHoe;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.level.BlockEvent;

public class BlockEvents {
    @SubscribeEvent
    public static void BlockToolModificationEvent(BlockEvent.BlockToolModificationEvent event) {
        ItemStack heldItem = event.getHeldItemStack();
        if (event.getToolAction().equals(ToolActions.HOE_TILL) && heldItem.getItem() instanceof ToggleableItem toggleableItem) {
            if (heldItem.getItem() instanceof FerricoreHoe && toggleableItem.getEnabled(heldItem)) {
                BlockState modifiedState = event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true);
                if (modifiedState != null && modifiedState.is(Blocks.FARMLAND))
                    event.setFinalState(Registration.GooSoil_Tier1.get().defaultBlockState());
            } else if (heldItem.getItem() instanceof BlazegoldHoe && toggleableItem.getEnabled(heldItem)) {
                BlockState modifiedState = event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true);
                if (modifiedState != null && modifiedState.is(Blocks.FARMLAND))
                    event.setFinalState(Registration.GooSoil_Tier2.get().defaultBlockState());
            } else if (heldItem.getItem() instanceof CelestigemHoe && toggleableItem.getEnabled(heldItem)) {
                BlockState modifiedState = event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true);
                if (modifiedState != null && modifiedState.is(Blocks.FARMLAND))
                    event.setFinalState(Registration.GooSoil_Tier3.get().defaultBlockState());
            } else if (heldItem.getItem() instanceof EclipseAlloyHoe && toggleableItem.getEnabled(heldItem)) {
                BlockState modifiedState = event.getState().getBlock().getToolModifiedState(event.getState(), event.getContext(), event.getToolAction(), true);
                if (modifiedState != null && modifiedState.is(Blocks.FARMLAND))
                    event.setFinalState(Registration.GooSoil_Tier4.get().defaultBlockState());
            }
        }
    }

    @SubscribeEvent
    public static void onFurnaceFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        Item item = event.getItemStack().getItem();
        if (item == Registration.Coal_T1.get()) event.setBurnTime(4800);
        else if (item == Registration.Coal_T2.get()) event.setBurnTime(14400);
        else if (item == Registration.Coal_T3.get()) event.setBurnTime(43200);
        else if (item == Registration.Coal_T4.get()) event.setBurnTime(129600);
        else if (item instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block == Registration.CoalBlock_T1.get()) event.setBurnTime(48000);
            else if (block == Registration.CoalBlock_T2.get()) event.setBurnTime(144000);
            else if (block == Registration.CoalBlock_T3.get()) event.setBurnTime(432000);
            else if (block == Registration.CoalBlock_T4.get()) event.setBurnTime(1296000);
        }
    }
}
