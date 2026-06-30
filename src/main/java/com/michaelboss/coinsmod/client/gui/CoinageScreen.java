package com.michaelboss.coinsmod.client.gui;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.menu.CoinageMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class CoinageScreen extends AbstractContainerScreen<CoinageMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CoinsMod.MOD_ID, "textures/gui/coinage.png");

    public CoinageScreen(CoinageMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 121;
        this.inventoryLabelX = 8;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int progress = menu.getData().get(0);
        int maxProgress = menu.getData().get(1);
        int arrowWidth = maxProgress == 0 ? 0 : 24 * progress / maxProgress;

        guiGraphics.blit(TEXTURE, leftPos + 79, topPos + 21, 176, 0, arrowWidth, 17);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
