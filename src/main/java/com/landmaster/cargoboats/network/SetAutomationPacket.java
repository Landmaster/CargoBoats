package com.landmaster.cargoboats.network;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.menu.MotorboatMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record SetAutomationPacket(boolean enabled) implements CustomPacketPayload {
    public static final Type<SetAutomationPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            CargoBoats.MODID, "set_automation"
    ));

    public static final StreamCodec<ByteBuf, SetAutomationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SetAutomationPacket::enabled,
            SetAutomationPacket::new
    );

    public void handle(IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof MotorboatMenu menu) {
            menu.getMotorboat().ifPresent(motorboat -> {
                motorboat.automationEnabled = enabled;
            });
        }
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
