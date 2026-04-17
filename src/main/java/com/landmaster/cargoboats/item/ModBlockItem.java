package com.landmaster.cargoboats.item;

import com.landmaster.cargoboats.block.TooltipBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class ModBlockItem extends BlockItem {
    public ModBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, @Nonnull TooltipContext context, @Nonnull TooltipDisplay display, @Nonnull Consumer<Component> builder, @Nonnull TooltipFlag tooltipFlag) {
        if (getBlock() instanceof TooltipBlock tooltipBlock) {
            tooltipBlock.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        }
    }

    @Override
    public @Nonnull Component getName(@Nonnull ItemStack itemStack) {
        return getBlock().getName();
    }
}
