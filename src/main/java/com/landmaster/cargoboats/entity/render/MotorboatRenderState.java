package com.landmaster.cargoboats.entity.render;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.neoforged.neoforge.fluids.FluidStack;

public class MotorboatRenderState extends EntityRenderState {
    float yRotation;
    float rotorRotation;
    boolean isUnderWater;
    FluidStack fluidStack = FluidStack.EMPTY;
    int tankCapacity;
}
