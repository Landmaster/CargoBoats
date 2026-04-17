package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.FluidMotorboat;
import com.landmaster.cargoboats.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class FluidMotorboatScreen extends AbstractContainerScreen<FluidMotorboatMenu> {
    private static final Identifier CONTAINER_BACKGROUND = Identifier.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/fluid_motorboat.png");

    public FluidMotorboatScreen(FluidMotorboatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 222);
        inventoryLabelY = imageHeight - 161;
    }

    @Override
    protected void init() {
        super.init();
        // automation
        addRenderableWidget(new MotorboatAutomationButton(leftPos + 154, topPos + 5, menu.dataSlots.get(1)::get, CONTAINER_BACKGROUND));
    }

    @Override
    protected void extractTooltip(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
            graphics.setTooltipForNextFrame(font, Component.translatable("tooltip.cargoboats.upgrade_slot"), mouseX, mouseY);
        }
        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof FluidMotorboat motorboat) {
            motorboat.nextStop().ifPresent(entry -> {
                var componentToDraw = Component.translatable("gui.cargoboats.next_stop", motorboat.nextStopIndex());
                var x = leftPos + 8;
                var y = topPos + 34;
                graphics.text(font, componentToDraw, x, y, 0xFF000000, false);
                var width = font.width(componentToDraw);
                if (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + font.lineHeight) {
                    graphics.setTooltipForNextFrame(font, Component.translatable("tooltip.cargoboats.next_stop",
                                    entry.dock().toShortString(), entry.dimension().identifier().toString()),
                            mouseX, mouseY);
                }
            });
            ClientUtil.drawEnergyBarTooltip(motorboat.getAmountAsInt(), motorboat.getCapacityAsInt(), graphics, leftPos + 8, topPos + 16, mouseX, mouseY, font);
            ClientUtil.drawFluidBarTooltip(
                    motorboat.tank.getResource(0).toStack(motorboat.tank.getAmountAsInt(0)),
                    motorboat.tank.getCapacityAsInt(0, motorboat.tank.getResource(0)), graphics, leftPos + 8, topPos + 47, mouseX, mouseY, font);
        }
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof FluidMotorboat motorboat) {
            ClientUtil.drawEnergyBar(motorboat.getAmountAsInt(), motorboat.getCapacityAsInt(), graphics, leftPos + 8, topPos + 16);
            ClientUtil.drawFluidBar(motorboat.tank.getResource(0).toStack(motorboat.tank.getAmountAsInt(0)),
                    motorboat.tank.getCapacityAsInt(0, motorboat.tank.getResource(0)), graphics, leftPos + 8, topPos + 47);
        }
    }
}
