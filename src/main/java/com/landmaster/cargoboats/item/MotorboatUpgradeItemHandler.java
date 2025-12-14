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
        if (!(stack.getItem() instanceof MotorboatUpgrade)) {
            return false;
        }
        for (int i=0; i<stacks.size(); ++i) {
            if (i != slot && stacks.get(i).getItem() == stack.getItem()) {
                return false;
            }
        }
        return true;
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
