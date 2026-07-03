package com.michaelboss.coinsmod.menu;

import com.michaelboss.coinsmod.item.WalletItem;
import com.michaelboss.coinsmod.registry.ModItems;
import com.michaelboss.coinsmod.registry.ModMenus;
import com.michaelboss.coinsmod.item.component.WalletContents;
import com.michaelboss.coinsmod.registry.ModDataComponents;
import com.michaelboss.coinsmod.tag.ModTags;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.item.ItemEntity;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class WalletMenu extends AbstractContainerMenu {
    private final Container walletContainer;
    private final ItemStack walletStack;

    private boolean isAcceptedPayment(ItemStack stack) {
        return stack.is(ModTags.Items.ATM_CARDS)
                || stack.is(ModTags.Items.ATM_CURRENCY);
    }

    public WalletMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, playerInventory.player.getMainHandItem());
    }

    public WalletMenu(int id, Inventory playerInventory, ItemStack walletStack) {
        super(ModMenus.WALLET_MENU.get(), id);
        this.walletStack = walletStack;
        this.walletContainer = new SimpleContainer(9);

        WalletContents contents = WalletItem.getContents(walletStack);
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
                    return isAcceptedPayment(stack);
                }
            });
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @SuppressWarnings("resource")
    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);
        for (int i = 0; i < 9; i++) {
            items.set(i, walletContainer.getItem(i).copy());
        }
        WalletContents newContents = new WalletContents(items);

        ItemStack currentHandItem = player.getMainHandItem();
        if (!currentHandItem.isEmpty() && currentHandItem.is(ModItems.WALLET.get())) {
            WalletItem.setContents(currentHandItem, newContents);
            return;
        }

        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty() && stack.is(ModItems.WALLET.get())) {
                WalletItem.setContents(stack, newContents);
                return;
            }
        }

        if (!player.level().isClientSide) {
            AABB searchArea = player.getBoundingBox().inflate(4.0);
            List<ItemEntity> droppedItems = player.level().getEntitiesOfClass(ItemEntity.class, searchArea);

            for (ItemEntity itemEntity : droppedItems) {
                ItemStack stack = itemEntity.getItem();
                if (!stack.isEmpty() && stack.is(ModItems.WALLET.get())) {
                    WalletItem.setContents(stack, newContents);
                    itemEntity.setItem(stack);
                    break;
                }
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return !this.walletStack.isEmpty() && player.getMainHandItem() == this.walletStack;
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
            if (isAcceptedPayment(stack)) {
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
