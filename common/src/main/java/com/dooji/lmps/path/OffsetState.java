package com.dooji.lmps.path;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public final class OffsetState {
    private OffsetState() {
    }

    public static boolean isEnabled(BlockGetter level, BlockPos position) {
        if (level instanceof Level world) {
            if (world instanceof ServerLevel serverLevel) {
                Boolean override = OffsetSavedData.get(serverLevel).override(position);
                if (override != null) {
                    return override;
                }
            } else if (world.isClientSide()) {
                Boolean override = OffsetClientState.override(world, position);
                if (override != null) {
                    return override;
                }
            }
        }

        Boolean override = OffsetClientState.override(position);
        if (override != null) {
            return override;
        }

        if (!PathSupport.hasDirectLoweringSupport(level, position)) {
            return false;
        }

        return false;
    }

    public static OffsetSavedData.ToggleResult toggle(ServerLevel level, BlockPos position) {
        return OffsetSavedData.get(level).toggle(position, false);
    }

    public static Boolean set(ServerLevel level, BlockPos position, boolean enabled) {
        return OffsetSavedData.get(level).apply(position, enabled, false);
    }
}
