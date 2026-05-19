package com.direwolf20.justdirethings.common.events;

import com.direwolf20.justdirethings.common.items.tools.basetools.BaseBow;
import com.direwolf20.justdirethings.datagen.recipes.FluidDropRecipe;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

	@SubscribeEvent
	public static void levelTick(TickEvent.LevelTickEvent event) {
		if (event.phase != TickEvent.Phase.END)
			return;
		Level level = event.level;
		if (level.isClientSide())
			return;
		if (!(level instanceof ServerLevel serverLevel))
			return;
		for (Entity entity : serverLevel.getAllEntities()) {
			if (!(entity instanceof ItemEntity itemEntity))
				continue;
			BlockPos blockPos = itemEntity.blockPosition();
			BlockState blockState = level.getBlockState(blockPos);
			if (!(blockState.getBlock() instanceof LiquidBlock))
				continue;
			BlockState fluidDropOutput = findRecipe(blockState, itemEntity);
			if (fluidDropOutput == null || fluidDropOutput.isAir())
				continue;
			if (level.setBlockAndUpdate(blockPos, fluidDropOutput)) {
				itemEntity.getItem().shrink(1);
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
