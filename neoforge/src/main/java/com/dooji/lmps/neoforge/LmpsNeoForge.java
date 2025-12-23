package com.dooji.lmps.neoforge;

import com.dooji.lmps.LMPS;
import com.dooji.lmps.LMPSClient;
import com.dooji.lmps.networking.ClientPayloadRegistrar;
import com.dooji.lmps.networking.LmpsNetworking;
import com.dooji.lmps.networking.payloads.OffsetOverridesPayload;
import com.dooji.lmps.networking.payloads.OffsetSupportsPayload;
import com.dooji.lmps.networking.payloads.OffsetTogglePayload;
import com.dooji.lmps.item.OffsetToolItem;
import com.dooji.lmps.platform.LmpsPlatform;
import com.dooji.lmps.registry.LmpsItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(LMPS.MOD_ID)
public class LmpsNeoForge {
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LMPS.MOD_ID);
    private static final DeferredHolder<Item, Item> OFFSET_TOOL_HOLDER = ITEMS.register("offset_tool", () -> new OffsetToolItem(new Item.Properties()));

    public LmpsNeoForge(IEventBus modEventBus) {
        LmpsPlatform.useConfigPathProvider(filename -> FMLPaths.CONFIGDIR.get().resolve(filename));
        LmpsPlatform.useNetworkSender((player, payload) -> PacketDistributor.sendToPlayer(player, payload));

        ITEMS.register(modEventBus);
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::addCreativeTabEntries);
        modEventBus.addListener(this::assignRegisteredItems);

        LMPS.onInitialize();

        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(this::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(this::onRightClickBlock);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(LMPS.MOD_ID);

        if (FMLEnvironment.dist.isClient()) {
            LMPSClient.onInitializeClient(buildClientRegistrar(registrar));
            return;
        }

        registrar.playToClient(OffsetOverridesPayload.TYPE, OffsetOverridesPayload.STREAM_CODEC, (payload, context) -> {
        });
        registrar.playToClient(OffsetSupportsPayload.TYPE, OffsetSupportsPayload.STREAM_CODEC, (payload, context) -> {
        });
        registrar.playToClient(OffsetTogglePayload.TYPE, OffsetTogglePayload.STREAM_CODEC, (payload, context) -> {
        });
    }

    private ClientPayloadRegistrar buildClientRegistrar(PayloadRegistrar registrar) {
        return new ClientPayloadRegistrar() {
            @Override
            public <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> streamCodec, ClientPayloadListener<T> listener) {
                registrar.playToClient(type, streamCodec, (payload, context) -> context.enqueueWork(() -> listener.handle(payload)));
            }
        };
    }

    private void addCreativeTabEntries(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(LmpsItems.offsetTool());
        }
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        LmpsNetworking.sendSupports(serverPlayer);
        LmpsNetworking.sendSnapshot(serverPlayer);
    }

    private void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        LmpsNetworking.sendSupports(serverPlayer);
        LmpsNetworking.sendSnapshot(serverPlayer);
    }

    private void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        if (!event.getItemStack().is(LmpsItems.offsetTool())) {
            return;
        }

        InteractionResult interactionResult = LmpsItems.offsetTool().useOn(new UseOnContext(serverPlayer, event.getHand(), event.getHitVec()));
        if (interactionResult.consumesAction()) {
            event.setCanceled(true);
            event.setCancellationResult(interactionResult);
        }
    }

    private void assignRegisteredItems(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.ITEM)) {
            LmpsItems.setOffsetTool(OFFSET_TOOL_HOLDER.get());
        }
    }
}
