package com.michaelboss.newcoins.item;

import com.michaelboss.newcoins.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class CardItem extends Item {
    public CardItem(Properties properties) {
        super(properties);
    }

    public static CardItem create() {
        return new CardItem(new Item.Properties().stacksTo(1));
    }

    public static DataComponentType<String> getOwnerComponent() {
        return ModDataComponents.CARD_OWNER.get();
    }

    public static String getOwnerName(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CARD_OWNER, "Unknown");
    }

    public static void setOwner(ItemStack stack, String value) {
        stack.set(ModDataComponents.CARD_OWNER.get(), value);
    }

    public static DataComponentType<String> getUuidComponent() {
        return ModDataComponents.CARD_UUID.get();
    }

    public static void setUUID(ItemStack stack, UUID value) {
        stack.set(ModDataComponents.CARD_UUID.get(), value.toString());
    }

    public static int getDeposit(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CARD_DEPOSIT.get(), 0);
    }

    public static void setDeposit(ItemStack stack, int value) {
        stack.set(ModDataComponents.CARD_DEPOSIT.get(), value);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        String ownerName = stack.get(getOwnerComponent());

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
        String ownerUUID = stack.get(getUuidComponent());

        if (Screen.hasShiftDown()) {
            if (ownerUUID != null && !ownerUUID.isEmpty()) {
                tooltipComponents.add(Component.translatable("tooltip.newcoins.card.id")
                        .append(": ")
                        .append(Component.literal(ownerUUID).withStyle(ChatFormatting.GRAY)));

                int deposit = Math.round(getDeposit(stack) / 10.0F);

                tooltipComponents.add(Component.translatable("tooltip.newcoins.card.deposit", deposit));
            } else {
                tooltipComponents.add(Component.translatable("tooltip.newcoins.card.no_biometrics").withStyle(ChatFormatting.RED));
            }
        } else {
            tooltipComponents.add(Component.translatable("tooltip.newcoins.hold_shift").withStyle(ChatFormatting.YELLOW));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}
