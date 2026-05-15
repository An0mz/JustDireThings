package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.items.interfaces.*;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.util.MagicHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class PolymorphicWandV2 extends BaseToggleableTool implements LeftClickableTool, FluidContainingItem, PoweredItem {

    public PolymorphicWandV2() {
        super(new Properties()
                .fireResistant()
                .stacksTo(1));
        registerAbility(Ability.LAVAREPAIR);
        registerAbility(Ability.POLYMORPH_RANDOM);
    }

    @Override
    public int getMaxMB() {
        return Config.POLYMORPHIC_WAND_V2_MAX_FLUID.get();
    }

    @Override
    public int getMaxEnergy() {
        return Config.POLYMORPHIC_WAND_V2_MAX_FE.get();
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack itemStack = pContext.getItemInHand();
        Player player = pContext.getPlayer();
        if (player == null || itemStack.isEmpty()) return InteractionResult.FAIL;
        BlockHitResult blockhitresult = getPlayerPOVHitResult(player.level(), player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() == HitResult.Type.BLOCK) {
            if (FluidContainingItem.pickupFluid(player.level(), player, itemStack, blockhitresult))
                return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        Level level = player.level();
        if (level.isClientSide) return true;
        ItemStack itemStack = player.getMainHandItem();
        Set<Ability> abilities = LeftClickableTool.getLeftClickList(itemStack);
        if (itemStack.getItem() instanceof ToggleableTool toggleableTool && !abilities.isEmpty()) {
            toggleableTool.useAbility(level, player, InteractionHand.MAIN_HAND, false);
        }
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() == HitResult.Type.BLOCK) {
            if (FluidContainingItem.pickupFluid(level, player, itemStack, blockhitresult))
                return InteractionResultHolder.fail(itemStack);
        }
        return super.use(level, player, hand);
    }

    @Override
    public boolean showBarWhenFull() {
        return true;
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
        return color == -1 ? super.getBarColor(stack) : color;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        if (level == null) return;
        IFluidHandlerItem fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (fluidHandler == null) return;
        tooltip.add(Component.translatable("justdirethings.polymorphicfluidamt",
                MagicHelpers.formatted(fluidHandler.getFluidInTank(0).getAmount()),
                MagicHelpers.formatted(fluidHandler.getTankCapacity(0))).withStyle(ChatFormatting.GREEN));
    }
}
