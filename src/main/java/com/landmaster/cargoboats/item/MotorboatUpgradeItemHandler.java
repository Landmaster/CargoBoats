package com.landmaster.cargoboats.item;

import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;

import javax.annotation.Nonnull;

public class MotorboatUpgradeItemHandler extends ItemStacksResourceHandler {
    public final EntityType<?> entityType;

    public MotorboatUpgradeItemHandler(EntityType<?> entityType, int slots) {
        super(slots);
        this.entityType = entityType;
    }

    @Override
    public boolean isValid(int index, ItemResource resource) {
        if (!(resource.getItem() instanceof MotorboatUpgrade)) {
            return false;
        }
        for (int i=0; i<stacks.size(); ++i) {
            if (i != index && stacks.get(i).getItem() == resource.getItem()) {
                return false;
            }
        }
        return super.isValid(index, resource);
    }

    @Override
    protected int getCapacity(int index, @Nonnull ItemResource resource) {
        int limit = super.getCapacity(index, resource);
        if (resource.getItem() instanceof MotorboatUpgrade upgrade) {
            limit = Math.min(limit, upgrade.maxUpgradeAmount(entityType));
        }
        return limit;
    }
}
