package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.common.items.tools.basetools.BaseBow;
import com.direwolf20.justdirethings.datagen.recipes.FluidDropRecipe;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityEvents {
	public record FluidInputs(BlockState blockState, Item item) {
	}

	static Map<FluidInputs, BlockState> fluidCraftCache = new HashMap<>();

	@SubscribeEvent
	public static void livingUseItem(LivingEntityUseItemEvent.Start event) {
		if (event.getEntity() instanceof Player && event.getItem().getItem() instanceof BaseBow baseBow) {
			event.setDuration(event.getDuration() - (20 - (int) baseBow.getMaxDraw()));
		}
	}

	public static void handleFluidDrop(ItemEntity itemEntity) {
		Level level = itemEntity.level();
		if (level.isClientSide() || itemEntity.isRemoved())
			return;
		BlockState blockState = itemEntity.getFeetBlockState();
		if (!(blockState.getBlock() instanceof LiquidBlock))
			return;
		BlockState fluidDropOutput = findRecipe(blockState, itemEntity);
		if (fluidDropOutput == null || fluidDropOutput.isAir())
			return;
		BlockPos blockPos = itemEntity.blockPosition();
		if (level.setBlockAndUpdate(blockPos, fluidDropOutput)) {
			itemEntity.getItem().shrink(1);
			FluidState fluidState = level.getFluidState(blockPos);
			FluidStack fluidStack = new FluidStack(fluidState.getType(), 1000);
			FluidType fluidType = fluidState.getType().getFluidType();
			if (!fluidState.isEmpty() && fluidType.isVaporizedOnPlacement(level, blockPos, fluidStack)) {
				level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
				fluidType.onVaporize(null, level, blockPos, fluidStack);
			} else {
				level.playSound(null, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	@Nullable
	private static BlockState findRecipe(BlockState blockState, ItemEntity entity) {
		FluidInputs fluidInputs = new FluidInputs(blockState, entity.getItem().getItem());
		if (fluidCraftCache.containsKey(fluidInputs))
			return fluidCraftCache.get(fluidInputs);
		RecipeManager recipeManager = entity.level().getRecipeManager();
		List<FluidDropRecipe> recipes = recipeManager.getAllRecipesFor(Registration.FLUID_DROP_RECIPE_TYPE.get());
		for (FluidDropRecipe recipe : recipes) {
			if (recipe.matches(blockState, entity.getItem())) {
				fluidCraftCache.put(fluidInputs, recipe.getOutput());
				break;
			}
		}
		if (!fluidCraftCache.containsKey(fluidInputs))
			fluidCraftCache.put(fluidInputs, Blocks.AIR.defaultBlockState());
		return fluidCraftCache.get(fluidInputs);
	}

	private static void clearCache() {
		fluidCraftCache.clear();
	}

	@SubscribeEvent
	public static void onServerStarted(ServerStartedEvent e) {
		clearCache();
	}

	@SubscribeEvent
	public static void onReloadServerResources(AddReloadListenerEvent e) {
		clearCache();
	}
}
