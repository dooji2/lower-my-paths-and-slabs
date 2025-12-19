package com.dooji.lmps.networking.payloads;

import com.dooji.lmps.LMPS;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PathRulesPayload(List<ResourceLocation> pathRuleResourceLocations) implements CustomPacketPayload {
    public static final Type<PathRulesPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LMPS.MOD_ID, "path_rules"));

    public static final StreamCodec<FriendlyByteBuf, PathRulesPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, ResourceLocation.STREAM_CODEC),
        PathRulesPayload::pathRuleResourceLocations,
        PathRulesPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
