package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.network.SyncBuoyPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;

public class BuoyScreen extends AbstractContainerScreen<BuoyMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/buoy.png");

    private class AdjustingButton extends Button {
        public AdjustingButton(int x, int y, int delta, boolean doOffsetZ) {
            super(x, y, 12, 12, Component.literal(delta >= 0 ? "+" : "-"), btn -> {
                if (menu.buoy != null) {
                    int offsetX = menu.buoy.offsetX;
                    int offsetZ = menu.buoy.offsetZ;
                    if (doOffsetZ) {
                        offsetZ += delta;
                    } else {
                        offsetX += delta;
                    }
                    PacketDistributor.sendToServer(new SyncBuoyPacket(menu.buoy.getBlockPos(), (byte)offsetX, (byte)offsetZ));
                }
            }, DEFAULT_NARRATION);
        }
    }

    public BuoyScreen(BuoyMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 145;
        imageHeight = 109;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new AdjustingButton(leftPos + 8, topPos + 20, -1, false));
        addRenderableWidget(new AdjustingButton(leftPos + 20, topPos + 20, 1, false));
        addRenderableWidget(new AdjustingButton(leftPos + 8, topPos + 40, -1, true));
        addRenderableWidget(new AdjustingButton(leftPos + 20, topPos + 40, 1, true));
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (menu.buoy != null) {
            guiGraphics.drawString(font, Component.translatable("gui.cargoboats.buoy_offset.x", menu.buoy.offsetX), leftPos + 40, topPos + 22, 0xff000000, false);
            guiGraphics.drawString(font, Component.translatable("gui.cargoboats.buoy_offset.z", menu.buoy.offsetZ), leftPos + 40, topPos + 42, 0xff000000, false);
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // main background
        guiGraphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
}
