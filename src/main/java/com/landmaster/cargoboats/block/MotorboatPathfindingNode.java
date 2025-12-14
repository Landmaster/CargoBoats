package com.landmaster.cargoboats.block;

import com.landmaster.cargoboats.entity.Motorboat;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;

public interface MotorboatPathfindingNode {
    Pair<Vec3i, Vec3i> getBoxForMotorboatPathfinding();

    default boolean isMotorboatDocked(Motorboat motorboat) {
        var pair = getBoxForMotorboatPathfinding();
        return motorboat.getBoundingBox().intersects(
                AABB.encapsulatingFullBlocks(new BlockPos(pair.first()), new BlockPos(pair.second()))
        );
    }

    boolean doBoatHorn();

    int defaultStopTime();
}
