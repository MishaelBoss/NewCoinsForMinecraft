package com.michaelboss.coinsmod.menu;

import com.michaelboss.coinsmod.CoinsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, CoinsMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<WalletMenu>> WALLET_MENU =
            MENUS.register("wallet_menu", () -> new MenuType<>(WalletMenu::new, FeatureFlags.VANILLA_SET));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}