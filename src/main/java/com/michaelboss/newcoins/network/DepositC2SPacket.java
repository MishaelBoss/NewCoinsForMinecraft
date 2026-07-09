package com.michaelboss.newcoins.network;

import com.michaelboss.newcoins.blockentity.ATMBlockEntity;
import com.michaelboss.newcoins.item.CardItem;
import com.michaelboss.newcoins.item.CurrencyItem;
import com.michaelboss.newcoins.menu.ATMMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record DepositC2SPacket() implements CustomPacketPayload {
    public static final Type<DepositC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("newcoins", "deposit_c2s"));

    public static final StreamCodec<FriendlyByteBuf, DepositC2SPacket> STREAM_CODEC =
            StreamCodec.unit(new DepositC2SPacket());

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final IPayloadContext context) {
        context.enqueueWork(() -> {
            if(context.player() instanceof ServerPlayer player && player.containerMenu instanceof ATMMenu atmMenu) {
                ATMBlockEntity be = atmMenu.getBlockEntity();

                ItemStack cardStack = be.getItem(6);
                if (cardStack.isEmpty()) return;

                int totalCollected = 0;
                for (int slot = 0; slot <= 5; slot++) {
                    ItemStack coinStack = be.getItem(slot);
                    if (!coinStack.isEmpty() && coinStack.getItem() instanceof CurrencyItem currencyItem) {
                        totalCollected += (currencyItem.getInternalValue() * coinStack.getCount());
                    }
                }

                if (totalCollected <= 0) return;

                int newBalance = CardItem.getDeposit(cardStack) + totalCollected;
                CardItem.setDeposit(cardStack, newBalance);

                for (int slot = 0; slot <= 5; slot++) {
                    be.setItem(slot, ItemStack.EMPTY);
                }

                be.setChanged();
            }
        });
    }
}
