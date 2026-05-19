package com.direwolf20.justdirethings.common.capabilities;

import com.direwolf20.justdirethings.common.blockentities.ExperienceHolderBE;
import com.direwolf20.justdirethings.common.fluids.xpfluid.XPFluid;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class ExperienceHolderFluidTank extends FluidTank {

    private final ExperienceHolderBE be;

    public ExperienceHolderFluidTank(ExperienceHolderBE be) {
        super(Integer.MAX_VALUE, fs -> fs.getFluid() instanceof XPFluid);
        this.be = be;
    }

    private int expAsMilliBuckets() {
        if (be.exp > Integer.MAX_VALUE / 20) return Integer.MAX_VALUE;
        return be.exp * 20;
    }

    @Override
    public FluidStack getFluid() {
        int amount = expAsMilliBuckets();
        if (amount == 0) return FluidStack.EMPTY;
        return new FluidStack(Registration.XP_FLUID_SOURCE.get(), amount);
    }

    @Override
    public int getFluidAmount() {
        return expAsMilliBuckets();
    }

    @Override
    public int getCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isEmpty() {
        return be.exp <= 0;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !isFluidValid(resource)) return 0;
        int expToAdd = resource.getAmount() / 20;
        if (expToAdd == 0) return 0;
        long space = (long) Integer.MAX_VALUE - be.exp;
        int canAdd = (int) Math.min(expToAdd, space);
        if (canAdd == 0) return 0;
        if (action.execute()) {
            be.addExp(canAdd);
            onContentsChanged();
        }
        return canAdd * 20;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) return FluidStack.EMPTY;
        FluidStack mine = getFluid();
        if (mine.isEmpty() || !mine.isFluidEqual(resource)) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (be.exp == 0 || maxDrain <= 0) return FluidStack.EMPTY;
        int drained = maxDrain - (maxDrain % 20);
        drained = Math.min(drained, expAsMilliBuckets());
        if (drained == 0) return FluidStack.EMPTY;
        if (action.execute()) {
            be.subExp(drained / 20);
            onContentsChanged();
        }
        return new FluidStack(Registration.XP_FLUID_SOURCE.get(), drained);
    }

    @Override
    protected void onContentsChanged() {
        be.markDirtyClient();
    }
}
