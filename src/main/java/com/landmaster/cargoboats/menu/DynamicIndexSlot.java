package com.landmaster.cargoboats.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.IndexModifier;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.world.inventory.StackCopySlot;

import javax.annotation.Nonnull;
import java.util.function.IntSupplier;

public class DynamicIndexSlot extends StackCopySlot {
    private final ResourceHandler<ItemResource> itemHandler;
    protected final IntSupplier indexSupplier;
    protected final IndexModifier<ItemResource> modifier;

    public DynamicIndexSlot(ResourceHandler<ItemResource> itemHandler, IndexModifier<ItemResource> modifier, IntSupplier indexSupplier, int xPosition, int yPosition) {
        super(indexSupplier.getAsInt(), xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.indexSupplier = indexSupplier;
        this.modifier = modifier;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        return itemHandler.isValid(indexSupplier.getAsInt(), ItemResource.of(stack));
    }

    @Nonnull
    @Override
    public ItemStack getStackCopy() {
        int index = indexSupplier.getAsInt();
        return itemHandler.getResource(index).toStack(itemHandler.getAmountAsInt(index));
    }

    // Override if your IItemHandler does not implement IItemHandlerModifiable
    @Override
    public void setStackCopy(@Nonnull ItemStack stack) {
        modifier.set(indexSupplier.getAsInt(), ItemResource.of(stack), stack.getCount());
    }

    @Override
    public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {}

    @Override
    public int getMaxStackSize() {
        return this.itemHandler.getCapacityAsInt(indexSupplier.getAsInt(), ItemResource.EMPTY);
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        return this.itemHandler.getCapacityAsInt(indexSupplier.getAsInt(), ItemResource.of(stack));
    }

    @Override
    public boolean mayPickup(@Nonnull Player playerIn) {
        int index = indexSupplier.getAsInt();
        var resource = itemHandler.getResource(index);
        if (resource.isEmpty()) {
            return false;
        }
        try (var tx = Transaction.openRoot()) {
            // Simulated extraction
            return itemHandler.extract(index, resource, 1, tx) == 1;
        }
    }

    @Override
    public boolean isSameInventory(@Nonnull Slot other) {
        return other instanceof DynamicIndexSlot otherIndexSlot && itemHandler == otherIndexSlot.itemHandler;
    }
}
