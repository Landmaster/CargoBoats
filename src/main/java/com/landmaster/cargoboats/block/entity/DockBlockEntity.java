package com.landmaster.cargoboats.block.entity;

import com.google.common.collect.ImmutableList;
import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.block.DockBlock;
import com.landmaster.cargoboats.entity.Motorboat;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DockBlockEntity extends BlockEntity {
    private @Nullable UUID dockedMotorboatId;
    private @Nullable Motorboat dockedMotorboat;

    public DockBlockEntity(BlockPos pos, BlockState blockState) {
        super(CargoBoats.DOCK_TE.get(), pos, blockState);
    }

    public static <T extends BlockEntity> void serverTick(Level level, BlockPos pos, BlockState state, T t) {
        if (t instanceof DockBlockEntity dockBlockEntity && level instanceof ServerLevel serverLevel) {
            if (dockBlockEntity.dockedMotorboatId == null) {
                var boatToDock = dockBlockEntity.getMotorboatToDock();
                if (boatToDock.isPresent()) {
                    dockBlockEntity.dockedMotorboatId = boatToDock.get().getUUID();
                    dockBlockEntity.dockedMotorboat = boatToDock.get();
                    dockBlockEntity.invalidateCapabilities();
                }
            } else if (!(
                    dockBlockEntity.dockedMotorboat != null
                            && serverLevel.getEntity(dockBlockEntity.dockedMotorboatId) == dockBlockEntity.dockedMotorboat
                            && dockBlockEntity.dockedMotorboat.getBoundingBox().intersects(dockBlockEntity.getMotorboatAABB())
            )) {
                dockBlockEntity.dockedMotorboatId = null;
                dockBlockEntity.dockedMotorboat = null;
                dockBlockEntity.invalidateCapabilities();
            }
        }
    }

    public AABB getMotorboatAABB() {
        var pair = getBoxForMotorboatPathfinding();
        return AABB.encapsulatingFullBlocks(
                new BlockPos(pair.first()),
                new BlockPos(pair.second())
        );
    }

    public Pair<Vec3i, Vec3i> getBoxForMotorboatPathfinding() {
        var facingDir = getBlockState().getValue(DockBlock.FACING);
        var ccwDir = facingDir.getCounterClockWise();
        var pos = this.getBlockPos();
        var pos1 = pos.relative(facingDir).relative(ccwDir).below();
        var pos2 = pos.relative(facingDir, 4).relative(ccwDir, -1).above();
        return Pair.of(
                new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ())),
                new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()))
        );
    }

    public List<Motorboat> getMotorboatCandidates() {
        if (level == null) {
            return ImmutableList.of();
        }
        return level.getEntitiesOfClass(Motorboat.class, getMotorboatAABB());
    }

    public Optional<Motorboat> getMotorboatToDock() {
        var cands = getMotorboatCandidates();
        if (cands.isEmpty()) {
            return Optional.empty();
        }
        for (var cand: cands) {
            if (cand.nextStop().filter(entry -> entry.matchesDock(getBlockPos(), level)).isPresent()) {
                return Optional.of(cand);
            }
        }
        return Optional.of(cands.getFirst());
    }

    public Optional<Motorboat> getDockedMotorboat() {
        return Optional.ofNullable(dockedMotorboat);
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.hasUUID("DockedMotorboat")) {
            dockedMotorboatId = tag.getUUID("DockedMotorboat");
            dockedMotorboat = null;
        }
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (dockedMotorboatId != null) {
            tag.putUUID("DockedMotorboat", dockedMotorboatId);
        }
    }
}
