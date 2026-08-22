package com.michaelboss.newcoins.item;

import com.michaelboss.newcoins.registry.ModDataComponents;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class CurrencyItem extends Item {
    private final Supplier<Integer> defaultValueSupplier;

    public CurrencyItem(Properties properties, Supplier<Integer> valueSupplier) {
        super(properties);
        this.defaultValueSupplier = valueSupplier;
    }

    public int getValue(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.MONEY_VALUE.get(), defaultValueSupplier.get());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            int value = getValue(stack);

            float singleValue = value / 10.0F;
            tooltipComponents.add(Component.translatable("tooltip.newcoins.coin.details", singleValue));

            if (stack.getCount() > 1) {
                float total = (value * stack.getCount()) / 10.0F;
                tooltipComponents.add(Component.translatable("tooltip.newcoins.coin.total_details", total));
            }
        } else {
            tooltipComponents.add(Component.translatable("tooltip.newcoins.hold_shift"));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
