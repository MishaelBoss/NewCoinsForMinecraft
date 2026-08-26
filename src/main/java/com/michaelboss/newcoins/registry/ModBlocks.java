package com.michaelboss.newcoins.registry;

import com.michaelboss.newcoins.NewCoins;
import com.michaelboss.newcoins.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    private ModBlocks() {
        /* This utility class should not be instantiated */
        throw new IllegalStateException("Utility class");
    }

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(NewCoins.MOD_ID);

    public static final DeferredBlock<Block> ATM_TOP_BLOCK =
            BLOCKS.register("atm_top_block",
                    () -> new ATMTopBlock(BlockBehaviour.Properties.of().strength(4.0F, 5.0F).noOcclusion().requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final DeferredBlock<Block> ATM_BOTTOM_BLOCK =
            BLOCKS.register("atm_bottom_block",
                    () -> new ATMBottomBlock(BlockBehaviour.Properties.of().strength(4.0F, 5.0F).noOcclusion().requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final DeferredBlock<Block> COINAGE_BLOCK =
            BLOCKS.register("coinage_block",
                    () -> new CoinageBlock(BlockBehaviour.Properties.of().strength(4.0F, 5.0F).forceSolidOn().noOcclusion().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> BANK_CARD_PRINTING_MACHINE_BLOCK =
            BLOCKS.register("bank_card_printing_machine_block",
                    () -> new BankCardPrintingMachineBlock(BlockBehaviour.Properties.of().strength(4.0F, 5.0F).forceSolidOn().requiresCorrectToolForDrops()));

    public static final DeferredBlock<Block> PRINTER_PAPER_MONEY_BLOCK =
            BLOCKS.register("printer_paper_money_block",
                    ()-> new PrinterPaperMoneyLeftBlock(BlockBehaviour.Properties.of().strength(4.0F, 5.0F).forceSolidOn().noCollission().requiresCorrectToolForDrops()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
