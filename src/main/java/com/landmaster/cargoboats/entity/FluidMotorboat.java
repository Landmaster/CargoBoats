package com.landmaster.cargoboats.entity;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.Config;
import com.landmaster.cargoboats.menu.FluidMotorboatMenu;
import com.landmaster.cargoboats.network.SyncFluidMotorboatPacket;
import com.landmaster.cargoboats.util.AdjustableCapacityTank;
import com.landmaster.cargoboats.util.Util;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidMotorboat extends Motorboat {
    public final AdjustableCapacityTank tank;

    public FluidMotorboat(EntityType<? extends Motorboat> entityType, Level level) {
        super(entityType, level, 0, CargoBoats.FLUID_MOTORBOAT_ITEM::get);
        tank = new AdjustableCapacityTank(1, Config.MOTORBOAT_BASE_FLUID_CAPACITY.getAsInt()) {
            @Override
            protected void onContentsChanged(int index, @Nonnull FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                PacketDistributor.sendToPlayersTrackingEntity(
                        FluidMotorboat.this, new SyncFluidMotorboatPacket(getId(), tank.getResource(0).toStack(tank.getAmountAsInt(0)),
                                tank.getCapacityAsInt(0, tank.getResource(0))));
            }

            @Override
            public void onCapacityChanged(int previousCapacity) {
                super.onCapacityChanged(previousCapacity);
                PacketDistributor.sendToPlayersTrackingEntity(
                        FluidMotorboat.this, new SyncFluidMotorboatPacket(getId(), tank.getResource(0).toStack(tank.getAmountAsInt(0)),
                                tank.getCapacityAsInt(0, tank.getResource(0))));
            }
        };
    }

    public FluidMotorboat(Level level, double x, double y, double z) {
        this(CargoBoats.FLUID_MOTORBOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    public void addMotorboatSaveData(ValueOutput output) {
        super.addMotorboatSaveData(output);
        output.putChild("FluidTank", tank);
    }

    @Override
    public void readMotorboatSaveData(ValueInput input) {
        super.readMotorboatSaveData(input);
        input.readChild("FluidTank", tank);
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
        if (!level().isClientSide()) {
            int capacityUpgradeCount = Util.countItem(upgradeHandler, CargoBoats.CAPACITY_UPGRADE.get());
            int newCapacity = capacityUpgradeCount > 0 ? Config.MOTORBOAT_FLUID_CAPACITY.get().get(capacityUpgradeCount - 1)
                    : Config.MOTORBOAT_BASE_FLUID_CAPACITY.getAsInt();
            if (newCapacity != tank.getCapacityAsInt(0, tank.getResource(0))) {
                tank.setCapacity(newCapacity);
            }
        }
    }

    @Override
    public void startSeenByPlayer(@Nonnull ServerPlayer serverPlayer) {
        PacketDistributor.sendToPlayer(serverPlayer, new SyncFluidMotorboatPacket(getId(), tank.getResource(0).toStack(tank.getAmountAsInt(0)),
                tank.getCapacityAsInt(0, tank.getResource(0))));
    }
}
