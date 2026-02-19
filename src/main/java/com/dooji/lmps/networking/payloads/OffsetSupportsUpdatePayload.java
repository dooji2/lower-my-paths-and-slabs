package com.dooji.lmps.networking.payloads;

import com.dooji.lmps.LMPS;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OffsetSupportsUpdatePayload(List<String> supports) implements CustomPacketPayload {
    public static final Type<OffsetSupportsUpdatePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(LMPS.MOD_ID, "offset_supports_update"));

    public static final StreamCodec<FriendlyByteBuf, OffsetSupportsUpdatePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.STRING_UTF8),
        OffsetSupportsUpdatePayload::supports,
        OffsetSupportsUpdatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
