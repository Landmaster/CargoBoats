package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.network.SetMotorboatPagePacket;
import com.landmaster.cargoboats.util.ClientUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nonnull;

public class MotorboatScreen extends AbstractContainerScreen<MotorboatMenu> {
    private static final Identifier CONTAINER_BACKGROUND = Identifier.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/motorboat.png");
    public MotorboatScreen(MotorboatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 222);
    }

    private class PageButton extends Button {
        public PageButton(int x, int y, int delta) {
            super(x, y, 12, 12, Component.literal(delta >= 0 ? "+" : "-"), btn -> {
                int readPageLimit = menu.dataSlots.get(3).get();
                ClientPacketDistributor.sendToServer(new SetMotorboatPagePacket(Math.clamp(menu.page + delta, 0, readPageLimit - 1)));
            }, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(@Nonnull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
            extractDefaultSprite(guiGraphicsExtractor);
            extractDefaultLabel(guiGraphicsExtractor.textRenderer());
        }
    }

    @Override
    protected void init() {
        super.init();
        // automation
        addRenderableWidget(new MotorboatAutomationButton(leftPos + 154, topPos + 5, menu.dataSlots.get(1)::get, CONTAINER_BACKGROUND));
        addRenderableWidget(new PageButton(leftPos + 8, topPos + 44, 1));
        addRenderableWidget(new PageButton(leftPos + 8, topPos + 56, -1));
    }

    @Override
    protected void extractLabels(@Nonnull GuiGraphicsExtractor graphics, int xm, int ym) {
        super.extractLabels(graphics, xm, ym);
        int writePageLimit = menu.dataSlots.get(2).get();
        int readPageLimit = menu.dataSlots.get(3).get();
        var pageStyle = menu.page < writePageLimit ? new ChatFormatting[0] : new ChatFormatting[] {ChatFormatting.STRIKETHROUGH};
        graphics.text(
                font,
                Component.translatable(writePageLimit < readPageLimit ? "gui.cargoboats.page.asterisk" : "gui.cargoboats.page",
                        menu.page+1, readPageLimit).withStyle(pageStyle),
                24, 51, 0xFF000000, false);
        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof Motorboat motorboat) {
            motorboat.nextStop().ifPresent(entry -> {
                var componentToDraw = Component.translatable("gui.cargoboats.next_stop", motorboat.nextStopIndex());
                var x = leftPos + 8;
                var y = topPos + 34;
                graphics.text(font, componentToDraw, x - leftPos, y - topPos, 0xFF000000, false);
                var width = font.width(componentToDraw);
                if (xm >= x && ym >= y && xm < x + width && ym < y + font.lineHeight) {
                    graphics.setTooltipForNextFrame(font, Component.translatable("tooltip.cargoboats.next_stop",
                                    entry.dock().toShortString(), entry.dimension().identifier().toString()),
                            xm, ym);
                }
            });
            ClientUtil.drawEnergyBarTooltip(motorboat.getAmountAsInt(), motorboat.getCapacityAsInt(), graphics, leftPos + 8, topPos + 16, xm, ym, font);
        }
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof Motorboat motorboat) {
            // energy
            ClientUtil.drawEnergyBar(motorboat.getAmountAsInt(), motorboat.getCapacityAsInt(), graphics, leftPos + 8, topPos + 16);
        }
    }

    @Override
    protected void extractTooltip(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot instanceof MotorboatUpgradeSlot && !this.hoveredSlot.hasItem()) {
            graphics.setTooltipForNextFrame(font, Component.translatable("tooltip.cargoboats.upgrade_slot"), mouseX, mouseY);
        }
    }
}
