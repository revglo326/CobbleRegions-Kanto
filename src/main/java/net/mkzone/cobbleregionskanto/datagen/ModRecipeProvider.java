package net.mkzone.cobbleregionskanto.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.mkzone.cobbleregionskanto.block.ModBlocks;
import net.mkzone.cobbleregionskanto.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TELEPORTER.get())
                .pattern("SOS")
                .pattern("OEO")
                .pattern("SOS")
                .define('S', Blocks.STONE.asItem())
                .define('O', Blocks.ORANGE_CONCRETE.asItem())
                .define('E', Items.ENDER_PEARL)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(recipeOutput)
                ;

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.LINKING_TOOL.get())
                .pattern("I I")
                .pattern(" E ")
                .pattern(" N ")
                .define('I', Items.IRON_INGOT)
                .define('E', Items.ENDER_PEARL)
                .define('N', Items.NETHERITE_INGOT)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .unlockedBy("has_netherite", has(Items.NETHERITE_INGOT))
                .save(recipeOutput)
        ;
    }
}
