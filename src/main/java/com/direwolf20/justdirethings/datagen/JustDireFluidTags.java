package com.direwolf20.justdirethings.datagen;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("removal")
public class JustDireFluidTags extends FluidTagsProvider {

	public JustDireFluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, JustDireThings.MODID, existingFileHelper);
	}

	private static TagKey<Fluid> forgeFluidTag(String name) {
		return FluidTags.create(new ResourceLocation("forge", "fluids/" + name));
	}

	private static TagKey<Fluid> fluidTag(String namespace, String name) {
		return FluidTags.create(new ResourceLocation(namespace, name));
	}

	public static final TagKey<Fluid> FORGE_EXPERIENCE = fluidTag("forge", "experience");
	public static final TagKey<Fluid> FORGE_XPJUICE = fluidTag("forge", "xpjuice");
	public static final TagKey<Fluid> INDUSTRIALFOREGOING_ESSENCE = fluidTag("industrialforegoing", "essence");

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(forgeFluidTag("refined_t2_fuel")).add(Registration.REFINED_T2_FLUID_SOURCE.get())
				.add(Registration.REFINED_T2_FLUID_FLOWING.get());
		tag(forgeFluidTag("refined_t3_fuel")).add(Registration.REFINED_T3_FLUID_SOURCE.get())
				.add(Registration.REFINED_T3_FLUID_FLOWING.get());
		tag(forgeFluidTag("refined_t4_fuel")).add(Registration.REFINED_T4_FLUID_SOURCE.get())
				.add(Registration.REFINED_T4_FLUID_FLOWING.get());
		tag(forgeFluidTag("unrefined_t2_fuel")).add(Registration.UNREFINED_T2_FLUID_SOURCE.get())
				.add(Registration.UNREFINED_T2_FLUID_FLOWING.get());
		tag(forgeFluidTag("unrefined_t3_fuel")).add(Registration.UNREFINED_T3_FLUID_SOURCE.get())
				.add(Registration.UNREFINED_T3_FLUID_FLOWING.get());
		tag(forgeFluidTag("unrefined_t4_fuel")).add(Registration.UNREFINED_T4_FLUID_SOURCE.get())
				.add(Registration.UNREFINED_T4_FLUID_FLOWING.get());
		tag(forgeFluidTag("portal_fluid")).add(Registration.PORTAL_FLUID_SOURCE.get())
				.add(Registration.PORTAL_FLUID_FLOWING.get());
		tag(forgeFluidTag("unstable_portal_fluid")).add(Registration.UNSTABLE_PORTAL_FLUID_SOURCE.get())
				.add(Registration.UNSTABLE_PORTAL_FLUID_FLOWING.get());
		tag(forgeFluidTag("time_fluid")).add(Registration.TIME_FLUID_SOURCE.get())
				.add(Registration.TIME_FLUID_FLOWING.get());
		tag(forgeFluidTag("xp_fluid")).add(Registration.XP_FLUID_SOURCE.get()).add(Registration.XP_FLUID_FLOWING.get());
		// Interop tags so other mods' XP-fluid handling (pipes, tanks, machines)
		// recognizes this fluid
		tag(FORGE_EXPERIENCE).add(Registration.XP_FLUID_SOURCE.get()).add(Registration.XP_FLUID_FLOWING.get());
		tag(FORGE_XPJUICE).add(Registration.XP_FLUID_SOURCE.get()).add(Registration.XP_FLUID_FLOWING.get());
		tag(INDUSTRIALFOREGOING_ESSENCE).add(Registration.XP_FLUID_SOURCE.get())
				.add(Registration.XP_FLUID_FLOWING.get());
		tag(forgeFluidTag("polymorphic_fluid")).add(Registration.POLYMORPHIC_FLUID_SOURCE.get())
				.add(Registration.POLYMORPHIC_FLUID_FLOWING.get());
	}

	@Override
	public String getName() {
		return "JustDireThings Fluid Tags";
	}
}
