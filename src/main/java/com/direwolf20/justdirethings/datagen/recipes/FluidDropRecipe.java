package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

@SuppressWarnings("removal")
public class FluidDropRecipe implements CraftingRecipe {
	private final ResourceLocation id;
	protected final BlockState input;
	protected final BlockState output;
	protected final Item catalyst;

	public FluidDropRecipe(ResourceLocation id, BlockState input, BlockState output, Item catalyst) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.catalyst = catalyst;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeType<?> getType() {
		return Registration.FLUID_DROP_RECIPE_TYPE.get();
	}

	public boolean matches(BlockState blockState, ItemStack catalystStack) {
		if (!catalystStack.is(catalyst))
			return false;
		return blockState.getFluidState().is(input.getFluidState().getType()) && blockState.getFluidState().isSource();
	}

	public BlockState getOutput() {
		return output;
	}

	public BlockState getInput() {
		return input;
	}

	public Item getCatalyst() {
		return catalyst;
	}

	@Override
	public CraftingBookCategory category() {
		return CraftingBookCategory.MISC;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public boolean matches(CraftingContainer pInv, Level pLevel) {
		return false;
	}

	@Override
	public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Registration.FLUID_DROP_RECIPE_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<FluidDropRecipe> {
		private static final Codec<Item> ITEM_CODEC = ResourceLocation.CODEC
				.xmap(rl -> ForgeRegistries.ITEMS.getValue(rl), item -> ForgeRegistries.ITEMS.getKey(item));
		private static final Codec<FluidDropRecipe> CODEC = RecordCodecBuilder.create(instance -> instance
				.group(ResourceLocation.CODEC.fieldOf("id").forGetter(r -> r.id),
						BlockState.CODEC.fieldOf("input").forGetter(r -> r.input),
						BlockState.CODEC.fieldOf("output").forGetter(r -> r.output),
						ITEM_CODEC.fieldOf("catalyst").forGetter(r -> r.catalyst))
				.apply(instance, FluidDropRecipe::new));

		public Codec<FluidDropRecipe> codec() {
			return CODEC;
		}

		@Override
		public FluidDropRecipe fromJson(ResourceLocation id, JsonObject json) {
			BlockState inputState = BlockState.CODEC.parse(JsonOps.INSTANCE, json.get("input")).resultOrPartial(e -> {
			}).orElse(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
			BlockState outputState = BlockState.CODEC.parse(JsonOps.INSTANCE, json.get("output")).resultOrPartial(e -> {
			}).orElse(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
			ResourceLocation catalystId = new ResourceLocation(json.get("catalyst").getAsString());
			Item catalyst = ForgeRegistries.ITEMS.getValue(catalystId);
			return new FluidDropRecipe(id, inputState, outputState, catalyst);
		}

		@Override
		public FluidDropRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			ResourceLocation recipeId = buf.readResourceLocation();
			BlockState inputState = Block.stateById(buf.readInt());
			BlockState outputState = Block.stateById(buf.readInt());
			ResourceLocation catalystId = buf.readResourceLocation();
			Item catalyst = ForgeRegistries.ITEMS.getValue(catalystId);
			return new FluidDropRecipe(recipeId, inputState, outputState, catalyst);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, FluidDropRecipe recipe) {
			buf.writeResourceLocation(recipe.id);
			buf.writeInt(Block.getId(recipe.input));
			buf.writeInt(Block.getId(recipe.output));
			buf.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.catalyst));
		}
	}
}
