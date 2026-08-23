package com.michaelboss.newcoins.blockentity;

import com.michaelboss.newcoins.menu.PrinterPaperMoneyMenu;
import com.michaelboss.newcoins.registry.ModBlockEntities;
import com.michaelboss.newcoins.registry.ModDataComponents;
import com.michaelboss.newcoins.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrinterPaperMoneyBlockEntity extends BlockEntity implements MenuProvider, Container {
    private static final int PAPER_SLOT = 0;
    private static final int DYE_SLOT = 1;
    private static final int TEMPLATE_SLOT = 2;
    private static final int VALUE_SLOT = 3;
    private static final int OUTPUT_SLOT = 4;

    private static final int STAGE_ONE_MAX_PROGRESS = 100;
    private static final int STAGE_TWO_MAX_PROGRESS = 40;

    private int stageOneProgress = 0;
    private int stageTwoProgress = 0;

    private final SimpleContainer items = new SimpleContainer(5);
    private final ContainerData data = new SimpleContainerData(4);

    private boolean canProcessStageOne() {
        ItemStack paper = getItem(PAPER_SLOT);
        ItemStack dye = getItem(DYE_SLOT);
        ItemStack template = getItem(TEMPLATE_SLOT);

        return paper.is(Items.PAPER)
                && dye.is(Items.GREEN_DYE)
                && (
                template.isEmpty()
                        || (
                        template.is(ModItems.PAPER_MONEY.get())
                                && template.getCount()
                                < template.getMaxStackSize()
                )
        );
    }

    public PrinterPaperMoneyBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.PRINTER_PAPER_MONEY_BLOCK_ENTITY.get(), pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PrinterPaperMoneyBlockEntity printer) {
        if (level.isClientSide()) {
            return;
        }

        printer.tickStageOne();
        printer.tickStageTwo();

        printer.updateData();
    }

    private void tickStageOne() {
        if (!canProcessStageOne()) {
            this.stageOneProgress = 0;
            return;
        }

        this.stageOneProgress++;

        if (this.stageOneProgress >= STAGE_ONE_MAX_PROGRESS) {
            createTemplate();
            this.stageOneProgress = 0;
        }

        setChanged();
    }

    private void createTemplate() {
        ItemStack paper = getItem(PAPER_SLOT);
        ItemStack dye = getItem(DYE_SLOT);
        ItemStack template = getItem(TEMPLATE_SLOT);

        if (!paper.is(Items.PAPER)) {
            return;
        }

        if (!dye.is(Items.GREEN_DYE)) {
            return;
        }

        if (!template.isEmpty()
                && !template.is(ModItems.PAPER_MONEY.get())) {
            return;
        }

        if (!template.isEmpty()
                && template.getCount() >= template.getMaxStackSize()) {
            return;
        }

        paper.shrink(1);
        dye.shrink(1);

        if (template.isEmpty()) {
            template = new ItemStack(
                    ModItems.PAPER_MONEY.get(),
                    1
            );
        } else {
            template.grow(1);
        }

        setItem(PAPER_SLOT, paper);
        setItem(DYE_SLOT, dye);
        setItem(TEMPLATE_SLOT, template);
    }

    private void tickStageTwo() {
        if (!canProcessStageTwo()) {
            this.stageTwoProgress = 0;
            return;
        }

        this.stageTwoProgress++;

        if (this.stageTwoProgress >= STAGE_TWO_MAX_PROGRESS) {
            createMoney();
            this.stageTwoProgress = 0;
        }

        setChanged();
    }

    private boolean canProcessStageTwo() {
        ItemStack template = getItem(TEMPLATE_SLOT);
        ItemStack valueItem = getItem(VALUE_SLOT);
        ItemStack output = getItem(OUTPUT_SLOT);

        if (!template.is(ModItems.PAPER_MONEY.get())) {
            return false;
        }

        if (getValue(valueItem) <= 0) {
            return false;
        }

        if (output.isEmpty()) {
            return true;
        }

        if (!output.is(ModItems.PAPER_MONEY.get())) {
            return false;
        }

        if (output.getCount() >= output.getMaxStackSize()) {
            return false;
        }

        int outputValue = output.getOrDefault(
                ModDataComponents.MONEY_VALUE.get(),
                0
        );

        return outputValue == getValue(valueItem);
    }

    private void createMoney() {
        int value = getValue(getItem(VALUE_SLOT));

        if (value <= 0) {
            return;
        }

        ItemStack result = new ItemStack(
                ModItems.PAPER_MONEY.get()
        );

        result.set(
                ModDataComponents.MONEY_VALUE.get(),
                value
        );

        ItemStack output = getItem(OUTPUT_SLOT);

        if (output.isEmpty()) {
            setItem(OUTPUT_SLOT, result);
        } else {
            output.grow(1);
            setItem(OUTPUT_SLOT, output);
        }

        ItemStack template = getItem(TEMPLATE_SLOT);
        ItemStack valueItem = getItem(VALUE_SLOT);

        template.shrink(1);
        valueItem.shrink(1);

        setItem(TEMPLATE_SLOT, template);
        setItem(VALUE_SLOT, valueItem);
    }

    private int getValue(ItemStack stack) {
        if (stack.is(Items.GOLD_INGOT))
            return 5;

        if (stack.is(Items.IRON_INGOT))
            return 3;

        return 0;
    }

    private void updateData() {
        this.data.set(0, this.stageOneProgress);
        this.data.set(1, STAGE_ONE_MAX_PROGRESS);

        this.data.set(2, this.stageTwoProgress);
        this.data.set(3, STAGE_TWO_MAX_PROGRESS);
    }

    @Override
    public int getContainerSize() {
        return this.items.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.items.getItem(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack result = this.items.removeItem(slot, amount);
        setChanged();
        return result;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        return this.items.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack itemStack) {
        this.items.setItem(slot, itemStack);
        this.setChanged();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.items.stillValid(player);
    }

    @Override
    public void clearContent() {
        this.items.clearContent();
        setChanged();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("title.newcoins.menu.printer_paper_money_block");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        assert this.level != null;
        return new PrinterPaperMoneyMenu(id, inventory, this, data, net.minecraft.world.inventory.ContainerLevelAccess.create(this.level, this.worldPosition));
    }

    public ContainerData getData() {
        return this.data;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);

        ListTag itemsTag = new ListTag();

        for (int i = 0; i < this.items.getContainerSize(); i++) {
            ItemStack stack = this.items.getItem(i);

            if (!stack.isEmpty()) {
                CompoundTag slotTag = new CompoundTag();
                slotTag.putInt("Slot", i);

                itemsTag.add(stack.save(registries, slotTag));
            }
        }

        tag.put("Items", itemsTag);
        tag.putInt("StageOneProgress", this.stageOneProgress);
        tag.putInt("StageTwoProgress", this.stageTwoProgress);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);

        this.items.clearContent();

        ListTag itemsTag = tag.getList(
                "Items",
                Tag.TAG_COMPOUND
        );

        for (int i = 0; i < itemsTag.size(); i++) {
            CompoundTag slotTag = itemsTag.getCompound(i);
            int slot = slotTag.getInt("Slot");

            if (slot >= 0 && slot < this.items.getContainerSize()) {
                ItemStack stack = ItemStack.parse(
                        registries,
                        slotTag
                ).orElse(ItemStack.EMPTY);

                this.items.setItem(slot, stack);
            }
        }

        this.stageOneProgress = tag.getInt("StageOneProgress");
        this.stageTwoProgress = tag.getInt("StageTwoProgress");

        if (this.stageOneProgress < 0
                || this.stageOneProgress >= STAGE_ONE_MAX_PROGRESS) {
            this.stageOneProgress = 0;
        }

        if (this.stageTwoProgress < 0
                || this.stageTwoProgress >= STAGE_TWO_MAX_PROGRESS) {
            this.stageTwoProgress = 0;
        }

        updateData();
    }
}
