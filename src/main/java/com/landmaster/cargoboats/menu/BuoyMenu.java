package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.entity.BuoyBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class BuoyMenu extends AbstractContainerMenu {
    public BuoyBlockEntity buoy;

    private static class ClientRetrieveBuoy {
        public static BuoyBlockEntity fromPos(BlockPos pos) {
            return Minecraft.getInstance().level.getBlockEntity(pos, CargoBoats.BUOY_TE.get()).orElse(null);
        }
    }

    public BuoyMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extra) {
        super(CargoBoats.BUOY_MENU.get(), containerId);
        buoy = ClientRetrieveBuoy.fromPos(extra.readBlockPos());
    }

    public BuoyMenu(int containerId, BuoyBlockEntity buoy) {
        super(CargoBoats.BUOY_MENU.get(), containerId);
        this.buoy = buoy;
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return buoy != null && (!buoy.isRemoved() && player.canInteractWithBlock(buoy.getBlockPos(), 4.0));
    }
}
