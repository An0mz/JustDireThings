package com.direwolf20.justdirethings.common.fluids.refinedt2fuel;

import com.direwolf20.justdirethings.common.fluids.basefluids.RefinedFuel;
import com.direwolf20.justdirethings.setup.Config;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class RefinedT2Fuel extends ForgeFlowingFluid implements RefinedFuel {

    private static ForgeFlowingFluid.Properties makeProperties() {
        return new ForgeFlowingFluid.Properties(
                () -> Registration.REFINED_T2_FUEL_TYPE.get(),
                () -> Registration.REFINED_T2_FUEL_SOURCE.get(),
                () -> Registration.REFINED_T2_FUEL_FLOWING.get()
        ).bucket(() -> Registration.REFINED_T2_FUEL_BUCKET.get())
         .block(() -> Registration.REFINED_T2_FUEL_BLOCK.get());
    }

    protected RefinedT2Fuel() {
        super(makeProperties());
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        return false;
    }

    @Override
    public int fePerMb() {
        return Config.FUEL_TIER2_FE_PER_MB.get();
    }

    public static class Source extends RefinedT2Fuel {
        @Override
        public boolean isSource(FluidState state) { return true; }
        @Override
        public int getAmount(FluidState state) { return 8; }
    }

    public static class Flowing extends RefinedT2Fuel {
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
