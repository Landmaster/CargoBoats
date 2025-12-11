package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.network.SetAutomationPacket;
import com.landmaster.cargoboats.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
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

    @Override
    protected void init() {
        super.init();
        // automation
        addRenderableWidget(new AutomationButton(leftPos + 154, topPos + 5));
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof Motorboat motorboat) {
            motorboat.nextStop().ifPresent(entry -> {
                guiGraphics.drawString(font, Component.translatable("gui.cargoboats.next_stop", entry.dock().toShortString()),
                        i + 5, j + 34, 0xFF000000, false);
            });
            ClientUtil.drawEnergyBarTooltip(motorboat.getEnergyStored(), motorboat.getMaxEnergyStored(), guiGraphics, i + 5, j + 16, mouseX, mouseY, font);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private class AutomationButton extends Button {
        private static final Tooltip[] TOOLTIPS = new Tooltip[] {
                Tooltip.create(Component.translatable("tooltip.cargoboats.automation_disabled")),
                Tooltip.create(Component.translatable("tooltip.cargoboats.automation_enabled"))
        };

        public AutomationButton(int x, int y) {
            super(x, y, 16, 16, Component.translatable("gui.cargoboats.automation_button"),
                    btn -> {
                        PacketDistributor.sendToServer(new SetAutomationPacket(
                                menu.dataSlots.get(1).get() == 0
                        ));
                    }, DEFAULT_NARRATION);
        }

        @Override
        protected void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            int automationEnabledValue = menu.dataSlots.get(1).get();
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            guiGraphics.blit(CONTAINER_BACKGROUND, getX(), getY(),
                    224 + 16*automationEnabledValue, 64, getWidth(), getHeight());
            setTooltip(TOOLTIPS[automationEnabledValue]);
        }

        @Override
        public void renderString(@Nonnull GuiGraphics guiGraphics, @Nonnull Font font, int color) {
            // no-op
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        // main background
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);

        var entity = Minecraft.getInstance().level.getEntity(menu.dataSlots.get(0).get());
        if (entity instanceof Motorboat motorboat) {
            // energy
            ClientUtil.drawEnergyBar(motorboat.getEnergyStored(), motorboat.getMaxEnergyStored(), guiGraphics, i + 5, j + 16);
        }
    }
}
