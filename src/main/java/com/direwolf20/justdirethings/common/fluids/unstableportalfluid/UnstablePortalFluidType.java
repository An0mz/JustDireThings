package com.direwolf20.justdirethings.common.fluids.unstableportalfluid;

import com.direwolf20.justdirethings.common.fluids.JustDireFluidType;
import com.direwolf20.justdirethings.datagen.JustDireBiomeTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

@SuppressWarnings("removal")
public class UnstablePortalFluidType extends JustDireFluidType {
	public UnstablePortalFluidType(int tintColor, Properties properties) {
		super(tintColor, properties);
	}

	@Override
	public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
		return !level.getBiome(pos).is(JustDireBiomeTags.UNSTABLE_PORTAL_FLUID_VIABLE);
	}

	@Override
	public void onVaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack stack) {
		SoundEvent sound = this.getSound(player, level, pos, SoundActions.FLUID_VAPORIZE);
		level.playSound(player, pos, sound != null ? sound : SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F,
				2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

		for (int l = 0; l < 8; ++l)
			level.addAlwaysVisibleParticle(ParticleTypes.DRAGON_BREATH, (double) pos.getX() + Math.random(),
					(double) pos.getY() + Math.random(), (double) pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
	}
}
