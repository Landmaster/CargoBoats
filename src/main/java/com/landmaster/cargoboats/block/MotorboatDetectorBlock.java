package com.landmaster.cargoboats.block;

import com.landmaster.cargoboats.block.entity.MotorboatDetectorBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MotorboatDetectorBlock extends BaseEntityBlock implements WrenchInteractable {
    public static final MapCodec<MotorboatDetectorBlock> CODEC = simpleCodec(MotorboatDetectorBlock::new);
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");

    public MotorboatDetectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWERED, false));
    }

    @Nonnull
    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> blockEntityType) {
        return !level.isClientSide ? MotorboatDetectorBlockEntity::serverTick : null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new MotorboatDetectorBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(@Nonnull StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(POWERED);
    }

    @Nonnull
    @Override
    protected RenderShape getRenderShape(@Nonnull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nonnull
    @Override
    protected InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            var blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof MotorboatDetectorBlockEntity te) {
                player.openMenu(te, pos);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected boolean isSignalSource(@Nonnull BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction direction) {
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MotorboatDetectorBlockEntity te) {
            return te.output;
        }
        return super.getSignal(state, level, pos, direction);
    }

    @Override
    protected int getDirectSignal(@Nonnull BlockState state, @Nonnull BlockGetter level, @Nonnull BlockPos pos, @Nonnull Direction direction) {
        return getSignal(state, level, pos, direction);
    }
}
