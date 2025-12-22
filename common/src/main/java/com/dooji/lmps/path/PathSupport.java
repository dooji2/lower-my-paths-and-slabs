package com.dooji.lmps.path;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

public final class PathSupport {
    public static final double PATH_STEP = 1.0 / 16.0;
    private static final double PATH_COLLISION_STEP = -PATH_STEP;
    private static final double SLAB_STEP = 0.5;
    private static final double SLAB_COLLISION_STEP = -SLAB_STEP;

    private PathSupport() {
    }

    public static boolean isPathBlock(BlockState blockState) {
        return blockState.is(Blocks.DIRT_PATH);
    }

    public static boolean hasDirectLoweringSupport(BlockGetter level, BlockPos position) {
        return hasLoweringSupport(level.getBlockState(position.below()));
    }

    public static LoweringOffsets loweringOffsets(BlockGetter level, BlockPos position) {
        int remainingChecks = calculateRemainingChecks(level, position);
        LoweringOffsets offsets = loweringOffsets(level, position, remainingChecks);
        if (offsets == null) {
            return null;
        }

        List<BlockPos> linkedPositions = MultipartHelper.collectLinked(level, position);
        for (BlockPos linkedPos : linkedPositions) {
            LoweringOffsets linkedOffsets = loweringOffsets(level, linkedPos, calculateRemainingChecks(level, linkedPos));
            if (!offsets.equals(linkedOffsets)) {
                return null;
            }
        }

        return offsets;
    }

    private static int calculateRemainingChecks(BlockGetter level, BlockPos position) {
        int remainingChecks = position.getY() - level.getMinBuildHeight() + 1;
        if (remainingChecks < 1) {
            remainingChecks = 1;
        }

        return remainingChecks;
    }

    private static LoweringOffsets loweringOffsets(BlockGetter level, BlockPos position, int remainingChecks) {
        if (remainingChecks <= 0) {
            return null;
        }

        if (!OffsetState.isEnabled(level, position)) {
            return null;
        }

        BlockState belowState = level.getBlockState(position.below());
        LoweringOffsets offsets = loweringOffsets(belowState);
        if (offsets != null) {
            return offsets;
        }

        return loweringOffsets(level, position.below(), remainingChecks - 1);
    }

    public static LoweringOffsets loweringOffsets(BlockState blockState) {
        if (isPathBlock(blockState)) {
            return new LoweringOffsets(PATH_STEP, PATH_COLLISION_STEP);
        }

        if (blockState.getBlock() instanceof SlabBlock) {
            SlabType type = blockState.getValue(SlabBlock.TYPE);
            if (type == SlabType.BOTTOM && type != SlabType.DOUBLE) {
                return new LoweringOffsets(SLAB_STEP, SLAB_COLLISION_STEP);
            }
        }

        return null;
    }

    public static boolean hasLoweringSupport(BlockState blockState) {
        return loweringOffsets(blockState) != null;
    }

    public record LoweringOffsets(double renderOffset, double collisionOffset) {
    }
}
