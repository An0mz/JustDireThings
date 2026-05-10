package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.common.entities.TimeWandEntity;
import com.direwolf20.justdirethings.common.items.interfaces.FluidContainingItem;
import com.direwolf20.justdirethings.common.items.interfaces.PoweredItem;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.util.MagicHelpers;
import com.direwolf20.justdirethings.util.MiscTools;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class TimeWand extends BasePoweredItem implements FluidContainingItem {

    public TimeWand() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getMaxEnergy() {
        return Config.TIME_WAND_MAX_FE.get();
    }

    @Override
    public int getMaxMB() {
        return Config.TIME_WAND_MAX_FLUID.get();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!Config.TIME_WAND_FAKE_PLAYER_ALLOWED.get() && player instanceof FakePlayer)
            return InteractionResultHolder.fail(itemStack);
        BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            if (FluidContainingItem.pickupFluid(level, player, itemStack, hitResult))
                return InteractionResultHolder.success(itemStack);
            if (spawnEntity(level, player, hitResult.getBlockPos(), itemStack))
                return InteractionResultHolder.success(itemStack);
        }
        return InteractionResultHolder.fail(itemStack);
    }

    public boolean spawnEntity(Level level, Player player, BlockPos blockPos, ItemStack itemStack) {
        if (level.isClientSide) return false;
        BlockState blockState = level.getBlockState(blockPos);
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        if (!MiscTools.isValidTickAccelBlock((ServerLevel) level, blockState, blockEntity))
            return false;

        Optional<TimeWandEntity> existing = level.getEntitiesOfClass(TimeWandEntity.class, new AABB(blockPos))
                .stream().findFirst();

        if (existing.isPresent()) {
            TimeWandEntity entity = existing.get();
            int newLevel = entity.getTickSpeed() + 1;
            if (newLevel > Config.logBase2(Config.TIME_WAND_MAX_MULTIPLIER.get()))
                return false;
            float accelRate = TimeWandEntity.calculateAccelRate(newLevel);
            int fluidCost = calculateFluidCost(player, (int) accelRate);
            int feCost = calculateFECost(player, (int) accelRate);
            if (!hasResources(player, itemStack, feCost, fluidCost)) return false;

            entity.setTickSpeed(newLevel);
            int timeExisted = entity.getTotalTime() - entity.getRemainingTime();
            entity.addTime(timeExisted / 2);
            FluidContainingItem.consumeFluid(itemStack, fluidCost);
            PoweredItem.consumeEnergy(itemStack, feCost);
            playTimeWandSound(level, blockPos, newLevel);
        } else {
            int setRate = 1;
            float accelRate = TimeWandEntity.calculateAccelRate(setRate);
            int fluidCost = calculateFluidCost(player, (int) accelRate);
            int feCost = calculateFECost(player, (int) accelRate);
            if (!hasResources(player, itemStack, feCost, fluidCost)) return false;

            TimeWandEntity entity = new TimeWandEntity(level, blockPos);
            level.addFreshEntity(entity);
            FluidContainingItem.consumeFluid(itemStack, fluidCost);
            PoweredItem.consumeEnergy(itemStack, feCost);
            playTimeWandSound(level, blockPos, setRate);
        }
        return true;
    }

    public boolean hasResources(Player player, ItemStack itemStack, int feCost, int fluidCost) {
        if (!FluidContainingItem.hasEnoughFluid(itemStack, fluidCost)) {
            player.displayClientMessage(Component.translatable("justdirethings.lowtimefluid"), true);
            player.playNotifySound(SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(), SoundSource.PLAYERS, 1.0F, 0.5F);
            return false;
        }
        if (!PoweredItem.hasEnoughEnergy(itemStack, feCost)) {
            player.displayClientMessage(Component.translatable("justdirethings.lowenergy"), true);
            player.playNotifySound(SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(), SoundSource.PLAYERS, 1.0F, 0.5F);
            return false;
        }
        return true;
    }

    public static int calculateFECost(Player player, int accelRate) {
        if (player.isCreative()) return 0;
        return accelRate * Config.TIME_WAND_FE_COST.get();
    }

    public static int calculateFluidCost(Player player, int accelRate) {
        if (player.isCreative()) return 0;
        return (int) (accelRate * Config.TIME_WAND_FLUID_COST.get().floatValue());
    }

    private void playTimeWandSound(Level level, BlockPos pos, int speed) {
        float pitch = switch (speed) {
            case 1  -> 0.707107F;
            case 2  -> 0.793701F;
            case 3  -> 0.890899F;
            case 4  -> 0.943874F;
            case 5  -> 1.059463F;
            case 6  -> 1.189207F;
            case 7  -> 1.334840F;
            case 8  -> 1.414214F;
            default -> 1.0F;
        };
        level.playSound(null, pos, SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(), SoundSource.PLAYERS, 1.0F, pitch);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (level == null) return;
        IFluidHandlerItem fh = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (fh != null) {
            tooltip.add(Component.translatable("justdirethings.timefluidamt",
                    MagicHelpers.formatted(fh.getFluidInTank(0).getAmount()),
                    MagicHelpers.formatted(fh.getTankCapacity(0))).withStyle(ChatFormatting.GREEN));
        }
    }
}
