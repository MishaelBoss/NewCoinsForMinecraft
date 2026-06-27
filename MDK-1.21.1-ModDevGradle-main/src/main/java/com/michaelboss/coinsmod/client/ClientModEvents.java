package com.michaelboss.coinsmod.client;

import com.michaelboss.coinsmod.client.gui.WalletScreen;
import com.michaelboss.coinsmod.menu.ModMenus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientModEvents {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.WALLET_MENU.get(), WalletScreen::new);
    }
}