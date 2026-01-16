package com.landmaster.cargoboats.block.entity;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.MotorboatDetectorBlock;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.menu.MotorboatDetectorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MotorboatDetectorBlockEntity extends BlockEntity implements MenuProvider {
    public int minX = -3;
    public int maxX = 3;
    public int minY = -3;
    public int maxY = 3;
    public int minZ = -3;
    public int maxZ = 3;
    public int output = 0;

    public MotorboatDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(CargoBoats.MOTORBOAT_DETECTOR_TE.get(), pos, blockState);
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T t) {
        if (t instanceof MotorboatDetectorBlockEntity te) {
            te.output = level.getEntitiesOfClass(Motorboat.class, te.getDetectionBox()).isEmpty() ? 0 : 15;
            boolean newPowered = te.output > 0;
            if (newPowered != state.getValue(MotorboatDetectorBlock.POWERED)) {
                level.setBlockAndUpdate(pos, state.setValue(MotorboatDetectorBlock.POWERED, newPowered));
            }
        }
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
        this.output = tag.getByte("output");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("minX", (byte)this.minX);
        tag.putByte("maxX", (byte)this.maxX);
        tag.putByte("minY", (byte)this.minY);
        tag.putByte("maxY", (byte)this.maxY);
        tag.putByte("minZ", (byte)this.minZ);
        tag.putByte("maxZ", (byte)this.maxZ);
        tag.putByte("output", (byte)this.output);
    }

    public AABB getDetectionBox() {
        return AABB.encapsulatingFullBlocks(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))
                .move(worldPosition);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        var result = super.getUpdateTag(registries);
        saveAdditional(result, registries);
        return result;
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return CargoBoats.MOTORBOAT_DETECTOR.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return new MotorboatDetectorMenu(containerId, this);
    }
}
