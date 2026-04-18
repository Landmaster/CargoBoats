package com.landmaster.cargoboats.level;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.entity.MotorboatDetectorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nullable;
import java.util.Arrays;

@EventBusSubscriber(Dist.CLIENT)
public class LevelRendering {
    @Nullable
    public static Vector3fc trackedPos = null;

    @SubscribeEvent
    private static void onLevelRender(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        var mc = Minecraft.getInstance();
        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        var cameraPos = event.getLevelRenderState().cameraRenderState.pos;

        if (trackedPos != null) {
            var consumer = bufferSource.getBuffer(RenderTypes.lines());
            var poseStack = event.getPoseStack();
            var pose = poseStack.last();
            float partialTick = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
            float startX = (float)(Mth.lerp(partialTick, mc.player.xOld, mc.player.position().x) - cameraPos.x);
            float startY = (float)(Mth.lerp(partialTick, mc.player.yOld, mc.player.position().y) - cameraPos.y + mc.player.getEyeHeight() * 0.5);
            float startZ = (float)(Mth.lerp(partialTick, mc.player.zOld, mc.player.position().z) - cameraPos.z);
            var endPos = cameraPos.toVector3f().sub(trackedPos).negate();
            var diff = new Vector3f(endPos).sub(startX, startY, startZ);
            if (diff.lengthSquared() > 0.001) {
                diff.normalize();
                consumer.addVertex(pose, startX, startY, startZ).setColor(0, 255, 0, 255).setNormal(pose, diff.x, diff.y, diff.z).setLineWidth(2);
                consumer.addVertex(pose, endPos).setColor(0, 255, 0, 255).setNormal(pose, diff.x, diff.y, diff.z).setLineWidth(2);
            }
        }

        Arrays.stream(InteractionHand.values())
                .map(mc.player::getItemInHand)
                .filter(stack -> stack.is(CargoBoats.MOTORBOAT_PROGRAMMER))
                .findFirst()
                .ifPresent(programmer -> {
                    var schedule = programmer.get(CargoBoats.MOTORBOAT_SCHEDULE);
                    var dockChecked = new LongOpenHashSet();
                    for (var entry: schedule.entries()) {
                        if (entry.dimension() == mc.player.level().dimension()
                                && !dockChecked.contains(entry.dock().asLong())
                                && mc.player.level().isLoaded(entry.dock())) {
                            dockChecked.add(entry.dock().asLong());
                            renderShape(
                                    event.getPoseStack(),
                                    bufferSource.getBuffer(RenderTypes.lines()),
                                    Shapes.create(AABB.encapsulatingFullBlocks(entry.dock(), entry.dock())),
                                    -cameraPos.x,
                                    -cameraPos.y,
                                    -cameraPos.z,
                                    1.0f, 0.0f, 0.0f, 1.0f);
                        }
                    }
                });

        if (Minecraft.getInstance().hitResult instanceof BlockHitResult blockHitResult) {
            var cap = mc.level.getCapability(CargoBoats.MOTORBOAT_PATHFINDING_NODE, blockHitResult.getBlockPos());
            if (cap != null) {
                var box = cap.getBoxForMotorboatPathfinding();
                renderShape(
                        event.getPoseStack(),
                        bufferSource.getBuffer(RenderTypes.lines()),
                        Shapes.create(AABB.encapsulatingFullBlocks(new BlockPos(box.first()), new BlockPos(box.second()))),
                        -cameraPos.x,
                        -cameraPos.y,
                        -cameraPos.z,
                        0.0f, 0.0f, 1.0f, 1.0f);
            } else {
                var te = mc.level.getBlockEntity(blockHitResult.getBlockPos());
                if (te instanceof MotorboatDetectorBlockEntity detector) {
                    renderShape(
                            event.getPoseStack(),
                            bufferSource.getBuffer(RenderTypes.lines()),
                            Shapes.create(detector.getDetectionBox()),
                            -cameraPos.x,
                            -cameraPos.y,
                            -cameraPos.z,
                            1.0f, 0.0f, 1.0f, 1.0f);
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
                            .setNormal(posestack$pose, f, f1, f2)
                            .setLineWidth(1);
                    consumer.addVertex(posestack$pose, (float)(p_323076_ + x), (float)(p_323077_ + y), (float)(p_323078_ + z))
                            .setColor(red, green, blue, alpha)
                            .setNormal(posestack$pose, f, f1, f2)
                            .setLineWidth(1);
                }
        );
    }
}
