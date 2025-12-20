package com.landmaster.cargoboats.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.function.IntSupplier;

public class DynamicIndexSlot extends Slot {
    private static final Container emptyInventory = new SimpleContainer(0);
    private final IItemHandler itemHandler;
    protected final IntSupplier indexSupplier;

    public DynamicIndexSlot(IItemHandler itemHandler, IntSupplier indexSupplier, int xPosition, int yPosition) {
        super(emptyInventory, indexSupplier.getAsInt(), xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.indexSupplier = indexSupplier;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        return itemHandler.isItemValid(indexSupplier.getAsInt(), stack);
    }

    @Nonnull
    @Override
    public ItemStack getItem() {
        return this.getItemHandler().getStackInSlot(indexSupplier.getAsInt());
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    @Override
    public void set(@Nonnull ItemStack stack) {
        ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(indexSupplier.getAsInt(), stack);
        this.setChanged();
    }

    @Override
    public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {}

    @Override
    public int getMaxStackSize() {
        return this.itemHandler.getSlotLimit(indexSupplier.getAsInt());
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Math.min(stack.getMaxStackSize(), this.itemHandler.getSlotLimit(indexSupplier.getAsInt()));
    }

    @Override
    public boolean mayPickup(@Nonnull Player playerIn) {
        return !this.getItemHandler().extractItem(indexSupplier.getAsInt(), 1, true).isEmpty();
    }

    @Nonnull
    @Override
    public ItemStack remove(int amount) {
        return this.getItemHandler().extractItem(indexSupplier.getAsInt(), amount, false);
    }

    public IItemHandler getItemHandler() {
        return itemHandler;
    }
}
