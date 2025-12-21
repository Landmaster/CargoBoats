package com.landmaster.cargoboats.util;

import com.landmaster.cargoboats.block.WrenchInteractable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class WrenchHook {
    public static final TagKey<Item> WRENCH_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/wrench"));

    @SubscribeEvent
    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.isCanceled()) {
            return;
        }

        if (event.getItemStack().is(WRENCH_TAG)) {
            var pos = event.getPos();
            var state = event.getLevel().getBlockState(pos);
            if (state.getBlock() instanceof WrenchInteractable wrenchInteractable) {
                var result = InteractionResult.PASS;
                if (event.getEntity().isShiftKeyDown()) {
                    result = wrenchInteractable.disassemble(event.getLevel(), event.getPos(), event.getEntity());
                } else {
                    result = wrenchInteractable.rotate(event.getLevel(), event.getPos(), event.getEntity());
                }
                if (result != InteractionResult.PASS) {
                    event.setCanceled(true);
                    event.setCancellationResult(result);
                }
            }
        }
    }
}
