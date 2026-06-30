package com.michaelboss.coinsmod.item.component;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public interface ContainerAccessor {
    NonNullList<ItemStack> getContent();
}
