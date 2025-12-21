package com.dooji.lmps.registry;

import com.dooji.lmps.LMPS;
import com.dooji.lmps.item.OffsetToolItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class LmpsItems {
    public static Item OFFSET_TOOL;

    private LmpsItems() {
    }

    public static void registerFabric() {
        OFFSET_TOOL = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(LMPS.MOD_ID, "offset_tool"), new OffsetToolItem(new Item.Properties()));
    }

    public static Item offsetTool() {
        if (OFFSET_TOOL != null) {
            return OFFSET_TOOL;
        }

        throw new IllegalStateException("Offset tool not registered");
    }

    public static void setOffsetTool(Item item) {
        OFFSET_TOOL = item;
    }
}
