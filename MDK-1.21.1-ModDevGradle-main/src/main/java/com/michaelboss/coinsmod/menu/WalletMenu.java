package com.michaelboss.coinsmod.menu;

import com.michaelboss.coinsmod.item.ModItems;

import com.michaelboss.coinsmod.item.component.WalletContents;
import com.michaelboss.coinsmod.registry.ModDataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WalletMenu extends AbstractContainerMenu {
    private final Container walletContainer;
    private final ItemStack walletStack;

    public WalletMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, playerInventory.player.getMainHandItem());
    }

    public WalletMenu(int id, Inventory playerInventory, ItemStack walletStack) {
        super(ModMenus.WALLET_MENU.get(), id);
        this.walletStack = walletStack;
        this.walletContainer = new SimpleContainer(9);

        WalletContents contents = walletStack.get(ModDataComponents.WALLET_CONTENTS.get());
        if (contents != null) {
            for (int i = 0; i < Math.min(contents.getContent().size(), 9); i++) {
                walletContainer.setItem(i, contents.getContent().get(i));
            }
        }

        int startX = 62;
        int startY = 17;

        for (int i = 0; i < 9; i++) {
            int row = i / 3;
            int col = i % 3;
            this.addSlot(new Slot(walletContainer, i, startX + col * 18, startY + row * 18) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return stack.is(ModItems.COPPER_COIN.get())
                            || stack.is(ModItems.IRON_COIN.get())
                            || stack.is(ModItems.GOLD_COIN.get())
                            || stack.is(ModItems.CARD_CLASSIC.get())
                            || stack.is(ModItems.GOLD_CLASSIC.get());
                }
            });
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < 9; i++) {
            items.set(i, walletContainer.getItem(i).copy());
        }
        walletStack.set(ModDataComponents.WALLET_CONTENTS.get(), new WalletContents(items));
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < 9) {
            if (!this.moveItemStackTo(stack, 9, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stack.is(ModItems.COPPER_COIN.get())
                    || stack.is(ModItems.IRON_COIN.get())
                    || stack.is(ModItems.GOLD_COIN.get())
                    || stack.is(ModItems.CARD_CLASSIC.get())
                    || stack.is(ModItems.GOLD_CLASSIC.get())) {
                if (!this.moveItemStackTo(stack, 0, 9, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }
}