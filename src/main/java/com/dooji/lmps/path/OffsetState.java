package com.dooji.lmps.path;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import java.util.List;

public final class OffsetState {
    private OffsetState() {
    }

    public static boolean isEnabled(BlockGetter level, BlockPos position) {
        boolean defaultEnabled = hasGroupedSupport(level, position);
        if (level instanceof Level world) {
            if (world.isClientSide()) {
                Boolean override = OffsetClientState.override(world, position);
                if (override != null) {
                    return override;
                }
            } else if (world instanceof ServerLevel serverLevel) {
                OffsetSavedData offsetSavedData = OffsetSavedData.get(serverLevel);
                Boolean override = offsetSavedData.override(position);
                if (override != null) {
                    return override;
                }
            }
        }

        Boolean override = OffsetClientState.override(position);
        if (override != null) {
            return override;
        }

        return defaultEnabled;
    }

    public static OffsetSavedData.ToggleResult toggle(ServerLevel level, BlockPos position) {
        boolean defaultEnabled = PathSupport.hasDirectLoweringSupport(level, position);
        return OffsetSavedData.get(level).toggle(position, defaultEnabled);
    }

    public static Boolean set(ServerLevel level, BlockPos position, boolean enabled) {
        boolean defaultEnabled = PathSupport.hasDirectLoweringSupport(level, position);
        return OffsetSavedData.get(level).apply(position, enabled, defaultEnabled);
    }

    private static boolean hasGroupedSupport(BlockGetter level, BlockPos position) {
        if (PathSupport.hasDirectLoweringSupport(level, position)) {
            List<BlockPos> linked = MultipartHelper.collectLinked(level, position);
            for (BlockPos linkedPos : linked) {
                if (!PathSupport.hasDirectLoweringSupport(level, linkedPos)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}
