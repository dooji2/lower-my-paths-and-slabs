package com.dooji.lmps.networking;

import com.dooji.lmps.networking.payloads.OffsetOverridesPayload;
import com.dooji.lmps.networking.payloads.OffsetTogglePayload;
import com.dooji.lmps.networking.payloads.OffsetSupportsPayload;
import com.dooji.lmps.path.OffsetSavedData;
import com.dooji.lmps.path.OffsetSupports;
import com.dooji.lmps.platform.LmpsPlatform;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class LmpsNetworking {
    private LmpsNetworking() {
    }

    public static void sendSnapshot(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        Long2BooleanMap overrides = OffsetSavedData.get(level).snapshot();
        LmpsPlatform.sendTo(player, new OffsetOverridesPayload(overrides));
    }

    public static void sendSupports(ServerPlayer player) {
        LmpsPlatform.sendTo(player, new OffsetSupportsPayload(OffsetSupports.currentEntries()));
    }

    public static void broadcastToggle(ServerLevel level, BlockPos position, Boolean override) {
        OffsetTogglePayload payload = OffsetTogglePayload.of(position, override);
        for (ServerPlayer player : level.players()) {
            LmpsPlatform.sendTo(player, payload);
        }
    }
}
