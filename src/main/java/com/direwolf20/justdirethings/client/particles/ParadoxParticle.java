package com.direwolf20.justdirethings.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParadoxParticle extends TextureSheetParticle {

    private final double originX, originY, originZ;
    private double angle;
    private final double radius;

    ParadoxParticle(ClientLevel level, double x, double y, double z, double dx, double dy, double dz, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        this.originX = x;
        this.originY = y;
        this.originZ = z;
        this.angle = Math.random() * Math.PI * 2;
        this.radius = 0.3 + Math.random() * 0.4;
        this.lifetime = 40 + (int) (Math.random() * 20);
        this.quadSize = 0.15f + (float) Math.random() * 0.1f;
        this.alpha = 0.8f;
        this.gravity = 0f;
        this.rCol = 0.4f + (float) Math.random() * 0.3f;
        this.gCol = 0.1f + (float) Math.random() * 0.2f;
        this.bCol = 0.8f + (float) Math.random() * 0.2f;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.angle += 0.15;
        this.x = originX + Math.cos(angle) * radius;
        this.y = originY + (double) age / lifetime * 0.5;
        this.z = originZ + Math.sin(angle) * radius;
        this.alpha = 0.8f * (1.0f - (float) this.age / this.lifetime);
        super.tick();
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
            return new ParadoxParticle(level, x, y, z, dx, dy, dz, sprites);
        }
    }
}
