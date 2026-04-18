package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.network.AdjustBoundingBoxPacket;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nonnull;

public class AdjustableBoundingBoxScreen extends AbstractContainerScreen<AdjustableBoundingBoxMenu> {
    private static final Identifier CONTAINER_BACKGROUND = Identifier.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/adjustable_bounding_box.png");

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

                    ClientPacketDistributor.sendToServer(new AdjustBoundingBoxPacket(menu.detector.getBlockPos(),
                            (byte) minX, (byte) minY, (byte) minZ, (byte) maxX, (byte) maxY, (byte) maxZ));
                }
            }, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(@Nonnull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
            extractDefaultSprite(guiGraphicsExtractor);
            extractDefaultLabel(guiGraphicsExtractor.textRenderer());
        }
    }

    public AdjustableBoundingBoxScreen(AdjustableBoundingBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 145, 109);
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
    protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int xm, int ym) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
        if (menu.detector != null) {
            graphics.text(font,
                    Component.translatable("gui.cargoboats.adjustable_bounding_box.min"), 8, topPos + 22, 0xff000000, false);
            graphics.text(font,
                    Component.translatable("gui.cargoboats.adjustable_bounding_box.max"), 8, topPos + 52, 0xff000000, false);
            var text = Integer.toString(menu.detector.minX);
            graphics.text(font, text, 28 - font.width(text)/2, 36, 0xff000000, false);
            text = Integer.toString(menu.detector.maxX);
            graphics.text(font, text, 28 - font.width(text)/2, 68, 0xff000000, false);
            text = Integer.toString(menu.detector.minY);
            graphics.text(font, text, 70 - font.width(text)/2, 36, 0xff000000, false);
            text = Integer.toString(menu.detector.maxY);
            graphics.text(font, text, 70 - font.width(text)/2, 68, 0xff000000, false);
            text = Integer.toString(menu.detector.minZ);
            graphics.text(font, text, 112 - font.width(text)/2, 36, 0xff000000, false);
            text = Integer.toString(menu.detector.maxZ);
            graphics.text(font, text, 112 - font.width(text)/2, 68, 0xff000000, false);
        }
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
