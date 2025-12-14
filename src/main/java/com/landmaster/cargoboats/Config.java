package com.landmaster.cargoboats;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MOTORBOAT_BASE_ENERGY_USAGE = BUILDER
            .comment("Base energy usage / tick of the motorboat")
            .defineInRange("motorboat_base_energy_usage", 5, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.DoubleValue MOTORBOAT_MAX_SEARCH_DISTANCE = BUILDER
            .comment("Maximum search distance between nodes for motorboats")
            .defineInRange("motorboat_max_search_distance", 400.0, 0.0, 10000.0);

    static final ModConfigSpec SPEC = BUILDER.build();
}
