package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.entity.DockBlockEntity;
import com.landmaster.cargoboats.util.MotorboatSchedule;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MotorboatProgrammerItem extends Item {
    public MotorboatProgrammerItem(Properties properties) {
        super(properties);
    }

    public int maxEntriesAllowed(ItemStack stack) {
        return 20;
    }

    @Nonnull
    @Override
    public InteractionResult onItemUseFirst(@Nonnull ItemStack stack, @Nonnull UseOnContext context) {
        var pos = context.getClickedPos();
        var level = context.getLevel();
        if (level.getBlockEntity(pos) instanceof DockBlockEntity) {
            var schedule = stack.get(CargoBoats.MOTORBOAT_SCHEDULE);
            if (schedule.entries().size() < maxEntriesAllowed(stack)) {
                stack.set(CargoBoats.MOTORBOAT_SCHEDULE,
                        new MotorboatSchedule(
                                Stream.concat(
                                        schedule.entries().stream(),
                                        Stream.of(new MotorboatSchedule.Entry(pos, 200, level.dimension()))
                                ).collect(Collectors.toList())
                        )
                );
                if (level.isClientSide && context.getPlayer() != null) {
                    context.getPlayer().displayClientMessage(Component.translatable("message.cargoboats.dock_added", pos.toShortString()), false);
                }
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.FAIL;
        }
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        var schedule = stack.get(CargoBoats.MOTORBOAT_SCHEDULE);
        for (int i=0; i<4; ++i) {
            tooltipComponents.add(Component.translatable("tooltip.cargoboats.motorboat_programmer_instructions." + i).withStyle(ChatFormatting.AQUA));
        }
        for (var entry: schedule.entries()) {
            tooltipComponents.add(Component.translatable("tooltip.cargoboats.motorboat_schedule",
                            entry.dock().toShortString(), entry.dimension().location().toString(), entry.stopTime())
                    .withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return !stack.get(CargoBoats.MOTORBOAT_SCHEDULE).entries().isEmpty();
    }
}
