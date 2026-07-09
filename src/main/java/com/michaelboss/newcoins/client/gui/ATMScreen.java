package com.michaelboss.newcoins.client.gui;

import com.michaelboss.newcoins.NewCoins;
import com.michaelboss.newcoins.item.CardItem;
import com.michaelboss.newcoins.item.CurrencyItem;
import com.michaelboss.newcoins.menu.ATMMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ATMScreen extends AbstractContainerScreen<ATMMenu> {
    private static final ResourceLocation TEXTURE =
            (NewCoins.id("textures/gui/atm.png"));

    private boolean isFullyBooted = false;

    private Button depositBtn;
    private Button withdrawBtn;
    private Button toManeBtn;
    private Button toDepositBtn;
    private Button toWithdrawBtn;

    private EditBox amountInput;

    private enum Page {
        START_SCREEN,
        MAIN,
        DEPOSIT,
        WITHDRAW
    }

    private Page currentPage = Page.START_SCREEN;

    public ATMScreen(ATMMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 178;
        this.inventoryLabelY = this.imageHeight - 95;
        this.inventoryLabelX = 8;
        this.titleLabelX = 88;
    }

    @Override
    protected void init() {
        super.init();

        this.depositBtn = Button.builder(Component.literal(""), press -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.michaelboss.newcoins.network.DepositC2SPacket());
            }
        }).bounds(this.leftPos + 41, this.topPos + 50, 54, 18).build();

        this.withdrawBtn = Button.builder(Component.literal(""), press -> {
            String enteredText = this.amountInput.getValue();

            if(this.minecraft != null && this.minecraft.player != null && !enteredText.isEmpty()) {
                try {
                    int amountToSend = Integer.parseInt(enteredText);
                    net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                            new com.michaelboss.newcoins.network.WithdrawC2SPacket(amountToSend)
                    );
                    this.amountInput.setValue("");
                } catch (NumberFormatException e) {
                    NewCoins.LOGGER.warn(e.getMessage());
                }
            }
        }).bounds(this.leftPos + 41, this.topPos + 50, 54, 18).build();

        this.toManeBtn = Button.builder(Component.literal(""), press -> {
            if(this.minecraft != null && this.minecraft.player != null && this.isFullyBooted) {
                this.currentPage = Page.MAIN;
            }
        }).bounds(this.leftPos + 12, this.topPos + 9, 10, 10).build();

        this.toDepositBtn = Button.builder(Component.literal(""), press -> {
            if(this.minecraft != null && this.minecraft.player != null && this.isFullyBooted) {
                this.currentPage = Page.DEPOSIT;
            }
        }).bounds(this.leftPos + 23, this.topPos + 9, 10, 10).build();

        this.toWithdrawBtn = Button.builder(Component.literal(""), press -> {
            if(this.minecraft != null && this.minecraft.player != null && this.isFullyBooted) {
                this.currentPage = Page.WITHDRAW;
            }
        }).bounds(this.leftPos + 34, this.topPos + 9, 10, 10).build();

        this.amountInput = new EditBox(this.font, this.leftPos + 38, this.topPos + 33, 60, 12, Component.translatable("text.newcoins.atm_screen.amount_editBox"));

        this.amountInput.setMaxLength(5);
        this.amountInput.setTextColor(0xFF00F5D4);
        this.amountInput.setBordered(true);
        this.amountInput.setFilter(text -> text.matches("^\\d*$"));

        this.addRenderableWidget(this.depositBtn);
        this.addRenderableWidget(this.withdrawBtn);
        this.addRenderableWidget(this.toManeBtn);
        this.addRenderableWidget(this.toDepositBtn);
        this.addRenderableWidget(this.toWithdrawBtn);
        this.addRenderableWidget(this.amountInput);

        setWidgetVisibility();
    }

    private void setWidgetVisibility() {
        if (this.depositBtn != null) this.depositBtn.visible = this.isFullyBooted && this.currentPage == Page.DEPOSIT;
        if (this.withdrawBtn != null) this.withdrawBtn.visible = this.isFullyBooted && this.currentPage == Page.WITHDRAW;
        if (this.amountInput != null) {
            boolean showWithdrawInput = this.isFullyBooted && this.currentPage == Page.WITHDRAW;
            this.amountInput.visible = showWithdrawInput;
            this.amountInput.setEditable(showWithdrawInput);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int bootProgress = this.menu.getData().get(0);
        int maxBootProgress = this.menu.getData().get(1);
        int cardStatus = this.menu.getData().get(2);

        if (cardStatus == 0) {
            guiGraphics.blit(TEXTURE, leftPos + 17, topPos + 33, 0, 178, 105, 18);
            Component insertCardText = Component.translatable("text.newcoins.atm_screen.insert_card");
            int textWidth = this.font.width(insertCardText);

            int textX = leftPos + 32 + (76 / 2) - (textWidth / 2);
            int textY = topPos + 35 + (16 / 2) - (this.font.lineHeight / 2);
            guiGraphics.drawString(this.font, insertCardText, textX, textY, 0xFF00B4D8, false);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);

            int smallTextY = (int)((topPos + 62) / 0.5F);
            int osTextX = (int)((leftPos + 14) / 0.5F);

            Component osText = Component.literal("CoinsMod OS v" + NewCoins.getVersion());
            guiGraphics.drawString(this.font, osText, osTextX, smallTextY, 0xFF334155, false);

            guiGraphics.pose().popPose();
        } else if (cardStatus == 1) {
            int barWidth = 80 * bootProgress / maxBootProgress;
            barWidth = Math.min(barWidth, 80);

            if (barWidth > 0) {
                guiGraphics.blit(TEXTURE,
                        leftPos + 17, topPos + 33,
                        176, 72,
                        barWidth, 8
                );
            }

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);
            int loadTextY = (int)((topPos + 44) / 0.5F);
            int loadTextX = (int)((leftPos + 15) / 0.5F);
            guiGraphics.drawString(this.font, Component.translatable("text.newcoins.atm_screen.load"), loadTextX, loadTextY, 0xFF00B4D8, false);
            guiGraphics.pose().popPose();
        } else if (cardStatus == 3) {
            Component errorText = Component.translatable("text.newcoins.atm_screen.error_dio");
            int textWidth = this.font.width(errorText);
            int textX = leftPos + 12 + (116 / 2) - (textWidth / 2);

            guiGraphics.drawString(this.font, errorText, textX, topPos + 26, 0xFFFF5400, false);

            Component subError = Component.translatable("text.newcoins.atm_screen.chip_not_coded");
            int subWidth = this.font.width(subError);
            int subX = leftPos + 12 + (116 / 2) - (subWidth / 2);
            guiGraphics.drawString(this.font, subError, subX, topPos + 38, 0xFF808080, false);
        }
    }

    @SuppressWarnings("java:S1186")
    private void renderStartScreen() {
    }

    private void renderMainPage(GuiGraphics guiGraphics) {
        ItemStack cardStack = this.menu.getSlot(6).getItem();

        if (this.isFullyBooted && cardStack.has(CardItem.getOwnerComponent())) {
            String ownerName = CardItem.getOwnerName(cardStack);
            int deposit = Math.round(CardItem.getDeposit(cardStack) / 10.0F);

            Component welcomeText = Component.translatable("text.newcoins.atm_screen.welcome_client", ownerName);
            guiGraphics.drawString(this.font, welcomeText, leftPos + 18, topPos + 25, 0xFFFFFFFF, false);

            Component balanceText = Component.translatable("text.newcoins.atm_screen.balance", deposit);
            guiGraphics.drawString(this.font, balanceText, leftPos + 18, topPos + 35, 0xFF00F5D4, false);
        }
    }

    private void renderDepositPage(GuiGraphics guiGraphics) {
        ItemStack cardStack = this.menu.getSlot(6).getItem();

        if (this.isFullyBooted) {
            int rawPendingCoins = 0;
            for (int slotIdx = 0; slotIdx <= 5; slotIdx++) {
                ItemStack slotStack = this.menu.getSlot(slotIdx).getItem();
                if (!slotStack.isEmpty() && slotStack.getItem() instanceof CurrencyItem currencyItem) {
                    rawPendingCoins += (currencyItem.getInternalValue() * slotStack.getCount());
                }
            }
            int currentCardDepositRaw = CardItem.getDeposit(cardStack);
            int totalCoinsRaw  = rawPendingCoins + currentCardDepositRaw;

            float pendingCoinsDisplay = rawPendingCoins / 10.0F;
            float togetherCoinsDisplay = totalCoinsRaw / 10.0F;

            Component enrollmentText = Component.translatable("text.newcoins.atm_screen.enrollment", pendingCoinsDisplay);
            guiGraphics.drawString(this.font, enrollmentText, leftPos + 18, topPos + 25, 0xFF00F5D4, false);

            Component togetherText = Component.translatable("text.newcoins.atm_screen.together", togetherCoinsDisplay);
            guiGraphics.drawString(this.font, togetherText, leftPos + 18, topPos + 35, 0xFF00F5D4, false);
        }
    }

    private void renderWithdrawPage(GuiGraphics guiGraphics) {
        ItemStack cardStack = this.menu.getSlot(6).getItem();

        if (this.isFullyBooted) {
            int deposit = Math.round(CardItem.getDeposit(cardStack) / 10.0F);

            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.5F, 0.5F, 1.0F);

            int smallTextY = (int)((topPos + 25) / 0.5F);
            int balanceTextX = (int)((leftPos + 50) / 0.5F);

            Component balanceText = Component.translatable("text.newcoins.atm_screen.balance", deposit);
            guiGraphics.drawString(this.font, balanceText, balanceTextX, smallTextY, 0xFF00F5D4, false);

            guiGraphics.pose().popPose();
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        int bootProgress = this.menu.getData().get(0);
        int maxBootProgress = this.menu.getData().get(1);
        int cardStatus = this.menu.getData().get(2);
        this.isFullyBooted = bootProgress >= maxBootProgress && maxBootProgress > 0 && cardStatus == 2;

        if (cardStatus == 0 || cardStatus == 3) {
            this.currentPage = Page.START_SCREEN;
        } else if (this.isFullyBooted && this.currentPage == Page.START_SCREEN) {
            this.currentPage = Page.MAIN;
        }

        setWidgetVisibility();

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (this.isFullyBooted) {
            switch (this.currentPage) {
                case START_SCREEN -> renderStartScreen();
                case MAIN -> renderMainPage(guiGraphics);
                case DEPOSIT -> renderDepositPage(guiGraphics);
                case WITHDRAW -> renderWithdrawPage(guiGraphics);
            }
        }

        renderTabIcons(guiGraphics);
        renderActionButtons(guiGraphics);

        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderTabIcons (GuiGraphics guiGraphics) {
        if(this.toManeBtn != null && this.toDepositBtn != null && this.toWithdrawBtn != null) {
            if (this.toManeBtn.isHoveredOrFocused() && this.isFullyBooted) {
                guiGraphics.blit(TEXTURE, leftPos + 12, topPos + 9, 176, 90, 10, 10);
            } else {
                guiGraphics.blit(TEXTURE, leftPos + 12, topPos + 9, 176, 80, 10, 10);
            }

            if (this.toDepositBtn.isHoveredOrFocused() && this.isFullyBooted) {
                guiGraphics.blit(TEXTURE, leftPos + 23, topPos + 9, 186, 90, 10, 10);
            } else {
                guiGraphics.blit(TEXTURE, leftPos + 23, topPos + 9, 186, 80, 10, 10);
            }

            if (this.toWithdrawBtn.isHoveredOrFocused() && this.isFullyBooted) {
                guiGraphics.blit(TEXTURE, leftPos + 34, topPos + 9, 196, 90, 10, 10);
            } else {
                guiGraphics.blit(TEXTURE, leftPos + 34, topPos + 9, 196, 80, 10, 10);
            }
        }
    }

    private void renderActionButtons(GuiGraphics guiGraphics) {
        if (!this.isFullyBooted || this.depositBtn == null || this.withdrawBtn == null) {
            return;
        }

        if (this.currentPage == Page.DEPOSIT) {
            if (this.depositBtn.isHoveredOrFocused()) {
                guiGraphics.blit(TEXTURE, leftPos + 40, topPos + 50, 176, 18, 56, 18);
            } else {
                guiGraphics.blit(TEXTURE, leftPos + 40, topPos + 50, 176, 0, 56, 18);
            }
        }

        if (this.currentPage == Page.WITHDRAW) {
            if (this.withdrawBtn.isHoveredOrFocused()) {
                guiGraphics.blit(TEXTURE, leftPos + 40, topPos + 50, 176, 54, 56, 18);
            } else {
                guiGraphics.blit(TEXTURE, leftPos + 40, topPos + 50, 176, 36, 56, 18);
            }
        }

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(0.6F, 0.6F, 1.0F);

        int btnY = (int)((topPos + 57) / 0.6F);
        int smallOffset = 6;

        if (this.currentPage == Page.DEPOSIT) {
            Component text = Component.translatable("text.newcoins.atm_screen.deposit_btn");
            int textWidth = this.font.width(text);

            int buttonCenter = this.depositBtn.getX() + this.depositBtn.getWidth() / 2;
            int txtDepositX = (int)((buttonCenter - (float) textWidth / 2 + smallOffset) / 0.6F);

            guiGraphics.drawString(this.font, text, txtDepositX, btnY, 0xFFFFFFFF, false);
        }
        if (this.currentPage == Page.WITHDRAW) {
            Component text = Component.translatable("text.newcoins.atm_screen.withdraw_btn");
            int textWidth = this.font.width(text);

            int buttonCenter = this.withdrawBtn.getX() + this.withdrawBtn.getWidth() / 2;
            int txtWithdrawX = (int)((buttonCenter - (float) textWidth / 2 + smallOffset) / 0.6F);

            guiGraphics.drawString(this.font, text, txtWithdrawX, btnY, 0xFFFFFFFF, false);
        }


        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x00F5D4, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0xFFAA00, false);
    }
}
