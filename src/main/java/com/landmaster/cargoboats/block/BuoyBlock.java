package com.landmaster.cargoboats.block;

import com.landmaster.cargoboats.entity.Motorboat;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
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
        public Pair<Vec3i, Vec3i> getBoxForMotorboatPathfinding() {
            return Pair.of(pos.offset(-1, -1, -1), pos.offset(1, 1, 1));
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
