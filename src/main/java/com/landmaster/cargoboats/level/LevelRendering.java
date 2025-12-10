package com.landmaster.cargoboats.level;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3f;

import java.util.Arrays;

@EventBusSubscriber(Dist.CLIENT)
public class LevelRendering {
    @SubscribeEvent
    private static void onRender(RenderLevelStageEvent event) {
//        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
//            var camera = event.getCamera();
//            if (camera.getEntity() instanceof Player player) {
//                Arrays.stream(InteractionHand.values())
//                        .map(player::getItemInHand)
//                        .filter(item -> item.is(CargoBoats.MOTORBOAT_PROGRAMMER))
//                        .findFirst()
//                        .ifPresent(programmer -> {
//
//                        });
//            }
//        }

//        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
//            var camera = event.getCamera();
//            float camX = (float)camera.getPosition().x;
//            float camY = (float)camera.getPosition().y;
//            float camZ = (float)camera.getPosition().z;
//            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
//            var vertexConsumer = bufferSource.getBuffer(RenderType.lines());
//            var motorboats = Minecraft.getInstance().level.getEntitiesOfClass(
//                    Motorboat.class, AABB.ofSize(Minecraft.getInstance().player.position(), 50, 50, 50)
//            );
//            for (var motorboat: motorboats) {
//                for (int i=0; i+1<motorboat.path.size(); ++i) {
//                    var point = motorboat.path.get(i);
//                    var point2 = motorboat.path.get(i+1);
//                    var normal = new Vector3f(point2.getX() - point.getX(), point2.getY() - point.getY(), point2.getZ() - point.getZ()).normalize();
//                    vertexConsumer.addVertex(point.getX() - camX, point.getY() - camY, point.getZ() - camZ).setColor(0, 255, 0, 255).setNormal(normal.x, normal.y, normal.z);
//                    vertexConsumer.addVertex(point2.getX() - camX, point2.getY() - camY, point2.getZ() - camZ).setColor(0, 255, 0, 255).setNormal(normal.x, normal.y, normal.z);
//                }
//            }
//        }
    }
}
