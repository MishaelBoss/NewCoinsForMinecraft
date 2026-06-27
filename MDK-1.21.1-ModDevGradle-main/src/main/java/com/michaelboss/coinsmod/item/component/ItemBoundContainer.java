package com.michaelboss.coinsmod.item.component;

import com.michaelboss.coinsmod.CoinsMod;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class ItemBoundContainer<C extends ContainerAccessor> extends SimpleContainer implements MenuProvider {
    private final ItemStack owner;
    private final Supplier<DataComponentType<C>> typeSupplier;
    private final Function<NonNullList<ItemStack>, C> containerConstructor;

    public ItemBoundContainer(int size, ItemStack owner, Supplier<DataComponentType<C>> typeSupplier, Function<NonNullList<ItemStack>, C> containerConstructor) {
        super(size);
        this.owner = owner;
        this.typeSupplier = typeSupplier;
        this.containerConstructor = containerConstructor;

        C c = owner.get(typeSupplier);
        if (c != null){
            NonNullList<ItemStack> content = c.getContent();
            if(content.size() != size)
                // У контента нет размера, пожалуйста укажите его
                CoinsMod.LOGGER.warn("The content has no size, please specify it");
            for (int i = 0; i < Math.min(content.size(), size); i++) {
                this.setItem(i, content.get(i));
            }
        }
    }

    @Override
    public void stopOpen(Player player) {
        owner.set(typeSupplier, containerConstructor.apply(this.getItems()));
        super.stopOpen(player);
    }

    @Override
    public Component getDisplayName() {
        return owner.getHoverName();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        // return ChestMenu.threeRows(i, inventory, this);
        return ChestMenu.oneRow(i, inventory);
    }
}
