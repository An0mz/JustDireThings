package com.direwolf20.justdirethings.common.network.handler;

import com.direwolf20.justdirethings.common.items.interfaces.LeftClickableTool;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableItem;
import com.direwolf20.justdirethings.common.items.interfaces.ToggleableTool;
import com.direwolf20.justdirethings.common.network.data.LeftClickPayload;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;


import static com.direwolf20.justdirethings.util.MiscTools.getHitResult;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;
public class LeftClickPacket {
    public static final LeftClickPacket INSTANCE = new LeftClickPacket();

    public static LeftClickPacket get() {
        return INSTANCE;
    }

    public static void handle(final LeftClickPayload payload, final Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null)
                return;

            ItemStack toggleableItem = ItemStack.EMPTY;
            if (payload.inventorySlot() == -1)
                toggleableItem = ToggleableItem.getToggleableItem(player);
            else
                toggleableItem = player.getInventory().getItem(payload.inventorySlot());
            if (toggleableItem.getItem() instanceof LeftClickableTool && toggleableItem.getItem() instanceof ToggleableTool toggleableTool) {
                if (payload.keyCode() == -1) {//left Click
                    InteractionHand hand = payload.mainHand() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                    if (payload.type() == 0) { //Air
                        toggleableTool.useAbility(player.level(), player, hand, false);
                    } else if (payload.type() == 1) { //Block
                        UseOnContext useoncontext = new UseOnContext(player.level(), player, hand, toggleableItem, new BlockHitResult(Vec3.atCenterOf(payload.blockPos()), Direction.values()[payload.direction()], payload.blockPos(), false));
                        toggleableTool.useOnAbility(useoncontext, false);
                    }
                } else { //Key Binding
                    toggleableTool.useAbility(player.level(), player, toggleableItem, payload.keyCode(), payload.isMouse());
                    BlockHitResult blockHitResult = getHitResult(player);
                    if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                        UseOnContext useoncontext = new UseOnContext(player.level(), player, InteractionHand.MAIN_HAND, toggleableItem, blockHitResult);
                        toggleableTool.useOnAbility(useoncontext, toggleableItem, payload.keyCode(), payload.isMouse());
                    }
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }
}
