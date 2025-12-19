package com.landmaster.cargoboats.util;

import com.landmaster.cargoboats.CargoBoats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

public class ClientUtil {
    public static final ResourceLocation POWER_METER_UNACTIVATED = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/util/power_meter_unactivated.png");
    public static final ResourceLocation POWER_METER = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/util/power_meter.png");
    public static final ResourceLocation FLUID_METER = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/util/fluid_meter.png");

    public static void drawEnergyBar(int energy, int maxEnergy, GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.blit(POWER_METER_UNACTIVATED, x, y, 0, 0, 128, 16, 128, 16);
        guiGraphics.blit(POWER_METER, x, y, 0, 0, (int) (14 + 114L * energy / maxEnergy), 16, 128, 16);
    }

    public static void drawEnergyBarTooltip(int energy, int maxEnergy, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Font font) {
        if (x <= mouseX && mouseX < x + 128 && y <= mouseY && mouseY < y + 16) {
            guiGraphics.renderTooltip(font, Component.translatable("gui.cargoboats.rf_indicator", energy, maxEnergy), mouseX, mouseY);
        }
    }

    public static void drawFluidBarTooltip(FluidStack fluidStack, int capacity, GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY, Font font) {
        if (x <= mouseX && mouseX < x + 64 && y <= mouseY && mouseY < y + 12) {
            guiGraphics.renderTooltip(font, Component.translatable(
                    "gui.cargoboats.fluid_indicator", fluidStack.getHoverName(), fluidStack.getAmount(), capacity), mouseX, mouseY);
        }
    }

    public static void drawFluidBar(FluidStack fluidStack, int capacity, GuiGraphics guiGraphics, int x, int y) {
        IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        if(attributes == null) {
            return;
        }
        ResourceLocation fluidStill = attributes.getStillTexture(fluidStack);
        if(fluidStill == null) {
            return;
        }

        int color = attributes.getTintColor();
        var sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(fluidStill);

        guiGraphics.blit(x, y, 0, (int)(64L * fluidStack.getAmount() / capacity), 12, sprite,
                FastColor.ARGB32.red(color) / 256.0f, FastColor.ARGB32.green(color) / 256.0f,
                FastColor.ARGB32.blue(color) / 256.0f, FastColor.ARGB32.alpha(color) / 256.0f);
        guiGraphics.blit(FLUID_METER, x, y, 0, 0, 64, 12, 64, 32);
    }
}
