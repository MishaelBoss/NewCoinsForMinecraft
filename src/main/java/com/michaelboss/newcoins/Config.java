package com.michaelboss.newcoins;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private Config() {
        /* This utility class should not be instantiated */
        throw new IllegalStateException("Utility class");
    }

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue COPPER_COIN_INTERNAL_COIN_VALUE = BUILDER
            .comment("Intrinsic Value of Copper Coin (10 = 1.0)")
            .defineInRange("copperCoinValue", 10, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue GOLD_COIN_INTERNAL_COIN_VALUE = BUILDER
            .comment("Intrinsic Value of Gold Coin (23 = 2.3)")
            .defineInRange("goldCoinValue", 23, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue IRON_COIN_INTERNAL_COIN_VALUE = BUILDER
            .comment("Intrinsic Value of Iron Coin (15 = 1.5)")
            .defineInRange("ironCoinValue", 15, 1, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();
}
