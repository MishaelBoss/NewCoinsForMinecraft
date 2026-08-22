package com.michaelboss.newcoins.client;

import com.michaelboss.newcoins.client.gui.*;
import com.michaelboss.newcoins.client.renderer.BankCardPrintingMachineBlockRender;
import com.michaelboss.newcoins.client.renderer.CoinageBlockRenderer;
import com.michaelboss.newcoins.registry.ModBlockEntities;
import com.michaelboss.newcoins.registry.ModMenus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientModEvents {
    private ClientModEvents() {
        throw new IllegalStateException("Utility class");
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.WALLET_MENU.get(), WalletScreen::new);
        event.register(ModMenus.COINAGE_MENU.get(), CoinageScreen::new);
        event.register(ModMenus.BANK_CARD_PRINTING_MACHINE_MENU.get(), BankCardPrintingMachineScreen::new);
        event.register(ModMenus.ATM_MENU.get(), ATMScreen::new);
        event.register(ModMenus.PRINTER_PAPER_MONEY_MENU.get(), PrinterPaperMoneyScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.COINAGE_BLOCK_ENTITY.get(), context -> new CoinageBlockRenderer());
        event.registerBlockEntityRenderer(ModBlockEntities.BANK_CARD_PRINTING_MACHINE_BLOCK_ENTITY.get(), context -> new BankCardPrintingMachineBlockRender());
    }
}