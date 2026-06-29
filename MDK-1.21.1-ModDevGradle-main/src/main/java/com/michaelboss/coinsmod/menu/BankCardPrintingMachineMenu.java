package com.michaelboss.coinsmod.menu;

import com.michaelboss.coinsmod.block.entity.BankCardPrintingMachineBlockEntity;
import com.michaelboss.coinsmod.item.ModItems;
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

    public BankCardPrintingMachineMenu(int id, Inventory inventory, BankCardPrintingMachineBlockEntity blockEntity) {
        this(id, inventory, blockEntity, blockEntity.getContainerData());
    }

    @SuppressWarnings("resource")
    public BankCardPrintingMachineMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, (BankCardPrintingMachineBlockEntity) inventory.player.level().getBlockEntity(buf.readBlockPos()));
    }

    protected BankCardPrintingMachineMenu(int id, Inventory inventory, BankCardPrintingMachineBlockEntity blockEntity, ContainerData data) {
        super(ModMenus.BANK_CARD_PRINTING_MACHINE_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.data = data;
        assert blockEntity.getLevel() != null;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        this.addDataSlots(data);

        this.addSlot(new Slot(blockEntity, 0, 31, 34) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return isAcceptedSlot0(stack);
            }
        });

        this.addSlot(new Slot(blockEntity, 1, 56, 34) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return isAcceptedSlot1(stack);
            }
        });

        this.addSlot(new Slot(blockEntity, 2, 116, 34) {
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
        return stillValid(this.access, player, this.blockEntity.getBlockState().getBlock());
    }

    public ContainerData getData() {
        return this.data;
    }
}
