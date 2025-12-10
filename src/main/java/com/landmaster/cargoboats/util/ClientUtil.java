package com.landmaster.cargoboats.util;

import com.landmaster.cargoboats.CargoBoats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientUtil {
    public static final ResourceLocation POWER_METER_UNACTIVATED = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/util/power_meter_unactivated.png");
    public static final ResourceLocation POWER_METER = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/util/power_meter.png");

    public static void drawEnergyBar(int energy, int maxEnergy, GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(POWER_METER_UNACTIVATED, x, y, 0, 0, 128, 16, 128, 16);
        guiGraphics.blit(POWER_METER, x, y, 0, 0, (int) (14 + 114L * energy / maxEnergy), 16, 128, 16);
    }

    public static void drawEnergyBarTooltip(int energy, int maxEnergy, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Font font) {
        if (x <= mouseX && mouseX < x + 128 && y <= mouseY && mouseY < y + 16) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.cargoboats.rf_indicator", energy, maxEnergy), mouseX, mouseY);
        }
    }
}
