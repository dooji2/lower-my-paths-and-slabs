package com.dooji.lmps.path;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PathSupport {
    private PathSupport() {
    }

    public static boolean hasDirectLoweringSupport(BlockGetter level, BlockPos position) {
        return hasLoweringSupport(level, position.below());
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
        LoweringOffsets offsets = loweringOffsets(level, position.below(), belowState);
        if (offsets != null) {
            return offsets;
        }

        return loweringOffsets(level, position.below(), remainingChecks - 1);
    }

    private static LoweringOffsets loweringOffsets(BlockGetter level, BlockPos position, BlockState blockState) {
        if (!OffsetSupports.isSupported(blockState)) {
            return null;
        }

        VoxelShape shape = blockState.getCollisionShape(level, position);
        if (shape.isEmpty()) {
            shape = blockState.getShape(level, position);
        }

        double height = Math.min(1.0, shape.max(Axis.Y));
        double offset = 1.0 - height;
        if (offset <= 0.0) {
            return null;
        }

        return new LoweringOffsets(offset, -offset);
    }

    public static boolean hasLoweringSupport(BlockGetter level, BlockPos position) {
        return loweringOffsets(level, position, level.getBlockState(position)) != null;
    }

    public record LoweringOffsets(double renderOffset, double collisionOffset) {
    }
}
