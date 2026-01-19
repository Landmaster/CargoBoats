package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.entity.AdjustableBoundingBoxBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class AdjustableBoundingBoxMenu extends AbstractContainerMenu {
    public AdjustableBoundingBoxBlockEntity detector;

    private static class ClientRetrieveDetector {
        public static AdjustableBoundingBoxBlockEntity fromPos(BlockPos pos) {
            var res = Minecraft.getInstance().level.getBlockEntity(pos);
            return res instanceof AdjustableBoundingBoxBlockEntity detector ? detector : null;
        }
    }

    public AdjustableBoundingBoxMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extra) {
        super(CargoBoats.ADJUSTABLE_BOUNDING_BOX_MENU.get(), containerId);
        detector = ClientRetrieveDetector.fromPos(extra.readBlockPos());
    }

    public AdjustableBoundingBoxMenu(int containerId, AdjustableBoundingBoxBlockEntity detector) {
        super(CargoBoats.ADJUSTABLE_BOUNDING_BOX_MENU.get(), containerId);
        this.detector = detector;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return detector != null && (!detector.isRemoved() && player.canInteractWithBlock(detector.getBlockPos(), 4.0));
    }
}
