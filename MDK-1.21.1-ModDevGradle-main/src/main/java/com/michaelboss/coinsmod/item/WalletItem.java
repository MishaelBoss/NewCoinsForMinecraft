package com.michaelboss.coinsmod.item;

import com.michaelboss.coinsmod.item.component.WalletContents;
import com.michaelboss.coinsmod.menu.WalletMenu;
import com.michaelboss.coinsmod.registry.ModDataComponents;
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

public class WalletItem extends Item {

//    public WalletItem(Properties properties, int size) {
//        super(properties.component(
//                ModDataComponents.WALLET_CONTENTS,
//                new WalletContents(NonNullList.createWithCapacity(size))
//        ));
//    }

    public WalletItem(Properties properties) {
        super(properties);
    }

//    @Override
//    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand useHand){
//        if (!level.isClientSide) {
//            ItemStack useItem = player.getItemInHand(useHand);
//            player.openMenu(new ItemBoundContainer<>(9, useItem, ModDataComponents.WALLET_CONTENTS, WalletContents::new));
//            return InteractionResultHolder.success(useItem);
//        }
//        return super.use(level, player, useHand);
//    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
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
    public void appendHoverText(ItemStack stack, TooltipContext context, java.util.List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        WalletContents contents = stack.get(ModDataComponents.WALLET_CONTENTS.get());
        int coins = 0;
        int cards = 0;

        if (contents != null) {
            for (ItemStack item : contents.getContent()) {
                if (item.isEmpty()) continue;
                if (item.is(ModItems.COPPER_COIN.get()) || item.is(ModItems.IRON_COIN.get()) || item.is(ModItems.GOLD_COIN.get())) {
                    coins += item.getCount();
                } else if (item.is(ModItems.CARD_CLASSIC.get()) || item.is(ModItems.GOLD_CLASSIC.get())) {
                    cards += item.getCount();
                }
            }
        }

        tooltip.add(Component.literal("Монет: " + coins));
        tooltip.add(Component.literal("Карт: " + cards));
    }
}
