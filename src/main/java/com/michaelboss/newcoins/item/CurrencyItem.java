package com.michaelboss.newcoins.item;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class CurrencyItem extends Item {
    private final Supplier<Integer> valueSupplier;

    public CurrencyItem(Properties properties, Supplier<Integer> valueSupplier) {
        super(properties);
        this.valueSupplier = valueSupplier;
    }

    public int getInternalValue() {
        return this.valueSupplier.get();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            int internalCoinValue = getInternalValue();

            float singleValue = internalCoinValue / 10.0F;
            tooltipComponents.add(Component.translatable("tooltip.newcoins.coin.details", singleValue));

            if (stack.getCount() > 1) {
                int totalInternalValue = internalCoinValue * stack.getCount();
                float totalValue = totalInternalValue / 10.0F;

                tooltipComponents.add(Component.translatable("tooltip.newcoins.coin.total_details", totalValue));
            }
        } else {
            tooltipComponents.add(Component.translatable("tooltip.newcoins.hold_shift"));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
