package com.direwolf20.justdirethings.client.particles.paradoxparticle;

import com.direwolf20.justdirethings.client.particles.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Locale;

public class ParadoxParticleData implements ParticleOptions {
    private final ItemStack itemStack;
    public final double targetX;
    public final double targetY;
    public final double targetZ;

    public ParadoxParticleData(ItemStack itemStack, double tx, double ty, double tz) {
        this.itemStack = itemStack.copy();
        this.targetX = tx;
        this.targetY = ty;
        this.targetZ = tz;
    }

    @Nonnull
    @Override
    public ParticleType<ParadoxParticleData> getType() {
        return ModParticles.PARADOX.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeItem(this.itemStack);
        buffer.writeDouble(this.targetX);
        buffer.writeDouble(this.targetY);
        buffer.writeDouble(this.targetZ);
    }

    @Nonnull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f", this.getType(), this.targetX, this.targetY, this.targetZ);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public static final Deserializer<ParadoxParticleData> DESERIALIZER = new Deserializer<>() {
        @Nonnull
        @Override
        public ParadoxParticleData fromCommand(ParticleType<ParadoxParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            ItemParser.ItemResult result = ItemParser.parseForItem(BuiltInRegistries.ITEM.asLookup(), reader);
            ItemStack stack = new ItemInput(result.item(), result.nbt()).createItemStack(1, false);
            reader.expect(' ');
            double tx = reader.readDouble();
            reader.expect(' ');
            double ty = reader.readDouble();
            reader.expect(' ');
            double tz = reader.readDouble();
            return new ParadoxParticleData(stack, tx, ty, tz);
        }

        @Override
        public ParadoxParticleData fromNetwork(ParticleType<ParadoxParticleData> type, FriendlyByteBuf buffer) {
            return new ParadoxParticleData(buffer.readItem(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
    };
}
