package com.landmaster.cargoboats.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public record MotorboatSchedule(List<Entry> entries) {
    public static final Codec<MotorboatSchedule> CODEC = Entry.CODEC.listOf().xmap(MotorboatSchedule::new, MotorboatSchedule::entries);

    public static final StreamCodec<ByteBuf, MotorboatSchedule> STREAM_CODEC = ByteBufCodecs.collection(
            v -> (List<Entry>)new ArrayList<Entry>(v), Entry.STREAM_CODEC
            ).map(MotorboatSchedule::new, MotorboatSchedule::entries);

    public static final MotorboatSchedule INITIAL = new MotorboatSchedule(ImmutableList.of());

    public record Entry(BlockPos dock, Integer stopTime, ResourceKey<Level> dimension) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BlockPos.CODEC.fieldOf("dock").forGetter(Entry::dock),
                        Codec.INT.fieldOf("stopTime").forGetter(Entry::stopTime),
                        ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(Entry::dimension)
                ).apply(instance, Entry::new)
        );

        public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Entry::dock,
                ByteBufCodecs.INT, Entry::stopTime,
                ResourceKey.streamCodec(Registries.DIMENSION), Entry::dimension,
                Entry::new
        );

        public boolean matchesDock(BlockPos pos, Level level) {
            return dock.equals(pos) && level.dimension() == dimension;
        }
    }
}
