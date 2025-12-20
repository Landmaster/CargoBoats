package com.landmaster.cargoboats.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class ExpandableItemStackHandler extends ItemStackHandler {
    private int actualCapacity;

    public ExpandableItemStackHandler(int initialSlots) {
        super(NonNullList.createWithCapacity(initialSlots));
        while (stacks.size() < initialSlots) {
            stacks.add(ItemStack.EMPTY);
        }
        this.actualCapacity = initialSlots;
    }

    @Override
    public void setSize(int newCapacity) {
        while (this.stacks.size() < newCapacity) {
            this.stacks.add(ItemStack.EMPTY);
        }
        while (this.stacks.size() > newCapacity && this.stacks.getLast().isEmpty()) {
            this.stacks.removeLast();
        }
        actualCapacity = newCapacity;
    }

    public void trim() {
        setSize(actualCapacity);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return slot < actualCapacity;
    }

    public int getActualCapacity() {
        return actualCapacity;
    }

    @Nonnull
    @Override
    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
        var tag = super.serializeNBT(provider);
        tag.putInt("ActualCapacity", actualCapacity);
        return tag;
    }

    @Override
    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, @Nonnull CompoundTag nbt) {
        super.deserializeNBT(provider, nbt);
        actualCapacity = nbt.getInt("ActualCapacity");
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot >= getSlots()) {
            return ItemStack.EMPTY;
        }
        return super.getStackInSlot(slot);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (slot >= actualCapacity) {
            return;
        }
        super.setStackInSlot(slot, stack);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot >= actualCapacity) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot >= getSlots()) {
            return ItemStack.EMPTY;
        }
        return super.extractItem(slot, amount, simulate);
    }
}
