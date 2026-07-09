package com.michaelboss.newcoins.item.tab;

import com.michaelboss.newcoins.NewCoins;
import com.michaelboss.newcoins.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    private ModCreativeModeTabs() {
        /* This utility class should not be instantiated */
        throw new IllegalStateException("Utility class");
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NewCoins.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod"))
            .icon(() -> ModItems.COPPER_COIN.get().getDefaultInstance())
            .displayItems((parameters, output) ->
                ModItems.ITEMS.getEntries().forEach(entry -> output.accept(entry.get()))
            ).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
