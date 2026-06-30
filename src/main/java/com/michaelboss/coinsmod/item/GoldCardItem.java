package com.michaelboss.coinsmod.item;

import com.michaelboss.coinsmod.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoldCardItem extends Item {
    public GoldCardItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        String ownerName = stack.get(ModDataComponents.CARD_OWNER.get());

        if (ownerName != null && !ownerName.isEmpty()) {
            return Component.translatable(this.getDescriptionId(stack))
                    .append(" (")
                    .append(Component.literal(ownerName).withStyle(ChatFormatting.GREEN))
                    .append(")");

        }
        return super.getName(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        String ownerUUID = stack.get(ModDataComponents.CARD_UUID.get());

        if (Screen.hasShiftDown()) {
            if (ownerUUID != null && !ownerUUID.isEmpty()) {
                tooltipComponents.add(Component.translatable("tooltip.coinsmod.card.id")
                        .append(": ")
                        .append(Component.literal(ownerUUID).withStyle(ChatFormatting.GRAY)));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.coinsmod.card.no_biometrics").withStyle(ChatFormatting.RED));
            }
        } else {
            tooltipComponents.add(Component.translatable("tooltip.coinsmod.hold_shift").withStyle(ChatFormatting.YELLOW));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
