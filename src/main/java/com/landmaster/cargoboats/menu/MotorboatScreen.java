package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.util.ClientUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import javax.annotation.Nonnull;

public class MotorboatScreen extends AbstractContainerScreen<MotorboatMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/motorboat.png");

    public MotorboatScreen(MotorboatMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageHeight = 222;
        inventoryLabelY = imageHeight - 94;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        var entity = Minecraft.getInstance().level.getEntity(menu.motorboatId.get());
        if (entity instanceof Motorboat motorboat) {
            motorboat.nextStop().ifPresent(entry -> {
                guiGraphics.drawString(font, Component.translatable("gui.cargoboats.next_stop", entry.dock().toShortString()),
                        i + 65, j + 6, 0xFF000000, false);
            });
            ClientUtil.drawEnergyBarTooltip(motorboat.getEnergyStored(), motorboat.getMaxEnergyStored(), guiGraphics, i + 5, j + 16, mouseX, mouseY, font);
        }
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(CONTAINER_BACKGROUND, i, j, 0, 0, this.imageWidth, this.imageHeight);
        var entity = Minecraft.getInstance().level.getEntity(menu.motorboatId.get());
        if (entity instanceof Motorboat motorboat) {
            ClientUtil.drawEnergyBar(motorboat.getEnergyStored(), motorboat.getMaxEnergyStored(), guiGraphics, i + 5, j + 16);
        }
    }
}
