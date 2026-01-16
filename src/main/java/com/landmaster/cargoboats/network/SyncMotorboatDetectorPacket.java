package com.landmaster.cargoboats.network;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.entity.MotorboatDetectorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nonnull;

public record SyncMotorboatDetectorPacket(BlockPos pos, byte minX, byte minY, byte minZ, byte maxX, byte maxY, byte maxZ) implements CustomPacketPayload {
    public static final Type<SyncMotorboatDetectorPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            CargoBoats.MODID, "sync_motorboat_detector"
    ));

    public static final StreamCodec<FriendlyByteBuf, SyncMotorboatDetectorPacket> STREAM_CODEC = new StreamCodec<>() {
        @Nonnull
        @Override
        public SyncMotorboatDetectorPacket decode(FriendlyByteBuf buffer) {
            return new SyncMotorboatDetectorPacket(
                    buffer.readBlockPos(),
                    buffer.readByte(),
                    buffer.readByte(),
                    buffer.readByte(),
                    buffer.readByte(),
                    buffer.readByte(),
                    buffer.readByte()
            );
        }

        @Override
        public void encode(@Nonnull FriendlyByteBuf buffer, @Nonnull SyncMotorboatDetectorPacket value) {
            buffer.writeBlockPos(value.pos);
            buffer.writeByte(value.minX);
            buffer.writeByte(value.minY);
            buffer.writeByte(value.minZ);
            buffer.writeByte(value.maxX);
            buffer.writeByte(value.maxY);
            buffer.writeByte(value.maxZ);
        }
    };

    public void handle(IPayloadContext ctx) {
        var level = ctx.player().level();

        if (minX > maxX || minY > maxY || minZ > maxZ
            || minX < -3 || minY < -3 || minZ < -3
            || maxX > 3 || maxY > 3 || maxZ > 3) {
            return;
        }

        var chunkPos = new ChunkPos(pos);
        if (level.hasChunk(chunkPos.x, chunkPos.z)) {
            if (level.getBlockEntity(pos) instanceof MotorboatDetectorBlockEntity te) {
                te.minX = minX;
                te.minY = minY;
                te.minZ = minZ;
                te.maxX = maxX;
                te.maxY = maxY;
                te.maxZ = maxZ;
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
