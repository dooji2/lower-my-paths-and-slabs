package com.dooji.lmps.networking;

import com.dooji.lmps.networking.payloads.OffsetOverridesPayload;
import com.dooji.lmps.networking.payloads.OffsetSupportsPayload;
import com.dooji.lmps.networking.payloads.OffsetSupportsUpdatePayload;
import com.dooji.lmps.networking.payloads.OffsetTogglePayload;
import com.dooji.lmps.networking.payloads.PermissionLevelSyncPayload;
import com.dooji.lmps.networking.payloads.PermissionLevelUpdatePayload;
import com.dooji.lmps.path.OffsetSavedData;
import com.dooji.lmps.path.OffsetSupports;
import com.dooji.lmps.permission.LmpsPermissions;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import java.util.List;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class LmpsNetworking {
    private LmpsNetworking() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(OffsetOverridesPayload.TYPE, OffsetOverridesPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OffsetTogglePayload.TYPE, OffsetTogglePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OffsetSupportsPayload.TYPE, OffsetSupportsPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PermissionLevelSyncPayload.TYPE, PermissionLevelSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PermissionLevelUpdatePayload.TYPE, PermissionLevelUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(OffsetSupportsUpdatePayload.TYPE, OffsetSupportsUpdatePayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PermissionLevelUpdatePayload.TYPE, (payload, context) ->
            context.server().execute(() -> handlePermissionLevelUpdate(context.player(), payload.permissionLevel()))
        );

        ServerPlayNetworking.registerGlobalReceiver(OffsetSupportsUpdatePayload.TYPE, (payload, context) ->
            context.server().execute(() -> handleSupportsUpdate(context.player(), payload.supports()))
        );
    }

    public static void sendSnapshot(ServerPlayer player) {
        ServerLevel level = player.level();
        Long2BooleanMap overrides = OffsetSavedData.get(level).snapshot();
        ServerPlayNetworking.send(player, new OffsetOverridesPayload(overrides));
    }

    public static void sendSupports(ServerPlayer player) {
        ServerPlayNetworking.send(player, new OffsetSupportsPayload(OffsetSupports.currentEntries()));
    }

    public static void broadcastSupports(MinecraftServer server) {
        OffsetSupportsPayload payload = new OffsetSupportsPayload(OffsetSupports.currentEntries());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void sendPermissionLevel(ServerPlayer player) {
        ServerPlayNetworking.send(player, new PermissionLevelSyncPayload(LmpsPermissions.requiredPermissionLevel()));
    }

    public static void broadcastPermissionLevel(MinecraftServer server) {
        PermissionLevelSyncPayload payload = new PermissionLevelSyncPayload(LmpsPermissions.requiredPermissionLevel());
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    public static void broadcastToggle(ServerLevel level, BlockPos position, Boolean override) {
        OffsetTogglePayload payload = OffsetTogglePayload.of(position, override);
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    private static void handlePermissionLevelUpdate(ServerPlayer player, int permissionLevel) {
        if (!LmpsPermissions.canToggle(player)) {
            sendPermissionLevel(player);
            return;
        }

        boolean changed = LmpsPermissions.setRequiredPermissionLevel(permissionLevel);
        MinecraftServer server = player.level().getServer();
        if (changed && server != null) {
            broadcastPermissionLevel(server);
            return;
        }

        sendPermissionLevel(player);
    }

    private static void handleSupportsUpdate(ServerPlayer player, List<String> supports) {
        OffsetSupports.load();

        if (!LmpsPermissions.canToggle(player)) {
            sendSupports(player);
            return;
        }

        boolean changed = OffsetSupports.setEntries(supports);
        MinecraftServer server = player.level().getServer();
        if (changed && server != null) {
            broadcastSupports(server);
            return;
        }

        sendSupports(player);
    }
}
