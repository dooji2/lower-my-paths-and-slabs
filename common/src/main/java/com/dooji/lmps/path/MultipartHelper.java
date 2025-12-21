package com.dooji.lmps.path;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class MultipartHelper {
    private MultipartHelper() {
    }

    public static List<BlockPos> collectLinked(BlockGetter level, BlockPos position) {
        List<BlockPos> positions = new ArrayList<>();
        LongOpenHashSet visited = new LongOpenHashSet();
        Deque<BlockPos> queue = new ArrayDeque<>();
        visited.add(position.asLong());
        queue.add(position);

        while (!queue.isEmpty()) {
            BlockPos current = queue.removeFirst();
            BlockState currentState = level.getBlockState(current);
            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);
                long key = neighbor.asLong();
                if (visited.contains(key)) {
                    continue;
                }

                BlockState neighborState = level.getBlockState(neighbor);
                if (isLinkedPart(currentState, neighborState)) {
                    visited.add(key);
                    queue.add(neighbor);
                    positions.add(neighbor);
                }
            }
        }

        return positions;
    }

    public static boolean isLinkedPart(BlockState state, BlockState otherState) {
        if (!otherState.is(state.getBlock())) {
            return false;
        }

        boolean foundLinkProperty = false;
        for (Property<?> property : state.getProperties()) {
            if (!otherState.hasProperty(property)) {
                continue;
            }

            Comparable<?> value = state.getValue(property);
            Comparable<?> otherValue = otherState.getValue(property);
            if (value.equals(otherValue)) {
                continue;
            }

            if (!isLinkValue(value) || !isLinkValue(otherValue)) {
                return false;
            }

            foundLinkProperty = true;
        }

        return foundLinkProperty;
    }

    private static boolean isLinkValue(Comparable<?> comparable) {
        if (!(comparable instanceof StringRepresentable stringRepresentable)) {
            return false;
        }

        String serialized = stringRepresentable.getSerializedName();
        return serialized.equals("upper")
            || serialized.equals("lower")
            || serialized.equals("top")
            || serialized.equals("bottom")
            || serialized.equals("head")
            || serialized.equals("foot")
            || serialized.equals("left")
            || serialized.equals("right");
    }
}
