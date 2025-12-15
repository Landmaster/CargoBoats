package com.landmaster.cargoboats.block;

import com.landmaster.cargoboats.entity.Motorboat;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public interface MotorboatPathfindingNode {
    Pair<BlockPos, BlockPos> getBoxForMotorboatPathfinding();

    default boolean isMotorboatDocked(Motorboat motorboat) {
        var pair = getBoxForMotorboatPathfinding();
        return motorboat.getBoundingBox().intersects(
                AABB.encapsulatingFullBlocks(pair.first(), pair.second())
        );
    }

    boolean doBoatHorn();

    int defaultStopTime();
}
