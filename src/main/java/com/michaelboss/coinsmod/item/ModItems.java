package com.michaelboss.coinsmod.item;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CoinsMod.MOD_ID);

    // ATM - Банкомат
    public static final DeferredItem<BlockItem> ATM_ITEM =
            ITEMS.registerSimpleBlockItem("atm", ModBlocks.ATM_BOTTOM_BLOCK);

    // Coins - Монеты
    public static final DeferredItem<Item> COPPER_COIN = ITEMS.register("copper_coin",
            () -> new CopperCoinItem(new Item.Properties()));

    public static final DeferredItem<Item> GOLD_COIN = ITEMS.register("gold_coin",
            () -> new GoldCoinItem(new Item.Properties()));

    public static final DeferredItem<Item> IRON_COIN = ITEMS.register("iron_coin",
            () -> new IronCoinItem(new Item.Properties()));

    // Chip - чип
    public static final DeferredItem<Item> CHIP = ITEMS.register("chip",
            () -> new Item(new Item.Properties()));

    // Cards - Карты
    public static final DeferredItem<Item> CLASSIC_CARD = ITEMS.register("classic_card",
            () -> new ClassicCardItem(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> GOLD_CARD = ITEMS.register("gold_card",
            () -> new GoldCardItem(new Item.Properties().stacksTo(1)));

    // Wallet - Кошелек
    public static final DeferredItem<Item> WALLET = ITEMS.register("wallet",
            () -> new WalletItem(new Item.Properties().stacksTo(1)));

    // Coinage - чеканка монет
    public static final DeferredItem<BlockItem> COINAGE_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem("coinage_block", ModBlocks.COINAGE_BLOCK);

    // Bank card printing machine - станок для создания банковских карт
    public static final DeferredItem<BlockItem> BANK_CARD_PRINTING_MACHINE_BLOCK_ITEM =
            ITEMS.registerSimpleBlockItem("bank_card_printing_machine_block", ModBlocks.BANK_CARD_PRINTING_MACHINE_BLOCK);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
