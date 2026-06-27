package com.michaelboss.coinsmod.init;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.block.ModBlocks;
import com.michaelboss.coinsmod.block.entity.CoinageBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, CoinsMod.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoinageBlockEntity>> COINAGE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("coinage_block", () ->
                    BlockEntityType.Builder.of(CoinageBlockEntity::new, ModBlocks.COINAGE_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}