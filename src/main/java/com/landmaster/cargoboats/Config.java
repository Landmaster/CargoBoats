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
            .defineInRange("motorboat_max_search_distance", 400.0, 0.0, 10000.0);

    public static final ModConfigSpec.DoubleValue MOTORBOAT_BASE_SPEED = BUILDER
            .comment("Base speed of motorboat in meters / tick")
            .defineInRange("motorboat_base_speed", 0.15, 0.1, 100.0);

    public static final ModConfigSpec.ConfigValue<List<? extends Double>> MOTORBOAT_SPEED_MULTIPLIERS = BUILDER
            .comment("Speed upgrade speed multipliers per boat")
            .defineList(ImmutableList.of("motorboat_speed_upgrade_multipliers"),
                    () -> ImmutableList.of(1.5, 2.25, 3.375), () -> 1.0,
                    elem -> elem instanceof Double num && num >= 1.0, ModConfigSpec.Range.of(0, 64));

    static final ModConfigSpec SPEC = BUILDER.build();
}
