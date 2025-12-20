package com.dooji.lmps.path;

import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class OffsetClientState {
    private static ResourceKey<Level> dimension;
    private static final Long2BooleanOpenHashMap OVERRIDES = new Long2BooleanOpenHashMap();

    private OffsetClientState() {
    }

    public static Boolean override(Level level, BlockPos position) {
        if (dimension == null || !dimension.equals(level.dimension())) {
            return null;
        }

        return override(position);
    }

    public static Boolean override(BlockPos position) {
        if (dimension == null) {
            return null;
        }

        long key = position.asLong();
        if (!OVERRIDES.containsKey(key)) {
            return null;
        }

        return OVERRIDES.get(key);
    }

    public static void replace(Level level, Long2BooleanMap overrides) {
        dimension = level.dimension();
        OVERRIDES.clear();
        OVERRIDES.putAll(overrides);
    }

    public static void applyToggle(Level level, BlockPos position, Boolean override) {
        if (dimension == null || !dimension.equals(level.dimension())) {
            replace(level, new Long2BooleanOpenHashMap());
        }

        long key = position.asLong();
        if (override == null) {
            OVERRIDES.remove(key);
            return;
        }

        OVERRIDES.put(key, override.booleanValue());
    }
}
