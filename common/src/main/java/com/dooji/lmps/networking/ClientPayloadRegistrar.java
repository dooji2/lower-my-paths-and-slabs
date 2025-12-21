package com.dooji.lmps.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@FunctionalInterface
public interface ClientPayloadRegistrar {
    <T extends CustomPacketPayload> void register(CustomPacketPayload.Type<T> type, StreamCodec<FriendlyByteBuf, T> streamCodec, ClientPayloadListener<T> listener);

    @FunctionalInterface
    interface ClientPayloadListener<T> {
        void handle(T payload);
    }
}
