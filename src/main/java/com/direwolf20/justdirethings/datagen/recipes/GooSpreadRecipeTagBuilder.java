package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.JustDireThings;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("removal")
public class GooSpreadRecipeTagBuilder implements RecipeBuilder {
	@Nullable
	private String group;

	private final ResourceLocation id;
	protected final TagKey<Block> input;
	protected final BlockState output;
	protected final int tierRequirement;
	protected final int craftingDuration;
	private final NonNullList<Ingredient> ingredients = NonNullList.create();
	private final Map<String, CriterionTriggerInstance> criteria = new LinkedHashMap<>();

	public GooSpreadRecipeTagBuilder(ResourceLocation id, TagKey<Block> input, BlockState output, int tierRequirement,
			int craftingDuration) {
		this.id = id;
		this.input = input;
		this.output = output;
		this.tierRequirement = tierRequirement;
		this.craftingDuration = craftingDuration;
	}

	public static GooSpreadRecipeTagBuilder shapeless(ResourceLocation id, TagKey<Block> input, BlockState output,
			int tierRequirement, int craftingDuration) {
		return new GooSpreadRecipeTagBuilder(id, input, output, tierRequirement, craftingDuration);
	}

	public GooSpreadRecipeTagBuilder requires(ItemLike pItem) {
		return this.requires(pItem, 1);
	}

	public GooSpreadRecipeTagBuilder requires(ItemLike pItem, int pQuantity) {
		for (int i = 0; i < pQuantity; ++i) {
			this.requires(Ingredient.of(pItem));
		}
		return this;
	}

	public GooSpreadRecipeTagBuilder requires(Ingredient pIngredient) {
		return this.requires(pIngredient, 1);
	}

	public GooSpreadRecipeTagBuilder requires(Ingredient pIngredient, int pQuantity) {
		for (int i = 0; i < pQuantity; ++i) {
			this.ingredients.add(pIngredient);
		}
		return this;
	}

	@Override
	public GooSpreadRecipeTagBuilder unlockedBy(String pName, CriterionTriggerInstance pCriterion) {
		this.criteria.put(pName, pCriterion);
		return this;
	}

	@Override
	public GooSpreadRecipeTagBuilder group(@Nullable String pGroupName) {
		this.group = pGroupName;
		return this;
	}

	@Override
	public Item getResult() {
		return ItemStack.EMPTY.getItem();
	}

	public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer) {
		this.save(pFinishedRecipeConsumer,
				new ResourceLocation(JustDireThings.MODID, this.input.location().getPath() + "-goospread_tag"));
	}

	@Override
	public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pId) {
		this.ensureValid(pId);
		pFinishedRecipeConsumer.accept(new FinishedGooSpreadRecipeTag(pId, this.input, this.output,
				this.tierRequirement, this.craftingDuration));
	}

	private void ensureValid(ResourceLocation pId) {
		if (this.criteria.isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + pId);
		}
	}
}
