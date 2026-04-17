package com.landmaster.cargoboats.data;

import com.landmaster.cargoboats.CargoBoats;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, CargoBoats.MODID);
    }

    @Override
    protected void registerModels(@Nonnull BlockModelGenerators blockModels, @Nonnull ItemModelGenerators itemModels) {
        blockModels.createNonTemplateHorizontalBlock(CargoBoats.DOCK.get());
        blockModels.createNonTemplateModelBlock(CargoBoats.BUOY.get());

        itemModels.generateFlatItem(CargoBoats.MOTORBOAT_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(CargoBoats.FLUID_MOTORBOAT_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(CargoBoats.MOTORBOAT_PROGRAMMER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(CargoBoats.MOTORBOAT_TRACKER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(CargoBoats.CAPACITY_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(CargoBoats.FISHING_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(CargoBoats.ICEBREAKER_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(CargoBoats.LAVA_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(CargoBoats.SPEED_UPGRADE.get(), ModelTemplates.FLAT_ITEM);
    }

    @Nonnull
    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return super.getKnownBlocks().filter(block -> block.value() != CargoBoats.MOTORBOAT_DETECTOR.get());
    }
}
