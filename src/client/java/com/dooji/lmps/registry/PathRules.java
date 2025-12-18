package com.dooji.lmps.registry;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public final class PathRules {
    private static final Set<ResourceLocation> FRIENDLY_PATH_SUPPORTS = new ObjectOpenHashSet<>();

    private PathRules() {
    }

    public static void replaceWith(Collection<ResourceLocation> resourceLocations) {
        FRIENDLY_PATH_SUPPORTS.clear();
        FRIENDLY_PATH_SUPPORTS.addAll(resourceLocations);
    }

    public static boolean isFriendly(BlockStateLike stateLike) {
        return isFriendly(stateLike.getBlock());
    }

    public static boolean isFriendly(Block block) {
        ResourceLocation resourceLocation = BuiltInRegistries.BLOCK.getKey(block);
        return resourceLocation != null && FRIENDLY_PATH_SUPPORTS.contains(resourceLocation);
    }

    public static List<ResourceLocation> snapshot() {
        return new ArrayList<>(FRIENDLY_PATH_SUPPORTS);
    }

    public interface BlockStateLike {
        Block getBlock();
    }
}
