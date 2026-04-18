package com.landmaster.cargoboats.entity.render;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class FluidMotorboatModel extends MotorboatModel {
    public FluidMotorboatModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition boat = partdefinition.addOrReplaceChild("boat", CubeListBuilder.create().texOffs(16, 3).addBox(-1.99F, -2.0F, -7.0F, 2.0F, 4.0F, 17.0F, new CubeDeformation(0.0F))
                .texOffs(16, 3).mirror().addBox(-12.01F, -2.0F, -7.0F, 2.0F, 4.0F, 17.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 25).addBox(-11.0F, 2.0F, -9.0F, 10.0F, 2.0F, 19.0F, new CubeDeformation(0.0F))
                .texOffs(0, 13).addBox(-11.0F, -2.0F, -9.0F, 10.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(15, 7).addBox(-10.0F, -2.0F, 9.0F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(1, 25).addBox(-8.0F, -3.0F, 9.01F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(2, 48).addBox(-10.0F, -6.01F, 1.99F, 8.0F, 8.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, 20.0F, -1.0F));

        PartDefinition propeller = boat.addOrReplaceChild("propeller", CubeListBuilder.create().texOffs(1, 34).addBox(-4.0F, -4.0F, 2.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.offset(-6.0F, 0.0F, 12.0F));

        PartDefinition cube_r1 = propeller.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(11, 25).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -2.3562F));

        PartDefinition cube_r2 = propeller.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(11, 25).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 1.0F, 0.0F, 0.0F, -0.7854F));

        //PartDefinition waterPatch = partdefinition.addOrReplaceChild("waterPatch", CubeListBuilder.create().texOffs(-20, -14).addBox(-4.0F, -6.0F, -8.0F, 8.0F, 6.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}
