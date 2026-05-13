package com.direwolf20.justdirethings.common.items;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.capabilities.FluidHandlerItemStack;
import com.direwolf20.justdirethings.common.items.interfaces.FluidContainingItem;
import com.direwolf20.justdirethings.util.MagicHelpers;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class FluidCanister extends Item implements FluidContainingItem {
    private static final String FILL_MODE_KEY = "FillMode";

    public enum FillMode {
        NONE("none"),
        JDTONLY("jdtonly"),
        ALL("all");

        private final String baseName;

        FillMode(String baseName) {
            this.baseName = baseName;
        }

        public Component getTooltip() {
            return Component.translatable(JustDireThings.MODID + ".fillmode." + baseName);
        }

        public FillMode next() {
            FillMode[] values = values();
            return values[(this.ordinal() + 1) % values.length];
        }
    }

    public FluidCanister() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getMaxMB() {
        return 8000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() == HitResult.Type.BLOCK) {
            if (player.isShiftKeyDown()) {
                if (placeFluid(level, player, itemStack, blockhitresult))
                    return InteractionResultHolder.success(itemStack);
            } else {
                if (pickupFluid(level, player, itemStack, blockhitresult))
                    return InteractionResultHolder.success(itemStack);
                if (placeFluid(level, player, itemStack, blockhitresult))
                    return InteractionResultHolder.success(itemStack);
            }
        } else {
            if (player.isShiftKeyDown()) {
                nextFillMode(itemStack);
                player.displayClientMessage(Component.translatable("justdirethings.fillmode.changed", getFillMode(itemStack).getTooltip()), true);
            }
        }
        return InteractionResultHolder.fail(itemStack);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemStack, @NotNull Level world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        if (world.isClientSide) return;
        FillMode fillMode = getFillMode(itemStack);
        if (fillMode == FillMode.NONE) return;
        if (entity instanceof Player player) {
            IFluidHandlerItem fluidHandler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
            if (fluidHandler == null || fluidHandler.getFluidInTank(0).isEmpty()) return;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack slotStack = player.getInventory().getItem(i);
                if (slotStack.getItem() instanceof FluidCanister) continue;
                if (fillMode == FillMode.JDTONLY) {
                    String modId = slotStack.getItem().getCreatorModId(slotStack);
                    if (!JustDireThings.MODID.equals(modId)) continue;
                }
                IFluidHandlerItem slotFluidHandler = slotStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
                if (slotFluidHandler != null) {
                    FluidStack fluidStack = fluidHandler.getFluidInTank(0);
                    int amtToFill = Math.min(fluidStack.getAmount(), 100);
                    int acceptedFluid = slotFluidHandler.fill(new FluidStack(fluidStack.getFluid(), amtToFill), IFluidHandler.FluidAction.SIMULATE);
                    if (acceptedFluid > 0) {
                        FluidStack extractedFluid = fluidHandler.drain(new FluidStack(fluidStack.getFluid(), acceptedFluid), IFluidHandler.FluidAction.EXECUTE);
                        slotFluidHandler.fill(extractedFluid, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        IFluidHandlerItem fluidHandler = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (fluidHandler == null) return;
        FluidStack fluidStack = fluidHandler.getFluidInTank(0);
        int fluidColor = getFluidColor(stack);
        Style fluidStyle = Style.EMPTY.withColor(TextColor.fromRgb(fluidColor));
        Component fluidName = fluidStack.isEmpty()
                ? Component.literal("-").withStyle(fluidStyle)
                : fluidStack.getDisplayName().copy().withStyle(fluidStyle);
        tooltip.add(Component.translatable("justdirethings.fluidname", fluidName)
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("justdirethings.fluidamt",
                Component.literal(MagicHelpers.formatted(fluidStack.getAmount()) + "/" + MagicHelpers.formatted(getMaxMB())).withStyle(ChatFormatting.GREEN))
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("justdirethings.fillmode",
                getFillMode(stack).getTooltip().copy().withStyle(ChatFormatting.GREEN))
                .withStyle(ChatFormatting.GRAY));
    }

    public boolean placeFluid(Level level, Player player, ItemStack itemStack, BlockHitResult blockhitresult) {
        BlockPos blockpos = blockhitresult.getBlockPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.getBlock() instanceof LiquidBlock && !player.isShiftKeyDown()) return false;
        IFluidHandlerItem fluidHandler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
        if (fluidHandler == null) return false;
        FluidStack fluidStack = fluidHandler.getFluidInTank(0);
        if (fluidStack.isEmpty() || fluidStack.getAmount() < 1000) return false;
        Direction direction = blockhitresult.getDirection();
        BlockPos blockpos1 = blockpos.relative(direction);
        BlockPos target = canBlockContainFluid(player, level, blockpos, blockstate, fluidStack.getFluid()) ? blockpos : blockpos1;
        if (emptyContents(player, level, target, fluidStack.getFluid())) {
            fluidHandler.drain(1000, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    public boolean pickupFluid(Level level, Player player, ItemStack itemStack, BlockHitResult blockhitresult) {
        BlockPos blockpos = blockhitresult.getBlockPos();
        BlockState blockstate1 = level.getBlockState(blockpos);
        if (blockstate1.getBlock() instanceof LiquidBlock liquidBlock) {
            IFluidHandlerItem fluidHandler = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM, null).orElse(null);
            if (fluidHandler == null) return false;
            Fluid fluid = blockstate1.getFluidState().getType();
            FluidStack existing = fluidHandler.getFluidInTank(0);
            if (!blockstate1.getFluidState().isSource() || (!existing.isEmpty() && !existing.getFluid().isSame(fluid)))
                return false;
            int filledAmt = fluidHandler.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.SIMULATE);
            if (filledAmt == 1000) {
                ItemStack itemstack2 = liquidBlock.pickupBlock(level, blockpos, blockstate1);
                fluidHandler.fill(new FluidStack(fluid, 1000), IFluidHandler.FluidAction.EXECUTE);
                liquidBlock.getPickupSound().ifPresent(s -> player.playSound(s, 1.0F, 1.0F));
                if (!level.isClientSide) {
                    CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemstack2);
                }
                return true;
            }
        }
        return false;
    }

    public boolean emptyContents(@Nullable Player player, Level level, BlockPos pos, Fluid fluid) {
        if (!(fluid instanceof FlowingFluid flowingFluid)) return false;
        BlockState blockState = level.getBlockState(pos);
        Block block = blockState.getBlock();
        boolean canBeReplaced = blockState.canBeReplaced(fluid);
        if (!blockState.isAir() && !canBeReplaced) {
            if (block instanceof LiquidBlockContainer lbc && lbc.canPlaceLiquid(level, pos, blockState, fluid)) {
                lbc.placeLiquid(level, pos, blockState, flowingFluid.getSource(false));
                playEmptySound(player, level, pos, fluid);
                return true;
            }
            return false;
        }
        if (!level.isClientSide && canBeReplaced && !blockState.liquid()) {
            level.destroyBlock(pos, true);
        }
        if (level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), 11) || blockState.getFluidState().isSource()) {
            playEmptySound(player, level, pos, fluid);
            return true;
        }
        return false;
    }

    protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos, Fluid fluid) {
        SoundEvent sound = fluid.getFluidType().getSound(SoundActions.BUCKET_EMPTY);
        if (sound == null)
            sound = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
    }

    protected boolean canBlockContainFluid(@Nullable Player player, Level world, BlockPos pos, BlockState blockstate, Fluid fluid) {
        return blockstate.getBlock() instanceof LiquidBlockContainer lbc && lbc.canPlaceLiquid(world, pos, blockstate, fluid);
    }

    public static FluidStack getFluidStack(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        if (tag == null || !tag.contains(FluidHandlerItemStack.FLUID_NBT_KEY)) return FluidStack.EMPTY;
        return FluidStack.loadFluidStackFromNBT(tag.getCompound(FluidHandlerItemStack.FLUID_NBT_KEY));
    }

    public static Fluid getFluid(ItemStack itemStack) {
        FluidStack fs = getFluidStack(itemStack);
        return fs.isEmpty() ? null : fs.getFluid();
    }

    public static int getFullness(ItemStack itemStack) {
        FluidStack fs = getFluidStack(itemStack);
        if (fs.isEmpty()) return 0;
        return (int) Math.ceil((double) fs.getAmount() / 1000);
    }

    public static int getFluidColor(ItemStack itemStack) {
        FluidStack fs = getFluidStack(itemStack);
        if (fs.isEmpty()) return 0xFFFFFFFF;
        if (fs.getFluid().isSame(Fluids.LAVA)) return 0xFFFF4500;
        return IClientFluidTypeExtensions.of(fs.getFluid().getFluidType()).getTintColor();
    }

    public static FillMode getFillMode(ItemStack itemStack) {
        CompoundTag tag = itemStack.getTag();
        int ordinal = tag != null && tag.contains(FILL_MODE_KEY) ? tag.getInt(FILL_MODE_KEY) : 0;
        FillMode[] values = FillMode.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : FillMode.NONE;
    }

    public static void nextFillMode(ItemStack itemStack) {
        itemStack.getOrCreateTag().putInt(FILL_MODE_KEY, getFillMode(itemStack).next().ordinal());
    }
}
