package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.network.SetMotorboatPagePacket;
import com.landmaster.cargoboats.util.ClientUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;

public class MotorboatScreen extends AbstractContainerScreen<MotorboatMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/motorboat.png");
    public MotorboatScreen(MotorboatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageHeight = 222;
        inventoryLabelY = imageHeight - 94;
    }

    private class PageButton extends Button {
        public PageButton(int x, int y, int delta) {
            super(x, y, 12, 12, Component.literal(delta >= 0 ? "+" : "-"), btn -> {
                int readPageLimit = menu.dataSlots.get(3).get();
                PacketDistributor.sendToServer(new SetMotorboatPagePacket(Math.clamp(menu.page + delta, 0, readPageLimit - 1)));
            }, DEFAULT_NARRATION);
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
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int writePageLimit = menu.dataSlots.get(2).get();
        int readPageLimit = menu.dataSlots.get(3).get();
        var pageStyle = menu.page < writePageLimit ? new ChatFormatting[0] : new ChatFormatting[] {ChatFormatting.STRIKETHROUGH};
        guiGraphics.drawString(
                font,
                Component.translatable(writePageLimit < readPageLimit ? "gui.cargoboats.page.asterisk" : "gui.cargoboats.page",
                        menu.page+1, readPageLimit).withStyle(pageStyle),
                leftPos + 24, topPos + 51, 0xFF000000, false);
        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof Motorboat motorboat) {
            motorboat.nextStop().ifPresent(entry -> {
                var componentToDraw = Component.translatable("gui.cargoboats.next_stop", motorboat.nextStopIndex());
                var x = leftPos + 8;
                var y = topPos + 34;
                guiGraphics.drawString(font, componentToDraw, x, y, 0xFF000000, false);
                var width = font.width(componentToDraw);
                if (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + font.lineHeight) {
                    guiGraphics.renderTooltip(font, Component.translatable("tooltip.cargoboats.next_stop",
                                    entry.dock().toShortString(), entry.dimension().location().toString()),
                            mouseX, mouseY);
                }
            });
            ClientUtil.drawEnergyBarTooltip(motorboat.getEnergyStored(), motorboat.getMaxEnergyStored(), guiGraphics, leftPos + 8, topPos + 16, mouseX, mouseY, font);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // main background
        guiGraphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof Motorboat motorboat) {
            // energy
            ClientUtil.drawEnergyBar(motorboat.getEnergyStored(), motorboat.getMaxEnergyStored(), guiGraphics, leftPos + 8, topPos + 16);
        }
    }
}
