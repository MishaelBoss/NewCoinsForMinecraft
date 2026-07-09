package com.michaelboss.newcoins.item;

import com.michaelboss.newcoins.item.component.WalletContents;
import com.michaelboss.newcoins.menu.WalletMenu;
import com.michaelboss.newcoins.registry.ModDataComponents;
import com.michaelboss.newcoins.tag.ModTags;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WalletItem extends Item {
    public WalletItem(Properties properties) {
        super(properties);
    }

    public static WalletContents getContents(ItemStack stack) {
        return stack.get(ModDataComponents.WALLET_CONTENTS.get());
    }

    public static void setContents(ItemStack stack, WalletContents contents) {
        stack.set(ModDataComponents.WALLET_CONTENTS.get(), contents);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            MenuProvider provider = new SimpleMenuProvider(
                    (id, inventory, p) -> new WalletMenu(id, inventory, stack),
                    stack.getHoverName()
            );
            serverPlayer.openMenu(provider);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, java.util.@NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        WalletContents contents = getContents(stack);

        int coins = 0;
        int cards = 0;

        if (contents != null) {
            for (ItemStack item : contents.getContent()) {
                if (item.isEmpty()) continue;
                if (item.is(ModTags.Items.ATM_CURRENCY)) {
                    coins += item.getCount();
                } else if (item.is(ModTags.Items.ATM_CARDS)) {
                    cards += item.getCount();
                }
            }
        }

        tooltip.add(Component.translatable("text.newcoins.wallet.coins", coins));
        tooltip.add(Component.translatable("text.newcoins.wallet.cards", cards));
    }
}
