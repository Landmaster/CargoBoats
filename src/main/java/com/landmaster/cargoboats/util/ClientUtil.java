package com.landmaster.cargoboats.util;

import com.landmaster.cargoboats.CargoBoats;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

public class ClientUtil {
    public static final Identifier POWER_METER_UNACTIVATED = Identifier.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/util/power_meter_unactivated.png");
    public static final Identifier POWER_METER = Identifier.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/util/power_meter.png");
    public static final Identifier FLUID_METER = Identifier.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/util/fluid_meter.png");

    public static void drawEnergyBar(int energy, int maxEnergy, GuiGraphicsExtractor guiGraphics, int x, int y) {
        guiGraphics.blit(POWER_METER_UNACTIVATED, x, y, 0, 0, 128, 16, 128, 16);
        guiGraphics.blit(POWER_METER, x, y, 0, 0, Math.clamp(14 + 114L * energy / maxEnergy, 14, 128), 16, 128, 16);
    }

    public static void drawEnergyBarTooltip(int energy, int maxEnergy, GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Font font) {
        if (x <= mouseX && mouseX < x + 128 && y <= mouseY && mouseY < y + 16) {
            guiGraphics.setTooltipForNextFrame(font, Component.translatable("gui.cargoboats.rf_indicator", energy, maxEnergy), mouseX, mouseY);
        }
    }

    public static void drawFluidBarTooltip(FluidStack fluidStack, int capacity, GuiGraphicsExtractor guiGraphics, int x, int y, int mouseX, int mouseY, Font font) {
        if (x <= mouseX && mouseX < x + 64 && y <= mouseY && mouseY < y + 12) {
            guiGraphics.setTooltipForNextFrame(font, Component.translatable(
                    "gui.cargoboats.fluid_indicator", fluidStack.getHoverName(), fluidStack.getAmount(), capacity), mouseX, mouseY);
        }
    }

    public static FluidModel getModel(FluidStack stack) {
        return Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(stack.getFluid().defaultFluidState());
    }

    public static void drawFluidBar(FluidStack fluidStack, int capacity, GuiGraphicsExtractor guiGraphics, int x, int y) {
        var model = getModel(fluidStack);
        var sprite = model.stillMaterial().sprite();
        int color = Optional.ofNullable(model.fluidTintSource()).map(t -> t.colorAsStack(fluidStack))
                .orElse(0xFFFFFFFF);

        guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, Math.clamp(64L * fluidStack.getAmount() / capacity, 0, 64), 12, color);

        guiGraphics.blit(FLUID_METER, x, y, 0, 0, 64, 12, 64, 32);
    }
}
