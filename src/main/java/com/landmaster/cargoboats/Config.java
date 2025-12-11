package com.landmaster.cargoboats;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MOTORBOAT_BASE_ENERGY_USAGE = BUILDER
            .comment("Base energy usage / tick of the motorboat")
            .defineInRange("motorboat_base_energy_usage", 5, 0, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
