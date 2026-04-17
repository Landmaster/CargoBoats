package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.network.SetAutomationPacket;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import javax.annotation.Nonnull;
import java.util.function.IntSupplier;

class MotorboatAutomationButton extends Button {
    private static final Tooltip[] TOOLTIPS = new Tooltip[]{
            Tooltip.create(Component.translatable("tooltip.cargoboats.automation_disabled")),
            Tooltip.create(Component.translatable("tooltip.cargoboats.automation_enabled"))
    };

    private final IntSupplier automationEnabledSupplier;
    private final Identifier atlas;

    public MotorboatAutomationButton(int x, int y, IntSupplier automationEnabledSupplier, Identifier atlas) {
        super(x, y, 16, 16, Component.translatable("gui.cargoboats.automation_button"),
                btn -> {
                    ClientPacketDistributor.sendToServer(new SetAutomationPacket(
                            automationEnabledSupplier.getAsInt() == 0
                    ));
                }, DEFAULT_NARRATION);
        this.automationEnabledSupplier = automationEnabledSupplier;
        this.atlas = atlas;
    }

    @Override
    protected void extractContents(@Nonnull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
        extractDefaultSprite(guiGraphicsExtractor);
        int automationEnabledValue = automationEnabledSupplier.getAsInt();
        guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, atlas, getX(), getY(),
                224 + 16 * automationEnabledValue, 64, getWidth(), getHeight(), 256, 256);
        setTooltip(TOOLTIPS[automationEnabledValue]);
    }
}
