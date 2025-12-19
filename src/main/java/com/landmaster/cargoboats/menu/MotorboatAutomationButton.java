package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.network.SetAutomationPacket;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.function.IntSupplier;

class MotorboatAutomationButton extends Button {
    private static final Tooltip[] TOOLTIPS = new Tooltip[]{
            Tooltip.create(Component.translatable("tooltip.cargoboats.automation_disabled")),
            Tooltip.create(Component.translatable("tooltip.cargoboats.automation_enabled"))
    };

    private final IntSupplier automationEnabledSupplier;
    private final ResourceLocation atlas;

    public MotorboatAutomationButton(int x, int y, IntSupplier automationEnabledSupplier, ResourceLocation atlas) {
        super(x, y, 16, 16, Component.translatable("gui.cargoboats.automation_button"),
                btn -> {
                    PacketDistributor.sendToServer(new SetAutomationPacket(
                            automationEnabledSupplier.getAsInt() == 0
                    ));
                }, DEFAULT_NARRATION);
        this.automationEnabledSupplier = automationEnabledSupplier;
        this.atlas = atlas;
    }

    @Override
    protected void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int automationEnabledValue = automationEnabledSupplier.getAsInt();
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.blit(atlas, getX(), getY(),
                224 + 16 * automationEnabledValue, 64, getWidth(), getHeight());
        setTooltip(TOOLTIPS[automationEnabledValue]);
    }

    @Override
    public void renderString(@Nonnull GuiGraphics guiGraphics, @Nonnull Font font, int color) {
        // no-op
    }
}
