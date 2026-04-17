package com.landmaster.cargoboats.entity.render;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import javax.annotation.Nonnull;

public class MotorboatModel extends EntityModel<MotorboatRenderState> {
    private final ModelPart main;
    private final ModelPart waterPatch;
    private final ModelPart rotor;

    public MotorboatModel(ModelPart root) {
        super(root);
        this.main = root.getChild("boat");
        this.waterPatch = root.getChild("waterPatch");
        this.rotor = main.getChild("propeller");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition boat = partdefinition.addOrReplaceChild("boat", CubeListBuilder.create().texOffs(16, 3).addBox(-2.0F, -2.0F, -7.0F, 2.0F, 4.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(16, 3).mirror().addBox(-12.0F, -2.0F, -7.0F, 2.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 25).addBox(-11.0F, 2.0F, -9.0F, 10.0F, 2.0F, 19.0F, new CubeDeformation(0.0F))
                .texOffs(0, 13).addBox(-11.0F, -2.0F, -9.0F, 10.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-10.0F, -2.0F, 2.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(1, 25).addBox(-8.0F, -3.0F, 9.0F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 20.0F, -1.0F));

        PartDefinition propeller = boat.addOrReplaceChild("propeller", CubeListBuilder.create().texOffs(1, 34).addBox(-4.0F, -4.0F, 2.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offset(-6.0F, 0.0F, 12.0F));

        PartDefinition cube_r1 = propeller.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(11, 25).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -2.3562F));

        PartDefinition cube_r2 = propeller.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(11, 25).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition waterPatch = partdefinition.addOrReplaceChild("waterPatch", CubeListBuilder.create().texOffs(-13, -7).addBox(-4.0F, -6.0F, -8.0F, 8.0F, 6.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(MotorboatRenderState state) {
        super.setupAnim(state);
        state.rotorRotation = 0;
    }

//    @Override
//    public void setupAnim(@Nonnull Motorboat motorboat, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
//        motorboat.rotorAnimationState.updateTime(ageInTicks, motorboat.rotorSpeed * 10);
//        rotor.zRot = (float) Math.toRadians(motorboat.rotorAnimationState.getAccumulatedTime() % 360);
//    }

    @Nonnull
    public ModelPart waterPatch() {
        return waterPatch;
    }
}
