package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.Config;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class CapacityUpgradeItem extends Item implements MotorboatUpgrade {
    public CapacityUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int maxUpgradeAmount(EntityType<?> entityType) {
        if (entityType == CargoBoats.FLUID_MOTORBOAT.get()) {
            return Config.MOTORBOAT_FLUID_CAPACITY.get().size();
        }
        return Config.MOTORBOAT_ITEM_CAPACITY_MULTIPLIER.get().size();
    }
}
