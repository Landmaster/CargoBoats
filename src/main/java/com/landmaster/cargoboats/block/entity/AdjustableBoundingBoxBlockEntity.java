package com.landmaster.cargoboats.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import javax.annotation.Nonnull;

public abstract class AdjustableBoundingBoxBlockEntity extends BlockEntity {
    public int minX;
    public int maxX;
    public int minY;
    public int maxY;
    public int minZ;
    public int maxZ;

    public AdjustableBoundingBoxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(@Nonnull ValueInput input) {
        super.loadAdditional(input);
        minX = input.getByteOr("minX", (byte) 0);
        maxX = input.getByteOr("maxX", (byte) 0);
        minY = input.getByteOr("minY", (byte) 0);
        maxY = input.getByteOr("maxY", (byte) 0);
        minZ = input.getByteOr("minZ", (byte) 0);
        maxZ = input.getByteOr("maxZ", (byte) 0);
    }

    @Override
    protected void saveAdditional(@Nonnull ValueOutput output) {
        super.saveAdditional(output);
        output.putByte("minX", (byte) minX);
        output.putByte("maxX", (byte) maxX);
        output.putByte("minY", (byte) minY);
        output.putByte("maxY", (byte) maxY);
        output.putByte("minZ", (byte) minZ);
        output.putByte("maxZ", (byte) maxZ);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        TagValueOutput output = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING, // Choose to discard all errors
                registries
        );
        saveAdditional(output);
        return output.buildResult();
    }

    public abstract int maxDimension();
}
