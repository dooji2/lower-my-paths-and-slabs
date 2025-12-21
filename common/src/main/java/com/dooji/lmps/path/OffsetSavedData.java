package com.dooji.lmps.path;

import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

public final class OffsetSavedData extends SavedData {
    private static final String KEY = "Overrides";
    private final Long2BooleanOpenHashMap overrides = new Long2BooleanOpenHashMap();

    public static OffsetSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(OffsetSavedData::new, OffsetSavedData::load, DataFixTypes.SAVED_DATA_RANDOM_SEQUENCES), "lmps_offsets");
    }

    private static OffsetSavedData load(CompoundTag compoundTag, HolderLookup.Provider provider) {
        OffsetSavedData offsetSavedData = new OffsetSavedData();
        ListTag overridesTag = compoundTag.getList(KEY, Tag.TAG_COMPOUND);
        for (int i = 0; i < overridesTag.size(); i++) {
            CompoundTag entry = overridesTag.getCompound(i);
            offsetSavedData.overrides.put(entry.getLong("pos"), entry.getBoolean("value"));
        }

        return offsetSavedData;
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

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListTag overridesTag = new ListTag();
        for (Long2BooleanMap.Entry entry : overrides.long2BooleanEntrySet()) {
            CompoundTag override = new CompoundTag();
            override.putLong("pos", entry.getLongKey());
            override.putBoolean("value", entry.getBooleanValue());
            overridesTag.add(override);
        }

        compoundTag.put(KEY, overridesTag);
        return compoundTag;
    }

    public record ToggleResult(boolean enabled, Boolean override) {
    }
}
