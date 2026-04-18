package com.landmaster.cargoboats.entity.render;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.FluidMotorboat;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.util.FluidRenderUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;

import javax.annotation.Nonnull;

public class FluidMotorboatRenderer extends EntityRenderer<FluidMotorboat, MotorboatRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(CargoBoats.MODID, "textures/entity/fluid_motorboat.png"), "main");
    private final FluidMotorboatModel model;
    private final Model.Simple waterPatchModel;

    public FluidMotorboatRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new FluidMotorboatModel(context.bakeLayer(LAYER_LOCATION));
        waterPatchModel = new Model.Simple(context.bakeLayer(MotorboatRenderer.WATER_PATCH_LAYER_LOCATION), t -> RenderTypes.waterMask());
    }

    @Nonnull
    @Override
    public MotorboatRenderState createRenderState() {
        return new MotorboatRenderState();
    }

    @Override
    public void extractRenderState(@Nonnull FluidMotorboat entity, @Nonnull MotorboatRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.isUnderWater = entity.isUnderWater();
        state.yRotation = entity.getYRot(partialTicks);
    }

    @Override
    public void submit(MotorboatRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector submitNodeCollector, @Nonnull CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0, 1.5, 0);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRotation + 180));
        submitNodeCollector.submitModel(model, state, poseStack, LAYER_LOCATION.model(), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        if (!state.isUnderWater) {
            submitNodeCollector.submitModel(
                    this.waterPatchModel, Unit.INSTANCE, poseStack, MotorboatRenderer.WATER_PATCH_LAYER_LOCATION.model(), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null
            );
        }
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

//    @Override
//    public void render(@Nonnull FluidMotorboat p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
//        poseStack.pushPose();
//        poseStack.translate(0, 1.5, 0);
//        poseStack.scale(-1.0F, -1.0F, 1.0F);
//        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw + 180));
//        this.model.setupAnim(p_entity, 0.0F, 0.0F, p_entity.tickCount + partialTick, 0.0F, 0.0F);
//        VertexConsumer vertexconsumer = bufferSource.getBuffer(this.model.renderType(LAYER_LOCATION.getModel()));
//        this.model.renderToBuffer(poseStack, vertexconsumer, packedLight, OverlayTexture.NO_OVERLAY);
//        if (!p_entity.isUnderWater()) {
//            VertexConsumer vertexconsumer1 = bufferSource.getBuffer(RenderType.waterMask());
//            model.waterPatch().render(poseStack, vertexconsumer1, packedLight, OverlayTexture.NO_OVERLAY);
//        }
//
//        poseStack.pushPose();
//        var fluidStack = p_entity.tank.getFluid();
//        var capacity = p_entity.tank.getCapacity();
//        poseStack.translate(3.99 / 16, 1.5 - 2.02 / 16, 1.0 / 16);
//        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
//        poseStack.scale(7.98f / 16, (float) (7.98 * Math.clamp((double)fluidStack.getAmount() / capacity, 0.0, 1.0) / 16), 6.98f / 16);
//        FluidRenderUtil.renderCubeUsingQuads(fluidStack, partialTick, poseStack, bufferSource, packedLight, packedLight);
////        poseStack.translate(-0.56f, -0.25f, 0.37f);
////        poseStack.mulPose(Axis.XP.rotationDegrees(180));
////        poseStack.scale(0.73f, 0.73f * Math.clamp((float)fluidStack.getAmount() / capacity, 0.0f, 1.0f), 0.73f);
////        FluidRenderUtil.renderCubeUsingQuads(fluidStack, partialTick, poseStack, bufferSource, packedLight, packedLight);
//        poseStack.popPose();
//
//        poseStack.popPose();
//        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
//    }
//
//    @Nonnull
//    @Override
//    public Identifier getTextureLocation(@Nonnull FluidMotorboat motorboat) {
//        return LAYER_LOCATION.getModel();
//    }
}
