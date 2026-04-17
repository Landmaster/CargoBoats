package com.landmaster.cargoboats.data;

import com.landmaster.cargoboats.CargoBoats;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class GatherData {
    @EventBusSubscriber(modid = CargoBoats.MODID)
    public static class Client {
        @SubscribeEvent
        private static void gatherData(GatherDataEvent.Client event) {
            event.createProvider(ModModelProvider::new);
        }
    }
}
