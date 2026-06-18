package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TierUpgradeRecipe implements SmithingRecipe {
	private final ResourceLocation id;
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final ItemStack result;

	public TierUpgradeRecipe(ResourceLocation id, Ingredient template, Ingredient base, Ingredient addition,
			ItemStack result) {
		this.id = id;
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.result = result;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeType<?> getType() {
		return RecipeType.SMITHING;
	}

	@Override
	public boolean matches(Container inv, Level level) {
		return template.test(inv.getItem(0)) && base.test(inv.getItem(1)) && addition.test(inv.getItem(2));
	}

	@Override
	public ItemStack assemble(Container inv, RegistryAccess registryAccess) {
		ItemStack result = this.result.copy();
		CompoundTag baseTag = inv.getItem(1).getTag();
		if (baseTag != null) {
			result.setTag(baseTag.copy());
		}
		return result;
	}

	@Override
	public ItemStack getResultItem(RegistryAccess registryAccess) {
		return this.result;
	}

	@Override
	public boolean isTemplateIngredient(ItemStack stack) {
		return template.test(stack);
	}

	@Override
	public boolean isBaseIngredient(ItemStack stack) {
		return base.test(stack);
	}

	@Override
	public boolean isAdditionIngredient(ItemStack stack) {
		return addition.test(stack);
	}

	@Override
	public boolean isIncomplete() {
		return Stream.of(template, base, addition).anyMatch(Ingredient::isEmpty);
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return true;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return Registration.TIER_UPGRADE_RECIPE_SERIALIZER.get();
	}

	public static class Serializer implements RecipeSerializer<TierUpgradeRecipe> {
		@Override
		public TierUpgradeRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
			Ingredient template = Ingredient.fromJson(json.get("template"));
			Ingredient base = Ingredient.fromJson(json.get("base"));
			Ingredient addition = Ingredient.fromJson(json.get("addition"));
			ItemStack result = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));
			return new TierUpgradeRecipe(recipeId, template, base, addition, result);
		}

		@Override
		@Nullable
		public TierUpgradeRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
			Ingredient template = Ingredient.fromNetwork(buffer);
			Ingredient base = Ingredient.fromNetwork(buffer);
			Ingredient addition = Ingredient.fromNetwork(buffer);
			ItemStack result = buffer.readItem();
			return new TierUpgradeRecipe(recipeId, template, base, addition, result);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, TierUpgradeRecipe recipe) {
			recipe.template.toNetwork(buffer);
			recipe.base.toNetwork(buffer);
			recipe.addition.toNetwork(buffer);
			buffer.writeItem(recipe.result);
		}
	}
}
