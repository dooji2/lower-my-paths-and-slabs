package com.dooji.lmps.path;

import com.dooji.lmps.LMPS;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class OffsetSupports {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<String> DEFAULT_SUPPORTS = List.of("minecraft:dirt_path", "#minecraft:slabs");

    private static List<String> configuredEntries = DEFAULT_SUPPORTS;
    private static List<Entry> compiledEntries = compile(DEFAULT_SUPPORTS);

    private OffsetSupports() {
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("lmps_supports.json");
        if (Files.exists(configPath)) {
            Config config = read(configPath);
            if (config != null && !config.supports().isEmpty()) {
                apply(config.supports());
                return;
            }
        }

        writeDefault(configPath);
        apply(DEFAULT_SUPPORTS);
    }

    public static void applyFromNetwork(List<String> supports) {
        apply(supports);
    }

    public static List<String> currentEntries() {
        return new ArrayList<>(configuredEntries);
    }

    public static boolean isSupported(BlockState blockState) {
        List<Entry> snapshot = compiledEntries;

        for (Entry entry : snapshot) {
            if (entry.matches(blockState)) {
                return true;
            }
        }

        return false;
    }

    private static void apply(List<String> supports) {
        configuredEntries = List.copyOf(supports);
        compiledEntries = compile(configuredEntries);
    }

    private static Config read(Path configPath) {
        try (Reader reader = Files.newBufferedReader(configPath)) {
            JsonObject jsonObject = GSON.fromJson(reader, JsonObject.class);
            if (jsonObject == null || !jsonObject.has("supports")) {
                return null;
            }

            JsonArray supportsArray = jsonObject.getAsJsonArray("supports");
            List<String> supports = new ArrayList<>();
            supportsArray.forEach(element -> supports.add(element.getAsString()));
            return new Config(supports);
        } catch (Exception exception) {
            LMPS.LOGGER.warn("Failed to load lmps supports config, using defaults", exception);
            return null;
        }
    }

    private static void writeDefault(Path configPath) {
        try (Writer writer = Files.newBufferedWriter(configPath)) {
            JsonObject jsonObject = new JsonObject();
            JsonArray supportsArray = new JsonArray();
            DEFAULT_SUPPORTS.forEach(supportsArray::add);
            jsonObject.add("supports", supportsArray);
            GSON.toJson(jsonObject, writer);
        } catch (IOException exception) {
            LMPS.LOGGER.warn("Failed to write default lmps supports config", exception);
        }
    }

    private static List<Entry> compile(List<String> supports) {
        Set<String> unique = new LinkedHashSet<>(supports);
        List<Entry> entries = new ArrayList<>(unique.size());
        for (String raw : unique) {
            Entry entry = parse(raw);
            if (entry != null) {
                entries.add(entry);
            }
        }

        return entries;
    }

    private static Entry parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            if (raw.startsWith("#")) {
                Identifier id = Identifier.parse(raw.substring(1));
                TagKey<Block> tagKey = TagKey.create(Registries.BLOCK, id);
                return new Entry(null, tagKey);
            }

            Identifier id = Identifier.parse(raw);
            return BuiltInRegistries.BLOCK.getOptional(id)
                .map(block -> new Entry(block, null))
                .orElseGet(() -> {
                    LMPS.LOGGER.warn("Ignoring unknown block id in supports config: {}", id);
                    return null;
                });
        } catch (Exception exception) {
            LMPS.LOGGER.warn("Ignoring invalid entry in supports config: {}", raw, exception);
            return null;
        }
    }

    private record Entry(Block block, TagKey<Block> tag) {
        Entry {
            if (block == null && tag == null) {
                throw new IllegalArgumentException("Entry must have either block or tag");
            }
        }

        boolean matches(BlockState blockState) {
            try {
                if (block != null) {
                    return blockState.is(block);
                }

                return blockState.is(Objects.requireNonNull(tag));
            } catch (IllegalStateException exception) {
                return false;
            }
        }
    }

    private record Config(List<String> supports) {
    }
}
