package com.michaelboss.newcoins.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public record WalletContents(NonNullList<ItemStack> contents) implements ContainerAccessor {
    public static final Codec<WalletContents> CODEC =
            NonNullList.codecOf(ItemStack.OPTIONAL_CODEC)
                    .xmap(WalletContents::new, WalletContents::contents);

    public static final StreamCodec<RegistryFriendlyByteBuf, WalletContents> STREAM_CODEC =
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list())
                    .map(NonNullList::copyOf, Function.identity())
                    .map(WalletContents::new, WalletContents::contents);

    @Override
    public NonNullList<ItemStack> getContent() {
        return contents;
    }
}
