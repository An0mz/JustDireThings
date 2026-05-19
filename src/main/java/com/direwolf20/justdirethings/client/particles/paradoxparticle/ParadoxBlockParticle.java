package com.direwolf20.justdirethings.client.particles.paradoxparticle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BreakingItemParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ParadoxBlockParticle extends BreakingItemParticle {

	private final double targetX;
	private final double targetY;
	private final double targetZ;
	private double angle;

	public ParadoxBlockParticle(ClientLevel level, double x, double y, double z, double targetX, double targetY,
			double targetZ, ItemStack itemStack) {
		super(level, x, y, z, itemStack);
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
		this.xd = 0;
		this.yd = 0;
		this.zd = 0;
		this.gravity = 0.0f;
		this.hasPhysics = false;
		this.lifetime = 50 + random.nextInt(30);
		this.angle = random.nextDouble() * Math.PI * 2;
		this.scale(0.35f + random.nextFloat() * 0.2f);

		if (this.sprite == null) {
			ItemStack fallback = new ItemStack(Items.STONE);
			this.setSprite(Minecraft.getInstance().getItemRenderer().getModel(fallback, level, (LivingEntity) null, 0)
					.getParticleIcon());
		}
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
		double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if (dist < 0.15) {
			this.remove();
			return;
		}

		// Accelerate toward target; small angular deflection gives slight spiral feel
		double pullStrength = Math.min(0.08, 0.005 + 0.3 / (dist * dist + 1));
		angle += 0.08;
		double perpX = -dz / dist;
		double perpZ = dx / dist;
		double spiral = Math.sin(angle) * 0.02 * dist;

		this.xd = this.xd * 0.6 + (dx / dist) * pullStrength + perpX * spiral;
		this.yd = this.yd * 0.6 + (dy / dist) * pullStrength;
		this.zd = this.zd * 0.6 + (dz / dist) * pullStrength + perpZ * spiral;

		this.move(this.xd, this.yd, this.zd);
	}

	public static final ParticleProvider<ParadoxParticleData> FACTORY = (data, level, x, y, z, dx, dy,
			dz) -> new ParadoxBlockParticle(level, x, y, z, data.targetX, data.targetY, data.targetZ,
					data.getItemStack());
}
