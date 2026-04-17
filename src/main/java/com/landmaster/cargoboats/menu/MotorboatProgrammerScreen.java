package com.landmaster.cargoboats.menu;

import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.network.ModifySchedulePacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;

public class MotorboatProgrammerScreen extends AbstractContainerScreen<MotorboatProgrammerMenu> {
    private static final Identifier CONTAINER_BACKGROUND = Identifier.fromNamespaceAndPath(CargoBoats.MODID, "textures/gui/container/motorboat_programmer.png");
    private int page = 0;
    public static final int PAGE_SIZE = 12;

    public MotorboatProgrammerScreen(MotorboatProgrammerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 208, 222);
    }

    private class PageButton extends Button {
        public PageButton(int x, int y, int delta) {
            super(x, y, 12, 12, Component.literal(delta >= 0 ? "+" : "-"), btn -> {
                page = Math.clamp(page + delta, 0, (menu.getSchedule().entries().size() - 1) / PAGE_SIZE);
            }, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(@Nonnull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
            extractDefaultSprite(guiGraphicsExtractor);
            extractDefaultLabel(guiGraphicsExtractor.textRenderer());
        }
    }

    private class AdjustingButton extends Button {
        private final int index;
        public final int delta;

        public AdjustingButton(int x, int y, int index, int delta) {
            super(x, y, 12, 12, Component.literal(delta == Integer.MIN_VALUE ? "" : delta >= 0 ? "+" : "-"), btn -> {}, DEFAULT_NARRATION);
            this.index = index;
            this.delta = delta;
            setTooltip(Tooltip.create(Component.translatable("tooltip.cargoboats.programmer_adjust", delta, 10*delta)));
        }

        @Override
        public void onPress(@Nonnull InputWithModifiers input) {
            boolean isShifting = InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
                    || InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
            ClientPacketDistributor.sendToServer(new ModifySchedulePacket(adjustedIndex(), isShifting ? 10*delta : delta));
        }

        @Override
        protected void extractContents(@Nonnull GuiGraphicsExtractor guiGraphicsExtractor, int i, int i1, float v) {
            extractDefaultSprite(guiGraphicsExtractor);
            if (delta == Integer.MIN_VALUE) {
                guiGraphicsExtractor.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, getX(), getY(), 240, 0, 12, 12,
                        256, 256);
            } else {
                extractDefaultLabel(guiGraphicsExtractor.textRenderer());
            }
        }

        public int adjustedIndex() {
            return page * PAGE_SIZE + index;
        }
    };

    @Override
    protected void init() {
        super.init();
        for (int i=0; i<PAGE_SIZE; ++i) {
            addRenderableWidget(new AdjustingButton(leftPos + 8, topPos + 32 + i * 12, i, -10));
            addRenderableWidget(new AdjustingButton(leftPos + 50, topPos + 32 + i * 12, i, 10));
            addRenderableWidget(new AdjustingButton(leftPos + 190, topPos + 32 + i * 12, i, Integer.MIN_VALUE));
        }
        addRenderableWidget(new PageButton(leftPos + imageWidth / 2 - 32 - 6, topPos + 20, -1));
        addRenderableWidget(new PageButton(leftPos + imageWidth / 2 + 32 - 6, topPos + 20, 1));
    }

    @Override
    public void extractContents(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        var schedule = menu.getSchedule();

        for (var renderable: renderables) {
            if (renderable instanceof AdjustingButton button) {
                button.visible = button.adjustedIndex() < schedule.entries().size();
            }
        }

        super.extractContents(graphics, mouseX, mouseY, a);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int xm, int ym) {
        var schedule = menu.getSchedule();

        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);

        var pageComponent = Component.translatable("gui.cargoboats.page", page+1, 1+(schedule.entries().size() - 1) / PAGE_SIZE);
        graphics.text(font, pageComponent, leftPos + (imageWidth - font.width(pageComponent)) / 2,
                topPos + 22, 0xff000000, false);

        for (int i=page * PAGE_SIZE; i<schedule.entries().size() && i < (page+1) * PAGE_SIZE; ++i) {
            var entry = schedule.entries().get(i);
            int x = leftPos + 36;
            int y = topPos + 34 + (i % PAGE_SIZE)*12;
            var component = Component.literal(Integer.toString(entry.stopTime()));
            graphics.text(font, component, x - font.width(component) / 2, y, 0xff000000, false);
            x = leftPos + 66;
            component = Component.translatable("gui.cargoboats.motorboat_schedule", i, entry.dock().toShortString());
            graphics.text(font, component, x, y, 0xff000000, false);
            if (xm >= x && ym >= y && xm < x+font.width(component) && ym < y+font.lineHeight) {
                graphics.setTooltipForNextFrame(font, Component.translatable("tooltip.cargoboats.motorboat_schedule",
                        entry.dock().toShortString(), entry.dimension().identifier().toString(), entry.stopTime()), xm, ym);
            }
        }
    }

    @Override
    public void extractBackground(@Nonnull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_BACKGROUND, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

}
