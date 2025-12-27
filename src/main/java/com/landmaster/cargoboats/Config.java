package com.landmaster.cargoboats;

import com.google.common.collect.ImmutableList;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MOTORBOAT_BASE_ENERGY_USAGE = BUILDER
            .comment("Base energy usage / tick of the motorboat")
            .defineInRange("motorboat_base_energy_usage", 5, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue MOTORBOAT_MAX_SEARCH_DISTANCE = BUILDER
            .comment("Maximum search distance between nodes for motorboats")
            .defineInRange("motorboat_max_search_distance", 200.0, 0.0, 1000.0);

    public static final ModConfigSpec.DoubleValue MOTORBOAT_BASE_SPEED = BUILDER
            .comment("Base speed of motorboat in meters / tick")
            .defineInRange("motorboat_base_speed", 0.15, 0.1, 100.0);

    public static final ModConfigSpec.ConfigValue<List<? extends Double>> MOTORBOAT_SPEED_MULTIPLIERS = BUILDER
            .comment("Speed upgrade speed multipliers by number of installed upgrades")
            .defineList(ImmutableList.of("motorboat_speed_upgrade_multipliers"),
                    () -> ImmutableList.of(1.5, 2.25, 3.375), () -> 1.0,
                    elem -> elem instanceof Double num && num >= 1.0, ModConfigSpec.Range.of(0, 64));

    public static final ModConfigSpec.IntValue MOTORBOAT_BASE_ENERGY_CAPACITY = BUILDER
            .comment("Base energy capacity of motorboat")
            .defineInRange("motorboat_base_energy_capacity", 100000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue MOTORBOAT_BASE_FLUID_CAPACITY = BUILDER
            .comment("Base fluid capacity of fluid motorboat")
            .defineInRange("motorboat_base_fluid_capacity", 64000, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> MOTORBOAT_ITEM_CAPACITY_MULTIPLIER = BUILDER
            .comment("Motorboat item capacity multipliers by number of installed upgrades")
            .defineList(ImmutableList.of("motorboat_capacity_upgrade_item_multipliers"),
                    () -> ImmutableList.of(2, 4, 8), () -> 1,
                    elem -> elem instanceof Integer num && num >= 1, ModConfigSpec.Range.of(0, 64));

    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> MOTORBOAT_FLUID_CAPACITY = BUILDER
            .comment("Motorboat fluid capacity by number of installed upgrades")
            .defineList(ImmutableList.of("motorboat_capacity_upgrade_fluid"),
                    () -> ImmutableList.of(192000, 512000, 1536000), () -> 1,
                    elem -> elem instanceof Integer num && num >= 1, ModConfigSpec.Range.of(0, 64));

    // default 3 minutes
    public static final ModConfigSpec.IntValue OVERFISHING_FORGIVENESS = BUILDER
            .comment("If a chunk has not been fished in this amount of ticks, overfishing stat resets")
            .defineInRange("overfishing_forgiveness", 3600, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue OVERFISHING_EXPONENTIAL_BASE = BUILDER
            .comment("If this value is B, then the fishing probability is multiplied by B^x where x is the fraction of time fished")
            .defineInRange("overfishing_exponential_base", 0.01, 0.0, 1.0);

    public static final ModConfigSpec.DoubleValue BASE_FISHING_TIME = BUILDER
            .comment("Base fishing time (in SECONDS) that the fishing time reduction is subtracted from")
            .defineInRange("base_fishing_time", 25.0, 0.0, Double.POSITIVE_INFINITY);

    // default: 2/20
    public static final ModConfigSpec.DoubleValue FISHING_PROBABILITY_FACTOR = BUILDER
            .comment("This number is divided by the calculated fishing time in seconds to calculate the per-tick probability before overfishing")
            .defineInRange("fishing_probability_factor", 0.10, 0.0, Double.POSITIVE_INFINITY);

    public static final ModConfigSpec.IntValue FISHING_ENERGY_CONSUMPTION = BUILDER
            .comment("Fishing energy consumption per tick")
            .defineInRange("fishing_energy_consumption", 5, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
