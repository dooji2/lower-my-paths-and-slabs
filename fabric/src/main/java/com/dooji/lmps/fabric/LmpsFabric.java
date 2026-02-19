package com.dooji.lmps.fabric;

import com.dooji.lmps.LMPS;
import com.dooji.lmps.LMPSClient;
import com.dooji.lmps.networking.ClientPayloadRegistrar;
import com.dooji.lmps.networking.LmpsNetworking;
import com.dooji.lmps.networking.payloads.OffsetOverridesPayload;
import com.dooji.lmps.networking.payloads.OffsetSupportsPayload;
import com.dooji.lmps.networking.payloads.OffsetTogglePayload;
import com.dooji.lmps.networking.payloads.OffsetSupportsUpdatePayload;
import com.dooji.lmps.networking.payloads.PermissionLevelSyncPayload;
import com.dooji.lmps.networking.payloads.PermissionLevelUpdatePayload;
import com.dooji.lmps.path.OffsetSupports;
import com.dooji.lmps.permission.LmpsPermissions;
import com.dooji.lmps.platform.LmpsPlatform;
import com.dooji.lmps.registry.LmpsItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public class LmpsFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        LmpsPlatform.useConfigPathProvider(filename -> FabricLoader.getInstance().getConfigDir().resolve(filename));
        LmpsPlatform.useNetworkSender(ServerPlayNetworking::send);

        PayloadTypeRegistry.playS2C().register(OffsetOverridesPayload.TYPE, OffsetOverridesPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OffsetSupportsPayload.TYPE, OffsetSupportsPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(OffsetTogglePayload.TYPE, OffsetTogglePayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PermissionLevelSyncPayload.TYPE, PermissionLevelSyncPayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(PermissionLevelUpdatePayload.TYPE, PermissionLevelUpdatePayload.STREAM_CODEC);
        PayloadTypeRegistry.playC2S().register(OffsetSupportsUpdatePayload.TYPE, OffsetSupportsUpdatePayload.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PermissionLevelUpdatePayload.TYPE, (payload, context) ->
            context.server().execute(() -> handlePermissionLevelUpdate(context.player(), payload.permissionLevel()))
        );

        ServerPlayNetworking.registerGlobalReceiver(OffsetSupportsUpdatePayload.TYPE, (payload, context) ->
            context.server().execute(() -> handleSupportsUpdate(context.player(), payload.supports()))
        );

        LmpsItems.registerFabric();
        LMPS.onInitialize();

        registerCreativeTab();

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LmpsNetworking.sendSupports(handler.player);
            LmpsNetworking.sendSnapshot(handler.player);
            LmpsNetworking.sendPermissionLevel(handler.player);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((serverPlayer, serverLevel, serverLevel1) -> {
            LmpsNetworking.sendSupports(serverPlayer);
            LmpsNetworking.sendSnapshot(serverPlayer);
            LmpsNetworking.sendPermissionLevel(serverPlayer);
        });

        UseBlockCallback.EVENT.register((player, level, hand, blockHitResult) -> {
            if (!player.getItemInHand(hand).is(LmpsItems.offsetTool())) {
                return InteractionResult.PASS;
            }

            return LmpsItems.offsetTool().useOn(new UseOnContext(player, hand, blockHitResult));
        });
    }

    @Override
    public void onInitializeClient() {
        LMPSClient.onInitializeClient(buildClientRegistrar());
    }

    private static void handlePermissionLevelUpdate(ServerPlayer player, int permissionLevel) {
        if (!LmpsPermissions.canToggle(player)) {
            LmpsNetworking.sendPermissionLevel(player);
            return;
        }

        boolean changed = LmpsPermissions.setRequiredPermissionLevel(permissionLevel);
        if (changed && player.getServer() != null) {
            LmpsNetworking.broadcastPermissionLevel(player.getServer());
            return;
        }

        LmpsNetworking.sendPermissionLevel(player);
    }

    private static void handleSupportsUpdate(ServerPlayer player, java.util.List<String> supports) {
        OffsetSupports.load();

        if (!LmpsPermissions.canToggle(player)) {
            LmpsNetworking.sendSupports(player);
            return;
        }

        boolean changed = OffsetSupports.setEntries(supports);
        if (changed && player.getServer() != null) {
            LmpsNetworking.broadcastSupports(player.getServer());
            return;
        }

        LmpsNetworking.sendSupports(player);
    }

    private ClientPayloadRegistrar buildClientRegistrar() {
        return new ClientPayloadRegistrar() {
            @Override
            public <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> streamCodec, ClientPayloadListener<T> listener) {
                ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) ->
                    context.client().execute(() -> listener.handle(payload))
                );
            }
        };
    }

    private void registerCreativeTab() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(itemGroup -> itemGroup.accept(new ItemStack(LmpsItems.offsetTool())));
    }
}
