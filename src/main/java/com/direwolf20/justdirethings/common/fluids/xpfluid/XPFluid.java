package com.direwolf20.justdirethings.common.fluids.xpfluid;

import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class XPFluid extends ForgeFlowingFluid {

    private static ForgeFlowingFluid.Properties makeProperties() {
        return new ForgeFlowingFluid.Properties(
                () -> Registration.XP_FLUID_TYPE.get(),
                () -> Registration.XP_FLUID_SOURCE.get(),
                () -> Registration.XP_FLUID_FLOWING.get()
        ).bucket(() -> Registration.XP_FLUID_BUCKET.get())
         .block(() -> Registration.XP_FLUID_BLOCK.get());
    }

    protected XPFluid() {
        super(makeProperties());
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    public static class Source extends XPFluid {
        @Override
        public boolean isSource(FluidState state) { return true; }
        @Override
        public int getAmount(FluidState state) { return 8; }
    }

    public static class Flowing extends XPFluid {
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
