package com.landmaster.cargoboats.entity;

import com.google.common.collect.ImmutableList;
import com.landmaster.cargoboats.CargoBoats;
import com.landmaster.cargoboats.Config;
import com.landmaster.cargoboats.item.ExpandableItemStackHandler;
import com.landmaster.cargoboats.item.MotorboatUpgradeItemHandler;
import com.landmaster.cargoboats.menu.MotorboatMenu;
import com.landmaster.cargoboats.sound.MotorboatSoundInstance;
import com.landmaster.cargoboats.util.MotorboatSchedule;
import com.landmaster.cargoboats.util.Util;
import com.landmaster.cargoboats.util.WrenchHook;
import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.fish.WaterAnimal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableDouble;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class Motorboat extends Boat implements MenuProvider, HasCustomInventoryScreen, EnergyHandler {
    public static final long STUCK_TIME_THRESHOLD = 100;

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
    private static final EntityDataAccessor<Float> CLIENT_MOTORBOAT_SPEED = SynchedEntityData.defineId(Motorboat.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> LAVA_UPGRADE_ACTIVE = SynchedEntityData.defineId(Motorboat.class, EntityDataSerializers.BOOLEAN);
    private List<BlockPos> path = ImmutableList.of();
    private int dockTime = 0;
    private boolean automationEnabled = true;
    private ChunkPos lastChunk;
    private final LongSet chunkSet = new LongOpenHashSet();
    public static final int NUM_UPGRADES = 5;
    public final MotorboatUpgradeItemHandler upgradeHandler;
    public final ExpandableItemStackHandler itemHandler;
    public final ResourceHandler<ItemResource> combinedHandler;
    private long pathCheckTimestamp = Long.MIN_VALUE;
    private long stuckTime = STUCK_TIME_THRESHOLD;
    private final int baseInvSize;
    private boolean icebreakerActive;

    public static final int CONTAINER_SLOTS = 4;

    public final ContainerData containerData = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> getId();
                case 1 -> automationEnabled ? 1 : 0;
                case 2 -> itemCapacityMultiplier();
                case 3 -> baseInvSize == 0 ? 0 : Math.ceilDiv(itemHandler.size(), baseInvSize);
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return CONTAINER_SLOTS;
        }
    };

    public Motorboat(EntityType<? extends Motorboat> entityType, Level level, int invSize, Supplier<Item> itemSupplier) {
        super(entityType, level, itemSupplier);
        this.upgradeHandler = new MotorboatUpgradeItemHandler(entityType, NUM_UPGRADES);
        this.itemHandler = new ExpandableItemStackHandler(invSize);
        this.combinedHandler = new CombinedResourceHandler<>(upgradeHandler, itemHandler);
        this.baseInvSize = invSize;
    }

    public Motorboat(EntityType<? extends Motorboat> entityType, Level level) {
        this(entityType, level, 27, CargoBoats.MOTORBOAT_ITEM::get);
    }

    public Motorboat(Level level, double x, double y, double z) {
        this(CargoBoats.MOTORBOAT.get(), level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;
    }

    public ItemStack getWrenchDrop() {
        var stack = new ItemStack(getDropItem());
        var output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, registryAccess());
        addMotorboatSaveData(output);
        stack.set(CargoBoats.MOTORBOAT_SAVE_DATA, output.buildResult());
        stack.set(CargoBoats.MOTORBOAT_HAS_DATA, true);
        return stack;
    }

    public void setAutomationEnabled(boolean newValue) {
        this.automationEnabled = newValue;
        if (!automationEnabled) {
            resetDockingData(false);
        }
    }

    @Override
    protected float getSinglePassengerXOffset() {
        return 0.05F;
    }

    @Override
    protected int getMaxPassengers() {
        return 1;
    }

    @Override
    protected void defineSynchedData(@Nonnull SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MOTORBOAT_SCHEDULE, MotorboatSchedule.INITIAL);
        builder.define(MOTOR_ACTIVE, false);
        builder.define(NEXT_STOP_INDEX, 0);
        builder.define(ENERGY, 0);
        builder.define(CLIENT_MOTORBOAT_SPEED, 1.0f);
        builder.define(LAVA_UPGRADE_ACTIVE, true);
    }

    @Override
    protected void readAdditionalSaveData(@Nonnull ValueInput input) {
        super.readAdditionalSaveData(input);
        readMotorboatSaveData(input, false);
    }

    public void readMotorboatSaveData(ValueInput input, boolean forceSync) {
        getEntityData().set(ENERGY, input.getIntOr("EnergyStorage", 0));
        getEntityData().set(MOTORBOAT_SCHEDULE, input.read("MotorboatSchedule", MotorboatSchedule.CODEC).get());
        getEntityData().set(NEXT_STOP_INDEX, input.getIntOr("NextStop", 0));
        dockTime = input.getIntOr("DockTime", 0);
        automationEnabled = input.getBooleanOr("AutomationEnabled", false);
        input.readChild("Upgrades", upgradeHandler);
        input.readChild("Inventory", itemHandler);
    }

    @Override
    protected void addAdditionalSaveData(@Nonnull ValueOutput output) {
        super.addAdditionalSaveData(output);
        addMotorboatSaveData(output);
    }

    public void addMotorboatSaveData(ValueOutput output) {
        output.putInt("EnergyStorage", getEntityData().get(ENERGY));
        output.store("MotorboatSchedule", MotorboatSchedule.CODEC, getEntityData().get(MOTORBOAT_SCHEDULE));
        output.putInt("NextStop", getEntityData().get(NEXT_STOP_INDEX));
        output.putInt("DockTime", dockTime);
        output.putBoolean("AutomationEnabled", automationEnabled);
        output.putChild("Upgrades", upgradeHandler);
        output.putChild("Inventory", itemHandler);
    }

    public MotorboatSchedule getMotorboatSchedule() {
        return getEntityData().get(MOTORBOAT_SCHEDULE);
    }

    public boolean lavaUpgradeActive() {
        return getEntityData().get(LAVA_UPGRADE_ACTIVE);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        if (player.isSpectator()) {
            return null;
        } else {
            return new MotorboatMenu(containerId, playerInventory, this);
        }
    }

    @Override
    public void destroy(@Nonnull ServerLevel level, @Nonnull Item dropItem) {
        super.destroy(level, dropItem);
        if (level.getGameRules().get(GameRules.ENTITY_DROPS)) {
            dropContents();
        }
    }

    public int energyConsumption() {
        return Config.MOTORBOAT_BASE_ENERGY_USAGE.getAsInt();
    }

    @Override
    public void remove(@Nonnull RemovalReason reason) {
        if (!this.level().isClientSide() && reason == RemovalReason.KILLED) {
            dropContents();
        }

        super.remove(reason);

        if (level() instanceof ServerLevel level) {
            for (long chunkPos: chunkSet) {
                CargoBoats.TICKET_CONTROLLER.forceChunk(level, this, (int)chunkPos, (int)(chunkPos >> 32), false, false);
            }
        }
    }

    private void dropContents() {
        for (int i = 0; i < combinedHandler.size(); ++i) {
            Containers.dropItemStack(level(), getX(), getY(), getZ(),
                    combinedHandler.getResource(i).toStack(combinedHandler.getAmountAsInt(i)));
        }
    }

    public double motorboatSpeed() {
        return Config.MOTORBOAT_BASE_SPEED.getAsDouble() * relativeMotorboatSpeed();
    }

    public double relativeMotorboatSpeed() {
        double res = 1.0;
        int numSpeedUpgrades = Util.countItem(upgradeHandler, CargoBoats.SPEED_UPGRADE.get());
        if (numSpeedUpgrades > 0) {
            var multipliers = Config.MOTORBOAT_SPEED_MULTIPLIERS.get();
            res *= multipliers.get(Math.min(numSpeedUpgrades - 1, multipliers.size()));
        }
        return res;
    }

    private void chunkLoad() {
        if (level() instanceof ServerLevel level && !this.chunkPosition().equals(lastChunk)) {
            var newChunkSet = new LongOpenHashSet();
            for (int i=-1; i<=1; ++i) {
                for (int j=-1; j<=1; ++j) {
                    var chunkPos = new ChunkPos(chunkPosition().x() + i, chunkPosition().z() + j);
                    newChunkSet.add(chunkPos.pack());
                    CargoBoats.TICKET_CONTROLLER.forceChunk(level, this, chunkPos.x(), chunkPos.z(), true, false);
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
    }

    private void adjustCapacity() {
        itemHandler.resize(baseInvSize * itemCapacityMultiplier());
    }

    public int itemCapacityMultiplier() {
        int numUpgrades = Util.countItem(upgradeHandler, CargoBoats.CAPACITY_UPGRADE.get());
        return numUpgrades > 0
                ? Config.MOTORBOAT_ITEM_CAPACITY_MULTIPLIER.get().get(numUpgrades - 1)
                : 1;
    }

    protected void controlManualMotorboat() {
        if (this.isVehicle()) {
            float f = 0.0F;
            if (this.inputLeft) {
                this.deltaRotation--;
            }

            if (this.inputRight) {
                this.deltaRotation++;
            }

            if (this.inputRight != this.inputLeft && !this.inputUp && !this.inputDown) {
                f += 0.005F;
            }

            this.setYRot(this.getYRot() + this.deltaRotation);
            if (this.inputUp) {
                f += 0.04F;
            }

            if (this.inputDown) {
                f -= 0.005F;
            }

            f *= getEntityData().get(CLIENT_MOTORBOAT_SPEED);

            this.setDeltaMovement(
                    this.getDeltaMovement()
                            .add(
                                    (Mth.sin(-this.getYRot() * (float) (Math.PI / 180.0)) * f),
                                    0.0,
                                    (Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)) * f)
                            )
            );
            this.setPaddleState(this.inputRight && !this.inputLeft || this.inputUp, this.inputLeft && !this.inputRight || this.inputUp);
        }
    }

    private void runFishing() {
        var upgrade = Util.findItem(upgradeHandler, CargoBoats.FISHING_UPGRADE.get());
        int energyConsumption = Config.FISHING_ENERGY_CONSUMPTION.getAsInt();
        try (var txn = Transaction.openRoot()) {
            if ((status == Status.IN_WATER || status == Status.UNDER_WATER)
                    && !upgrade.isEmpty()
                    && level() instanceof ServerLevel serverLevel
                    && serverLevel.getFluidState(positionForPathfinding()).is(FluidTags.WATER)
                    && extract(energyConsumption, txn) >= energyConsumption
            ) {
                double effectiveFishingInterval = Config.BASE_FISHING_TIME.getAsDouble() - EnchantmentHelper.getFishingTimeReduction(serverLevel, upgrade, this);
                double probability = Config.FISHING_PROBABILITY_FACTOR.getAsDouble() / Math.max(effectiveFishingInterval, Config.FISHING_PROBABILITY_FACTOR.getAsDouble());
                var chunkPos = chunkPosition();
                var chunk = serverLevel.getChunk(chunkPos.x(), chunkPos.z());
                double overfishingProportion = Util.calcAndIncreaseOverfishingProportion(chunk);
                probability *= Math.pow(Config.OVERFISHING_EXPONENTIAL_BASE.getAsDouble(), overfishingProportion);
                if (random.nextDouble() < probability) {
                    var lootparams = new LootParams.Builder(serverLevel)
                            .withParameter(LootContextParams.ORIGIN, this.position())
                            .withParameter(LootContextParams.TOOL, upgrade)
                            .withParameter(LootContextParams.THIS_ENTITY, this)
                            .withLuck(EnchantmentHelper.getFishingLuckBonus(serverLevel, upgrade, this))
                            .create(LootContextParamSets.FISHING);
                    LootTable loottable = serverLevel.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.FISHING);
                    loottable.getRandomItems(lootparams, stack -> {
                        itemHandler.insert(ItemResource.of(stack), stack.getCount(), txn);
                    });
                }
                txn.commit();
            }
        }
    }

    private void runIcebreaker() {
        icebreakerActive = Util.countItem(upgradeHandler, CargoBoats.ICEBREAKER_UPGRADE.get()) > 0;

        if (icebreakerActive && tickCount % Config.ICEBREAKER_INTERVAL.getAsInt() == 0 && (status == Status.IN_WATER || status == Status.UNDER_WATER)) {
            var centralPos = positionForPathfinding();
            var level = level();
            var icebreakerRange = Config.ICEBREAKER_RANGE.getAsInt();
            for (var pos: BlockPos.betweenClosed(centralPos.offset(-icebreakerRange, 0, -icebreakerRange),
                    centralPos.offset(icebreakerRange, 0, icebreakerRange))) {
                if (level.getBlockState(pos).is(BlockTags.ICE)) {
                    level.setBlockAndUpdate(pos, Blocks.WATER.defaultBlockState());
                }
            }
        }
    }

    @Override
    public void tick() {
        this.oldStatus = this.status;
        this.status = this.getStatus();

        chunkLoad();
        adjustCapacity();
        runFishing();
        runIcebreaker();

        if (!level().isClientSide() && !isLocalInstanceAuthoritative()) {
            getEntityData().set(CLIENT_MOTORBOAT_SPEED, (float)relativeMotorboatSpeed());
        }
        if (!level().isClientSide()) {
            getEntityData().set(LAVA_UPGRADE_ACTIVE, Util.countItem(upgradeHandler, CargoBoats.LAVA_UPGRADE.get()) > 0);
        }

        var motorActive = new MutableBoolean(false);
        var expectedSpeed2 = new MutableDouble(0);

        var motorboatSchedule = getMotorboatSchedule();

        final int nextStopIdx = getEntityData().get(NEXT_STOP_INDEX);

        if (!level().isClientSide() && automationEnabled && this.isLocalInstanceAuthoritative()) {
            if (!motorboatSchedule.entries().isEmpty()
                    && nextStop().get().dimension() == level().dimension()) {
                var pos = motorboatSchedule.entries().get(nextStopIdx).dock();
                var cap = level().getCapability(CargoBoats.MOTORBOAT_PATHFINDING_NODE, pos);
                if (cap != null) {
                    long gameTime = level().getGameTime();

                    if (cap.isMotorboatDocked(this)) {
                        if (dockTime >= motorboatSchedule.entries().get(nextStopIdx).stopTime()) {
                            getEntityData().set(NEXT_STOP_INDEX, (nextStopIdx + 1) % motorboatSchedule.entries().size());
                            dockTime = 0;
                            stuckTime = STUCK_TIME_THRESHOLD;
                            if (cap.doBoatHorn()) {
                                playSound(CargoBoats.BOAT_HORN_SOUND.get(), 0.5f, 0.0f);
                            }
                        }
                        path = ImmutableList.of();
                        ++dockTime;
                    } else if (pathCheckTimestamp + 30 < gameTime && (!detectedMotorboats().isEmpty() || stuckTime >= STUCK_TIME_THRESHOLD)) {
                        var pair = cap.getBoxForMotorboatPathfinding();
                        path = pathfindToDockAABB(
                                positionForPathfinding(),
                                pair.first(), pair.second());
                        pathCheckTimestamp = gameTime;
                    }
                }
            } else {
                resetDockingData(false);
            }
        }

        if (this.status != Boat.Status.UNDER_WATER && this.status != Boat.Status.UNDER_FLOWING_WATER) {
            this.outOfControlTicks = 0.0F;
        } else {
            ++this.outOfControlTicks;
        }

        if (!this.level().isClientSide() && this.outOfControlTicks >= 60.0F) {
            this.ejectPassengers();
        }

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        this.baseTick();
        this.getInterpolation().interpolate();
        if (this.isLocalInstanceAuthoritative()) {
            if (!(this.getFirstPassenger() instanceof Player)) {
                this.setPaddleState(false, false);
            }

            this.floatBoat();
            if (this.level().isClientSide()) {
                this.controlManualMotorboat();
            }

            if (!level().isClientSide() && automationEnabled) {
                targetLocation().ifPresent(vec -> {
                    var deltaVector = vec.subtract(position());
                    int energyConsumption = energyConsumption();
                    try (var txn = Transaction.openRoot()) {
                        if (deltaVector.horizontalDistance() < 0.1) {
                            path.removeLast();
                        } else if (extract(energyConsumption, txn) >= energyConsumption) {
                            txn.commit();
                            motorActive.setTrue();
                            var scalar = Math.min(
                                    motorboatSpeed() / deltaVector.horizontalDistance(),
                                    0.2);
                            deltaVector = new Vec3(deltaVector.x * scalar, 0, deltaVector.z * scalar);
                            setYRot((float) (Math.toDegrees(Math.atan2(deltaVector.z, deltaVector.x)) - 90));
                            this.setDeltaMovement(deltaVector);
                            expectedSpeed2.setValue(deltaVector.horizontalDistanceSqr());
                        }
                    }
                });
            }

            this.move(MoverType.SELF, this.getDeltaMovement());
        } else {
            this.setDeltaMovement(Vec3.ZERO);
        }

        this.applyEffectsFromBlocks();
        this.tickBubbleColumn();

        List<Entity> list = this.level().getEntities(this, this.getBoundingBox().inflate(0.2, -0.01, 0.2), EntitySelector.pushableBy(this));
        if (!list.isEmpty()) {
            boolean flag = !this.level().isClientSide() && !(this.getControllingPassenger() instanceof Player);

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

        if (!level().isClientSide()) {
            getEntityData().set(MOTOR_ACTIVE, motorActive.booleanValue());

            if (automationEnabled && isLocalInstanceAuthoritative()) {
                if (path.isEmpty() || position().distanceToSqr(xo, yo, zo) < 0.2 * 0.2 * expectedSpeed2.getValue()) {
                    ++stuckTime;
                } else {
                    stuckTime = 0;
                }
            }
        } else {
            rotorSpeed = Math.clamp(rotorSpeed + (getEntityData().get(MOTOR_ACTIVE) ? 0.01f : -0.01F), 0, 1);
        }
    }

    private void resetDockingData(boolean setStopIdx) {
        dockTime = 0;
        stuckTime = STUCK_TIME_THRESHOLD;
        if (setStopIdx) {
            var schedule = getEntityData().get(MOTORBOAT_SCHEDULE);
            int candMinDockIndex = 0;
            for (int i = 0; i < schedule.entries().size(); ++i) {
                var entry = schedule.entries().get(i);
                if (entry.dimension() == level().dimension()
                        && entry.dock().distToCenterSqr(position()) < schedule.entries().get(candMinDockIndex).dock().distToCenterSqr(position())) {
                    candMinDockIndex = i;
                }
            }
            getEntityData().set(NEXT_STOP_INDEX, candMinDockIndex);
        }
        path = ImmutableList.of();
    }

    @Nonnull
    @Override
    public InteractionResult interact(@Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull Vec3 location) {
        var stack = player.getItemInHand(hand);

        if (stack.is(WrenchHook.WRENCH_TAG) && player.isSecondaryUseActive()) {
            var wrenchDrop = getWrenchDrop();
            var invWrapper = PlayerInventoryWrapper.of(player.getInventory());

            try (var txn = Transaction.openRoot()) {
                if (invWrapper.insert(ItemResource.of(wrenchDrop), wrenchDrop.count(), txn) >= wrenchDrop.count()) {
                    txn.commit();
                    discard();
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }
            }
        }

        if (stack.has(CargoBoats.MOTORBOAT_SCHEDULE)) {
            var schedule = stack.get(CargoBoats.MOTORBOAT_SCHEDULE);
            getEntityData().set(MOTORBOAT_SCHEDULE, schedule);

            resetDockingData(true);

            if (level().isClientSide()) {
                player.sendSystemMessage(Component.translatable("message.cargoboats.motorboat_programmed"));
            }
            return InteractionResult.SUCCESS;
        }

        if (stack.is(CargoBoats.MOTORBOAT_TRACKER)) {
            stack.set(CargoBoats.TRACKED_MOTORBOAT, uuid);
            if (level().isClientSide()) {
                player.sendSystemMessage(Component.translatable("message.cargoboats.motorboat_tracked", uuid.toString()));
            }
            return InteractionResult.SUCCESS;
        }

        if (!player.isSecondaryUseActive()) {
            InteractionResult interactionresult = super.interact(player, hand, location);
            if (interactionresult != InteractionResult.PASS) {
                return interactionresult;
            }
        }

        if (this.canAddPassenger(player) && !player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        } else {
            var openMenuResult = player.openMenu(this);
            if (openMenuResult.isPresent()) {
                this.gameEvent(GameEvent.CONTAINER_OPEN, player);
            }

            return openMenuResult.isPresent() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
    }

    @Override
    public void openCustomInventoryScreen(Player player) {
        var openMenuResult = player.openMenu(this);
        if (openMenuResult.isPresent() && !level().isClientSide()) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, player);
        }
    }

    private final SnapshotJournal<Integer> energyJournal = new SnapshotJournal<>() {
        @Override
        protected Integer createSnapshot() {
            return getEntityData().get(ENERGY);
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            getEntityData().set(ENERGY, snapshot);
        }
    };

    @Override
    public long getAmountAsLong() {
        return getEntityData().get(ENERGY);
    }

    @Override
    public long getCapacityAsLong() {
        return Config.MOTORBOAT_BASE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int insert(int amount, @Nonnull TransactionContext transaction) {
        int energy = getEntityData().get(ENERGY);
        int inserted = Math.min(getCapacityAsInt() - energy, amount);
        if (inserted > 0) {
            energyJournal.updateSnapshots(transaction);
            getEntityData().set(ENERGY, energy + inserted);
            return inserted;
        }
        return 0;
    }

    @Override
    public int extract(int amount, @Nonnull TransactionContext transaction) {
        int energy = getEntityData().get(ENERGY);
        int extracted = Math.min(energy, amount);
        if (extracted > 0) {
            energyJournal.updateSnapshots(transaction);
            getEntityData().set(ENERGY, energy - extracted);
            return extracted;
        }
        return 0;
    }

    private class PlayMotorboatSound {
        private final MotorboatSoundInstance soundInstance = new MotorboatSoundInstance(Motorboat.this);

        public void run() {
            Minecraft.getInstance().getSoundManager().play(soundInstance);
        }
    }

    @Override
    public void onAddedToLevel() {
        if (level().isClientSide()) {
            new PlayMotorboatSound().run();
        }
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    @Override
    public boolean fireImmune() {
        return lavaUpgradeActive() || super.fireImmune();
    }

    private record PathfindPQEntry(double cost, long point) implements Comparable<PathfindPQEntry> {
        @Override
        public int compareTo(@Nonnull PathfindPQEntry o) {
            return Double.compare(cost, o.cost);
        }
    }

    private double calcHeuristic(BlockPos point, BlockPos minGoal, BlockPos maxGoal) {
        double cand = Double.POSITIVE_INFINITY;
        for (int x = minGoal.getX(); x <= maxGoal.getX(); ++x) {
            for (int z = minGoal.getZ(); z <= maxGoal.getZ(); ++z) {
                cand = Math.min(cand, point.distSqr(new BlockPos(x, point.getY(), z)));
            }
        }
        return 1.5 * Math.sqrt(cand);
    }

    private boolean posValid(BlockPos pos, boolean surroundingBlock) {
        if (status == Status.IN_WATER || status == Status.UNDER_WATER || status == Status.UNDER_FLOWING_WATER) {
            var curState = level().getBlockState(pos);
            if (icebreakerActive && curState.is(BlockTags.ICE)) {
                return true;
            }
            if (!surroundingBlock && !canBoatInFluid(level().getFluidState(pos))) {
                return false;
            }
            if (!curState.getCollisionShape(level(), pos, CollisionContext.of(this)).isEmpty()
                    || (curState.getBlock() instanceof BubbleColumnBlock
                    && curState.getOptionalValue(BubbleColumnBlock.DRAG_DOWN).orElse(false))) {
                return false;
            }
            var aboveState = level().getBlockState(pos.above());
            return aboveState.getCollisionShape(level(), pos.above(), CollisionContext.of(this)).isEmpty();
        } else {
            var curState = level().getBlockState(pos);
            if (!surroundingBlock && !curState.isCollisionShapeFullBlock(level(), pos)) {
                return false;
            }
            var aboveState = level().getBlockState(pos.above());
            return aboveState.getCollisionShape(level(), pos.above(), CollisionContext.of(this)).isEmpty();
        }
    }

    private boolean nodeValid(BlockPos point, Long2BooleanMap cache) {
        return cache.computeIfAbsent(point.asLong(), p -> BlockPos.betweenClosedStream(
                new BlockPos(point.getX() - 1, point.getY(), point.getZ() - 1),
                new BlockPos(point.getX() + 1, point.getY(), point.getZ() + 1)
        ).allMatch(pos -> posValid(pos, !pos.equals(point))));
    }

    private List<BlockPos> getNeighbors(BlockPos point, Long2BooleanMap cache) {
        List<BlockPos> result = new ArrayList<>(4);

        for (var direction: Direction.Plane.HORIZONTAL) {
            var cand = point.relative(direction);
            if (nodeValid(cand, cache)) {
                result.add(cand);
            }
        }
        return result;
    }

    private boolean hasLOS(BlockPos pointA, BlockPos pointB, Long2BooleanMap cache) {
        int deltaX = Math.abs(pointB.getX() - pointA.getX());
        int deltaZ = -Math.abs(pointB.getZ() - pointA.getZ());
        int sgnX = pointA.getX() < pointB.getX() ? 1 : -1;
        int sgnZ = pointA.getZ() < pointB.getZ() ? 1 : -1;
        int e = deltaX + deltaZ;
        int x = pointA.getX(), z = pointA.getZ();

        for (;;) {
            if (!nodeValid(new BlockPos(x, pointA.getY(), z), cache)) {
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

    private List<Motorboat> detectedMotorboats() {
        var start = positionForPathfinding();
        return level().getEntitiesOfClass(Motorboat.class, AABB.encapsulatingFullBlocks(
                new BlockPos(start.getX() - 4, start.getY(), start.getZ() - 4), new BlockPos(start.getX() + 4, start.getY(), start.getZ() + 4))
        );
    }

    private List<BlockPos> pathfindToDockAABB(BlockPos start, BlockPos minGoal, BlockPos maxGoal) {
        if (minGoal.getY() > start.getY() || maxGoal.getY() < start.getY()) {
            return ImmutableList.of();
        }

        Long2BooleanMap posCache = new Long2BooleanOpenHashMap();
        Long2DoubleMap dists = new Long2DoubleOpenHashMap();
        Long2LongMap parents = new Long2LongOpenHashMap();
        PriorityQueue<PathfindPQEntry> pq = new ObjectHeapPriorityQueue<>();

        dists.put(start.asLong(), 0.0);
        pq.enqueue(new PathfindPQEntry(calcHeuristic(start, minGoal, maxGoal), start.asLong()));

        for (var otherMotorboat: detectedMotorboats()) {
            if (otherMotorboat != this) {
                posCache.put(otherMotorboat.positionForPathfinding().asLong(), false);
            }
        }

        while (!pq.isEmpty()) {
            var entry = pq.dequeue();
            var maxSearchDist = Config.MOTORBOAT_MAX_SEARCH_DISTANCE.getAsDouble();
            if (entry.cost > maxSearchDist || dists.size() > Mth.floor(maxSearchDist * 16)) {
                break;
            }
            BlockPos point = BlockPos.of(entry.point);
            if (point.getX() >= minGoal.getX() && point.getX() <= maxGoal.getX()
                && point.getZ() >= minGoal.getZ() && point.getZ() <= maxGoal.getZ()) {

                List<BlockPos> result = new ArrayList<>();
                long pointLong = entry.point;
                for (;;) {
                    result.add(BlockPos.of(pointLong));
                    if (!parents.containsKey(pointLong)) break;
                    pointLong = parents.get(pointLong);
                }
                return result;
            }
            for (var cand: getNeighbors(point, posCache)) {
                BlockPos candParent = point;
                if (parents.containsKey(entry.point)) {
                    var parent = BlockPos.of(parents.get(entry.point));
                    if (hasLOS(parent, cand, posCache)) {
                        candParent = parent;
                    }
                }
                var candDist = dists.get(candParent.asLong()) + Math.sqrt(candParent.distSqr(cand));
                if (dists.getOrDefault(cand.asLong(), Double.POSITIVE_INFINITY) > candDist) {
                    dists.put(cand.asLong(), candDist);
                    parents.put(cand.asLong(), candParent.asLong());
                    pq.enqueue(new PathfindPQEntry(candDist + calcHeuristic(cand, minGoal, maxGoal), cand.asLong()));
                }
            }
        }

        return ImmutableList.of();
    }

    private BlockPos positionForPathfinding() {
        return BlockPos.containing(position().subtract(0, 0.2, 0));
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
