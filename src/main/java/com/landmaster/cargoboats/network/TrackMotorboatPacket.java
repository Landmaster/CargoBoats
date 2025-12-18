package com.landmaster.cargoboats.network;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.level.LevelRendering;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.Optional;

public record TrackMotorboatPacket(Optional<Vector3f> pos) implements CustomPacketPayload {
    public static final Type<TrackMotorboatPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            CargoBoats.MODID, "track_motorboat"
    ));

    public static final StreamCodec<ByteBuf, TrackMotorboatPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.VECTOR3F), TrackMotorboatPacket::pos,
            TrackMotorboatPacket::new
    );

    public void handle(IPayloadContext ctx) {
        LevelRendering.trackedPos = pos.orElse(null);
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
