package com.landmaster.cargoboats.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.minX = tag.getByte("minX");
        this.maxX = tag.getByte("maxX");
        this.minY = tag.getByte("minY");
        this.maxY = tag.getByte("maxY");
        this.minZ = tag.getByte("minZ");
        this.maxZ = tag.getByte("maxZ");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("minX", (byte) this.minX);
        tag.putByte("maxX", (byte) this.maxX);
        tag.putByte("minY", (byte) this.minY);
        tag.putByte("maxY", (byte) this.maxY);
        tag.putByte("minZ", (byte) this.minZ);
        tag.putByte("maxZ", (byte) this.maxZ);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        var result = super.getUpdateTag(registries);
        saveAdditional(result, registries);
        return result;
    }

    public abstract int maxDimension();
}
