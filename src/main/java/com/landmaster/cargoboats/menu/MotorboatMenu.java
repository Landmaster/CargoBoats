package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.item.MotorboatUpgrade;
import com.landmaster.cargoboats.item.MotorboatUpgradeItemHandler;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class MotorboatMenu extends AbstractContainerMenu {
    private @Nullable Motorboat motorboat = null;
    public int page = 0;

    public MotorboatMenu(int containerId, Inventory playerInventory) {
        this(CargoBoats.MOTORBOAT_MENU.get(), containerId, playerInventory,
                new MotorboatUpgradeItemHandler(CargoBoats.MOTORBOAT.get(), Motorboat.NUM_UPGRADES),
                        new ItemStackHandler(27 ), new SimpleContainerData(Motorboat.CONTAINER_SLOTS), false);
    }

    public MotorboatMenu(int containerId, Inventory playerInventory, Motorboat motorboat) {
        this(CargoBoats.MOTORBOAT_MENU.get(), containerId, playerInventory, motorboat.upgradeHandler,
                motorboat.itemHandler, motorboat.containerData, true);
        this.motorboat = motorboat;
    }

    public MotorboatMenu(MenuType<?> type, int containerId, Inventory playerInventory, MotorboatUpgradeItemHandler upgradeHandler,
                         IItemHandler itemHandler, ContainerData containerData, boolean paginateItems) {
        super(type, containerId);

        for (int i=0; i<Motorboat.NUM_UPGRADES; ++i) {
            addSlot(new MotorboatUpgradeSlot(upgradeHandler, i, 80 + i*18, 46));
        }

        for (int row=0; row<3; ++row) {
            for (int col=0; col<9; ++col) {
                final int theRow = row, theCol = col;
                addSlot(new DynamicIndexSlot(itemHandler, () -> (paginateItems ? page * 27 : 0) + theRow * 9 + theCol,
                        8 + col * 18, 72 + row * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return page < dataSlots.get(2).get() && super.mayPlace(stack);
                    }
                });
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

    public Optional<Motorboat> getMotorboat() {
        return Optional.ofNullable(motorboat);
    }


    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < Motorboat.NUM_UPGRADES + 27) {
                if (!this.moveItemStackTo(itemstack1, Motorboat.NUM_UPGRADES + 27, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0,
                    itemstack1.getItem() instanceof MotorboatUpgrade ? Motorboat.NUM_UPGRADES : Motorboat.NUM_UPGRADES + 27, false)) {
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
        return motorboat == null || player.canInteractWithEntity(motorboat, 4.0);
    }
}
