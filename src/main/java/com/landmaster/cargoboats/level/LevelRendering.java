package com.landmaster.cargoboats.level;

import com.landmaster.cargoboats.CargoBoats;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

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

    private static final Method getEntitesMeth;

    static {
        try {
            getEntitesMeth = Level.class.getDeclaredMethod("getEntities");
            getEntitesMeth.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    private static void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
            if (event.getCamera().getEntity() instanceof Player player) {
                Arrays.stream(InteractionHand.values())
                        .map(player::getItemInHand)
                        .map(stack -> stack.get(CargoBoats.TRACKED_MOTORBOAT))
                        .filter(Objects::nonNull)
                        .map(uuid -> {
                            try {
                                return ((LevelEntityGetter<Entity>)getEntitesMeth.invoke(player.level())).get(uuid);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .filter(Objects::nonNull)
                        .findFirst()
                        .ifPresent(motorboat -> {
                            var consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());
                            var poseStack = event.getPoseStack();
                            var pose = poseStack.last();
                            var cameraPos = event.getCamera().getPosition();
                            var startPos = player.position().subtract(cameraPos);
                            var endPos = motorboat.position().subtract(cameraPos);
                            var diff = endPos.subtract(startPos);
                            if (diff.lengthSqr() > 0.001) {
                                var normal = diff.normalize();
                                consumer.addVertex(pose, startPos.toVector3f()).setColor(0, 255, 0, 255).setNormal((float) normal.x, (float) normal.y, (float) normal.z);
                                consumer.addVertex(pose, endPos.toVector3f()).setColor(0, 255, 0, 255).setNormal((float) normal.x, (float) normal.y, (float) normal.z);
                            }
                        });
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
