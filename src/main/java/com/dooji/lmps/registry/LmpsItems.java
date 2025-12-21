package com.dooji.lmps.registry;

import com.dooji.lmps.LMPS;
import com.dooji.lmps.item.OffsetToolItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class LmpsItems {
    private static final ResourceKey<Item> OFFSET_TOOL_KEY = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LMPS.MOD_ID, "offset_tool"));
    public static final Item OFFSET_TOOL = new OffsetToolItem(OFFSET_TOOL_KEY, new Item.Properties());

    private LmpsItems() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, OFFSET_TOOL_KEY, OFFSET_TOOL);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(itemGroup -> itemGroup.accept(new ItemStack(OFFSET_TOOL)));
    }
}
