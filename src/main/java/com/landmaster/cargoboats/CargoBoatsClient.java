package com.landmaster.cargoboats;

import com.landmaster.cargoboats.entity.render.FluidMotorboatRenderer;
import com.landmaster.cargoboats.entity.render.MotorboatModel;
import com.landmaster.cargoboats.entity.render.MotorboatRenderer;
import com.landmaster.cargoboats.menu.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CargoBoats.MODID, dist = Dist.CLIENT)
@EventBusSubscriber
public class CargoBoatsClient {
    public CargoBoatsClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CargoBoats.MOTORBOAT.get(), MotorboatRenderer::new);
        event.registerEntityRenderer(CargoBoats.FLUID_MOTORBOAT.get(), FluidMotorboatRenderer::new);
    }

    @SubscribeEvent
    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MotorboatRenderer.LAYER_LOCATION, MotorboatModel::createBodyLayer);
        event.registerLayerDefinition(FluidMotorboatRenderer.LAYER_LOCATION, MotorboatModel::createBodyLayer);
    }

    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(CargoBoats.MOTORBOAT_MENU.get(), MotorboatScreen::new);
        event.register(CargoBoats.FLUID_MOTORBOAT_MENU.get(), FluidMotorboatScreen::new);
        event.register(CargoBoats.MOTORBOAT_PROGRAMMER_MENU.get(), MotorboatProgrammerScreen::new);
        event.register(CargoBoats.ADJUSTABLE_BOUNDING_BOX_MENU.get(), AdjustableBoundingBoxScreen::new);
    }
}
