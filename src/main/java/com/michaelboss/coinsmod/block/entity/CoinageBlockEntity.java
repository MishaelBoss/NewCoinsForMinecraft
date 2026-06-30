package com.michaelboss.coinsmod.block.entity;

import com.michaelboss.coinsmod.block.CoinageBlock;
import com.michaelboss.coinsmod.init.ModBlockEntities;
import com.michaelboss.coinsmod.item.ModItems;
import com.michaelboss.coinsmod.menu.CoinageMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.inventory.SimpleContainerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CoinageBlockEntity extends BlockEntity implements MenuProvider, Container, GeoAnimatable {
    private final SimpleContainer items = new SimpleContainer(2);
    private final ContainerData data = new SimpleContainerData(2);

    private int progress = 0;
    private int maxProgress = 40;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation HAMMER_ANIM = RawAnimation.begin().thenLoop("hammer");

    public CoinageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COINAGE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void setChanged() {
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
        super.setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CoinageBlockEntity be) {
        if (!level.isClientSide) {
            ItemStack input = be.getItem(0);
            ItemStack output = be.getItem(1);
            boolean isProcessing = be.canProcess(input) && !input.isEmpty();

            if (state.getValue(CoinageBlock.HAMMERING) != isProcessing) {
                level.setBlock(pos, state.setValue(CoinageBlock.HAMMERING, isProcessing), 3);
                be.setChanged();
            }

            if (isProcessing) {
                be.maxProgress = be.getProcessingTime(input);
                be.progress++;

                be.data.set(0, be.progress);
                be.data.set(1, be.maxProgress);

                if (be.progress >= be.maxProgress) {
                    ItemStack result = be.createResult(input);
                    if(!result.isEmpty()){
                        if (output.isEmpty()) {
                            be.setItem(1, result);
                        } else if (ItemStack.isSameItemSameComponents(output, result) && output.getCount() + result.getCount() <= output.getMaxStackSize()){
                            output.grow(result.getCount());
                        } else {
                            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), result);
                        }
                    }
                    input.shrink(1);
                    be.setItem(0, input);

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

    private ItemStack createResult(ItemStack input) {
        if (input.isEmpty()) return ItemStack.EMPTY;
        Item outputItem = null;

        if (input.is(Items.COPPER_INGOT)) outputItem = ModItems.COPPER_COIN.get();
        else if (input.is(Items.IRON_INGOT)) outputItem = ModItems.IRON_COIN.get();
        else if (input.is(Items.GOLD_INGOT)) outputItem = ModItems.GOLD_COIN.get();

        return outputItem != null ? new ItemStack(outputItem, 9) : ItemStack.EMPTY;
    }

    private boolean canProcess(ItemStack stack) {
        return stack.is(Items.COPPER_INGOT) || stack.is(Items.IRON_INGOT) || stack.is(Items.GOLD_INGOT);
    }

    private int getProcessingTime(ItemStack stack) {
        if (stack.is(Items.COPPER_INGOT)) return 40;
        if (stack.is(Items.IRON_INGOT)) return 60;
        if (stack.is(Items.GOLD_INGOT)) return 30;
        return 40;
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

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("title.coinsmod.menu.coinage");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new CoinageMenu(id, inventory, this);
    }

    public ContainerData getContainerData() {
        return this.data;
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
    public void setItem(int slot, @NotNull ItemStack stack) {
        this.items.setItem(slot, stack);
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
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main_controller", 4, state -> {
            Level level = this.getLevel();
            if (level != null) {
                BlockState blockState = level.getBlockState(this.getBlockPos());
                if (blockState.hasProperty(CoinageBlock.HAMMERING) && blockState.getValue(CoinageBlock.HAMMERING)) {
                    return state.setAndContinue(HAMMER_ANIM);
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
}