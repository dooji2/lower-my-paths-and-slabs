package com.dooji.lmps.permission;

import com.dooji.lmps.LMPS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

public final class LmpsPermissions {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final int DEFAULT_PERMISSION_LEVEL = 4;
    private static int requiredPermissionLevel = DEFAULT_PERMISSION_LEVEL;

    private LmpsPermissions() {
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("lmps_permissions.json");
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
                if (jsonObject != null && jsonObject.has("required_permission_level")) {
                    requiredPermissionLevel = Math.max(0, jsonObject.get("required_permission_level").getAsInt());
                }
            } catch (Exception exception) {
                LMPS.LOGGER.warn("Failed to load lmps config, using defaults", exception);
            }

            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("required_permission_level", DEFAULT_PERMISSION_LEVEL);
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            GSON.toJson(jsonObject, writer);
        } catch (IOException exception) {
            LMPS.LOGGER.warn("Failed to write lmps config", exception);
        }
    }

    public static boolean canToggle(ServerPlayer serverPlayer) {
        return serverPlayer.hasPermissions(requiredPermissionLevel);
    }
}
