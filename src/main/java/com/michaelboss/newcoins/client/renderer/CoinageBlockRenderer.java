package com.michaelboss.newcoins.client.renderer;

import com.michaelboss.newcoins.blockentity.CoinageBlockEntity;
import com.michaelboss.newcoins.client.geckolib.CoinageBlockGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CoinageBlockRenderer extends GeoBlockRenderer<CoinageBlockEntity> {
    public CoinageBlockRenderer() {
        super(new CoinageBlockGeoModel());
    }
}