package com.michaelboss.newcoins.menu;

import com.michaelboss.newcoins.registry.ModBlocks;
import com.michaelboss.newcoins.blockentity.BankCardPrintingMachineBlockEntity;
import com.michaelboss.newcoins.registry.ModItems;
import com.michaelboss.newcoins.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class BankCardPrintingMachineMenu extends AbstractContainerMenu {
    private final BankCardPrintingMachineBlockEntity blockEntity;
    private final ContainerData data;
    private final ContainerLevelAccess access;

    private boolean isAcceptedSlot0(ItemStack stack) {
        return stack.is(Items.IRON_INGOT)
                || stack.is(Items.GOLD_INGOT);
    }

    private boolean isAcceptedSlot1(ItemStack stack) {
        return stack.is(ModItems.CHIP.get());
    }

    @SuppressWarnings("resource")
    public BankCardPrintingMachineMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        super(ModMenus.BANK_CARD_PRINTING_MACHINE_MENU.get(), id);
        this.blockEntity = (BankCardPrintingMachineBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos());
        this.data = new SimpleContainerData(2);
        this.addDataSlots(this.data);

        this.access = ContainerLevelAccess.NULL;

        setupSlots(inventory);
    }

    public BankCardPrintingMachineMenu(int id, Inventory inventory, BankCardPrintingMachineBlockEntity blockEntity, ContainerData data, ContainerLevelAccess containerLevelAccess) {
        super(ModMenus.BANK_CARD_PRINTING_MACHINE_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;
        this.access = containerLevelAccess;

        this.addDataSlots(data);

        setupSlots(inventory);
    }

    private void setupSlots(Inventory inventory) {
        this.addSlot(new Slot(this.blockEntity, 0, 31, 34) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return isAcceptedSlot0(stack); }
        });

        this.addSlot(new Slot(this.blockEntity, 1, 56, 34) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return isAcceptedSlot1(stack); }
        });

        this.addSlot(new Slot(this.blockEntity, 2, 116, 34) {
            @Override public boolean mayPlace(@NotNull ItemStack stack) { return false; }
        });

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 69 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 127));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);

        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (index < 3) {
            if (!this.moveItemStackTo(stack, 2, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (stack.is(Items.IRON_INGOT) || stack.is(Items.GOLD_INGOT) || stack.is(Items.COPPER_INGOT)) {
                if (!this.moveItemStackTo(stack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (stack.is(ModItems.CHIP)) {
                if (!this.moveItemStackTo(stack, 1, 2, false)) {
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

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, ModBlocks.BANK_CARD_PRINTING_MACHINE_BLOCK.get());
    }

    public ContainerData getData() {
        return this.data;
    }
}
