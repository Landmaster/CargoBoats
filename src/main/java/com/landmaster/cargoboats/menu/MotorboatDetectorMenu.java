package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.entity.BuoyBlockEntity;
import com.landmaster.cargoboats.block.entity.MotorboatDetectorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class MotorboatDetectorMenu extends AbstractContainerMenu {
    public MotorboatDetectorBlockEntity detector;

    private static class ClientRetrieveDetector {
        public static MotorboatDetectorBlockEntity fromPos(BlockPos pos) {
            return Minecraft.getInstance().level.getBlockEntity(pos, CargoBoats.MOTORBOAT_DETECTOR_TE.get()).orElse(null);
        }
    }

    public MotorboatDetectorMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extra) {
        super(CargoBoats.MOTORBOAT_DETECTOR_MENU.get(), containerId);
        detector = ClientRetrieveDetector.fromPos(extra.readBlockPos());
    }

    public MotorboatDetectorMenu(int containerId, MotorboatDetectorBlockEntity detector) {
        super(CargoBoats.MOTORBOAT_DETECTOR_MENU.get(), containerId);
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
