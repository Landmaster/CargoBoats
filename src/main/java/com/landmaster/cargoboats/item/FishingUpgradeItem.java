package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;

public class FishingUpgradeItem extends Item implements MotorboatUpgrade {
    public FishingUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int maxUpgradeAmount(EntityType<?> entityType) {
        return entityType == CargoBoats.FLUID_MOTORBOAT.get() ? 0 : 1;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nonnull TooltipContext context, @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> builder, @Nonnull TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("tooltip.cargoboats.fishing_upgrade.0").withStyle(ChatFormatting.AQUA));
        builder.accept(Component.translatable("tooltip.cargoboats.fishing_upgrade.1", Config.FISHING_ENERGY_CONSUMPTION.getAsInt()).withStyle(ChatFormatting.AQUA));
    }
}
