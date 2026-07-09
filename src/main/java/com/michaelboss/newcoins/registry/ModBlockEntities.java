package com.michaelboss.newcoins.registry;

import com.michaelboss.newcoins.NewCoins;
import com.michaelboss.newcoins.blockentity.ATMBlockEntity;
import com.michaelboss.newcoins.blockentity.BankCardPrintingMachineBlockEntity;
import com.michaelboss.newcoins.blockentity.CoinageBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModBlockEntities {
    private ModBlockEntities() {
        /* This utility class should not be instantiated */
        throw new IllegalStateException("Utility class");
    }

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, NewCoins.MOD_ID);

    @SuppressWarnings("ConstantConditions")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoinageBlockEntity>> COINAGE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("coinage_block",
                    () -> BlockEntityType.Builder.of(CoinageBlockEntity::new, ModBlocks.COINAGE_BLOCK.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BankCardPrintingMachineBlockEntity>> BANK_CARD_PRINTING_MACHINE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("bank_card_printing_machine_block",
                    () -> BlockEntityType.Builder.of(BankCardPrintingMachineBlockEntity::new, ModBlocks.BANK_CARD_PRINTING_MACHINE_BLOCK.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ATMBlockEntity>> ATM_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("atm_bottom_block",
                    () -> BlockEntityType.Builder.of(ATMBlockEntity::new, ModBlocks.ATM_BOTTOM_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}