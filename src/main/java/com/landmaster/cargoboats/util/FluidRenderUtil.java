package com.landmaster.cargoboats.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Adapted from EnderIO's fluid rendering code
 */
public class FluidRenderUtil {

    public static void submitFluid(PoseStack poseStack, RenderType renderType, SubmitNodeCollector nodeCollector, FluidStack fluidStack,
                                   float fillAmount, int packedLight) {
        if (fluidStack.isEmpty()) return;

        Fluid fluid = fluidStack.getFluid();
        int light = fluid.getFluidType().getLightLevel(fluidStack);
        if (light > 0) {
            packedLight = LightCoordsUtil.pack(light, light);
        }
        submitFluid(poseStack, renderType, nodeCollector, fluid, fillAmount, packedLight);
    }

    public static void submitFluid(PoseStack poseStack, RenderType renderType, SubmitNodeCollector nodeCollector, Fluid fluid, float fillAmount, int packedLight) {
        // Get fluid model
        FluidState fluidState = fluid.defaultFluidState();
        FluidModel fluidModel = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(fluidState);

        // Get tint color
        int color = 0xFFFFFFFF;
        if (fluidModel.fluidTintSource() != null) {
            color = fluidModel.fluidTintSource().color(fluidState);
        }

        // Get fluid texture
        TextureAtlasSprite texture = fluidModel.stillMaterial().sprite();

        // Get sizes
        float fluidHeight = fillAmount;
        float inset = 0F;
        float faceSize = 1;

        // Top
        RenderUtil.submitFace(Direction.UP, poseStack, renderType, nodeCollector, texture, inset, inset, inset + fluidHeight, faceSize,
                faceSize, color, packedLight);

        // Sides
        RenderUtil.submitFace(Direction.SOUTH, poseStack, renderType, nodeCollector, texture, inset, inset, inset, faceSize, fluidHeight,
                color, packedLight);
        RenderUtil.submitFace(Direction.NORTH, poseStack, renderType, nodeCollector, texture, inset, inset, inset, faceSize, fluidHeight,
                color, packedLight);
        RenderUtil.submitFace(Direction.EAST, poseStack, renderType, nodeCollector, texture, inset, inset, inset, faceSize, fluidHeight,
                color, packedLight);
        RenderUtil.submitFace(Direction.WEST, poseStack, renderType, nodeCollector, texture, inset, inset, inset, faceSize, fluidHeight,
                color, packedLight);
    }
}