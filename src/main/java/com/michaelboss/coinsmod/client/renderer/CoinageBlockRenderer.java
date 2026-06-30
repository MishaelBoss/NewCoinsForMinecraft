package com.michaelboss.coinsmod.client.renderer;

import com.michaelboss.coinsmod.block.entity.CoinageBlockEntity;
import com.michaelboss.coinsmod.client.geckolib.CoinageBlockGeoModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CoinageBlockRenderer extends GeoBlockRenderer<CoinageBlockEntity> {
    public CoinageBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new CoinageBlockGeoModel());
    }
}