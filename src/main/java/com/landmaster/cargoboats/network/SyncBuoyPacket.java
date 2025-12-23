package com.landmaster.cargoboats.network;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.entity.BuoyBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record SyncBuoyPacket(BlockPos pos, byte offsetX, byte offsetZ) implements CustomPacketPayload {
    public static final Type<SyncBuoyPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            CargoBoats.MODID, "sync_buoy"
    ));

    public static final StreamCodec<ByteBuf, SyncBuoyPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncBuoyPacket::pos,
            ByteBufCodecs.BYTE, SyncBuoyPacket::offsetX,
            ByteBufCodecs.BYTE, SyncBuoyPacket::offsetZ,
            SyncBuoyPacket::new
    );

    public void handle(IPayloadContext ctx) {
        var level = ctx.player().level();
        var chunkPos = new ChunkPos(pos);
        if (level.hasChunk(chunkPos.x, chunkPos.z)) {
            if (level.getBlockEntity(pos) instanceof BuoyBlockEntity te) {
                te.offsetX = Math.clamp(offsetX, -3, 3);
                te.offsetZ = Math.clamp(offsetZ, -3, 3);
                te.setChanged();

                if (level instanceof ServerLevel serverLevel) {
                    PacketDistributor.sendToPlayersTrackingChunk(serverLevel, chunkPos, this);
                }
            }
        }
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
