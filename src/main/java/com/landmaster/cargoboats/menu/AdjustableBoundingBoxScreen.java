package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.network.AdjustBoundingBoxPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;

public class AdjustableBoundingBoxScreen extends AbstractContainerScreen<AdjustableBoundingBoxMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/adjustable_bounding_box.png");

    private class AdjustingButton extends Button {
        public AdjustingButton(int x, int y, int delta, Direction direction) {
            super(x, y, 12, 12, Component.literal(delta >= 0 ? "+" : "-"), btn -> {
                if (menu.detector != null) {
                    int minX = menu.detector.minX;
                    int minY = menu.detector.minY;
                    int minZ = menu.detector.minZ;
                    int maxX = menu.detector.maxX;
                    int maxY = menu.detector.maxY;
                    int maxZ = menu.detector.maxZ;

                    switch (direction) {
                        case DOWN:
                            minY += delta;
                            break;
                        case UP:
                            maxY += delta;
                            break;
                        case NORTH:
                            minZ += delta;
                            break;
                        case SOUTH:
                            maxZ += delta;
                            break;
                        case WEST:
                            minX += delta;
                            break;
                        case EAST:
                            maxX += delta;
                            break;
                    }

                    PacketDistributor.sendToServer(new AdjustBoundingBoxPacket(menu.detector.getBlockPos(),
                            (byte) minX, (byte) minY, (byte) minZ, (byte) maxX, (byte) maxY, (byte) maxZ));
                }
            }, DEFAULT_NARRATION);
        }
    }

    public AdjustableBoundingBoxScreen(AdjustableBoundingBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 145;
        imageHeight = 109;
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(new AdjustingButton(leftPos + 8, topPos + 34, -1, Direction.WEST));
        addRenderableWidget(new AdjustingButton(leftPos + 36, topPos + 34, 1, Direction.WEST));
        addRenderableWidget(new AdjustingButton(leftPos + 8, topPos + 66, -1, Direction.EAST));
        addRenderableWidget(new AdjustingButton(leftPos + 36, topPos + 66, 1, Direction.EAST));
        addRenderableWidget(new AdjustingButton(leftPos + 50, topPos + 34, -1, Direction.DOWN));
        addRenderableWidget(new AdjustingButton(leftPos + 78, topPos + 34, 1, Direction.DOWN));
        addRenderableWidget(new AdjustingButton(leftPos + 50, topPos + 66, -1, Direction.UP));
        addRenderableWidget(new AdjustingButton(leftPos + 78, topPos + 66, 1, Direction.UP));
        addRenderableWidget(new AdjustingButton(leftPos + 92, topPos + 34, -1, Direction.NORTH));
        addRenderableWidget(new AdjustingButton(leftPos + 120, topPos + 34, 1, Direction.NORTH));
        addRenderableWidget(new AdjustingButton(leftPos + 92, topPos + 66, -1, Direction.SOUTH));
        addRenderableWidget(new AdjustingButton(leftPos + 120, topPos + 66, 1, Direction.SOUTH));
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (menu.detector != null) {
            guiGraphics.drawString(font,
                    Component.translatable("gui.cargoboats.adjustable_bounding_box.min"), leftPos + 8, topPos + 22, 0xff000000, false);
            guiGraphics.drawString(font,
                    Component.translatable("gui.cargoboats.adjustable_bounding_box.max"), leftPos + 8, topPos + 52, 0xff000000, false);
            var text = Integer.toString(menu.detector.minX);
            guiGraphics.drawString(font, text, leftPos + 28 - font.width(text)/2, topPos + 36, 0xff000000, false);
            text = Integer.toString(menu.detector.maxX);
            guiGraphics.drawString(font, text, leftPos + 28 - font.width(text)/2, topPos + 68, 0xff000000, false);
            text = Integer.toString(menu.detector.minY);
            guiGraphics.drawString(font, text, leftPos + 70 - font.width(text)/2, topPos + 36, 0xff000000, false);
            text = Integer.toString(menu.detector.maxY);
            guiGraphics.drawString(font, text, leftPos + 70 - font.width(text)/2, topPos + 68, 0xff000000, false);
            text = Integer.toString(menu.detector.minZ);
            guiGraphics.drawString(font, text, leftPos + 112 - font.width(text)/2, topPos + 36, 0xff000000, false);
            text = Integer.toString(menu.detector.maxZ);
            guiGraphics.drawString(font, text, leftPos + 112 - font.width(text)/2, topPos + 68, 0xff000000, false);
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
