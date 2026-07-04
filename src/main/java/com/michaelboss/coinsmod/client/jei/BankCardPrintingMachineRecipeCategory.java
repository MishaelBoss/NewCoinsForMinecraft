package com.michaelboss.coinsmod.client.jei;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.recipe.BankCardPrintingMachineJeiRecipe;
import com.michaelboss.coinsmod.registry.ModItems;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BankCardPrintingMachineRecipeCategory implements IRecipeCategory<BankCardPrintingMachineJeiRecipe> {
    public static final RecipeType<BankCardPrintingMachineJeiRecipe> TYPE =
            RecipeType.create(CoinsMod.MOD_ID, "bank_card_printing_machine", BankCardPrintingMachineJeiRecipe.class);

    private final IGuiHelper guiHelper;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawable staticArrowBackground;

    public BankCardPrintingMachineRecipeCategory(IGuiHelper helper) {
        ResourceLocation guiTexture = CoinsMod.id("textures/gui/bank_card_printing_machine.png");

        this.guiHelper = helper;

        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModItems.BANK_CARD_PRINTING_MACHINE_BLOCK_ITEM.get()));

        this.staticArrowBackground = helper.createDrawable(guiTexture, 80, 34, 22, 16);

        this.arrow = helper.drawableBuilder(guiTexture, 176, 0, 22, 15)
                .buildAnimated(40, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public @NotNull RecipeType<BankCardPrintingMachineJeiRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.bank_card_printing_machine");
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 60;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, BankCardPrintingMachineJeiRecipe bankCardPrintingMachineJeiRecipe, @NotNull IFocusGroup iFocusGroup) {
        IDrawable slotBackground = this.guiHelper.getSlotDrawable();
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 31, 21).addItemStack(bankCardPrintingMachineJeiRecipe.input1()).setBackground(slotBackground, -1, -1);
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 56, 21).addItemStack(bankCardPrintingMachineJeiRecipe.input2()).setBackground(slotBackground, -1, -1);
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 116, 21).addItemStack(bankCardPrintingMachineJeiRecipe.output()).setBackground(slotBackground, -1, -1);
    }

    @Override
    public void draw(BankCardPrintingMachineJeiRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.staticArrowBackground.draw(guiGraphics, 80, 21);
        this.arrow.draw(guiGraphics, 80, 21);
        Font font = Minecraft.getInstance().font;
        double seconds = recipe.time() / 20.0;
        String timeString = String.format("%.1f s", seconds);
        guiGraphics.drawString(font, timeString, 114, 42, 0xFF404040, false);
    }
}
