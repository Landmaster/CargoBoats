package com.landmaster.cargoboats.util;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.Config;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class Util {
    public static int countItem(ResourceHandler<ItemResource> handler, Item item) {
        int count = 0;
        for (int i=0; i<handler.size(); ++i) {
            if (handler.getResource(i).is(item)) {
                count += handler.getAmountAsInt(i);
            }
        }
        return count;
    }

    public static ItemStack findItem(ResourceHandler<ItemResource> handler, Item item) {
        for (int i=0; i<handler.size(); ++i) {
            var resource = handler.getResource(i);
            if (resource.is(item)) {
                return resource.toStack(handler.getAmountAsInt(i));
            }
        }
        return ItemStack.EMPTY;
    }

    public static double calcAndIncreaseOverfishingProportion(ChunkAccess chunk) {
        long overfishingTicks = chunk.getData(CargoBoats.OVERFISHING_TICKS);
        long lastReset = chunk.getData(CargoBoats.OVERFISHING_LAST_RESET);
        long lastFished = chunk.getData(CargoBoats.OVERFISHING_LAST_FISHED);

        long gameTime = chunk.getLevel().getGameTime();
        int overfishingForgiveness = Config.OVERFISHING_FORGIVENESS.getAsInt();
        if (gameTime >= lastFished + overfishingForgiveness) {
            overfishingTicks = 0;
            lastReset = gameTime;
            chunk.setData(CargoBoats.OVERFISHING_LAST_RESET, gameTime);
        }
        chunk.setData(CargoBoats.OVERFISHING_TICKS, ++overfishingTicks);
        chunk.setData(CargoBoats.OVERFISHING_LAST_FISHED, gameTime);

        return Math.clamp((double)overfishingTicks / Math.max(gameTime - lastReset + 1, overfishingForgiveness), 0.0, 1.0);
    }
}
