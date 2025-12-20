package com.landmaster.cargoboats.util;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.items.IItemHandler;

public class Util {
    public static int countItem(IItemHandler handler, Item item) {
        int count = 0;
        for (int i=0; i<handler.getSlots(); ++i) {
            var stack = handler.getStackInSlot(i);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        return count;
    }
}
