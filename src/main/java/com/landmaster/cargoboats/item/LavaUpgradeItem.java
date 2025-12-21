package com.landmaster.cargoboats.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nonnull;
import java.util.List;

public class LavaUpgradeItem extends Item implements MotorboatUpgrade {
    public LavaUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.cargoboats.lava_upgrade").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public int maxUpgradeAmount(EntityType<?> entityType) {
        return 1;
    }
}
