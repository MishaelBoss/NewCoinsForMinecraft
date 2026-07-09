package com.michaelboss.newcoins.client.geckolib;

import com.michaelboss.newcoins.NewCoins;
import com.michaelboss.newcoins.blockentity.BankCardPrintingMachineBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BankCardPrintingMachineBlockGeoModel extends GeoModel<BankCardPrintingMachineBlockEntity> {

    @Override
    public ResourceLocation getModelResource(BankCardPrintingMachineBlockEntity animatable) {
        return (NewCoins.id("geo/bank_card_printing_machine_block.geo.json"));
    }

    @Override
    public ResourceLocation getTextureResource(BankCardPrintingMachineBlockEntity animatable) {
        return (NewCoins.id("textures/block/bank_card_printing_machine.png"));
    }

    @Override
    public ResourceLocation getAnimationResource(BankCardPrintingMachineBlockEntity animatable) {
        return (NewCoins.id("animations/bank_card_printing_machine_block.animation.json"));
    }
}
