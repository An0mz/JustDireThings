package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.blockentities.basebe.GooBlockBE_Base;
import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("removal")
public class GooSpreadRecipeTag implements CraftingRecipe {
	private final ResourceLocation id;

	@Override
	public ResourceLocation getId() {
		return id;
	}

	protected final TagKey<Block> input;
	protected final BlockState output;
	protected int tierRequirement;
	protected int craftingDuration;

	public GooSpreadRecipeTag(ResourceLocation id, TagKey<Block> input, BlockState output, int tierRequirement,
			int craftingDuration) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.tierRequirement = tierRequirement;
		this.craftingDuration = craftingDuration;
	}

	@Override
	public RecipeType<?> getType() {
		return Registration.GOO_SPREAD_RECIPE_TYPE_TAG.get();
	}

	public boolean matches(Level level, BlockPos blockPos, GooBlockBE_Base gooBlockBE_base, BlockState sourceState) {
		return sourceState.is(input) && gooBlockBE_base.getTier() >= tierRequirement;
	}

	public BlockState getOutput() {
		return output;
	}

	public TagKey<Block> getInput() {
		return input;
	}

	public int getTierRequirement() {
		return tierRequirement;
	}

	public int getCraftingDuration() {
		return craftingDuration;
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

	public boolean matches(CraftingContainer pInv, Level pLevel) {
		return false;
	}

	public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
		return ItemStack.EMPTY;
	}

	/**
	 * Used to determine if this recipe can fit in a grid of the given width/height
	 */
	@Override
	public boolean canCraftInDimensions(int pWidth, int pHeight) {
		return false;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Registration.GOO_SPREAD_RECIPE_SERIALIZER_TAG.get();
	}

	public static class Serializer implements RecipeSerializer<GooSpreadRecipeTag> {
		private static final ResourceLocation NAME = new ResourceLocation(JustDireThings.MODID, "goospread_tag");
		private static final Codec<GooSpreadRecipeTag> CODEC = RecordCodecBuilder.create(instance -> instance
				.group(ResourceLocation.CODEC.fieldOf("id").forGetter(recipe -> recipe.id),
						TagKey.codec(Registries.BLOCK).fieldOf("input").forGetter(recipe -> recipe.input),
						BlockState.CODEC.fieldOf("output").forGetter(recipe -> recipe.output),
						Codec.INT.fieldOf("tierRequirement").forGetter(recipe -> recipe.tierRequirement),
						Codec.INT.fieldOf("craftingDuration").forGetter(recipe -> recipe.craftingDuration))
				.apply(instance, GooSpreadRecipeTag::new));

		public Codec<GooSpreadRecipeTag> codec() {
			return CODEC;
		}

		@Override
		public GooSpreadRecipeTag fromJson(ResourceLocation id, com.google.gson.JsonObject json) {
			TagKey<Block> input = TagKey.create(Registries.BLOCK,
					new ResourceLocation(json.get("input").getAsString()));
			BlockState outputState = parseBlockState(json.get("output"));
			int tierRequirement = json.get("tierRequirement").getAsInt();
			int craftingDuration = json.get("craftingDuration").getAsInt();
			return new GooSpreadRecipeTag(id, input, outputState, tierRequirement, craftingDuration);
		}

		private static BlockState parseBlockState(JsonElement el) {
			return BlockState.CODEC.parse(JsonOps.INSTANCE, el).resultOrPartial(e -> {
			}).orElse(net.minecraft.world.level.block.Blocks.AIR.defaultBlockState());
		}

		@Override
		public GooSpreadRecipeTag fromNetwork(ResourceLocation id, FriendlyByteBuf pBuffer) {
			ResourceLocation resourceLocation = pBuffer.readResourceLocation();
			TagKey<Block> input = TagKey.create(Registries.BLOCK, pBuffer.readResourceLocation());
			BlockState outputState = Block.stateById(pBuffer.readInt());
			int tierRequirement = pBuffer.readInt();
			int craftingDuration = pBuffer.readInt();

			return new GooSpreadRecipeTag(resourceLocation, input, outputState, tierRequirement, craftingDuration);
		}

		public void toNetwork(FriendlyByteBuf pBuffer, GooSpreadRecipeTag pRecipe) {
			pBuffer.writeResourceLocation(pRecipe.id);
			pBuffer.writeResourceLocation(pRecipe.input.location());
			pBuffer.writeInt(Block.getId(pRecipe.output));
			pBuffer.writeInt(pRecipe.tierRequirement);
			pBuffer.writeInt(pRecipe.craftingDuration);
		}
	}
}
