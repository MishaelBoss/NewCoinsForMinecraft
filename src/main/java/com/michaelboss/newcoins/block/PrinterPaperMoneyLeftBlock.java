package com.michaelboss.newcoins.block;

import com.michaelboss.newcoins.blockentity.PrinterPaperMoneyBlockEntity;
import com.michaelboss.newcoins.registry.ModBlockEntities;
import com.michaelboss.newcoins.registry.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrinterPaperMoneyLeftBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<PrinterPaperMoneyLeftBlock> CODEC = simpleCodec(PrinterPaperMoneyLeftBlock::new);

    public PrinterPaperMoneyLeftBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new PrinterPaperMoneyBlockEntity(blockPos, blockState);
    }

    @Override
    public void setPlacedBy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
        if (!level.isClientSide) {
            level.setBlock(pos.south(),
                    ModBlocks.PRINTER_PAPER_MONEY_BLOCK.get().defaultBlockState().setValue(PrinterPaperMoneyRightBlock.FACING, state.getValue(FACING)),
                    3);
        }
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (direction == Direction.SOUTH && !neighborState.is(ModBlocks.PRINTER_PAPER_MONEY_BLOCK.get())) {
            level.removeBlock(pos, false);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos pos = context.getClickedPos();
        BlockPos otherPos = pos.relative(facing.getClockWise());

        if (!context.getLevel().getBlockState(otherPos).canBeReplaced(context))
            return null;

        return this.defaultBlockState()
                .setValue(FACING, facing);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.sidedSuccess(true);
        } else {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof PrinterPaperMoneyBlockEntity printerPaperMoneyBlockEntity) {
                player.openMenu(printerPaperMoneyBlockEntity, pos);
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }

        return type == ModBlockEntities.PRINTER_PAPER_MONEY_BLOCK_ENTITY.get() ? (lvl, pos, blockState, blockEntity) -> PrinterPaperMoneyBlockEntity.tick(lvl, pos, blockState, (PrinterPaperMoneyBlockEntity) blockEntity) : null;
    }

    @Override
    protected void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof PrinterPaperMoneyBlockEntity printer)
                Containers.dropContents(level, pos, printer);

            Direction facing = state.getValue(FACING);
            BlockPos otherPos = pos.relative(facing.getClockWise());

            if (level.getBlockState(otherPos).is(this))
                level.destroyBlock(otherPos, false);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
