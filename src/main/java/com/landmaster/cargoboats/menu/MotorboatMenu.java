package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.item.MotorboatUpgrade;
import com.landmaster.cargoboats.item.MotorboatUpgradeItemHandler;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class MotorboatMenu extends AbstractContainerMenu {
    private final IItemHandler itemHandler;
    private @Nullable Motorboat motorboat = null;

    public MotorboatMenu(int containerId, Inventory playerInventory) {
        this(CargoBoats.MOTORBOAT_MENU.get(), containerId, playerInventory,
                new CombinedInvWrapper(
                        new MotorboatUpgradeItemHandler(CargoBoats.MOTORBOAT.get(), Motorboat.NUM_UPGRADES),
                        new ItemStackHandler(27 )), new SimpleContainerData(2));
    }

    public MotorboatMenu(int containerId, Inventory playerInventory, Motorboat motorboat) {
        this(CargoBoats.MOTORBOAT_MENU.get(), containerId, playerInventory, motorboat.combinedHandler, motorboat.containerData);
        this.motorboat = motorboat;
    }

    public MotorboatMenu(MenuType<?> type, int containerId, Inventory playerInventory, IItemHandler itemHandler, ContainerData containerData) {
        super(type, containerId);

        this.itemHandler = itemHandler;

        for (int i=0; i<Motorboat.NUM_UPGRADES; ++i) {
            addSlot(new SlotItemHandler(itemHandler, i, 80 + i*18, 46) {
                @Override
                public int getMaxStackSize(@Nonnull ItemStack stack) {
                    int limit = super.getMaxStackSize(stack);
                    if (stack.getItem() instanceof MotorboatUpgrade upgrade) {
                        limit = Math.min(limit, upgrade.maxUpgradeAmount(CargoBoats.MOTORBOAT.get()));
                    }
                    return limit;
                }
            });
        }

        for (int row=0; row<3; ++row) {
            for (int col=0; col<9; ++col) {
                addSlot(new SlotItemHandler(itemHandler, Motorboat.NUM_UPGRADES + row * 9 + col, 8 + col*18, 72 + row*18));
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
            if (index < itemHandler.getSlots()) {
                if (!this.moveItemStackTo(itemstack1, itemHandler.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0,
                    itemstack1.getItem() instanceof MotorboatUpgrade ? Motorboat.NUM_UPGRADES : itemHandler.getSlots(), false)) {
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
