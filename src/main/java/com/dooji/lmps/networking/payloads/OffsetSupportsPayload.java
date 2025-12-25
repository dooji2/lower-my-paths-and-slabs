package com.dooji.lmps.networking.payloads;

import com.dooji.lmps.LMPS;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OffsetSupportsPayload(List<String> supports) implements CustomPacketPayload {
    public static final Type<OffsetSupportsPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(LMPS.MOD_ID, "offset_supports"));

    public static final StreamCodec<FriendlyByteBuf, OffsetSupportsPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8),
        OffsetSupportsPayload::supports,
        OffsetSupportsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
