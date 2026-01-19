package com.landmaster.cargoboats.block.entity;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.MotorboatDetectorBlock;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.menu.AdjustableBoundingBoxMenu;
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

public class MotorboatDetectorBlockEntity extends AdjustableBoundingBoxBlockEntity implements MenuProvider {
    public int output = 0;

    public MotorboatDetectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(CargoBoats.MOTORBOAT_DETECTOR_TE.get(), pos, blockState);
        minX = minY = minZ = -maxDimension();
        maxX = maxY = maxZ = -minX;
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
        this.output = tag.getByte("output");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("output", (byte)this.output);
    }

    @Override
    public int maxDimension() {
        return 3;
    }

    public AABB getDetectionBox() {
        return AABB.encapsulatingFullBlocks(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))
                .move(worldPosition);
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return CargoBoats.MOTORBOAT_DETECTOR.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return new AdjustableBoundingBoxMenu(containerId, this);
    }
}
