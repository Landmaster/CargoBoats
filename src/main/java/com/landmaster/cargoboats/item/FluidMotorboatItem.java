package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.Config;
import com.landmaster.cargoboats.entity.FluidMotorboat;
import com.landmaster.cargoboats.entity.Motorboat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

public class FluidMotorboatItem extends MotorboatItem {
    public FluidMotorboatItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        tooltipComponents.add(Component.translatable("tooltip.cargoboats.motorboat.base_fluid", Config.MOTORBOAT_BASE_FLUID_CAPACITY.getAsInt())
                .withStyle(ChatFormatting.AQUA));
    }

    @Override
    public Motorboat createBoat(Level level, double x, double y, double z) {
        return new FluidMotorboat(level, x, y, z);
    }
}
