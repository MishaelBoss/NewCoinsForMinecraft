package com.michaelboss.coinsmod.registry;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.item.component.WalletContents;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents REGISTRY =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, CoinsMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WalletContents>> WALLET_CONTENTS =
            REGISTRY.registerComponentType("wallet_contents", builder -> builder
                    .persistent(WalletContents.CODEC)
                    .networkSynchronized(WalletContents.STREAM_CODEC));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> CARD_OWNER =
            REGISTRY.register("card_owner", () -> DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .build());

    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}
