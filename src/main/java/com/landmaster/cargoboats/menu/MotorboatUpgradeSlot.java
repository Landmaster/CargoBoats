package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.item.MotorboatUpgrade;
import com.landmaster.cargoboats.item.MotorboatUpgradeItemHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class MotorboatUpgradeSlot extends SlotItemHandler {
    public MotorboatUpgradeSlot(MotorboatUpgradeItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        int limit = super.getMaxStackSize(stack);
        if (stack.getItem() instanceof MotorboatUpgrade upgrade) {
            limit = Math.min(limit, upgrade.maxUpgradeAmount(
                    ((MotorboatUpgradeItemHandler)getItemHandler()).entityType
            ));
        }
        return limit;
    }
}
