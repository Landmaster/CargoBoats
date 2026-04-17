package com.landmaster.cargoboats.entity.render;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;

import javax.annotation.Nonnull;

public class MotorboatRenderer extends EntityRenderer<Motorboat, MotorboatRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            Identifier.fromNamespaceAndPath(CargoBoats.MODID, "textures/entity/motorboat.png"), "main");
    private final MotorboatModel model;

    public MotorboatRenderer(EntityRendererProvider.Context context) {
        super(context);
        model = new MotorboatModel(context.bakeLayer(LAYER_LOCATION));
    }

    @Override
    public MotorboatRenderState createRenderState() {
        return new MotorboatRenderState();
    }

    @Override
    public void extractRenderState(Motorboat entity, MotorboatRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
    }

    @Override
    public void submit(MotorboatRenderState state, @Nonnull PoseStack poseStack, @Nonnull SubmitNodeCollector submitNodeCollector, @Nonnull CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0, 1.5, 0);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(state.yRotation + 180));
        submitNodeCollector.submitModel(model, state, poseStack, LAYER_LOCATION.model(), state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }

//    @Override
//    public void render(@Nonnull Motorboat p_entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
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
//        poseStack.popPose();
//        super.render(p_entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
//    }
//
//    @Nonnull
//    @Override
//    public Identifier getTextureLocation(@Nonnull Motorboat motorboat) {
//        return LAYER_LOCATION.getModel();
//    }
}
