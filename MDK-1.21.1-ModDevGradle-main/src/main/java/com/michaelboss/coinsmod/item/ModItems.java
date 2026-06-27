package com.michaelboss.coinsmod.item;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CoinsMod.MOD_ID);

    // ATM - Банкомат
    public static final DeferredItem<BlockItem> ATM_ITEM =
            ITEMS.registerSimpleBlockItem("atm", ModBlocks.ATM_BOTTOM_BLOCK);

    // Coins - Монеты
    public static final DeferredItem<Item> COPPER_COIN = ITEMS.register("copper_coin",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GOLD_COIN = ITEMS.register("gold_coin",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> IRON_COIN = ITEMS.register("iron_coin",
            () -> new Item(new Item.Properties()));

    // Cards - Карты
    public static final DeferredItem<Item> CARD_CLASSIC = ITEMS.register("card_classic",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GOLD_CLASSIC = ITEMS.register("card_gold",
            () -> new Item(new Item.Properties().stacksTo(1)));

    // Wallet - Кошелек
    public static final DeferredItem<Item> WALLET = ITEMS.register("wallet",
            () -> new WalletItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
