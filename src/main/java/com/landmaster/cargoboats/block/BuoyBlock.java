package com.landmaster.cargoboats.block;

import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;

public class BuoyBlock extends Block {
    protected static final VoxelShape AABB = Block.box(4.0, 0.0, 4.0, 12.0, 14.0, 12.0);

    public BuoyBlock(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    protected VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return AABB;
    }

    public record PathfindingNode(Level level, BlockPos pos) implements MotorboatPathfindingNode {
        @Override
        public Pair<BlockPos, BlockPos> getBoxForMotorboatPathfinding() {
            return Pair.of(pos.offset(0, -1, 0), pos.offset(0, 1, 0));
        }

        @Override
        public boolean doBoatHorn() {
            return false;
        }

        @Override
        public int defaultStopTime() {
            return 0;
        }
    }
}
