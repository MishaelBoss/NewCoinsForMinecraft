package com.michaelboss.newcoins.menu;

import com.michaelboss.newcoins.registry.ModBlocks;
import com.michaelboss.newcoins.blockentity.ATMBlockEntity;
import com.michaelboss.newcoins.registry.ModMenus;
import com.michaelboss.newcoins.tag.ModTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ATMMenu extends AbstractContainerMenu {
    private final ATMBlockEntity blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    private boolean isAcceptedSlotInputCoin(ItemStack stack) {
        return stack.is(ModTags.Items.ATM_CURRENCY);
    }

    private boolean isAcceptedSlotCard(ItemStack stack) {
        return stack.is(ModTags.Items.ATM_CARDS);
    }

    @SuppressWarnings("resource")
    public ATMMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        super(ModMenus.ATM_MENU.get(), id);
        this.blockEntity = (ATMBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos());

        assert this.blockEntity != null;
        this.data = this.blockEntity.getContainerData();
        this.addDataSlots(this.data);

        this.access = ContainerLevelAccess.NULL;

        setupMenu(inventory);
    }

    public ATMMenu(int id, Inventory inventory, ATMBlockEntity blockEntity, ContainerData data, ContainerLevelAccess containerLevelAccess) {
        super(ModMenus.ATM_MENU.get(), id);
        this.blockEntity = blockEntity;

        this.data = data;
        this.access = containerLevelAccess;

        setupMenu(inventory);
    }

    private void setupMenu(Inventory inventory) {
        this.addSlot(new Slot(this.blockEntity, 0, 135, 19) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return isAcceptedSlotInputCoin(stack); }
        });
        this.addSlot(new Slot(this.blockEntity, 1, 153, 19) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return isAcceptedSlotInputCoin(stack); }
        });
        this.addSlot(new Slot(this.blockEntity, 2, 135, 37) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return isAcceptedSlotInputCoin(stack); }
        });

        this.addSlot(new Slot(this.blockEntity, 3, 153, 37) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return isAcceptedSlotInputCoin(stack); }
        });
        this.addSlot(new Slot(this.blockEntity, 4, 135, 55) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return isAcceptedSlotInputCoin(stack); }
        });
        this.addSlot(new Slot(this.blockEntity, 5, 153, 55) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return isAcceptedSlotInputCoin(stack); }
        });

        this.addSlot(new Slot(this.blockEntity, 6, 152, 77) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return isAcceptedSlotCard(stack); }
        });

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 96 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 154));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < 7) {
            if (!this.moveItemStackTo(stack, 7, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (isAcceptedSlotInputCoin(stack)) {
                if (!this.moveItemStackTo(stack, 0, 6, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (isAcceptedSlotCard(stack)) {
                if (!this.moveItemStackTo(stack, 6, 7, false)) {
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

    @SuppressWarnings("resource")
    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);

        Slot cardSlot = this.slots.get(6);
        ItemStack cardStack = cardSlot.getItem();

        if (!cardStack.isEmpty()) {
            int playerInvStart = 7;
            int playerInvEnd = this.slots.size();

            if (this.moveItemStackTo(cardStack, playerInvStart, playerInvEnd, false)) {
                cardSlot.set(ItemStack.EMPTY);
            } else {
                if (!player.level().isClientSide) {
                    player.drop(cardStack, false);
                    cardSlot.set(ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(access, player, ModBlocks.ATM_BOTTOM_BLOCK.get());
    }

    public ContainerData getData() {
        return this.data;
    }

    public ATMBlockEntity getBlockEntity() {
        return this.blockEntity;
    }
}
