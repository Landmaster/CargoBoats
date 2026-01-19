package com.landmaster.cargoboats.network;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.entity.AdjustableBoundingBoxBlockEntity;
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

public record AdjustBoundingBoxPacket(BlockPos pos, byte minX, byte minY, byte minZ, byte maxX, byte maxY, byte maxZ) implements CustomPacketPayload {
    public static final Type<AdjustBoundingBoxPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            CargoBoats.MODID, "adjust_bounding_box"
    ));

    public static final StreamCodec<FriendlyByteBuf, AdjustBoundingBoxPacket> STREAM_CODEC = new StreamCodec<>() {
        @Nonnull
        @Override
        public AdjustBoundingBoxPacket decode(FriendlyByteBuf buffer) {
            return new AdjustBoundingBoxPacket(
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
        public void encode(@Nonnull FriendlyByteBuf buffer, @Nonnull AdjustBoundingBoxPacket value) {
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

        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return;
        }

        var chunkPos = new ChunkPos(pos);
        if (level.hasChunk(chunkPos.x, chunkPos.z)) {
            if (level.getBlockEntity(pos) instanceof AdjustableBoundingBoxBlockEntity te) {
                var maxDim = te.maxDimension();

                if (minX < -maxDim || minY < -maxDim || minZ < -maxDim
                        || maxX > maxDim || maxY > maxDim || maxZ > maxDim) {
                    return;
                }

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
