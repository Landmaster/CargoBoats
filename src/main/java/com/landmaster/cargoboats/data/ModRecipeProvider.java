package com.landmaster.cargoboats.data;

import com.landmaster.cargoboats.CargoBoats;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.BlockTagIngredient;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.MISC, CargoBoats.BUOY)
                .define('c', new Ingredient(new BlockTagIngredient(BlockTags.CAMPFIRES)))
                .define('i', Tags.Items.INGOTS_IRON)
                .pattern("c")
                .pattern("i")
                .pattern("i")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(output);

        shaped(RecipeCategory.MISC, CargoBoats.CAPACITY_UPGRADE)
                .define('l', Tags.Items.GEMS_LAPIS)
                .define('g', Tags.Items.INGOTS_GOLD)
                .define('b', Tags.Items.BARRELS_WOODEN)
                .pattern("glg")
                .pattern("lbl")
                .pattern("glg")
                .unlockedBy("has_barrel", has(Tags.Items.BARRELS_WOODEN))
                .save(output);

        // TODO add patchouli recipe

        shaped(RecipeCategory.MISC, CargoBoats.DOCK)
                .define('i', Tags.Items.INGOTS_IRON)
                .define('w', ItemTags.PLANKS)
                .define('b', ItemTags.BOATS)
                .pattern("iwi")
                .pattern("wbw")
                .pattern("iwi")
                .unlockedBy("has_boat", has(ItemTags.BOATS))
                .save(output);

        shaped(RecipeCategory.MISC, CargoBoats.FISHING_UPGRADE)
                .define('s', Items.PRISMARINE_SHARD)
                .define('p', Tags.Items.GEMS_PRISMARINE)
                .define('f', Items.FISHING_ROD)
                .pattern("psp")
                .pattern("sfs")
                .pattern("psp")
                .unlockedBy("has_fishing_rod", has(Items.FISHING_ROD))
                .save(output);

        shaped(RecipeCategory.MISC, CargoBoats.FLUID_MOTORBOAT_ITEM)
                .define('c', Tags.Items.INGOTS_COPPER)
                .define('p', Items.PISTON)
                .define('b', Items.BUCKET)
                .define('B', ItemTags.BOATS)
                .pattern(" b ")
                .pattern("pBc")
                .pattern("ccc")
                .unlockedBy("has_boat", has(ItemTags.BOATS))
                .save(output);

        shaped(RecipeCategory.MISC, CargoBoats.ICEBREAKER_UPGRADE)
                .define('i', Items.BLUE_ICE)
                .define('t', Items.TORCH)
                .define('p', Items.IRON_PICKAXE)
                .pattern("tit")
                .pattern("ipi")
                .pattern("tit")
                .unlockedBy("has_iron_pickaxe", has(Items.IRON_PICKAXE))
                .save(output);

        shaped(RecipeCategory.MISC, CargoBoats.LAVA_UPGRADE)
                .define('c', Tags.Items.OBSIDIANS_CRYING)
                .define('r', Tags.Items.RODS_BLAZE)
                .define('m', Items.MAGMA_CREAM)
                .define('w', Items.WARPED_FUNGUS_ON_A_STICK)
                .pattern("rcm")
                .pattern("cwc")
                .pattern("mcr")
                .unlockedBy("has_warped_fungus_on_stick", has(Items.WARPED_FUNGUS_ON_A_STICK))
                .save(output);

        shaped(RecipeCategory.MISC, CargoBoats.MOTORBOAT_ITEM)
                .define('c', Tags.Items.INGOTS_COPPER)
                .define('p', Items.PISTON)
                .define('b', Tags.Items.BARRELS_WOODEN)
                .define('B', ItemTags.BOATS)
                .pattern(" b ")
                .pattern("pBc")
                .pattern("ccc")
                .unlockedBy("has_boat", has(ItemTags.BOATS))
                .save(output);

        shaped(RecipeCategory.MISC, CargoBoats.MOTORBOAT_DETECTOR)
                .define('e', Tags.Items.ENDER_PEARLS)
                .define('t', Items.REDSTONE_TORCH)
                .define('b', ItemTags.BOATS)
                .define('s', Items.SCULK_SENSOR)
                .pattern(" e ")
                .pattern("tst")
                .pattern(" b ")
                .unlockedBy("has_sculk_sensor", has(Items.SCULK_SENSOR))
                .save(output);

        shaped(RecipeCategory.MISC, CargoBoats.MOTORBOAT_PROGRAMMER)
                .define('e', Tags.Items.GEMS_EMERALD)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('p', Items.PAPER)
                .pattern(" e ")
                .pattern("rpr")
                .pattern(" r ")
                .unlockedBy("has_paper", has(Items.PAPER))
                .save(output);

        shapeless(RecipeCategory.MISC, CargoBoats.MOTORBOAT_PROGRAMMER)
                .requires(CargoBoats.MOTORBOAT_PROGRAMMER)
                .unlockedBy("has_motorboat_programmer", has(CargoBoats.MOTORBOAT_PROGRAMMER))
                .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(CargoBoats.MODID, "motorboat_programmer_reset")));

        shaped(RecipeCategory.MISC, CargoBoats.MOTORBOAT_TRACKER)
                .define('y', Items.YELLOW_DYE)
                .define('c', Tags.Items.INGOTS_COPPER)
                .define('p', Items.COMPASS)
                .pattern(" c ")
                .pattern("ypy")
                .pattern(" c ")
                .unlockedBy("has_compass", has(Items.COMPASS))
                .save(output);

        shaped(RecipeCategory.MISC, CargoBoats.SPEED_UPGRADE)
                .define('l', Tags.Items.GEMS_LAPIS)
                .define('r', Items.RABBIT_HIDE)
                .define('s', Tags.Items.DUSTS_REDSTONE)
                .pattern("rlr")
                .pattern("lsl")
                .pattern("rlr")
                .unlockedBy("has_rabbit_hide", has(Items.RABBIT_HIDE))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        // Get the parameters from the `GatherDataEvent`s.
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        @Nonnull
        protected RecipeProvider createRecipeProvider(@Nonnull HolderLookup.Provider provider, @Nonnull RecipeOutput output) {
            return new ModRecipeProvider(provider, output);
        }

        @Override
        @Nonnull
        public String getName() {
            return "Cargo Boats Recipes";
        }
    }
}
