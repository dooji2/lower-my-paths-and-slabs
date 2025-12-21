package com.dooji.lmps.platform;

import java.nio.file.Path;
import java.util.Objects;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public final class LmpsPlatform {
    private static ConfigPathProvider configPathProvider;
    private static NetworkSender networkSender;

    private LmpsPlatform() {
    }

    public static void useConfigPathProvider(ConfigPathProvider provider) {
        configPathProvider = provider;
    }

    public static Path configPath(String filename) {
        return Objects.requireNonNull(configPathProvider, "Config path provider not set").configPath(filename);
    }

    public static void useNetworkSender(NetworkSender sender) {
        networkSender = sender;
    }

    public static void sendTo(ServerPlayer player, CustomPacketPayload payload) {
        Objects.requireNonNull(networkSender, "Network sender not set").send(player, payload);
    }

    @FunctionalInterface
    public interface ConfigPathProvider {
        Path configPath(String filename);
    }

    @FunctionalInterface
    public interface NetworkSender {
        void send(ServerPlayer player, CustomPacketPayload payload);
    }
}
