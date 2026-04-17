package com.landmaster.cargoboats.item;

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

public class IcebreakerUpgradeItem extends Item implements MotorboatUpgrade {
    public IcebreakerUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nonnull TooltipContext context, @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> builder, @Nonnull TooltipFlag tooltipFlag) {
        builder.accept(Component.translatable("tooltip.cargoboats.icebreaker_upgrade").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public int maxUpgradeAmount(EntityType<?> entityType) {
        return 1;
    }
}
