package com.landmaster.cargoboats.item;

import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import javax.annotation.Nonnull;

public class ExpandableItemStackHandler extends ItemStacksResourceHandler {
    @Getter
    private int actualCapacity;

    public ExpandableItemStackHandler(int initialSize) {
        super(NonNullList.create());
        stacks = NonNullList.createWithCapacity(initialSize);
        while (stacks.size() < initialSize) {
            stacks.add(ItemStack.EMPTY);
        }
    }

    public void resize(int newCapacity) {
        while (this.stacks.size() < newCapacity) {
            this.stacks.add(ItemStack.EMPTY);
        }
        while (this.stacks.size() > newCapacity && this.stacks.getLast().isEmpty()) {
            this.stacks.removeLast();
        }
        actualCapacity = newCapacity;
    }

    @Override
    public boolean isValid(int index, @Nonnull ItemResource resource) {
        return index < actualCapacity;
    }

    @Override
    public void serialize(@Nonnull ValueOutput output) {
        super.serialize(output);
        output.putInt("ActualCapacity", actualCapacity);
    }

    @Override
    public void deserialize(@Nonnull ValueInput input) {
        super.deserialize(input);
        actualCapacity = input.getIntOr("ActualCapacity", 0);
    }

    @Nonnull
    @Override
    public ItemResource getResource(int index) {
        return index >= stacks.size() ? ItemResource.EMPTY : super.getResource(index);
    }

    @Override
    public void set(int index, @Nonnull ItemResource resource, int amount) {
        if (index < actualCapacity) super.set(index, resource, amount);
    }

    @Override
    public int extract(int index, @Nonnull ItemResource resource, int amount, @Nonnull TransactionContext transaction) {
        return index < stacks.size() ? super.extract(index, resource, amount, transaction) : 0;
    }

    @Override
    public int insert(int index, @Nonnull ItemResource resource, int amount, @Nonnull TransactionContext transaction) {
        return index < actualCapacity ? super.insert(index, resource, amount, transaction) : 0;
    }

    //    private int actualCapacity;
//
//    public ExpandableItemStackHandler(int initialSlots) {
//        super(NonNullList.createWithCapacity(initialSlots));
//        while (stacks.size() < initialSlots) {
//            stacks.add(ItemStack.EMPTY);
//        }
//        this.actualCapacity = initialSlots;
//    }
//
//    @Override
//    public void setSize(int newCapacity) {
//        while (this.stacks.size() < newCapacity) {
//            this.stacks.add(ItemStack.EMPTY);
//        }
//        while (this.stacks.size() > newCapacity && this.stacks.getLast().isEmpty()) {
//            this.stacks.removeLast();
//        }
//        actualCapacity = newCapacity;
//    }
//
//    public void trim() {
//        setSize(actualCapacity);
//    }
//
//    @Override
//    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
//        return slot < actualCapacity;
//    }
//
//    public int getActualCapacity() {
//        return actualCapacity;
//    }
//
//    @Nonnull
//    @Override
//    public CompoundTag serializeNBT(@Nonnull HolderLookup.Provider provider) {
//        var tag = super.serializeNBT(provider);
//        tag.putInt("ActualCapacity", actualCapacity);
//        return tag;
//    }
//
//    @Override
//    public void deserializeNBT(@Nonnull HolderLookup.Provider provider, @Nonnull CompoundTag nbt) {
//        super.deserializeNBT(provider, nbt);
//        actualCapacity = nbt.getInt("ActualCapacity");
//    }
//
//    @Nonnull
//    @Override
//    public ItemStack getStackInSlot(int slot) {
//        if (slot >= getSlots()) {
//            return ItemStack.EMPTY;
//        }
//        return super.getStackInSlot(slot);
//    }
//
//    @Override
//    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
//        if (slot >= actualCapacity) {
//            return;
//        }
//        super.setStackInSlot(slot, stack);
//    }
//
//    @Nonnull
//    @Override
//    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
//        if (slot >= actualCapacity) {
//            return stack;
//        }
//        return super.insertItem(slot, stack, simulate);
//    }
//
//    @Nonnull
//    @Override
//    public ItemStack extractItem(int slot, int amount, boolean simulate) {
//        if (slot >= getSlots()) {
//            return ItemStack.EMPTY;
//        }
//        return super.extractItem(slot, amount, simulate);
//    }
}
