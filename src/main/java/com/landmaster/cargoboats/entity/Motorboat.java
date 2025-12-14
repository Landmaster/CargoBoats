package com.landmaster.cargoboats.entity;

import com.google.common.collect.ImmutableList;
import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.Config;
import com.landmaster.cargoboats.menu.MotorboatMenu;
import com.landmaster.cargoboats.sound.MotorboatSoundInstance;
import com.landmaster.cargoboats.util.MotorboatSchedule;
import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.apache.commons.lang3.mutable.MutableBoolean;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Motorboat extends ChestBoat implements IEnergyStorage {
    public final AnimationState rotorAnimationState = new AnimationState();
    public float rotorSpeed = 0;

    private static final EntityDataAccessor<MotorboatSchedule> MOTORBOAT_SCHEDULE = SynchedEntityData.defineId(
            Motorboat.class,
            CargoBoats.MOTORBOAT_SCHEDULE_EDS.get()
    );
    private static final EntityDataAccessor<Boolean> MOTOR_ACTIVE = SynchedEntityData.defineId(
            Motorboat.class,
            EntityDataSerializers.BOOLEAN
    );
    private static final EntityDataAccessor<Integer> NEXT_STOP_INDEX = SynchedEntityData.defineId(
            Motorboat.class,
            EntityDataSerializers.INT
    );
    private static final EntityDataAccessor<Integer> ENERGY = SynchedEntityData.defineId(Motorboat.class, EntityDataSerializers.INT);
    private List<Vec3i> path = ImmutableList.of();
    private int dockTime = 0;
    public boolean automationEnabled = true;
    private ChunkPos lastChunk;
    private final LongSet chunkSet = new LongOpenHashSet();

    public final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> getId();
                case 1 -> automationEnabled ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public Motorboat(EntityType<? extends Motorboat> entityType, Level level) {
        super(entityType, level);
        this.itemStacks = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
    }

    public Motorboat(Level level, double x, double y, double z) {
        this(CargoBoats.MOTORBOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    @Override
    protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOTORBOAT_SCHEDULE, MotorboatSchedule.INITIAL);
        builder.define(MOTOR_ACTIVE, false);
        builder.define(NEXT_STOP_INDEX, 0);
        builder.define(ENERGY, 0);
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        getEntityData().set(ENERGY, compound.getInt("EnergyStorage"));
        getEntityData().set(MOTORBOAT_SCHEDULE, MotorboatSchedule.CODEC.parse(NbtOps.INSTANCE, compound.get("MotorboatSchedule")).getOrThrow());
        getEntityData().set(NEXT_STOP_INDEX, compound.getInt("NextStop"));
        dockTime = compound.getInt("DockTime");
        automationEnabled = compound.getBoolean("AutomationEnabled");
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("EnergyStorage", getEnergyStored());
        compound.put("MotorboatSchedule", MotorboatSchedule.CODEC.encodeStart(NbtOps.INSTANCE, getMotorboatSchedule()).getOrThrow());
        compound.putInt("NextStop", getEntityData().get(NEXT_STOP_INDEX));
        compound.putInt("DockTime", dockTime);
        compound.putBoolean("AutomationEnabled", automationEnabled);
    }

    public MotorboatSchedule getMotorboatSchedule() {
        return getEntityData().get(MOTORBOAT_SCHEDULE);
    }

    @Override
    public int getContainerSize() {
        return 36;
    }

    @Nonnull
    @Override
    public Item getDropItem() {
        return CargoBoats.MOTORBOAT_ITEM.asItem();
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        player.openMenu(this);
        if (!player.level().isClientSide) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        if (this.getLootTable() != null && player.isSpectator()) {
            return null;
        } else {
            this.unpackLootTable(playerInventory.player);
            return new MotorboatMenu(containerId, playerInventory, this);
        }
    }

    public int energyConsumption() {
        return Config.MOTORBOAT_BASE_ENERGY_USAGE.getAsInt();
    }

    @Override
    public void remove(@Nonnull RemovalReason reason) {
        super.remove(reason);

        if (level() instanceof ServerLevel level) {
            for (long chunkPos: chunkSet) {
                CargoBoats.TICKET_CONTROLLER.forceChunk(level, this, (int)chunkPos, (int)(chunkPos >> 32), false, false);
            }
        }
    }

    @Override
    public void tick() {
        if (level() instanceof ServerLevel level && !this.chunkPosition().equals(lastChunk)) {
            var newChunkSet = new LongOpenHashSet();
            for (int i=-1; i<=1; ++i) {
                for (int j=-1; j<=1; ++j) {
                    var chunkPos = new ChunkPos(chunkPosition().x + i, chunkPosition().z + j);
                    newChunkSet.add(chunkPos.toLong());
                    CargoBoats.TICKET_CONTROLLER.forceChunk(level, this, chunkPos.x, chunkPos.z, true, false);
                }
            }
            if (chunkSet.removeAll(newChunkSet)) {
                for (long chunkPos: chunkSet) {
                    CargoBoats.TICKET_CONTROLLER.forceChunk(level, this, (int)chunkPos, (int)(chunkPos >> 32), false, false);
                }
            }
            chunkSet.clear();
            chunkSet.addAll(newChunkSet);
            lastChunk = chunkPosition();
        }

        var motorActive = new MutableBoolean(false);

        var motorboatSchedule = getMotorboatSchedule();

        final int nextStopIdx = getEntityData().get(NEXT_STOP_INDEX);

        if (!rotorAnimationState.isStarted()) {
            rotorAnimationState.start(tickCount);
        }

        if (!level().isClientSide && automationEnabled) {
            if (!motorboatSchedule.entries().isEmpty()
                    && nextStop().get().dimension() == level().dimension()) {
                var pos = motorboatSchedule.entries().get(nextStopIdx).dock();
                var cap = level().getCapability(CargoBoats.MOTORBOAT_PATHFINDING_NODE, pos);
                if (cap != null) {
                    if (path.isEmpty() || level().getGameTime() % 40 == 0) {
                        var pair = cap.getBoxForMotorboatPathfinding();
                        path = pathfindToDockAABB(
                                positionForPathfinding(),
                                pair.first(), pair.second());
                    }

                    if (cap.isMotorboatDocked(this)) {
                        if (dockTime >= motorboatSchedule.entries().get(nextStopIdx).stopTime()) {
                            getEntityData().set(NEXT_STOP_INDEX, (nextStopIdx + 1) % motorboatSchedule.entries().size());
                            dockTime = 0;
                            if (cap.doBoatHorn()) {
                                playSound(CargoBoats.BOAT_HORN_SOUND.get(), 0.5f, 0.0f);
                            }
                        }
                        path = ImmutableList.of();
                        ++dockTime;
                    }
                }
            } else {
                path = ImmutableList.of();
                dockTime = 0;
                getEntityData().set(NEXT_STOP_INDEX, 0);
            }
        }

        this.oldStatus = this.status;
        this.status = this.getStatus();
        if (this.status != Boat.Status.UNDER_WATER && this.status != Boat.Status.UNDER_FLOWING_WATER) {
            this.outOfControlTicks = 0.0F;
        } else {
            ++this.outOfControlTicks;
        }

        if (!this.level().isClientSide && this.outOfControlTicks >= 60.0F) {
            this.ejectPassengers();
        }

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        super.tick();
        this.tickLerp();
        if (this.isControlledByLocalInstance()) {
            if (!(this.getFirstPassenger() instanceof Player)) {
                this.setPaddleState(false, false);
            }

            this.floatBoat();
            if (this.level().isClientSide) {
                this.controlBoat();
            }

            if (!level().isClientSide && automationEnabled) {
                targetLocation().ifPresent(vec -> {
                    var deltaVector = vec.subtract(position());
                    int energyConsumption = energyConsumption();
                    if (deltaVector.horizontalDistance() < 0.15) {
                        path.removeLast();
                    } else if (extractEnergy(energyConsumption, true) >= energyConsumption) {
                        extractEnergy(energyConsumption, false);
                        motorActive.setTrue();
                        var scalar = 0.07 / deltaVector.horizontalDistance();
                        deltaVector = new Vec3(deltaVector.x * scalar, 0, deltaVector.z * scalar);
                        setYRot((float) (Math.toDegrees(Math.atan2(deltaVector.z, deltaVector.x)) - 90));
                        this.setDeltaMovement(deltaVector);
                    }
                });
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }

        this.tickBubbleColumn();

        this.checkInsideBlocks();
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0.2, -0.01, 0.2), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.level().isClientSide && !(this.getControllingPassenger() instanceof Player);

            for(Entity entity : list) {
                if (!entity.hasPassenger(this)) {
                    if (flag && this.getPassengers().size() < this.getMaxPassengers() && !entity.isPassenger() && this.hasEnoughSpaceFor(entity) && entity instanceof LivingEntity && !(entity instanceof WaterAnimal) && !(entity instanceof Player)) {
                        entity.startRiding(this);
                    } else {
                        this.push(entity);
                    }
                }
            }
        }

        if (!level().isClientSide) {
            getEntityData().set(MOTOR_ACTIVE, motorActive.booleanValue());
        } else {
            rotorSpeed = Math.clamp(rotorSpeed + (getEntityData().get(MOTOR_ACTIVE) ? 0.01f : -0.01F), 0, 1);
        }
    }

    @Nonnull
    @Override
    public InteractionResult interact(@Nonnull Player player, @Nonnull InteractionHand hand) {
        var stack = player.getItemInHand(hand);
        if (stack.has(CargoBoats.MOTORBOAT_SCHEDULE)) {
            getEntityData().set(MOTORBOAT_SCHEDULE, stack.get(CargoBoats.MOTORBOAT_SCHEDULE));
            dockTime = 0;
            getEntityData().set(NEXT_STOP_INDEX, 0);
            path = ImmutableList.of();
            if (level().isClientSide) {
                player.displayClientMessage(Component.translatable("message.cargoboats.motorboat_programmed"), false);
            }
            return InteractionResult.SUCCESS;
        }
        return super.interact(player, hand);
    }

    private class PlayMotorboatSound {
        private final MotorboatSoundInstance soundInstance = new MotorboatSoundInstance(Motorboat.this);

        public void run() {
            Minecraft.getInstance().getSoundManager().play(soundInstance);
        }
    }

    @Override
    public void onAddedToLevel() {
        if (level().isClientSide) {
            new PlayMotorboatSound().run();
        }
    }

    @Override
    public int receiveEnergy(int i, boolean b) {
        int energy = getEnergyStored();
        int toReceive = Math.clamp(i, 0, getMaxEnergyStored() - energy);
        if (!b) {
            getEntityData().set(ENERGY, energy + toReceive);
        }
        return toReceive;
    }

    @Override
    public int extractEnergy(int i, boolean b) {
        int energy = getEnergyStored();
        int toExtract = Math.clamp(i, 0, energy);
        if (!b) {
            getEntityData().set(ENERGY, energy - toExtract);
        }
        return toExtract;
    }

    @Override
    public int getEnergyStored() {
        return getEntityData().get(ENERGY);
    }

    @Override
    public int getMaxEnergyStored() {
        return 100000;
    }

    @Override
    public boolean canExtract() {
        return true;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    private record PathfindPQEntry(double cost, Vec3i point) implements Comparable<PathfindPQEntry> {
        @Override
        public int compareTo(@Nonnull PathfindPQEntry o) {
            return Double.compare(cost, o.cost);
        }
    }

    private double calcHeuristic(Vec3i point, Vec3i minGoal, Vec3i maxGoal) {
        double cand = Double.POSITIVE_INFINITY;
        for (int x = minGoal.getX(); x <= maxGoal.getX(); ++x) {
            for (int z = minGoal.getZ(); z <= maxGoal.getZ(); ++z) {
                cand = Math.min(cand, point.distSqr(new Vec3i(x, point.getY(), z)));
            }
        }
        return Math.sqrt(cand);
    }

    private boolean posValid(BlockPos pos, Object2BooleanMap<BlockPos> cache) {
        return cache.computeIfAbsent(pos,
                k -> canBoatInFluid(level().getFluidState(pos)) && level().getBlockState(pos.above()).getCollisionShape(level(), pos).isEmpty());
    }

    private boolean nodeValid(Vec3i point, Object2BooleanMap<BlockPos> cache) {
        return BlockPos.betweenClosedStream(
                new BlockPos(point.getX() - 1, point.getY(), point.getZ() - 1),
                new BlockPos(point.getX() + 1, point.getY(), point.getZ() + 1)
        ).allMatch(pos -> posValid(pos, cache));
    }

    private List<Vec3i> getNeighbors(Vec3i point, Object2BooleanMap<BlockPos> cache) {
        List<Vec3i> result = new ArrayList<>(4);

        for (var direction: Direction.Plane.HORIZONTAL) {
            var cand = point.relative(direction);
            if (nodeValid(cand, cache)) {
                result.add(cand);
            }
        }
        return result;
    }

    private boolean hasLOS(Vec3i pointA, Vec3i pointB, Object2BooleanMap<BlockPos> cache) {
        int deltaX = Math.abs(pointB.getX() - pointA.getX());
        int deltaZ = -Math.abs(pointB.getZ() - pointA.getZ());
        int sgnX = pointA.getX() < pointB.getX() ? 1 : -1;
        int sgnZ = pointA.getZ() < pointB.getZ() ? 1 : -1;
        int e = deltaX + deltaZ;
        int x = pointA.getX(), z = pointA.getZ();

        for (;;) {
            if (!nodeValid(new Vec3i(x, pointA.getY(), z), cache)) {
                return false;
            }

            if (x == pointB.getX() && z == pointB.getZ()) {
                break;
            }

            if (2*e >= deltaZ) {
                if (x == pointB.getX()) {
                    break;
                }
                e += deltaZ;
                x += sgnX;
            }

            if (2*e <= deltaX) {
                if (z == pointB.getZ()) {
                    break;
                }
                e += deltaX;
                z += sgnZ;
            }
        }
        return true;
    }

    private List<Vec3i> pathfindToDockAABB(Vec3i start, Vec3i minGoal, Vec3i maxGoal) {
        if (minGoal.getY() > start.getY() || maxGoal.getY() < start.getY()) {
            return ImmutableList.of();
        }

        Object2BooleanMap<BlockPos> posCache = new Object2BooleanOpenHashMap<>();
        Object2DoubleMap<Vec3i> dists = new Object2DoubleOpenHashMap<>();
        Object2ObjectMap<Vec3i, Vec3i> parents = new Object2ObjectOpenHashMap<>();
        PriorityQueue<PathfindPQEntry> pq = new ObjectHeapPriorityQueue<>();

        dists.put(start, 0.0);
        pq.enqueue(new PathfindPQEntry(calcHeuristic(start, minGoal, maxGoal), start));

        while (!pq.isEmpty()) {
            var entry = pq.dequeue();
            if (entry.cost > 200) {
                break;
            }
            if (entry.point.getX() >= minGoal.getX() && entry.point.getX() <= maxGoal.getX()
                && entry.point.getZ() >= minGoal.getZ() && entry.point.getZ() <= maxGoal.getZ()) {

                List<Vec3i> result = new ArrayList<>();
                Vec3i point = entry.point;
                while (point != null) {
                    result.add(point);
                    point = parents.get(point);
                }
                return result;
            }
            for (var cand: getNeighbors(entry.point, posCache)) {
                var parent = parents.get(entry.point);
                var candParent = parent != null && hasLOS(parent, cand, posCache) ? parent : entry.point;
                var candDist = dists.getDouble(candParent) + Math.sqrt(candParent.distSqr(cand));
                if (dists.getOrDefault(cand, Double.POSITIVE_INFINITY) > candDist) {
                    dists.put(cand, candDist);
                    parents.put(cand, candParent);
                    pq.enqueue(new PathfindPQEntry(candDist + calcHeuristic(cand, minGoal, maxGoal), cand));
                }
            }
        }

        return ImmutableList.of();
    }

    private Vec3i positionForPathfinding() {
        return new BlockPos((int) Math.floor(getX()), (int) Math.floor(getY()), (int) Math.floor(getZ()));
    }

    private Optional<Vec3> targetLocation() {
        if (path.size() >= 2) {
            var intVec = path.get(path.size() - 2);
            return Optional.of(new Vec3(intVec.getX() + 0.5, intVec.getY(), intVec.getZ() + 0.5));
        }
        return Optional.empty();
    }

    public int nextStopIndex() {
        return getEntityData().get(NEXT_STOP_INDEX);
    }

    public Optional<MotorboatSchedule.Entry> nextStop() {
        int idx = nextStopIndex();
        var entries = getMotorboatSchedule().entries();
        if (idx < 0 || idx >= entries.size()) {
            return Optional.empty();
        }
        return Optional.of(entries.get(idx));
    }
}
