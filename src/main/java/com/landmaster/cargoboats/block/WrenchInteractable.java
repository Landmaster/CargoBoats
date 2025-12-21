package com.landmaster.cargoboats.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

public interface WrenchInteractable {
    default InteractionResult disassemble(Level level, BlockPos pos, Player player) {
        var inventory = new PlayerMainInvWrapper(player.getInventory());
        var stack = new ItemStack(((Block) this).asItem());
        boolean canInsert = ItemHandlerHelper.insertItemStacked(inventory, stack, true).isEmpty();
        if (canInsert) {
            ItemHandlerHelper.insertItemStacked(inventory, stack, false);
            level.destroyBlock(pos, false);
        }
        return canInsert ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.FAIL;
    }
}
