package com.michaelboss.coinsmod.client.renderer;

import com.michaelboss.coinsmod.block.entity.BankCardPrintingMachineBlockEntity;
import com.michaelboss.coinsmod.client.geckolib.BankCardPrintingMachineBlockGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class BankCardPrintingMachineBlockRender extends GeoBlockRenderer<BankCardPrintingMachineBlockEntity> {
    public BankCardPrintingMachineBlockRender(BlockEntityRendererProvider.Context context) {
        super(new BankCardPrintingMachineBlockGeoModel());
    }
}
