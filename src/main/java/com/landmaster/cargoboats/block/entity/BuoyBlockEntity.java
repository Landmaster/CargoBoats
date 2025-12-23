package com.landmaster.cargoboats.block.entity;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.MotorboatPathfindingNode;
import com.landmaster.cargoboats.menu.BuoyMenu;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BuoyBlockEntity extends BlockEntity implements MotorboatPathfindingNode, MenuProvider {
    public int offsetX = 0, offsetZ = 0;

    public BuoyBlockEntity(BlockPos pos, BlockState blockState) {
        super(CargoBoats.BUOY_TE.get(), pos, blockState);
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        offsetX = tag.getByte("OffsetX");
        offsetZ = tag.getByte("OffsetZ");
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("OffsetX", (byte) offsetX);
        tag.putByte("OffsetZ", (byte) offsetZ);
    }

    @Override
    public Pair<BlockPos, BlockPos> getBoxForMotorboatPathfinding() {
        return Pair.of(worldPosition.offset(offsetX, -1, offsetZ), worldPosition.offset(offsetX, 1, offsetZ));
    }

    @Override
    public boolean doBoatHorn() {
        return false;
    }

    @Override
    public int defaultStopTime() {
        return 0;
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return CargoBoats.BUOY.get().getName();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return new BuoyMenu(containerId, this);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        var result = new CompoundTag();
        saveAdditional(result, registries);
        return result;
    }
}
