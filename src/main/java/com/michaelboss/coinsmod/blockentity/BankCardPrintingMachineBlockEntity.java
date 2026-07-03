package com.michaelboss.coinsmod.blockentity;

import com.michaelboss.coinsmod.block.BankCardPrintingMachineBlock;
import com.michaelboss.coinsmod.item.CardItem;
import com.michaelboss.coinsmod.registry.ModBlockEntities;
import com.michaelboss.coinsmod.registry.ModItems;
import com.michaelboss.coinsmod.menu.BankCardPrintingMachineMenu;
import com.michaelboss.coinsmod.registry.ModDataComponents;
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
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.UUID;

public class BankCardPrintingMachineBlockEntity extends BlockEntity implements MenuProvider, Container, GeoAnimatable {
    private final SimpleContainer items = new SimpleContainer(3);
    private final ContainerData data = new SimpleContainerData(2);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int progress = 0;
    private int maxProgress = 60;
    private UUID printingPlayerUUID = null;
    private String printingPlayerName = "Unknown";

    private static final RawAnimation PRINT_ANIM = RawAnimation.begin().thenLoop("typing_on_a_keyboard");

    public BankCardPrintingMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BANK_CARD_PRINTING_MACHINE_BLOCK_ENTITY.get(), pos, state);
    }

    public void setPrintingPlayer(Player player) {
        this.printingPlayerUUID = player.getUUID();
        this.printingPlayerName = player.getScoreboardName();
        this.setChanged();
    }

    @Override
    public void setChanged() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
        super.setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BankCardPrintingMachineBlockEntity be) {
        if (!level.isClientSide) {

            ItemStack ingot = be.getItem(0);
            ItemStack chip = be.getItem(1);
            ItemStack output = be.getItem(2);

            boolean isProcessing = be.canProcess(ingot, chip) && output.isEmpty();

            if (state.getValue(BankCardPrintingMachineBlock.PRINTING) != isProcessing) {
                level.setBlock(pos, state.setValue(BankCardPrintingMachineBlock.PRINTING, isProcessing), 3);
                be.setChanged();
            }

            if (isProcessing) {
                be.progress++;

                be.data.set(0, be.progress);
                be.data.set(1, be.maxProgress);

                if (be.progress >= be.maxProgress) {
                    ItemStack result = be.createResult(ingot);

                    if(!result.isEmpty()){
                        CardItem.setOwner(result, be.printingPlayerName);

                        if (be.printingPlayerUUID != null) {
                            CardItem.setUUID(result, be.printingPlayerUUID);
                        }

                        CardItem.setDeposit(result, 0);

                        be.setItem(2, result);
                    }

                    ingot.shrink(1);
                    chip.shrink(1);
                    be.setItem(0, ingot);
                    be.setItem(1, chip);

                    be.progress = 0;
                    be.data.set(0, 0);
                    be.setChanged();
                }
            } else {
                if (be.progress != 0) {
                    be.progress = 0;
                    be.data.set(0, 0);
                    be.setChanged();
                }
            }
        }
    }

    private ItemStack createResult(ItemStack ingot) {
        if (ingot.is(Items.IRON_INGOT)) return new ItemStack(ModItems.CLASSIC_CARD.get());
        if (ingot.is(Items.GOLD_INGOT)) return new ItemStack(ModItems.GOLD_CARD.get());
        return ItemStack.EMPTY;
    }

    private boolean canProcess(ItemStack ingot, ItemStack chip) {
        boolean hasIngot = ingot.is(Items.IRON_INGOT) || ingot.is(Items.GOLD_INGOT);
        boolean hasChip = chip.is(ModItems.CHIP.get());
        return hasIngot && hasChip;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "printer_controller", 4, state -> {
            Level lvl = this.getLevel();
            if (lvl != null) {
                BlockState blockState = lvl.getBlockState(this.getBlockPos());
                if (blockState.hasProperty(BankCardPrintingMachineBlock.PRINTING) && blockState.getValue(BankCardPrintingMachineBlock.PRINTING)) {
                    return state.setAndContinue(PRINT_ANIM);
                }
            }
            return PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object object) {
        return this.level != null ? this.level.getGameTime() : 0.0;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("title.coinsmod.menu.bank_card_printing_machine");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        assert this.level != null;
        return new BankCardPrintingMachineMenu(id, inventory, this, data, net.minecraft.world.inventory.ContainerLevelAccess.create(this.level, this.worldPosition));
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
        this.items.clearContent();
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
        tag.putInt("Progress", this.progress);
        tag.putInt("MaxProgress", this.maxProgress);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        ListTag itemsTag = tag.getList("Items", CompoundTag.TAG_COMPOUND);

        for (int i = 0; i < itemsTag.size(); i++) {
            CompoundTag slotTag = itemsTag.getCompound(i);
            int slot = slotTag.getInt("Slot");
            if (slot >= 0 && slot < this.items.getContainerSize()) {
                ItemStack stack = ItemStack.parse(registries, slotTag).orElse(ItemStack.EMPTY);
                this.items.setItem(slot, stack);
            }
        }

        this.progress = tag.getInt("Progress");
        this.maxProgress = tag.getInt("MaxProgress");
    }
}
