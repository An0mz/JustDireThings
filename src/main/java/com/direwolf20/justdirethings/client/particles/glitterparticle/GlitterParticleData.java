package com.direwolf20.justdirethings.client.particles.glitterparticle;

import com.direwolf20.justdirethings.client.particles.ModParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

import javax.annotation.Nonnull;
import java.util.Locale;

public class GlitterParticleData implements ParticleOptions {
    public static final Codec<GlitterParticleData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("targetX").forGetter(p -> p.targetX),
                    Codec.DOUBLE.fieldOf("targetY").forGetter(p -> p.targetY),
                    Codec.DOUBLE.fieldOf("targetZ").forGetter(p -> p.targetZ),
                    Codec.FLOAT.fieldOf("size").forGetter(p -> p.size),
                    Codec.FLOAT.fieldOf("r").forGetter(p -> p.r),
                    Codec.FLOAT.fieldOf("g").forGetter(p -> p.g),
                    Codec.FLOAT.fieldOf("b").forGetter(p -> p.b),
                    Codec.FLOAT.fieldOf("a").forGetter(p -> p.a),
                    Codec.FLOAT.fieldOf("maxAgeMul").forGetter(p -> p.maxAgeMul)
            ).apply(instance, GlitterParticleData::new));

    public final double targetX, targetY, targetZ;
    public final float size, r, g, b, a, maxAgeMul;

    public GlitterParticleData(double targetX, double targetY, double targetZ,
                                float size, float r, float g, float b, float a, float maxAgeMul) {
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.size = size;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        this.maxAgeMul = maxAgeMul;
    }

    @Nonnull
    @Override
    public ParticleType<GlitterParticleData> getType() {
        return ModParticles.GLITTER.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeDouble(targetX);
        buffer.writeDouble(targetY);
        buffer.writeDouble(targetZ);
        buffer.writeFloat(size);
        buffer.writeFloat(r);
        buffer.writeFloat(g);
        buffer.writeFloat(b);
        buffer.writeFloat(a);
        buffer.writeFloat(maxAgeMul);
    }

    @Nonnull
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%.2f %.2f %.2f %.4f %.2f %.2f %.2f %.2f %.2f",
                targetX, targetY, targetZ, size, r, g, b, a, maxAgeMul);
    }

    public static final Deserializer<GlitterParticleData> DESERIALIZER = new Deserializer<>() {
        @Nonnull
        @Override
        public GlitterParticleData fromCommand(ParticleType<GlitterParticleData> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double tx = reader.readDouble();
            reader.expect(' ');
            double ty = reader.readDouble();
            reader.expect(' ');
            double tz = reader.readDouble();
            reader.expect(' ');
            float size = reader.readFloat();
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float a = reader.readFloat();
            reader.expect(' ');
            float maxAgeMul = reader.readFloat();
            return new GlitterParticleData(tx, ty, tz, size, r, g, b, a, maxAgeMul);
        }

        @Override
        public GlitterParticleData fromNetwork(ParticleType<GlitterParticleData> type, FriendlyByteBuf buf) {
            return new GlitterParticleData(
                    buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readFloat(), buf.readFloat(), buf.readFloat(),
                    buf.readFloat(), buf.readFloat(), buf.readFloat()
            );
        }
    };
}
