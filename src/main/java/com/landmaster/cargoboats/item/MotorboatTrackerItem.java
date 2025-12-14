package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.CargoBoats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nonnull;
import java.util.List;

public class MotorboatTrackerItem extends Item {
    public MotorboatTrackerItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(CargoBoats.TRACKED_MOTORBOAT);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        var uuid = stack.get(CargoBoats.TRACKED_MOTORBOAT);
        if (uuid != null) {
            tooltipComponents.add(Component.translatable("tooltip.cargoboats.tracked_motorboat", uuid.toString()).withStyle(ChatFormatting.YELLOW));
        }
    }
}
