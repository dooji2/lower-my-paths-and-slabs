package com.dooji.lmps.networking.payloads;

import com.dooji.lmps.LMPS;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PathRulesPayload(List<ResourceLocation> pathRuleResourceLocations) implements CustomPacketPayload {
    public static final Type<PathRulesPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LMPS.MOD_ID, "path_rules"));

    public static final StreamCodec<FriendlyByteBuf, PathRulesPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public PathRulesPayload decode(FriendlyByteBuf buffer) {
            int entryCount = buffer.readVarInt();
            List<ResourceLocation> resourceLocations = new ArrayList<>(entryCount);
            for (int index = 0; index < entryCount; index++) {
                resourceLocations.add(buffer.readResourceLocation());
            }

            return new PathRulesPayload(resourceLocations);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, PathRulesPayload payload) {
            List<ResourceLocation> resourceLocations = payload.pathRuleResourceLocations();
            buffer.writeVarInt(resourceLocations.size());
            for (ResourceLocation resourceLocation : resourceLocations) {
                buffer.writeResourceLocation(resourceLocation);
            }
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
