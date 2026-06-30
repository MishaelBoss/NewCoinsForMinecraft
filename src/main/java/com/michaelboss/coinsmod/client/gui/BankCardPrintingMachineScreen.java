package com.michaelboss.coinsmod.client.gui;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.menu.BankCardPrintingMachineMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class BankCardPrintingMachineScreen extends AbstractContainerScreen<BankCardPrintingMachineMenu> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(CoinsMod.MOD_ID, "textures/gui/bank_card_printing_machine.png");

    public BankCardPrintingMachineScreen(BankCardPrintingMachineMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 108;
        this.inventoryLabelX = 8;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int progress = menu.getData().get(0);
        int maxProgress = menu.getData().get(1);
        int arrowWidth = maxProgress == 0 ? 0 : 24 * progress / maxProgress;

        guiGraphics.blit(TEXTURE, leftPos + 79, topPos + 34, 176, 0, arrowWidth, 17);

        assert this.minecraft != null;

        String ownerName = this.minecraft.player != null ? this.minecraft.player.getScoreboardName() : "Unknown";
        Component text = Component.translatable("title.coinsmod.menu.coinage.ownerName").append(ownerName);
        guiGraphics.drawString(this.font, text, leftPos + 8, topPos + 18, 0x555555, false);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
