package com.landmaster.cargoboats.entity.render;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.FluidMotorboat;
import com.landmaster.cargoboats.entity.Motorboat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public class FluidMotorboatRenderer extends EntityRenderer<FluidMotorboat> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/entity/fluid_motorboat.png"), "main");
    private final MotorboatModel model;

    public FluidMotorboatRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new MotorboatModel(context.bakeLayer(LAYER_LOCATION));
    }

    @Override
    public void render(@Nonnull FluidMotorboat p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw - 90));
        this.model.setupAnim(p_entity, 0.0F, 0.0F, p_entity.tickCount + partialTick, 0.0F, 0.0F);
        VertexConsumer vertexconsumer = bufferSource.getBuffer(this.model.renderType(LAYER_LOCATION.getModel()));
        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
        if (!p_entity.isUnderWater()) {
            VertexConsumer vertexconsumer1 = bufferSource.getBuffer(RenderType.waterMask());
            model.waterPatch().render(poseStack, vertexconsumer1, packedLight, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull FluidMotorboat motorboat) {
        return LAYER_LOCATION.getModel();
    }
}
