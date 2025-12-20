package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        var itemMultipliers = Config.MOTORBOAT_ITEM_CAPACITY_MULTIPLIER.get();
        var fluidCapacities = Config.MOTORBOAT_FLUID_CAPACITY.get();
        for (int i=0; i<Math.max(itemMultipliers.size(), fluidCapacities.size()); ++i) {
            var subComponents = new ArrayList<Component>(3);
            subComponents.add(Component.literal(Integer.toString(i+1)));
            if (i < itemMultipliers.size()) {
                subComponents.add(Component.translatable("tooltip.cargoboats.capacity_upgrade.items", itemMultipliers.get(i)));
            }
            if (i < fluidCapacities.size()) {
                subComponents.add(Component.translatable("tooltip.cargoboats.capacity_upgrade.fluids", fluidCapacities.get(i)));
            }
            tooltipComponents.add(Component.translatable(
                    "tooltip.cargoboats.capacity_upgrade." + (subComponents.size()-1),
                    subComponents.toArray()
            ).withStyle(ChatFormatting.AQUA));
        }
    }
}
