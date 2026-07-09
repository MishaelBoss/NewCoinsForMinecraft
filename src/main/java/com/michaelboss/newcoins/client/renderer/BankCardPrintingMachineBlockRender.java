package com.michaelboss.newcoins.client.renderer;

import com.michaelboss.newcoins.blockentity.BankCardPrintingMachineBlockEntity;
import com.michaelboss.newcoins.client.geckolib.BankCardPrintingMachineBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BankCardPrintingMachineBlockRender extends GeoBlockRenderer<BankCardPrintingMachineBlockEntity> {
    public BankCardPrintingMachineBlockRender() {
        super(new BankCardPrintingMachineBlockGeoModel());
    }
}
