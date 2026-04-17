package com.landmaster.cargoboats.util;

import net.minecraft.core.NonNullList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;

public class AdjustableCapacityTank extends FluidStacksResourceHandler {
    public AdjustableCapacityTank(int size, int capacity) {
        super(size, capacity);
    }

    public AdjustableCapacityTank(NonNullList<FluidStack> stacks, int capacity) {
        super(stacks, capacity);
    }

    public void setCapacity(int capacity) {
        int oldCapacity = this.capacity;
        this.capacity = capacity;
        onCapacityChanged(oldCapacity);
    }

    public void onCapacityChanged(int previousCapacity) {
    }
}
