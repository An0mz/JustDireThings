package com.direwolf20.justdirethings.common.fluids.unrefinedt2fuel;

import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class UnrefinedT2Fuel extends ForgeFlowingFluid {

    private static ForgeFlowingFluid.Properties makeProperties() {
        return new ForgeFlowingFluid.Properties(
                () -> Registration.UNREFINED_T2_FLUID_TYPE.get(),
                () -> Registration.UNREFINED_T2_FLUID_SOURCE.get(),
                () -> Registration.UNREFINED_T2_FLUID_FLOWING.get()
        ).bucket(() -> Registration.UNREFINED_T2_FLUID_BUCKET.get())
         .block(() -> Registration.UNREFINED_T2_FLUID_BLOCK.get());
    }

    protected UnrefinedT2Fuel() {
        super(makeProperties());
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    public static class Source extends UnrefinedT2Fuel {
        @Override
        public boolean isSource(FluidState state) { return true; }
        @Override
        public int getAmount(FluidState state) { return 8; }
    }

    public static class Flowing extends UnrefinedT2Fuel {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override
        public boolean isSource(FluidState state) { return false; }
        @Override
        public int getAmount(FluidState state) { return state.getValue(LEVEL); }
    }
}
