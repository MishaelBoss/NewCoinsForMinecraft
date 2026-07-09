package com.michaelboss.newcoins.tag;

import com.michaelboss.newcoins.NewCoins;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    private ModTags() {
        /* This utility class should not be instantiated */
        throw new IllegalStateException("Utility class");
    }

    public static class Items {
        private Items() {
            /* This utility class should not be instantiated */
            throw new IllegalStateException("Utility class");
        }

        public static final TagKey<Item> ATM_CURRENCY =
                ItemTags.create(NewCoins.id("atm_currency"));

        public static final TagKey<Item> ATM_CARDS =
                ItemTags.create(NewCoins.id("atm_cards"));
    }
}
