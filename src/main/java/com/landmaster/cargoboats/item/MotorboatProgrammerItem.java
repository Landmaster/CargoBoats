package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.menu.MotorboatProgrammerMenu;
import com.landmaster.cargoboats.util.MotorboatSchedule;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MotorboatProgrammerItem extends Item {
    public MotorboatProgrammerItem(Properties properties) {
        super(properties);
    }

    public ItemStack withSchedule(MotorboatSchedule schedule) {
        var res = new ItemStack(this);
        res.set(CargoBoats.MOTORBOAT_SCHEDULE, schedule);
        return res;
    }

    @Nonnull
    @Override
    public InteractionResult onItemUseFirst(@Nonnull ItemStack stack, @Nonnull UseOnContext context) {
        var pos = context.getClickedPos();
        var level = context.getLevel();
        var player = context.getPlayer();
        var cap = level.getCapability(CargoBoats.MOTORBOAT_PATHFINDING_NODE, pos);
        if (cap != null) {
            var prevSchedule = stack.get(CargoBoats.MOTORBOAT_SCHEDULE);
            stack.set(CargoBoats.MOTORBOAT_SCHEDULE,
                    new MotorboatSchedule(
                            Stream.concat(
                                    prevSchedule.entries().stream(),
                                    Stream.of(new MotorboatSchedule.Entry(pos, cap.defaultStopTime(), level.dimension()))
                            ).collect(Collectors.toList())
                    )
            );
            if (level.isClientSide && player != null) {
                player.displayClientMessage(Component.translatable("message.cargoboats.dock_added", pos.toShortString()), false);
                if (!prevSchedule.entries().isEmpty()) {
                    var lastEntry = prevSchedule.entries().getLast();
                    if (lastEntry.dimension() == level.dimension()) {
                        var lastPos = prevSchedule.entries().getLast().dock();
                        player.displayClientMessage(Component.translatable("message.cargoboats.dock_added.distance",
                                new DecimalFormat("0.0").format(Math.sqrt(lastPos.distSqr(pos)))), false);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.onItemUseFirst(stack, context);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand usedHand) {
        var stack = player.getItemInHand(usedHand);
        return player.openMenu(
                new SimpleMenuProvider((id, inv, thePlayer) -> new MotorboatProgrammerMenu(id, stack), stack.getHoverName()),
                buf -> MotorboatSchedule.STREAM_CODEC.encode(buf, stack.get(CargoBoats.MOTORBOAT_SCHEDULE))
        ).isPresent() ? InteractionResultHolder.success(stack) : InteractionResultHolder.fail(stack);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltipComponents, @Nonnull TooltipFlag tooltipFlag) {
        for (int i=0; i<3; ++i) {
            tooltipComponents.add(Component.translatable("tooltip.cargoboats.motorboat_programmer_instructions." + i).withStyle(ChatFormatting.AQUA));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return !stack.get(CargoBoats.MOTORBOAT_SCHEDULE).entries().isEmpty();
    }
}
