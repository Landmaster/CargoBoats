package com.landmaster.cargoboats;

import com.landmaster.cargoboats.block.BuoyBlock;
import com.landmaster.cargoboats.block.DockBlock;
import com.landmaster.cargoboats.block.MotorboatDetectorBlock;
import com.landmaster.cargoboats.block.MotorboatPathfindingNode;
import com.landmaster.cargoboats.block.entity.BuoyBlockEntity;
import com.landmaster.cargoboats.block.entity.DockBlockEntity;
import com.landmaster.cargoboats.block.entity.MotorboatDetectorBlockEntity;
import com.landmaster.cargoboats.entity.FluidMotorboat;
import com.landmaster.cargoboats.entity.Motorboat;
import com.landmaster.cargoboats.item.*;
import com.landmaster.cargoboats.menu.*;
import com.landmaster.cargoboats.network.*;
import com.landmaster.cargoboats.util.MotorboatSchedule;
import com.mojang.serialization.Codec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.energy.EmptyEnergyStorage;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.fluids.capability.templates.EmptyFluidHandler;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

import java.util.UUID;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(CargoBoats.MODID)
@EventBusSubscriber
public class CargoBoats {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "cargoboats";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, MODID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, MODID);

    public static final Supplier<SoundEvent> MOTORBOAT_SOUND = SOUND_EVENTS.register("motorboat", SoundEvent::createVariableRangeEvent);
    public static final Supplier<SoundEvent> BOAT_HORN_SOUND = SOUND_EVENTS.register("boat_horn", SoundEvent::createVariableRangeEvent);

    public static final Supplier<EntityDataSerializer<MotorboatSchedule>> MOTORBOAT_SCHEDULE_EDS = ENTITY_DATA_SERIALIZERS.register("motorboat_schedule",
            () -> EntityDataSerializer.forValueType(MotorboatSchedule.STREAM_CODEC));

    public static final Supplier<DataComponentType<MotorboatSchedule>> MOTORBOAT_SCHEDULE = DATA_COMPONENTS.registerComponentType("motorboat_schedule", builder ->
            builder.networkSynchronized(MotorboatSchedule.STREAM_CODEC).persistent(MotorboatSchedule.CODEC));
    public static final Supplier<DataComponentType<UUID>> TRACKED_MOTORBOAT = DATA_COMPONENTS.registerComponentType(
            "tracked_motorboat", builder -> builder.networkSynchronized(UUIDUtil.STREAM_CODEC).persistent(UUIDUtil.CODEC));
    public static final Supplier<DataComponentType<Boolean>> MOTORBOAT_HAS_DATA = DATA_COMPONENTS.registerComponentType(
            "motorboat_has_data", builder -> builder.networkSynchronized(ByteBufCodecs.BOOL).persistent(Codec.BOOL));
    public static final Supplier<DataComponentType<CompoundTag>> MOTORBOAT_SAVE_DATA = DATA_COMPONENTS.registerComponentType("motorboat_save_data", builder ->
            builder.persistent(CompoundTag.CODEC));

    public static final DeferredBlock<DockBlock> DOCK = BLOCKS.registerBlock("dock", DockBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD));
    public static final DeferredBlock<BuoyBlock> BUOY = BLOCKS.registerBlock("buoy", BuoyBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_RED)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(0.8F)
                    .sound(SoundType.METAL)
                    .noCollission()
                    .lightLevel(state -> 12)
    );
    public static final DeferredBlock<MotorboatDetectorBlock> MOTORBOAT_DETECTOR = BLOCKS.registerBlock("motorboat_detector", MotorboatDetectorBlock::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(0.8F)
                    .sound(SoundType.METAL)
    );

    public static final DeferredItem<BlockItem> DOCK_ITEM = ITEMS.registerSimpleBlockItem(DOCK);
    public static final DeferredItem<BlockItem> BUOY_ITEM = ITEMS.registerItem("buoy", props -> new PlaceOnWaterBlockItem(BUOY.get(), props));
    public static final DeferredItem<BlockItem> MOTORBOAT_DETECTOR_ITEM = ITEMS.registerSimpleBlockItem(MOTORBOAT_DETECTOR);

    public static final DeferredItem<MotorboatItem> MOTORBOAT_ITEM = ITEMS.registerItem("motorboat",
            MotorboatItem::new, new Item.Properties().stacksTo(1).fireResistant());
    public static final DeferredItem<MotorboatItem> FLUID_MOTORBOAT_ITEM = ITEMS.registerItem("fluid_motorboat",
            FluidMotorboatItem::new, new Item.Properties().stacksTo(1).fireResistant());
    public static final DeferredItem<MotorboatProgrammerItem> MOTORBOAT_PROGRAMMER = ITEMS.registerItem("motorboat_programmer",
            MotorboatProgrammerItem::new, new Item.Properties().stacksTo(1));
    public static final DeferredItem<SpeedUpgradeItem> SPEED_UPGRADE = ITEMS.registerItem("speed_upgrade", SpeedUpgradeItem::new);
    public static final DeferredItem<CapacityUpgradeItem> CAPACITY_UPGRADE = ITEMS.registerItem("capacity_upgrade", CapacityUpgradeItem::new);
    public static final DeferredItem<LavaUpgradeItem> LAVA_UPGRADE = ITEMS.registerItem("lava_upgrade", LavaUpgradeItem::new, new Item.Properties().fireResistant());
    public static final DeferredItem<FishingUpgradeItem> FISHING_UPGRADE = ITEMS.registerItem("fishing_upgrade", FishingUpgradeItem::new);
    public static final DeferredItem<MotorboatTrackerItem> MOTORBOAT_TRACKER = ITEMS.registerItem("motorboat_tracker", MotorboatTrackerItem::new,
            new Item.Properties().stacksTo(1));

    public static final Supplier<EntityType<Motorboat>> MOTORBOAT = ENTITIES.register("motorboat",
            () -> EntityType.Builder.<Motorboat>of(Motorboat::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
                    .build("motorboat"));
    public static final Supplier<EntityType<FluidMotorboat>> FLUID_MOTORBOAT = ENTITIES.register("fluid_motorboat",
            () -> EntityType.Builder.<FluidMotorboat>of(FluidMotorboat::new, MobCategory.MISC)
                    .sized(1.375F, 0.5625F).eyeHeight(0.5625F).clientTrackingRange(10)
                    .build("fluid_motorboat"));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_MODE_TABS.register("cargoboats", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.cargoboats"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(MOTORBOAT_ITEM::toStack)
            .displayItems((parameters, output) -> {
                output.accept(DOCK_ITEM);
                output.accept(BUOY_ITEM);
                output.accept(MOTORBOAT_DETECTOR_ITEM);
                output.accept(MOTORBOAT_ITEM);
                output.accept(FLUID_MOTORBOAT_ITEM);
                output.accept(MOTORBOAT_PROGRAMMER);
                output.accept(SPEED_UPGRADE);
                output.accept(CAPACITY_UPGRADE);
                output.accept(LAVA_UPGRADE);
                output.accept(FISHING_UPGRADE);
                output.accept(MOTORBOAT_TRACKER);
            }).build());

    public static final Supplier<MenuType<MotorboatMenu>> MOTORBOAT_MENU = MENU_TYPES.register(
            "motorboat", () -> new MenuType<>(MotorboatMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<FluidMotorboatMenu>> FLUID_MOTORBOAT_MENU = MENU_TYPES.register(
            "fluid_motorboat", () -> new MenuType<>(FluidMotorboatMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<MotorboatProgrammerMenu>> MOTORBOAT_PROGRAMMER_MENU = MENU_TYPES.register(
            "motorboat_programmer", () -> IMenuTypeExtension.create(MotorboatProgrammerMenu::new));
    public static final Supplier<MenuType<AdjustableBoundingBoxMenu>> ADJUSTABLE_BOUNDING_BOX_MENU = MENU_TYPES.register(
            "adjustable_bounding_box", () -> IMenuTypeExtension.create(AdjustableBoundingBoxMenu::new));

    public static final Supplier<BlockEntityType<DockBlockEntity>> DOCK_TE = BLOCK_ENTITY_TYPES.register("dock",
            () -> BlockEntityType.Builder.of(DockBlockEntity::new, DOCK.get()).build(null));
    public static final Supplier<BlockEntityType<BuoyBlockEntity>> BUOY_TE = BLOCK_ENTITY_TYPES.register("buoy",
            () -> BlockEntityType.Builder.of(BuoyBlockEntity::new, BUOY.get()).build(null));
    public static final Supplier<BlockEntityType<MotorboatDetectorBlockEntity>> MOTORBOAT_DETECTOR_TE = BLOCK_ENTITY_TYPES.register("motorboat_detector",
            () -> BlockEntityType.Builder.of(MotorboatDetectorBlockEntity::new, MOTORBOAT_DETECTOR.get()).build(null));

    public static final Supplier<AttachmentType<Long>> OVERFISHING_TICKS = ATTACHMENT_TYPES.register("overfishing_ticks",
            () -> AttachmentType.builder(() -> 0L).serialize(Codec.LONG).build());
    public static final Supplier<AttachmentType<Long>> OVERFISHING_LAST_RESET = ATTACHMENT_TYPES.register("overfishing_last_reset",
            () -> AttachmentType.builder(() -> Long.MIN_VALUE).serialize(Codec.LONG).build());
    public static final Supplier<AttachmentType<Long>> OVERFISHING_LAST_FISHED = ATTACHMENT_TYPES.register("overfishing_last_fished",
            () -> AttachmentType.builder(() -> Long.MIN_VALUE).serialize(Codec.LONG).build());

    public static final BlockCapability<MotorboatPathfindingNode, Void> MOTORBOAT_PATHFINDING_NODE = BlockCapability.createVoid(
            ResourceLocation.fromNamespaceAndPath(MODID, "motorboat_pathfinding_node"), MotorboatPathfindingNode.class
    );

    public static final TicketController TICKET_CONTROLLER = new TicketController(ResourceLocation.fromNamespaceAndPath(MODID, "ticket_controller"));

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public CargoBoats(IEventBus modEventBus, ModContainer modContainer) {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ATTACHMENT_TYPES.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        ENTITY_DATA_SERIALIZERS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    @SubscribeEvent
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(Capabilities.ItemHandler.ENTITY, MOTORBOAT.get(), (entity, ctx) -> entity.combinedHandler);
        event.registerEntity(Capabilities.ItemHandler.ENTITY_AUTOMATION, MOTORBOAT.get(), (entity, ctx) -> entity.itemHandler);
        event.registerEntity(Capabilities.EnergyStorage.ENTITY, MOTORBOAT.get(), (entity, ctx) -> entity);

        event.registerEntity(Capabilities.ItemHandler.ENTITY, FLUID_MOTORBOAT.get(), (entity, ctx) -> entity.combinedHandler);
        event.registerEntity(Capabilities.FluidHandler.ENTITY, FLUID_MOTORBOAT.get(), (entity, ctx) -> entity.tank);
        event.registerEntity(Capabilities.EnergyStorage.ENTITY, FLUID_MOTORBOAT.get(), (entity, ctx) -> entity);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, DOCK_TE.get(), (te, dir) -> te.getDockedMotorboat()
                .map(motorboat -> motorboat.getCapability(Capabilities.ItemHandler.ENTITY_AUTOMATION, null))
                .orElse(EmptyItemHandler.INSTANCE));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, DOCK_TE.get(), (te, dir) -> te.getDockedMotorboat()
                .map(motorboat -> motorboat.getCapability(Capabilities.EnergyStorage.ENTITY, null))
                .orElse(EmptyEnergyStorage.INSTANCE));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, DOCK_TE.get(), (te, dir) -> te.getDockedMotorboat()
                .map(motorboat -> motorboat.getCapability(Capabilities.FluidHandler.ENTITY, null))
                .orElse(EmptyFluidHandler.INSTANCE));

        event.registerBlockEntity(MOTORBOAT_PATHFINDING_NODE, DOCK_TE.get(), (te, ctx) -> te);
        event.registerBlockEntity(MOTORBOAT_PATHFINDING_NODE, BUOY_TE.get(), (te, ctx) -> te);
    }

    @SubscribeEvent
    private static void modifyComponents(ModifyDefaultComponentsEvent event) {
        event.modify(MOTORBOAT_PROGRAMMER, builder -> builder.set(MOTORBOAT_SCHEDULE.get(), MotorboatSchedule.INITIAL));
    }

    @SubscribeEvent
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("2");
        registrar.playToServer(SetAutomationPacket.TYPE, SetAutomationPacket.STREAM_CODEC, SetAutomationPacket::handle);
        registrar.playBidirectional(ModifySchedulePacket.TYPE, ModifySchedulePacket.STREAM_CODEC, ModifySchedulePacket::handle);
        registrar.playToClient(TrackMotorboatPacket.TYPE, TrackMotorboatPacket.STREAM_CODEC, TrackMotorboatPacket::handle);
        registrar.playToClient(SyncFluidMotorboatPacket.TYPE, SyncFluidMotorboatPacket.STREAM_CODEC, SyncFluidMotorboatPacket::handle);
        registrar.playBidirectional(SetMotorboatPagePacket.TYPE, SetMotorboatPagePacket.STREAM_CODEC, SetMotorboatPagePacket::handle);
        registrar.playBidirectional(AdjustBoundingBoxPacket.TYPE, AdjustBoundingBoxPacket.STREAM_CODEC, AdjustBoundingBoxPacket::handle);
    }

    @SubscribeEvent
    private static void registerTicketControllers(RegisterTicketControllersEvent event) {
        event.register(TICKET_CONTROLLER);
    }
}
