package com.michaelboss.newcoins.registry;

import com.michaelboss.newcoins.NewCoins;
import com.michaelboss.newcoins.menu.ATMMenu;
import com.michaelboss.newcoins.menu.BankCardPrintingMachineMenu;
import com.michaelboss.newcoins.menu.CoinageMenu;
import com.michaelboss.newcoins.menu.WalletMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenus {
    private ModMenus() {
        /* This utility class should not be instantiated */
        throw new IllegalStateException("Utility class");
    }

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, NewCoins.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<WalletMenu>> WALLET_MENU =
            MENUS.register("wallet_menu",
                    () -> new MenuType<>(WalletMenu::new, FeatureFlags.VANILLA_SET));

    public static final DeferredHolder<MenuType<?>, MenuType<CoinageMenu>> COINAGE_MENU =
            MENUS.register("coinage_menu",
                    () -> IMenuTypeExtension.create(CoinageMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<BankCardPrintingMachineMenu>> BANK_CARD_PRINTING_MACHINE_MENU =
            MENUS.register("bank_card_printing_machine_menu",
                    () -> IMenuTypeExtension.create(BankCardPrintingMachineMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<ATMMenu>> ATM_MENU =
            MENUS.register("atm_menu",
                    () -> IMenuTypeExtension.create(ATMMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}