package com.landmaster.cargoboats.data;

import com.landmaster.cargoboats.CargoBoats;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = CargoBoats.MODID)
public class GatherData {
    @SubscribeEvent
    private static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(ModRecipeProvider.Runner::new);
    }

    @EventBusSubscriber(modid = CargoBoats.MODID, value = Dist.CLIENT)
    public static class Client {
        @SubscribeEvent
        private static void gatherData(GatherDataEvent.Client event) {
            event.createProvider(ModModelProvider::new);
        }
    }
}
