package com.landmaster.cargoboats.entity.render;

import com.landmaster.cargoboats.entity.Motorboat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import javax.annotation.Nonnull;

public class MotorboatModel extends HierarchicalModel<Motorboat> implements WaterPatchModel {
    private final ModelPart root;
    private final ModelPart main;
    private final ModelPart waterPatch;
    private final ModelPart rotor;

    public MotorboatModel(ModelPart root) {
        this.root = root;
        this.main = root.getChild("main");
        this.waterPatch = root.getChild("waterPatch");
        this.rotor = main.getChild("rotor");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition main = partdefinition.addOrReplaceChild("main", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, -28.0F, -10.0F, 20.0F, 1.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(42, 36).addBox(-10.0F, -35.0F, -10.0F, 20.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(42, 45).addBox(-10.0F, -35.0F, 9.0F, 20.0F, 8.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 36).addBox(-10.0F, -35.0F, -10.0F, 1.0F, 8.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(76, 86).addBox(-9.0F, -40.0F, -6.0F, 12.0F, 12.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(56, 21).addBox(-13.0F, -8.0F, -1.0F, 14.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(19.0F, -27.0F, 1.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r2 = main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(42, 54).addBox(-13.0F, -8.0F, -1.4F, 14.0F, 8.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(18.0F, -27.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition cube_r3 = main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 21).addBox(-14.0F, -1.0F, 0.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(20.0F, -27.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition rotor = main.addOrReplaceChild("rotor", CubeListBuilder.create().texOffs(62, 119).addBox(-2.0F, -1.0F, -1.0F, 6.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(5, 106).addBox(-1.0F, -7.0F, -1.0F, 1.0F, 14.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(23, 107).addBox(-1.0F, -1.0F, -7.0F, 1.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(-14.0F, -31.0F, 0.0F));

        PartDefinition waterPatch = partdefinition.addOrReplaceChild("waterPatch", CubeListBuilder.create().texOffs(3, 2).addBox(-9.0F, -34.0F, -9.0F, 19.0F, 7.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 23.0F, 0.0F));

        PartDefinition cube_r4 = waterPatch.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 21).addBox(-14.0F, -7.0F, 0.0F, 14.0F, 7.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(20.0F, -27.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(@Nonnull PoseStack poseStack, @Nonnull VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Nonnull
    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(@Nonnull Motorboat motorboat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        motorboat.rotorAnimationState.updateTime(ageInTicks, motorboat.rotorSpeed);
        rotor.xRot = (float) Math.toRadians((motorboat.rotorAnimationState.getAccumulatedTime() * 10) % 360);
    }

    @Nonnull
    @Override
    public ModelPart waterPatch() {
        return waterPatch;
    }
}
