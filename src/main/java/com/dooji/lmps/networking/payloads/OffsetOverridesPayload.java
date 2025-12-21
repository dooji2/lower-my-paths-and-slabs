package com.dooji.lmps.networking.payloads;

import com.dooji.lmps.LMPS;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record OffsetOverridesPayload(Long2BooleanMap overrides) implements CustomPacketPayload {
    public static final Type<OffsetOverridesPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(LMPS.MOD_ID, "offset_overrides"));

    public static final StreamCodec<FriendlyByteBuf, OffsetOverridesPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.collection(ArrayList::new, OffsetEntry.STREAM_CODEC),
        OffsetOverridesPayload::entries,
        OffsetOverridesPayload::fromEntries
    );

    private List<OffsetEntry> entries() {
        List<OffsetEntry> entries = new ArrayList<>(overrides.size());
        for (Long2BooleanMap.Entry entry : overrides.long2BooleanEntrySet()) {
            entries.add(new OffsetEntry(entry.getLongKey(), entry.getBooleanValue()));
        }

        return entries;
    }

    private static OffsetOverridesPayload fromEntries(List<OffsetEntry> entries) {
        Long2BooleanOpenHashMap overrides = new Long2BooleanOpenHashMap(entries.size());
        for (OffsetEntry entry : entries) {
            overrides.put(entry.position(), entry.enabled());
        }

        return new OffsetOverridesPayload(overrides);
    }

    public record OffsetEntry(long position, boolean enabled) {
        public static final StreamCodec<FriendlyByteBuf, OffsetEntry> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            OffsetEntry::position,
            ByteBufCodecs.BOOL,
            OffsetEntry::enabled,
            OffsetEntry::new
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
