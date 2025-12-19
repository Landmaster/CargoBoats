package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.FluidMotorboat;
import com.landmaster.cargoboats.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class FluidMotorboatScreen extends AbstractContainerScreen<FluidMotorboatMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/fluid_motorboat.png");

    public FluidMotorboatScreen(FluidMotorboatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageHeight = 222;
        inventoryLabelY = imageHeight - 161;
    }

    @Override
    protected void init() {
        super.init();
        // automation
        addRenderableWidget(new MotorboatAutomationButton(leftPos + 154, topPos + 5, menu.dataSlots.get(1)::get, CONTAINER_BACKGROUND));
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof FluidMotorboat motorboat) {
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
            ClientUtil.drawFluidBarTooltip(motorboat.tank.getFluid(), motorboat.tank.getCapacity(), guiGraphics, leftPos + 8, topPos + 47, mouseX, mouseY, font);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // main background
        guiGraphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);

        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof FluidMotorboat motorboat) {
            ClientUtil.drawEnergyBar(motorboat.getEnergyStored(), motorboat.getMaxEnergyStored(), guiGraphics, leftPos + 8, topPos + 16);
            ClientUtil.drawFluidBar(motorboat.tank.getFluid(), motorboat.tank.getCapacity(), guiGraphics, leftPos + 8, topPos + 47);
        }
    }
}
