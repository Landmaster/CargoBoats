package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.network.ModifySchedulePacket;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;

public class MotorboatProgrammerScreen extends AbstractContainerScreen<MotorboatProgrammerMenu> {
    private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/motorboat_programmer.png");

    public MotorboatProgrammerScreen(MotorboatProgrammerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        imageWidth = 208;
        imageHeight = 222;
    }

    private static class AdjustingButton extends Button {
        public final int index;
        public final int delta;

        public AdjustingButton(int x, int y, int index, int delta) {
            super(x, y, 12, 12, Component.literal(delta == Integer.MIN_VALUE ? "" : delta >= 0 ? "+" : "-"), btn -> {
                PacketDistributor.sendToServer(new ModifySchedulePacket(index, delta));
            }, DEFAULT_NARRATION);
            this.index = index;
            this.delta = delta;
        }

        @Override
        protected void renderWidget(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.renderWidget(guiGraphics, mouseX, mouseY, partialTick);
            if (delta == Integer.MIN_VALUE) {
                guiGraphics.blit(CONTAINER_BACKGROUND, getX(), getY(), 240, 0, 12, 12);
            }
        }

        @Override
        public void renderString(@Nonnull GuiGraphics guiGraphics, @Nonnull Font font, int color) {
            if (delta != Integer.MIN_VALUE) {
                super.renderString(guiGraphics, font, color);
            }
        }
    };

    @Override
    protected void init() {
        super.init();
        for (int i=0; i<15; ++i) {
            addRenderableWidget(new AdjustingButton(leftPos + 8, topPos + 20 + i * 12, i, -10));
            addRenderableWidget(new AdjustingButton(leftPos + 50, topPos + 20 + i * 12, i, 10));
            addRenderableWidget(new AdjustingButton(leftPos + 190, topPos + 20 + i * 12, i, Integer.MIN_VALUE));
        }
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var schedule = menu.getSchedule();
        for (int i=0; i<schedule.entries().size(); ++i) {
            for (var renderable: renderables) {
                if (renderable instanceof AdjustingButton button) {
                    button.visible = button.index < schedule.entries().size();
                }
            }
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        for (int i=0; i<schedule.entries().size(); ++i) {
            var entry = schedule.entries().get(i);
            int x = leftPos + 36;
            int y = topPos + 22 + i*12;
            var component = Component.literal(Integer.toString(entry.stopTime()));
            guiGraphics.drawString(font, component, x - font.width(component) / 2, y, 0xff000000, false);
            x = leftPos + 66;
            component = Component.translatable("gui.cargoboats.motorboat_schedule", i, entry.dock().toShortString());
            guiGraphics.drawString(font, component, x, y, 0xff000000, false);
            if (mouseX >= x && mouseY >= y && mouseX < x+font.width(component) && mouseY < y+font.lineHeight) {
                guiGraphics.renderTooltip(font, Component.translatable("tooltip.cargoboats.motorboat_schedule",
                        entry.dock().toShortString(), entry.dimension().location().toString(), entry.stopTime()), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // main background
        guiGraphics.blit(CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }
}
