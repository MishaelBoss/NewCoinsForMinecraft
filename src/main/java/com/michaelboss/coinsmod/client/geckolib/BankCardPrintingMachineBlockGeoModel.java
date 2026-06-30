package com.michaelboss.coinsmod.client.geckolib;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.block.entity.BankCardPrintingMachineBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BankCardPrintingMachineBlockGeoModel extends GeoModel<BankCardPrintingMachineBlockEntity> {

    @Override
    public ResourceLocation getModelResource(BankCardPrintingMachineBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CoinsMod.MOD_ID,
                "geo/bank_card_printing_machine_block.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BankCardPrintingMachineBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CoinsMod.MOD_ID,
                "textures/block/bank_card_printing_machine.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BankCardPrintingMachineBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(CoinsMod.MOD_ID,
                "animations/bank_card_printing_machine_block.animation.json");
    }
}
