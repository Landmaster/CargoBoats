package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.Config;
import net.minecraft.world.item.Item;

public class SpeedUpgradeItem extends Item implements MotorboatUpgrade {
    public SpeedUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int maxUpgradeAmount() {
        return Config.MOTORBOAT_SPEED_MULTIPLIERS.get().size();
    }
}
