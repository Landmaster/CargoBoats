package com.landmaster.cargoboats.item;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class MotorboatUpgradeItemHandler extends ItemStackHandler {
    public MotorboatUpgradeItemHandler() {
    }

    public MotorboatUpgradeItemHandler(int slots) {
        super(slots);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof MotorboatUpgrade;
    }

    @Override
    protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
        int limit = super.getStackLimit(slot, stack);
        if (stack.getItem() instanceof MotorboatUpgrade upgrade) {
            limit = Math.min(limit, upgrade.maxUpgradeAmount());
        }
        return limit;
    }
}
