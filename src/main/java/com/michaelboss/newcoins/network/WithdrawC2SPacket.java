package com.michaelboss.newcoins.network;

import com.michaelboss.newcoins.blockentity.ATMBlockEntity;
import com.michaelboss.newcoins.item.CardItem;
import com.michaelboss.newcoins.registry.ModItems;
import com.michaelboss.newcoins.menu.ATMMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record WithdrawC2SPacket(int value) implements CustomPacketPayload {
    public static final Type<WithdrawC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("newcoins", "withdraw_c2s"));

    public static final StreamCodec<FriendlyByteBuf, WithdrawC2SPacket> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, WithdrawC2SPacket::value, WithdrawC2SPacket::new);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final WithdrawC2SPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player && player.containerMenu instanceof ATMMenu atmMenu) {
                ATMBlockEntity be = atmMenu.getBlockEntity();

                ItemStack cardStack = be.getItem(6);
                if (cardStack.isEmpty()) return;

                int requestedAmount = payload.value() * 10;
                if (requestedAmount <= 0) return;

                if (CardItem.getDeposit(cardStack) < requestedAmount) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("text.newcoins.withdraw_c2s.insufficient_funds"));
                    return;
                }

                int remainingToGive = requestedAmount;
                int goldCoinsToGive = 0;
                int ironCoinsToGive = 0;
                int copperCoinsToGive = 0;

                int maxGold = remainingToGive / 23;
                for (int g = maxGold; g >= 0; g--) {
                    int tempRemaining = remainingToGive - (g * 23);

                    int maxIron = tempRemaining / 15;
                    for (int i = maxIron; i >= 0; i--) {
                        int finalRemaining = tempRemaining - (i * 15);

                        if (finalRemaining >= 0 && finalRemaining % 10 == 0) {
                            goldCoinsToGive = g;
                            ironCoinsToGive = i;
                            copperCoinsToGive = finalRemaining / 10;
                            remainingToGive = 0;
                            break;
                        }
                    }
                    if (remainingToGive == 0) break;
                }

                if (remainingToGive > 0) {
                    copperCoinsToGive = requestedAmount / 10;
                }

                int requiredSlots = 0;
                if (goldCoinsToGive > 0) requiredSlots += (int) Math.ceil(goldCoinsToGive / 64.0);
                if (ironCoinsToGive > 0) requiredSlots += (int) Math.ceil(ironCoinsToGive / 64.0);
                if (copperCoinsToGive > 0) requiredSlots += (int) Math.ceil(copperCoinsToGive / 64.0);

                int emptySlotsCount = 0;
                for (int slot = 0; slot <= 5; slot++) {
                    if (be.getItem(slot).isEmpty()) {
                        emptySlotsCount++;
                    }
                }

                if (emptySlotsCount < requiredSlots) {
                    player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("text.newcoins.withdraw_c2s.not_enough_slots"));
                    return;
                }

                splitAndPlaceInSlots(be, ModItems.GOLD_COIN.get(), goldCoinsToGive);
                splitAndPlaceInSlots(be, ModItems.IRON_COIN.get(), ironCoinsToGive);
                splitAndPlaceInSlots(be, ModItems.COPPER_COIN.get(), copperCoinsToGive);

                int newBalance = CardItem.getDeposit(cardStack) - requestedAmount;
                CardItem.setDeposit(cardStack, newBalance);

                be.setChanged();
                player.sendSystemMessage(net.minecraft.network.chat.Component.translatable("text.newcoins.withdraw_c2s.successfully_removed", payload.value()));
            }

        });
    }

    private static void splitAndPlaceInSlots(ATMBlockEntity be, net.minecraft.world.item.Item coinItem, int totalAmount) {
        int remaining = totalAmount;

        while (remaining > 0) {
            int countToPlace = Math.min(remaining, 64);
            ItemStack stackToInsert = new ItemStack(coinItem, countToPlace);
            boolean placed = false;

            for (int slot = 0; slot <= 5; slot++) {
                ItemStack currentStack = be.getItem(slot);
                if (!currentStack.isEmpty() && currentStack.is(coinItem)) {
                    int currentCount = currentStack.getCount();
                    if (currentCount + countToPlace <= 64) {
                        currentStack.setCount(currentCount + countToPlace);
                        be.setItem(slot, currentStack);
                        placed = true;
                        break;
                    }
                }
            }

            if (!placed) {
                for (int slot = 0; slot <= 5; slot++) {
                    if (be.getItem(slot).isEmpty()) {
                        be.setItem(slot, stackToInsert);
                        break;
                    }
                }
            }

            remaining -= countToPlace;
        }
    }
}
