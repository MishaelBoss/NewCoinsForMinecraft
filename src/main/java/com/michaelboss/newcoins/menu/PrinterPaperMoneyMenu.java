package com.michaelboss.newcoins.menu;

import com.michaelboss.newcoins.blockentity.PrinterPaperMoneyBlockEntity;
import com.michaelboss.newcoins.registry.ModBlocks;
import com.michaelboss.newcoins.registry.ModItems;
import com.michaelboss.newcoins.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class PrinterPaperMoneyMenu extends AbstractContainerMenu {
    private final PrinterPaperMoneyBlockEntity blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    @SuppressWarnings("resource")
    public PrinterPaperMoneyMenu(int id, Inventory inventory, FriendlyByteBuf buf){
        super(ModMenus.PRINTER_PAPER_MONEY_MENU.get(), id);
        this.blockEntity = (PrinterPaperMoneyBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos());
        this.data = this.blockEntity.getData();
        this.access = ContainerLevelAccess.NULL;

        this.addDataSlots(this.data);

        setupSlots(inventory);
    }

    public PrinterPaperMoneyMenu(int id, Inventory inventory, PrinterPaperMoneyBlockEntity blockEntity, ContainerData data, ContainerLevelAccess containerLevelAccess){
        super(ModMenus.PRINTER_PAPER_MONEY_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;
        this.access = containerLevelAccess;

        this.addDataSlots(data);

        setupSlots(inventory);
    }

    private void setupSlots(Inventory inventory) {
        this.addSlot(new Slot(this.blockEntity, 0, 8, 24) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.PAPER);
            }
        });

        this.addSlot(new Slot(this.blockEntity, 1, 8, 49) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.GREEN_DYE);
            }
        });

        this.addSlot(new Slot(this.blockEntity, 2, 79, 35) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(ModItems.PAPER_MONEY);
            }
        });

        this.addSlot(new Slot(this.blockEntity, 3, 113, 17) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(Items.IRON_INGOT)
                        || stack.is(Items.GOLD_INGOT);
            }
        });

        this.addSlot(new Slot(this.blockEntity, 4, 148, 3) {
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
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 80 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 138));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) {
            return empty;
        }

        ItemStack source = slot.getItem();
        ItemStack copy = source.copy();

        if (index < 5) {
            if (!moveItemStackTo(source, 5, this.slots.size(), true)) {
                return empty;
            }
        } else {
            if (!moveFromInventoryToTargetSlot(source)){
                return ItemStack.EMPTY;
            }
        }

        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        slot.onTake(player, source);
        return copy;
    }

    private boolean moveFromInventoryToTargetSlot(ItemStack source) {
        if (source.is(Items.PAPER)) {
            return moveItemStackTo(source, 0, 1, false);
        }
        if (source.is(Items.GREEN_DYE)) {
            return moveItemStackTo(source, 1, 2, false);
        }
        if (source.is(ModItems.PAPER_MONEY.get())) {
            return moveItemStackTo(source, 2, 3, false);
        }
        if (source.is(Items.IRON_INGOT) || source.is(Items.GOLD_INGOT)) {
            return moveItemStackTo(source, 3, 4, false);
        }
        return false;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, ModBlocks.PRINTER_PAPER_MONEY_BLOCK.get());
    }

    public ContainerData getData() {
        return this.data;
    }
}
