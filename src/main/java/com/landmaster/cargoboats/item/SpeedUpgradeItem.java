package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.List;

public class SpeedUpgradeItem extends Item implements MotorboatUpgrade {
    public SpeedUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public int maxUpgradeAmount(EntityType<?> entityType) {
        return Config.MOTORBOAT_SPEED_MULTIPLIERS.get().size();
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        var multipliers = Config.MOTORBOAT_SPEED_MULTIPLIERS.get();
        for (int i=0; i<multipliers.size(); ++i) {
            tooltipComponents.add(Component.translatable(
                    "tooltip.cargoboats.speed_upgrade", i+1,
                    new DecimalFormat("0.0#").format(multipliers.get(i)))
                    .withStyle(ChatFormatting.AQUA));
        }
    }
}
