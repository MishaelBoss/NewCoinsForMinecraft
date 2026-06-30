package com.michaelboss.coinsmod.client;

import com.michaelboss.coinsmod.client.gui.BankCardPrintingMachineScreen;
import com.michaelboss.coinsmod.client.gui.CoinageScreen;
import com.michaelboss.coinsmod.client.gui.WalletScreen;
import com.michaelboss.coinsmod.client.renderer.BankCardPrintingMachineBlockRender;
import com.michaelboss.coinsmod.client.renderer.CoinageBlockRenderer;
import com.michaelboss.coinsmod.init.ModBlockEntities;
import com.michaelboss.coinsmod.menu.ModMenus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class ClientModEvents {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.WALLET_MENU.get(), WalletScreen::new);
        event.register(ModMenus.COINAGE_MENU.get(), CoinageScreen::new);
        event.register(ModMenus.BANK_CARD_PRINTING_MACHINE_MENU.get(), BankCardPrintingMachineScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.COINAGE_BLOCK_ENTITY.get(), CoinageBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BANK_CARD_PRINTING_MACHINE_BLOCK_ENTITY.get(), BankCardPrintingMachineBlockRender::new);
    }
}