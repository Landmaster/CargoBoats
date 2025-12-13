package com.landmaster.cargoboats.network;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.menu.MotorboatProgrammerMenu;
import com.landmaster.cargoboats.util.MotorboatSchedule;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public record ModifySchedulePacket(int index, int delta) implements CustomPacketPayload {
    public static final Type<ModifySchedulePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            CargoBoats.MODID, "modify_schedule"
    ));

    public static final StreamCodec<ByteBuf, ModifySchedulePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ModifySchedulePacket::index,
            ByteBufCodecs.INT, ModifySchedulePacket::delta,
            ModifySchedulePacket::new
    );

    public void handle(IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof MotorboatProgrammerMenu menu) {
            var newEntries = new ArrayList<>(menu.getSchedule().entries());
            if (index >= 0 && index < newEntries.size()) {
                if (delta == Integer.MIN_VALUE) {
                    newEntries.remove(index);
                } else {
                    var oldEntry = newEntries.get(index);
                    int newStopTime = Math.clamp(oldEntry.stopTime() + delta, 0, 20000);
                    newEntries.set(index, new MotorboatSchedule.Entry(oldEntry.dock(), newStopTime, oldEntry.dimension()));
                }
            }
            menu.setSchedule(new MotorboatSchedule(newEntries));
            if (!ctx.player().level().isClientSide) {
                ctx.reply(this);
            }
        }
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
