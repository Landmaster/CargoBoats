package com.landmaster.cargoboats.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public interface WrenchInteractable {
    default InteractionResult disassemble(Level level, BlockPos pos, Player player) {
        var inventory = PlayerInventoryWrapper.of(player).getMainSlots();
        try (var txn = Transaction.openRoot()) {
            if (inventory.insert(ItemResource.of((Block) this), 1, txn) >= 1) {
                txn.commit();
                level.destroyBlock(pos, false);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    default InteractionResult rotate(Level level, BlockPos pos, Player player) {
        var blockState = level.getBlockState(pos);
        var newBlockState = blockState.rotate(level, pos, Rotation.CLOCKWISE_90);
        if (newBlockState != blockState) {
            level.setBlockAndUpdate(pos, newBlockState);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
