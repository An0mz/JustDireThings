package com.direwolf20.justdirethings.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlitterParticle extends TextureSheetParticle {

    GlitterParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, dx, dy, dz);
        this.xd = dx + (Math.random() - 0.5) * 0.05;
        this.yd = dy + Math.random() * 0.05;
        this.zd = dz + (Math.random() - 0.5) * 0.05;
        this.lifetime = 20 + (int) (Math.random() * 15);
        this.quadSize = 0.05f + (float) Math.random() * 0.05f;
        this.alpha = 1.0f;
        this.gravity = 0.02f;
        this.rCol = 0.9f + (float) Math.random() * 0.1f;
        this.gCol = 0.7f + (float) Math.random() * 0.3f;
        this.bCol = 0.2f + (float) Math.random() * 0.5f;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.alpha = 1.0f - (float) this.age / this.lifetime;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            return new GlitterParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
