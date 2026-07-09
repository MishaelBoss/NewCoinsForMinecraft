package com.michaelboss.newcoins.registry;

import com.michaelboss.newcoins.NewCoins;
import com.michaelboss.newcoins.item.component.WalletContents;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    private ModDataComponents() {
        /* This utility class should not be instantiated */
        throw new IllegalStateException("Utility class");
    }

    public static final DeferredRegister.DataComponents REGISTRY =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, NewCoins.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WalletContents>> WALLET_CONTENTS =
            REGISTRY.registerComponentType("wallet_contents", builder -> builder
                    .persistent(WalletContents.CODEC)
                    .networkSynchronized(WalletContents.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CARD_OWNER =
            REGISTRY.register("card_owner", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CARD_UUID =
            REGISTRY.register("card_uuid", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> CARD_DEPOSIT =
            REGISTRY.register("card_deposit", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build());

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
