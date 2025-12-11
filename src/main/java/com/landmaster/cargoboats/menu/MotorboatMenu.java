package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class MotorboatMenu extends AbstractContainerMenu {
    public final Container container;

    public MotorboatMenu(int containerId, Inventory playerInventory) {
        this(CargoBoats.MOTORBOAT_MENU.get(), containerId, playerInventory, new SimpleContainer(36), new SimpleContainerData(2));
    }

    public MotorboatMenu(int containerId, Inventory playerInventory, Motorboat motorboat) {
        this(CargoBoats.MOTORBOAT_MENU.get(), containerId, playerInventory, motorboat, motorboat.containerData);
    }

    public MotorboatMenu(MenuType<?> type, int containerId, Inventory playerInventory, Container container, ContainerData containerData) {
        super(type, containerId);

        this.container = container;

        for (int row=0; row<container.getContainerSize()/9; ++row) {
            for (int col=0; col<9; ++col) {
                addSlot(new Slot(container, row * 9 + col, 8 + col*18, 54 + row*18));
            }
        }

        for (int row=0; row<3; ++row) {
            for (int col=0; col<9; ++col) {
                addSlot(new Slot(playerInventory, row * 9 + col + 9, 8 + col*18, 139 + row*18));
            }
        }

        for (int col=0; col<9; ++col) {
            addSlot(new Slot(playerInventory, col, 8 + col*18, 197));
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
            if (index < 45) {
                if (!this.moveItemStackTo(itemstack1, container.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, container.getContainerSize(), false)) {
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
        return container.stillValid(player);
    }
}
