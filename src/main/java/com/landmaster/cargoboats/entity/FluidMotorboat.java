package com.landmaster.cargoboats.entity;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.Config;
import com.landmaster.cargoboats.menu.FluidMotorboatMenu;
import com.landmaster.cargoboats.network.SyncFluidMotorboatPacket;
import com.landmaster.cargoboats.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidMotorboat extends Motorboat {
    private FluidStack oldFluidStack = FluidStack.EMPTY;
    public final FluidTank tank;

    public FluidMotorboat(EntityType<? extends Motorboat> entityType, Level level) {
        super(entityType, level, 0);
        tank = new FluidTank(Config.MOTORBOAT_BASE_FLUID_CAPACITY.getAsInt());
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
        if (player.isSpectator()) {
            return null;
        } else {
            return new FluidMotorboatMenu(containerId, playerInventory, this);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide) {
            int capacityUpgradeCount = Util.countItem(upgradeHandler, CargoBoats.CAPACITY_UPGRADE.get());
            int newCapacity = capacityUpgradeCount > 0 ? Config.MOTORBOAT_FLUID_CAPACITY.get().get(capacityUpgradeCount - 1)
                    : Config.MOTORBOAT_BASE_FLUID_CAPACITY.getAsInt();
            if (newCapacity != tank.getCapacity() || !FluidStack.matches(oldFluidStack, tank.getFluid())) {
                tank.setCapacity(newCapacity);
                PacketDistributor.sendToPlayersTrackingEntity(this, new SyncFluidMotorboatPacket(getId(), tank.getFluid(), tank.getCapacity()));
                oldFluidStack = tank.getFluid().copy();
            }
        }
    }

    @Override
    public void startSeenByPlayer(@Nonnull ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new SyncFluidMotorboatPacket(getId(), tank.getFluid(), tank.getCapacity()));
    }

    @Nonnull
    @Override
    public Item getDropItem() {
        return CargoBoats.FLUID_MOTORBOAT_ITEM.asItem();
    }
}
