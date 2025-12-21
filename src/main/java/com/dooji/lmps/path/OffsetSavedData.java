package com.dooji.lmps.path;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jetbrains.annotations.NotNull;

public final class OffsetSavedData extends SavedData {
    private static final String KEY = "Overrides";
    private static final Codec<Entry> ENTRY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.LONG.fieldOf("pos").forGetter(Entry::pos),
        Codec.BOOL.fieldOf("value").forGetter(Entry::value)
    ).apply(instance, Entry::new));

    private static final Codec<Long2BooleanOpenHashMap> OVERRIDES_CODEC = ENTRY_CODEC.listOf().xmap(
        entries -> {
            Long2BooleanOpenHashMap map = new Long2BooleanOpenHashMap(entries.size());
            for (Entry entry : entries) {
                map.put(entry.pos(), entry.value());
            }

            return map;
        },
        map -> {
            List<Entry> entries = new ArrayList<>(map.size());
            map.long2BooleanEntrySet().forEach(entry -> entries.add(new Entry(entry.getLongKey(), entry.getBooleanValue())));
            return entries;
        }
    );

    private static final Codec<OffsetSavedData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        OVERRIDES_CODEC.optionalFieldOf(KEY, new Long2BooleanOpenHashMap()).forGetter(data -> data.overrides)
    ).apply(instance, OffsetSavedData::fromOverrides));

    private static final SavedDataType<@NotNull OffsetSavedData> TYPE = new SavedDataType<>("lmps_offsets", OffsetSavedData::new, CODEC, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES);
    private final Long2BooleanOpenHashMap overrides = new Long2BooleanOpenHashMap();

    public static OffsetSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    private static OffsetSavedData fromOverrides(Long2BooleanOpenHashMap overrides) {
        OffsetSavedData data = new OffsetSavedData();
        data.overrides.putAll(overrides);
        return data;
    }

    private OffsetSavedData() {
    }

    public Boolean override(BlockPos position) {
        long key = position.asLong();
        if (!overrides.containsKey(key)) {
            return null;
        }

        return overrides.get(key);
    }

    public ToggleResult toggle(BlockPos position, boolean defaultEnabled) {
        Boolean override = override(position);
        boolean current = override != null ? override : defaultEnabled;
        boolean next = !current;
        setOverride(position, next, defaultEnabled);
        return new ToggleResult(next, override(position));
    }

    private void setOverride(BlockPos position, boolean value, boolean defaultEnabled) {
        long key = position.asLong();
        if (value == defaultEnabled) {
            overrides.remove(key);
        } else {
            overrides.put(key, value);
        }

        setDirty();
    }

    public Boolean apply(BlockPos position, boolean enabled, boolean defaultEnabled) {
        setOverride(position, enabled, defaultEnabled);
        return override(position);
    }

    public Long2BooleanMap snapshot() {
        return new Long2BooleanOpenHashMap(overrides);
    }

    public record ToggleResult(boolean enabled, Boolean override) {
    }

    private record Entry(long pos, boolean value) {
    }
}
