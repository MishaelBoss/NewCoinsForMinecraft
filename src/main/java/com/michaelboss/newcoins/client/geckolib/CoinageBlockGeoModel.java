package com.michaelboss.newcoins.client.geckolib;

import com.michaelboss.newcoins.NewCoins;
import com.michaelboss.newcoins.blockentity.CoinageBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CoinageBlockGeoModel extends GeoModel<CoinageBlockEntity> {

    @Override
    public ResourceLocation getModelResource(CoinageBlockEntity animatable) {
        return (NewCoins.id("geo/coinage_block.geo.json"));
    }

    @Override
    public ResourceLocation getTextureResource(CoinageBlockEntity animatable) {
        return (NewCoins.id("textures/block/coinage.png"));
    }

    @Override
    public ResourceLocation getAnimationResource(CoinageBlockEntity animatable) {
        return (NewCoins.id("animations/coinage_block.animation.json"));
    }
}