package com.michaelboss.coinsmod.client.jei;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.recipe.CoinageJeiRecipe;
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

public class CoinageRecipeCategory implements IRecipeCategory<CoinageJeiRecipe> {
    public static final RecipeType<CoinageJeiRecipe> TYPE =
            RecipeType.create(CoinsMod.MOD_ID, "coinage", CoinageJeiRecipe.class);

    private final IGuiHelper guiHelper;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawable staticArrowBackground;

    public CoinageRecipeCategory(IGuiHelper helper) {
        ResourceLocation guiTexture = CoinsMod.id("textures/gui/coinage.png");

        this.guiHelper = helper;

        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                new ItemStack(ModItems.COINAGE_BLOCK_ITEM.get()));

        this.staticArrowBackground = helper.createDrawable(guiTexture, 80, 21, 22, 15);

        this.arrow = helper.drawableBuilder(guiTexture, 176, 0, 22, 15)
                .buildAnimated(40, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public @NotNull RecipeType<CoinageJeiRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.coinage");
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
    public void draw(CoinageJeiRecipe coinageJeiRecipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.staticArrowBackground.draw(guiGraphics, 80, 21);
        this.arrow.draw(guiGraphics, 80, 21);
        Font font = Minecraft.getInstance().font;
        double seconds = coinageJeiRecipe.time() / 20.0;
        String timeString = String.format("%.1f s", seconds);
        guiGraphics.drawString(font, timeString, 114, 42, 0xFF404040, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, CoinageJeiRecipe coinageJeiRecipe, @NotNull IFocusGroup focuses) {
        IDrawable slotBackground = this.guiHelper.getSlotDrawable();
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 56, 21).addItemStack(coinageJeiRecipe.input()).setBackground(slotBackground, -1, -1);
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 116, 21).addItemStack(coinageJeiRecipe.output()).setBackground(slotBackground, -1, -1);
    }
}