package com.landmaster.cargoboats.item;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class LavaUpgradeItem extends Item implements MotorboatUpgrade {
    public LavaUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int maxUpgradeAmount(EntityType<?> entityType) {
        return 1;
    }
}
