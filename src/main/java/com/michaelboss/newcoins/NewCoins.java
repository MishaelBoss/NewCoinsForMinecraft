package com.michaelboss.newcoins;

import com.michaelboss.newcoins.registry.*;
import com.michaelboss.newcoins.client.ClientModEvents;
import com.michaelboss.newcoins.item.tab.ModCreativeModeTabs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(NewCoins.MOD_ID)
public class NewCoins {
    public static final String MOD_ID = "newcoins";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NewCoins(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);

        ModMenus.register(modEventBus);
        ModDataComponents.register(modEventBus);
        modEventBus.register(ClientModEvents.class);
        ModBlockEntities.register(modEventBus);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        modEventBus.addListener(this::registerPackets);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static String getVersion() {
        ModContainer container = ModList.get().getModContainerById(MOD_ID).orElse(null);
        if (container == null) return "unknown";
        return container.getModInfo().getVersion().toString();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");

        LOGGER.info("COPPER_COIN_INTERNAL_COIN_VALUE >> {}", Config.COPPER_COIN_INTERNAL_COIN_VALUE.get());
        LOGGER.info("IRON_COIN_INTERNAL_COIN_VALUE >> {}", Config.IRON_COIN_INTERNAL_COIN_VALUE.get());
        LOGGER.info("GOLD_COIN_INTERNAL_COIN_VALUE >> {}", Config.GOLD_COIN_INTERNAL_COIN_VALUE.get());
    }

    private void registerPackets(final net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent event) {
        final net.neoforged.neoforge.network.registration.PayloadRegistrar registrar = event.registrar("1.0.0");

        registrar.playToServer(
                com.michaelboss.newcoins.network.DepositC2SPacket.TYPE,
                com.michaelboss.newcoins.network.DepositC2SPacket.STREAM_CODEC,
                (payload, context) -> com.michaelboss.newcoins.network.DepositC2SPacket.handle(context)
        );

        registrar.playToServer(
                com.michaelboss.newcoins.network.WithdrawC2SPacket.TYPE,
                com.michaelboss.newcoins.network.WithdrawC2SPacket.STREAM_CODEC,
                com.michaelboss.newcoins.network.WithdrawC2SPacket::handle
        );
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }
}
