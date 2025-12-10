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

public record MotorboatSchedule(List<Entry> entries, ResourceKey<Level> dimension) {
    public static final Codec<MotorboatSchedule> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Entry.CODEC.listOf().fieldOf("entries").forGetter(MotorboatSchedule::entries),
                    ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(MotorboatSchedule::dimension)
            ).apply(instance, MotorboatSchedule::new)
    );

    public static final StreamCodec<ByteBuf, MotorboatSchedule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, Entry.STREAM_CODEC), MotorboatSchedule::entries,
            ResourceKey.streamCodec(Registries.DIMENSION), MotorboatSchedule::dimension,
            MotorboatSchedule::new
    );

    public static final MotorboatSchedule INITIAL = new MotorboatSchedule(ImmutableList.of(), Level.OVERWORLD);

    public record Entry(BlockPos dock, Integer stopTime) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BlockPos.CODEC.fieldOf("dock").forGetter(Entry::dock),
                        Codec.INT.fieldOf("stopTime").forGetter(Entry::stopTime)
                ).apply(instance, Entry::new)
        );

        public static final StreamCodec<ByteBuf, Entry> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Entry::dock,
                ByteBufCodecs.INT, Entry::stopTime,
                Entry::new
        );
    }
}
