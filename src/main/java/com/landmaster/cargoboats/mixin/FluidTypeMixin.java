package com.landmaster.cargoboats.mixin;

import com.landmaster.cargoboats.entity.Motorboat;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidType.class)
public class FluidTypeMixin {
    @Inject(method = "supportsBoating(Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;)Z", at = @At("HEAD"), cancellable = true)
    private void injectSupportsBoating(AbstractBoat boat, CallbackInfoReturnable<Boolean> cbInfo) {
        if ((Object)this == NeoForgeMod.LAVA_TYPE.value() && boat instanceof Motorboat motorboat && motorboat.lavaUpgradeActive()) {
            cbInfo.setReturnValue(true);
        }
    }
}
