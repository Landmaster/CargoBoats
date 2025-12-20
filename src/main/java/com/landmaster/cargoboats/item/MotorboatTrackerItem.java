package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.network.TrackMotorboatPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@EventBusSubscriber
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
        tooltipComponents.add(Component.translatable("tooltip.cargoboats.motorboat_tracker").withStyle(ChatFormatting.AQUA));
        var uuid = stack.get(CargoBoats.TRACKED_MOTORBOAT);
        if (uuid != null) {
            tooltipComponents.add(Component.translatable("tooltip.cargoboats.tracked_motorboat", uuid.toString()).withStyle(ChatFormatting.YELLOW));
        }
    }

    private static final Map<Player, Vector3f> playerToTracked = new WeakHashMap<>();

    @SubscribeEvent
    private static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity().level() instanceof ServerLevel level) {
            var pos = Arrays.stream(InteractionHand.values())
                    .map(event.getEntity()::getItemInHand)
                    .map(stack -> stack.get(CargoBoats.TRACKED_MOTORBOAT))
                    .filter(Objects::nonNull)
                    .map(level::getEntity)
                    .filter(Objects::nonNull)
                    .findFirst()
                            .map(ent -> ent.position().toVector3f());
            var unwrappedPos = pos.orElse(null);
            if (!Objects.equals(playerToTracked.get(event.getEntity()), unwrappedPos)) {
                playerToTracked.put(event.getEntity(), unwrappedPos);
                PacketDistributor.sendToPlayer((ServerPlayer) event.getEntity(), new TrackMotorboatPacket(pos));
            }
        }
    }
}
