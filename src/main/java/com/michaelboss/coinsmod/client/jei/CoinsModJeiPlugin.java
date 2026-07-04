package com.michaelboss.coinsmod.client.jei;

import com.michaelboss.coinsmod.CoinsMod;
import com.michaelboss.coinsmod.client.gui.BankCardPrintingMachineScreen;
import com.michaelboss.coinsmod.client.gui.CoinageScreen;
import com.michaelboss.coinsmod.recipe.BankCardPrintingMachineJeiRecipe;
import com.michaelboss.coinsmod.recipe.CoinageJeiRecipe;
import com.michaelboss.coinsmod.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class CoinsModJeiPlugin implements IModPlugin {
    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CoinsMod.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new CoinageRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new BankCardPrintingMachineRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<CoinageJeiRecipe> coinageJeiRecipeList = List.of(
                new CoinageJeiRecipe(new ItemStack(Items.COPPER_INGOT), new ItemStack(ModItems.COPPER_COIN.get(), 9), 40),
                new CoinageJeiRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.IRON_COIN.get(), 9), 60),
                new CoinageJeiRecipe(new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.GOLD_COIN.get(), 9), 30)
        );

        List<BankCardPrintingMachineJeiRecipe> bankCardPrintingMachineJeiRecipeList = List.of(
                new BankCardPrintingMachineJeiRecipe(new ItemStack(Items.IRON_INGOT), new ItemStack(ModItems.CHIP.get(), 1), new ItemStack(ModItems.CLASSIC_CARD.get(), 1), 60),
                new BankCardPrintingMachineJeiRecipe(new ItemStack(Items.GOLD_INGOT), new ItemStack(ModItems.CHIP.get(), 1), new ItemStack(ModItems.GOLD_CARD.get(), 1), 60)
        );

        registration.addRecipes(CoinageRecipeCategory.TYPE, coinageJeiRecipeList);
        registration.addRecipes(BankCardPrintingMachineRecipeCategory.TYPE, bankCardPrintingMachineJeiRecipeList);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(ModItems.COINAGE_BLOCK_ITEM.get()),
                CoinageRecipeCategory.TYPE);

        registration.addRecipeCatalyst(
                new ItemStack(ModItems.BANK_CARD_PRINTING_MACHINE_BLOCK_ITEM.get()),
                BankCardPrintingMachineRecipeCategory.TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(
                CoinageScreen.class,
                79, 21, 24, 17,
                CoinageRecipeCategory.TYPE);

        registration.addRecipeClickArea(
                BankCardPrintingMachineScreen.class,
                79,34,24,17,
                BankCardPrintingMachineRecipeCategory.TYPE);
    }
}