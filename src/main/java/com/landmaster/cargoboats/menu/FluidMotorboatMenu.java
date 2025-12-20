package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.FluidMotorboat;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.item.MotorboatUpgradeItemHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FluidMotorboatMenu extends AbstractContainerMenu {
    private @Nullable FluidMotorboat fluidMotorboat = null;

    public FluidMotorboatMenu(int containerId, Inventory playerInventory) {
        this(CargoBoats.FLUID_MOTORBOAT_MENU.get(), containerId, playerInventory,
                new MotorboatUpgradeItemHandler(CargoBoats.MOTORBOAT.get(), Motorboat.NUM_UPGRADES),
                new SimpleContainerData(Motorboat.CONTAINER_SLOTS));
    }

    public FluidMotorboatMenu(int containerId, Inventory playerInventory, FluidMotorboat motorboat) {
        this(CargoBoats.FLUID_MOTORBOAT_MENU.get(), containerId, playerInventory, motorboat.upgradeHandler, motorboat.containerData);
        this.fluidMotorboat = motorboat;
    }

    public FluidMotorboatMenu(MenuType<?> type, int containerId, Inventory playerInventory, MotorboatUpgradeItemHandler upgradeHandler, ContainerData containerData) {
        super(type, containerId);

        for (int i = 0; i< Motorboat.NUM_UPGRADES; ++i) {
            addSlot(new MotorboatUpgradeSlot(upgradeHandler, i, 80 + i*18, 46));
        }

        for (int row=0; row<3; ++row) {
            for (int col=0; col<9; ++col) {
                addSlot(new Slot(playerInventory, row * 9 + col + 9, 8 + col*18, 72 + row*18));
            }
        }

        for (int col=0; col<9; ++col) {
            addSlot(new Slot(playerInventory, col, 8 + col*18, 130));
        }

        addDataSlots(containerData);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < Motorboat.NUM_UPGRADES) {
                if (!this.moveItemStackTo(itemstack1, Motorboat.NUM_UPGRADES, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, Motorboat.NUM_UPGRADES, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return fluidMotorboat == null || player.canInteractWithEntity(fluidMotorboat, 4.0);
    }
}
