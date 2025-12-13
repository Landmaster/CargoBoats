package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.util.MotorboatSchedule;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class MotorboatProgrammerMenu extends AbstractContainerMenu {
    private final ItemStack stack;

    public MotorboatProgrammerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extra) {
        this(containerId, CargoBoats.MOTORBOAT_PROGRAMMER.get().withSchedule(MotorboatSchedule.STREAM_CODEC.decode(extra)));
    }

    public MotorboatProgrammerMenu(int containerId, ItemStack stack) {
        super(CargoBoats.MOTORBOAT_PROGRAMMER_MENU.get(), containerId);
        this.stack = stack;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return true;
    }

    public MotorboatSchedule getSchedule() {
        return stack.getOrDefault(CargoBoats.MOTORBOAT_SCHEDULE, MotorboatSchedule.INITIAL);
    }

    public void setSchedule(MotorboatSchedule schedule) {
        stack.set(CargoBoats.MOTORBOAT_SCHEDULE, schedule);
    }
}
