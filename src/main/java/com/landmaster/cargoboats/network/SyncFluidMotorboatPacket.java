package com.landmaster.cargoboats.network;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.FluidMotorboat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

import javax.annotation.Nonnull;

public record SyncFluidMotorboatPacket(int id, FluidStack fluidStack, int capacity) implements CustomPacketPayload {
    public static final Type<SyncFluidMotorboatPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(
            CargoBoats.MODID, "sync_fluid_motorboat"
    ));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncFluidMotorboatPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncFluidMotorboatPacket::id,
            FluidStack.OPTIONAL_STREAM_CODEC, SyncFluidMotorboatPacket::fluidStack,
            ByteBufCodecs.INT, SyncFluidMotorboatPacket::capacity,
            SyncFluidMotorboatPacket::new
    );

    public void handle(IPayloadContext ctx) {
        var entity = ctx.player().level().getEntity(id);
        if (entity instanceof FluidMotorboat fluidMotorboat) {
            fluidMotorboat.tank.set(0, FluidResource.of(fluidStack), fluidStack.amount());
            fluidMotorboat.tank.setCapacity(capacity);
        }
    }

    @Nonnull
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
