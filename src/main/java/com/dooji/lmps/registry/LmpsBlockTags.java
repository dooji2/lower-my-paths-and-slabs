package com.dooji.lmps.registry;

import com.dooji.lmps.LMPS;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class LmpsBlockTags {
    public static final TagKey<Block> PATH_FRIENDLY_SUPPORTS = create("path_friendly_supports");

    private LmpsBlockTags() {
    }

    private static TagKey<Block> create(String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(LMPS.MOD_ID, path));
    }
}
