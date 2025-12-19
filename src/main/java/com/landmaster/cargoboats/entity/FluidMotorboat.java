package com.landmaster.cargoboats.entity;

import com.landmaster.cargoboats.CargoBoats;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidMotorboat extends Motorboat {
    public final FluidTank tank;

    public FluidMotorboat(EntityType<? extends Motorboat> entityType, Level level) {
        super(entityType, level, 0);
        tank = new FluidTank(64000);
    }

    public FluidMotorboat(Level level, double x, double y, double z) {
        this(CargoBoats.FLUID_MOTORBOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.put("FluidTank", tank.writeToNBT(registryAccess(), new CompoundTag()));
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        tank.readFromNBT(registryAccess(), compound.getCompound("FluidTank"));
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return null;
    }
}
