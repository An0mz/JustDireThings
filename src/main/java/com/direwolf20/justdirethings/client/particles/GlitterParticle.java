package com.direwolf20.justdirethings.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlitterParticle extends TextureSheetParticle {

    private final double targetX, targetY, targetZ;

    public GlitterParticle(ClientLevel level, double x, double y, double z,
                            double targetX, double targetY, double targetZ,
                            float size, float r, float g, float b, float a,
                            float maxAgeMul, SpriteSet sprites) {
        super(level, x, y, z, 0, 0, 0);
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.quadSize = size;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = a;
        this.lifetime = Math.round(120 * maxAgeMul);
        this.gravity = 0f;
        this.hasPhysics = false;
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        double dx = targetX - this.x;
        double dy = targetY - this.y;
        double dz = targetZ - this.z;

        if (Math.sqrt(dx * dx + dy * dy + dz * dz) < 0.1) {
            this.remove();
            return;
        }

        double speed = 20.0;
        this.move(dx / speed, dy / speed, dz / speed);
        this.alpha = 1.0f - (float) this.age / this.lifetime;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }
}
