package com.michaelboss.newcoins.menu;

import com.michaelboss.newcoins.blockentity.CoinageBlockEntity;
import com.michaelboss.newcoins.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class CoinageMenu extends AbstractContainerMenu {
    private final CoinageBlockEntity blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    public CoinageMenu(int id, Inventory inventory, CoinageBlockEntity blockEntity) {
        this(id, inventory, blockEntity, blockEntity.getContainerData());
    }

    @SuppressWarnings("resource")
    public CoinageMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, (CoinageBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public CoinageMenu(int id, Inventory inventory, CoinageBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.COINAGE_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;

        if(blockEntity.getLevel() != null) {
            this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        } else {
            this.access = ContainerLevelAccess.NULL;
        }

        this.addDataSlots(data);

        this.addSlot(new Slot(blockEntity, 0, 56, 21) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.IRON_INGOT) || stack.is(Items.GOLD_INGOT) || stack.is(Items.COPPER_INGOT);
            }
        });

        this.addSlot(new Slot(blockEntity, 1, 116, 21) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 56 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 114));
        }
    }

    public ContainerData getData() {
        return this.data;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, this.blockEntity.getBlockState().getBlock());
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < 2) {
            if (!this.moveItemStackTo(stack, 2, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stack.is(Items.IRON_INGOT) || stack.is(Items.GOLD_INGOT) || stack.is(Items.COPPER_INGOT)) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
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
}