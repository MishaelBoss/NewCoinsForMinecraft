package com.michaelboss.coinsmod.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IronCoinItem extends Item {
    public IronCoinItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.coinsmod.coin.details"));
        } else {
            tooltipComponents.add(Component.translatable("tooltip.coinsmod.coin.hold_shift"));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
