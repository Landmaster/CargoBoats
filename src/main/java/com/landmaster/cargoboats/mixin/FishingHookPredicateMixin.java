package com.landmaster.cargoboats.mixin;

import com.landmaster.cargoboats.entity.Motorboat;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(FishingHookPredicate.class)
public class FishingHookPredicateMixin {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    private Optional<Boolean> inOpenWater;

    @Inject(method = "matches(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;)Z", at = @At("HEAD"), cancellable = true)
    private void injectMatches(Entity entity, ServerLevel level, Vec3 position, CallbackInfoReturnable<Boolean> cbInfo) {
        if (entity instanceof Motorboat) {
            cbInfo.setReturnValue(inOpenWater.orElse(true));
        }
    }
}
