package com.dooji.lmps.networking;

import com.dooji.lmps.networking.payloads.OffsetOverridesPayload;
import com.dooji.lmps.networking.payloads.OffsetTogglePayload;
import com.dooji.lmps.path.OffsetSavedData;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class LmpsNetworking {
    private LmpsNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(OffsetOverridesPayload.TYPE, OffsetOverridesPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OffsetTogglePayload.TYPE, OffsetTogglePayload.STREAM_CODEC);
    }

    public static void sendSnapshot(ServerPlayer player) {
        ServerLevel level = player.level().getLevel();
        Long2BooleanMap overrides = OffsetSavedData.get(level).snapshot();
        ServerPlayNetworking.send(player, new OffsetOverridesPayload(overrides));
    }

    public static void broadcastToggle(ServerLevel level, BlockPos position, Boolean override) {
        OffsetTogglePayload payload = OffsetTogglePayload.of(position, override);
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}
