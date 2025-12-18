package com.dooji.lmps;

import com.dooji.lmps.networking.payloads.PathRulesPayload;
import com.dooji.lmps.registry.LmpsBlockTags;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LMPS implements ModInitializer {
    public static final String MOD_ID = "lmps";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(PathRulesPayload.TYPE, PathRulesPayload.STREAM_CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Registry<Block> blockRegistry = server.registryAccess().registryOrThrow(Registries.BLOCK);
            int pathFriendlySupportCount = blockRegistry
                .getTag(LmpsBlockTags.PATH_FRIENDLY_SUPPORTS)
                .map(holderSet -> (int) holderSet.stream().count())
                .orElse(0);
            LOGGER.info("Loaded {} path friendly supports", pathFriendlySupportCount);
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Registry<Block> blockRegistry = server.registryAccess().registryOrThrow(Registries.BLOCK);
            List<ResourceLocation> pathFriendlySupportIds = blockRegistry
                .getTag(LmpsBlockTags.PATH_FRIENDLY_SUPPORTS)
                .map(holderSet -> holderSet.stream()
                    .map(Holder::value)
                    .map(blockRegistry::getKey)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()))
                .orElse(List.of());
            ServerPlayNetworking.send(handler.player, new PathRulesPayload(pathFriendlySupportIds));
        });
    }
}
