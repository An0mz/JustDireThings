package com.direwolf20.justdirethings.client.sounds;

import com.direwolf20.justdirethings.common.entities.ParadoxEntity;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class ParadoxAmbientSound extends AbstractTickableSoundInstance {

	private static final Set<Integer> active = new HashSet<>();

	private final ParadoxEntity entity;

	public static void playFor(ParadoxEntity entity) {
		if (active.contains(entity.getId()))
			return;
		active.add(entity.getId());
		Minecraft.getInstance().getSoundManager().play(new ParadoxAmbientSound(entity));
	}

	private ParadoxAmbientSound(ParadoxEntity entity) {
		super(Registration.PARADOX_AMBIENT.get(), SoundSource.AMBIENT, RandomSource.create());
		this.entity = entity;
		this.looping = true;
		this.delay = 0;
		this.volume = 1.0f;
		this.pitch = 1.0f;
		this.x = entity.getX();
		this.y = entity.getY() + 0.9;
		this.z = entity.getZ();
		this.attenuation = SoundInstance.Attenuation.LINEAR;
	}

	@Override
	public void tick() {
		if (entity.isRemoved() || entity.isCollapsing()) {
			this.stop();
			active.remove(entity.getId());
			return;
		}
		this.x = entity.getX();
		this.y = entity.getY() + 0.9;
		this.z = entity.getZ();
	}
}
