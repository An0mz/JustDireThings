package com.direwolf20.justdirethings.datagen.recipes;

import com.direwolf20.justdirethings.setup.Registration;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class FinishedGooSpreadRecipe implements FinishedRecipe {
    private final ResourceLocation id;
    private final BlockState input;
    private final BlockState output;
    private final int tierRequirement;
    private final int craftingDuration;
    private final Advancement.Builder advancement;
    private final ResourceLocation advancementId;

    public FinishedGooSpreadRecipe(ResourceLocation id, BlockState input, BlockState output, int tierRequirement, int craftingDuration) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.tierRequirement = tierRequirement;
        this.craftingDuration = craftingDuration;
        this.advancement = null;
        this.advancementId = null;
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
        json.addProperty("id", id.toString());
        // blockstate as block id strings
        json.addProperty("input", net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(input.getBlock()).toString());
        json.addProperty("output", net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(output.getBlock()).toString());
        json.addProperty("tierRequirement", tierRequirement);
        json.addProperty("craftingDuration", craftingDuration);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getType() {
        return Registration.GOO_SPREAD_RECIPE_SERIALIZER.get();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return advancementId;
    }
}

