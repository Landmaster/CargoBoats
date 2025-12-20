package com.landmaster.cargoboats.network;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.Config;
import com.landmaster.cargoboats.menu.MotorboatMenu;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record SetMotorboatPagePacket(int page) implements CustomPacketPayload {
    public static final Type<SetMotorboatPagePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            CargoBoats.MODID, "set_motorboat_page"
    ));

    public static final StreamCodec<ByteBuf, SetMotorboatPagePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SetMotorboatPagePacket::page,
            SetMotorboatPagePacket::new
    );

    public void handle(IPayloadContext ctx) {
        if (ctx.player().containerMenu instanceof MotorboatMenu menu) {
            if (ctx.player().level().isClientSide) {
                menu.page = page;
            } else {
                menu.getMotorboat().ifPresent(motorboat -> {
                    menu.page = Math.clamp(page, 0, Config.MOTORBOAT_ITEM_CAPACITY_MULTIPLIER.get().getLast());
                });
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
