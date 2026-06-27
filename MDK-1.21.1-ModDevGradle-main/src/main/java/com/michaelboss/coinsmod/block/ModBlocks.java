package com.michaelboss.coinsmod.block;

import com.michaelboss.coinsmod.CoinsMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(CoinsMod.MOD_ID);

    public static final DeferredBlock<Block> ATM_TOP_BLOCK =
            BLOCKS.register("atm_top_block",
                    () -> new ATMTopBlock(BlockBehaviour.Properties.of().strength(4.0F, 5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final DeferredBlock<Block> ATM_BOTTOM_BLOCK =
            BLOCKS.register("atm_bottom_block",
                    () -> new ATMBottomBlock(BlockBehaviour.Properties.of().strength(4.0F, 5.0F).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final DeferredBlock<Block> COINAGE_BLOCK =
            BLOCKS.register("coinage_block",
                    () -> new CoinageBlock(BlockBehaviour.Properties.of().strength(4.0F, 5.0F).requiresCorrectToolForDrops()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
