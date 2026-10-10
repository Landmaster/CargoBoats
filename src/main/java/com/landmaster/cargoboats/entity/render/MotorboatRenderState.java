package com.landmaster.cargoboats.entity.render;

import com.landmaster.cargoboats.CargoBoats;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.fluids.FluidStack;

public class MotorboatRenderState extends EntityRenderState {
    float yRotation;
    float rotorRotation;
    boolean isUnderWater;
    FluidStack fluidStack = FluidStack.EMPTY;
    int tankCapacity;
    DyeColor color;

    public Identifier motorboatTexture(String prefix) {
        if (color == DyeColor.BLUE) {
            return Identifier.fromNamespaceAndPath(
                    CargoBoats.MODID, String.format("textures/entity/%s.png", prefix));
        }
        return Identifier.fromNamespaceAndPath(
                CargoBoats.MODID, String.format("textures/entity/%s_%s.png", prefix, color));
    }
}
