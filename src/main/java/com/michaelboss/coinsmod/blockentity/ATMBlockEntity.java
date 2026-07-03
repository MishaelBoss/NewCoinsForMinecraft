package com.michaelboss.coinsmod.blockentity;

import com.michaelboss.coinsmod.block.ATMBottomBlock;
import com.michaelboss.coinsmod.item.CardItem;
import com.michaelboss.coinsmod.registry.ModBlockEntities;
import com.michaelboss.coinsmod.menu.ATMMenu;
import com.michaelboss.coinsmod.tag.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ATMBlockEntity extends BlockEntity implements MenuProvider, Container {
    private final SimpleContainer items = new SimpleContainer(7);
    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> ATMBlockEntity.this.bootProgress;
                case 1 -> ATMBlockEntity.this.maxBootTime;
                case 2 -> ATMBlockEntity.this.cardStatus;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> ATMBlockEntity.this.bootProgress = value;
                case 2 -> ATMBlockEntity.this.cardStatus = value;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    };

    private int bootProgress = 0;
    private final int maxBootTime = 60;
    private int cardStatus = 0;

    public ATMBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ATM_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void setChanged() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
        super.setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ATMBlockEntity be) {
        if (level.isClientSide) return;

        ItemStack card = be.getItem(6);
        boolean hasCard = card.is(ModTags.Items.ATM_CARDS);

        boolean isCardValid = hasCard
                && card.has(CardItem.getOwnerComponent())
                && card.has(CardItem.getUuidComponent());

        if (state.hasProperty(ATMBottomBlock.HAS_CARD) && state.getValue(ATMBottomBlock.HAS_CARD) != hasCard) {
            level.setBlock(pos, state.setValue(ATMBottomBlock.HAS_CARD, hasCard), 3);
            be.setChanged();
        }

        if (!hasCard) {
            be.bootProgress = 0;
            be.cardStatus = 0;
            be.data.set(0, 0);
            be.data.set(2, 0);
            be.setChanged();
            return;
        }

        if (isCardValid) {
            if (be.bootProgress < be.maxBootTime) {
                be.bootProgress++;
                be.cardStatus = 1;
                be.data.set(0, be.bootProgress);
                be.setChanged();
            } else {
                be.cardStatus = 2;
                be.data.set(2, be.cardStatus);
                be.setChanged();
            }
        } else {
            be.bootProgress = 0;
            be.cardStatus = 3;
            be.data.set(0, 0);
            be.data.set(2, be.cardStatus);
            be.setChanged();
        }
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
        tag.putInt("BootProgress", this.bootProgress);
        tag.putInt("CardStatus", this.cardStatus);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        ListTag itemsTag = tag.getList("Items", CompoundTag.TAG_COMPOUND);
        this.items.clearContent();

        for (int i = 0; i < itemsTag.size(); i++) {
            CompoundTag slotTag = itemsTag.getCompound(i);
            int slot = slotTag.getInt("Slot");
            if (slot >= 0 && slot < this.items.getContainerSize()) {
                ItemStack stack = ItemStack.parse(registries, slotTag).orElse(ItemStack.EMPTY);
                this.items.setItem(slot, stack);
            }
        }

        this.bootProgress = tag.getInt("BootProgress");
        this.cardStatus  = tag.getInt("CardStatus");
    }

    @Override
    public int getContainerSize() {
        return this.items.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return this.items.getItem(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return this.items.removeItem(slot, amount);
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
        items.clearContent();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return  Component.translatable("title.coinsmod.menu.atm");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        assert this.level != null;
        return new ATMMenu(id, inventory, this, data, net.minecraft.world.inventory.ContainerLevelAccess.create(this.level, this.worldPosition));
    }

    public ContainerData getContainerData() {
        return this.data;
    }

    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("BootProgress", this.bootProgress);
        tag.putInt("CardStatus", this.cardStatus);
        return tag;
    }

    @Override
    public void onDataPacket(net.minecraft.network.@NotNull Connection net, net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            this.bootProgress = tag.getInt("BootProgress");
            this.cardStatus  = tag.getInt("CardStatus");
            this.data.set(0, this.bootProgress);
            this.data.set(2, this.cardStatus);
        }
    }
}
