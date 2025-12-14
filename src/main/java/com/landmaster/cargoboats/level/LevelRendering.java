package com.landmaster.cargoboats.level;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.network.TrackMotorboatPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;

@EventBusSubscriber
public class LevelRendering {
    @SubscribeEvent
    private static void onRenderBlockOutline(RenderHighlightEvent.Block event) {
        var level = Minecraft.getInstance().level;
        if (level != null) {
            var cap = level.getCapability(CargoBoats.MOTORBOAT_PATHFINDING_NODE, event.getTarget().getBlockPos());
            if (cap != null) {
                var box = cap.getBoxForMotorboatPathfinding();
                var cameraPos = event.getCamera().getPosition();
                renderShape(
                        event.getPoseStack(),
                        event.getMultiBufferSource().getBuffer(RenderType.lines()),
                        Shapes.create(AABB.encapsulatingFullBlocks(new BlockPos(box.first()), new BlockPos(box.second()))),
                        -cameraPos.x,
                        -cameraPos.y,
                        -cameraPos.z,
                        0.0f, 0.0f, 1.0f, 1.0f);
            }
        }
    }

    @SubscribeEvent
    private static void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            if (event.getCamera().getEntity() instanceof Player player) {
                if (TrackMotorboatPacket.trackedPos != null) {
                    var consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
                    var poseStack = event.getPoseStack();
                    var pose = poseStack.last();
                    var cameraPos = event.getCamera().getPosition();
                    var startPos = player.position().subtract(cameraPos).toVector3f();
                    var endPos = cameraPos.toVector3f().sub(TrackMotorboatPacket.trackedPos).negate();
                    var diff = new Vector3f(endPos).sub(startPos);
                    if (diff.lengthSquared() > 0.001) {
                        diff.normalize();
                        consumer.addVertex(pose, startPos).setColor(0, 255, 0, 255).setNormal(pose, diff.x, diff.y, diff.z);
                        consumer.addVertex(pose, endPos).setColor(0, 255, 0, 255).setNormal(pose, diff.x, diff.y, diff.z);
                    }
                }
            }
        }
    }

    private static void renderShape(
            PoseStack poseStack,
            VertexConsumer consumer,
            VoxelShape shape,
            double x,
            double y,
            double z,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        PoseStack.Pose posestack$pose = poseStack.last();
        shape.forAllEdges(
                (p_323073_, p_323074_, p_323075_, p_323076_, p_323077_, p_323078_) -> {
                    float f = (float)(p_323076_ - p_323073_);
                    float f1 = (float)(p_323077_ - p_323074_);
                    float f2 = (float)(p_323078_ - p_323075_);
                    float f3 = Mth.sqrt(f * f + f1 * f1 + f2 * f2);
                    f /= f3;
                    f1 /= f3;
                    f2 /= f3;
                    consumer.addVertex(posestack$pose, (float)(p_323073_ + x), (float)(p_323074_ + y), (float)(p_323075_ + z))
                            .setColor(red, green, blue, alpha)
                            .setNormal(posestack$pose, f, f1, f2);
                    consumer.addVertex(posestack$pose, (float)(p_323076_ + x), (float)(p_323077_ + y), (float)(p_323078_ + z))
                            .setColor(red, green, blue, alpha)
                            .setNormal(posestack$pose, f, f1, f2);
                }
        );
    }
}
