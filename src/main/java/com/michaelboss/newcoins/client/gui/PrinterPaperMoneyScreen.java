package com.michaelboss.newcoins.client.gui;

import com.michaelboss.newcoins.NewCoins;
import com.michaelboss.newcoins.menu.PrinterPaperMoneyMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class PrinterPaperMoneyScreen extends AbstractContainerScreen<PrinterPaperMoneyMenu> {
    private static final ResourceLocation TEXTURE =
            (NewCoins.id("textures/gui/printer_paper_money.png"));

    private static final int PROGRESS_TEXTURE_X = 176;

    private static final int STAGE_ONE_TEXTURE_Y = 0;
    private static final int STAGE_TWO_TEXTURE_Y = 28;

    private static final int PROGRESS_BAR_ONE_WIDTH = 47;
    private static final int PROGRESS_BAR_ONE_HEIGHT = 28;

    private static final int PROGRESS_BAR_TWO_WIDTH = 41;
    private static final int PROGRESS_BAR_TWO_HEIGHT = 15;

    private static final int STAGE_ONE_SCREEN_X = 26;
    private static final int STAGE_ONE_SCREEN_Y = 30;

    private static final int STAGE_TWO_SCREEN_X = 101;
    private static final int STAGE_TWO_SCREEN_Y = 35;

    public PrinterPaperMoneyScreen(PrinterPaperMoneyMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 97;
        this.inventoryLabelX = 8;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        renderStageOneProgress(guiGraphics);
        renderStageTwoProgress(guiGraphics);
    }

    private void renderStageOneProgress(GuiGraphics guiGraphics) {
        int progress = this.menu.getData().get(0);
        int maxProgress = this.menu.getData().get(1);

        int width = getProgressWidthOne(progress, maxProgress);

        if (width <= 0) return;

        guiGraphics.blit(TEXTURE, this.leftPos + STAGE_ONE_SCREEN_X, this.topPos + STAGE_ONE_SCREEN_Y, PROGRESS_TEXTURE_X, STAGE_ONE_TEXTURE_Y, width, PROGRESS_BAR_ONE_HEIGHT);
    }

    private void renderStageTwoProgress(GuiGraphics guiGraphics) {
        int progress = this.menu.getData().get(2);
        int maxProgress = this.menu.getData().get(3);

        int width = getProgressWidthTwo(progress, maxProgress);

        if (width <= 0) return;

        guiGraphics.blit(TEXTURE, this.leftPos + STAGE_TWO_SCREEN_X, this.topPos + STAGE_TWO_SCREEN_Y, PROGRESS_TEXTURE_X, STAGE_TWO_TEXTURE_Y, width, PROGRESS_BAR_TWO_HEIGHT);
    }

    private int getProgressWidthOne(int progress, int maxProgress) {
        if (maxProgress <= 0) return 0;

        progress = Math.clamp(progress, 0, maxProgress);

        return progress * PROGRESS_BAR_ONE_WIDTH / maxProgress;
    }

    private int getProgressWidthTwo(int progress, int maxProgress) {
        if (maxProgress <= 0) return 0;

        progress = Math.clamp(progress, 0, maxProgress);

        return progress * PROGRESS_BAR_TWO_WIDTH / maxProgress;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
