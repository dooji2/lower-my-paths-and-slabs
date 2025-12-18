package com.dooji.lmps.path;

import com.dooji.lmps.registry.LmpsBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
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

    public static boolean canRestAbovePath(BlockState blockState) {
        return blockState.is(LmpsBlockTags.PATH_FRIENDLY_SUPPORTS);
    }

    public static boolean isCarpet(BlockState blockState) {
        return blockState.is(BlockTags.WOOL_CARPETS);
    }

    public static boolean allowFlatteningOver(BlockState blockState) {
        return blockState.isAir() || canRestAbovePath(blockState);
    }

    public static boolean isPathBelow(BlockGetter level, BlockPos position) {
        return isPathBlock(level.getBlockState(position.below()));
    }

    public static boolean isLoweringSupportBelow(BlockGetter level, BlockPos position) {
        return loweringOffsets(level, position, 1) != null;
    }

    public static LoweringOffsets loweringOffsets(BlockGetter level, BlockPos position, int remainingChecks) {
        if (remainingChecks <= 0) {
            return null;
        }

        BlockState belowState = level.getBlockState(position.below());
        if (isPathBlock(belowState)) {
            return new LoweringOffsets(PATH_STEP, PATH_COLLISION_STEP);
        }

        if (belowState.getBlock() instanceof SlabBlock) {
            SlabType type = belowState.getValue(SlabBlock.TYPE);
            if (type == SlabType.BOTTOM && type != SlabType.DOUBLE) {
                return new LoweringOffsets(SLAB_STEP, SLAB_COLLISION_STEP);
            }
        }

        return loweringOffsets(level, position.below(), remainingChecks - 1);
    }

    public record LoweringOffsets(double renderOffset, double collisionOffset) {
    }
}
