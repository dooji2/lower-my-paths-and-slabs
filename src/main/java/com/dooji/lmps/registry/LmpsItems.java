package com.dooji.lmps.registry;

import com.dooji.lmps.LMPS;
import com.dooji.lmps.item.OffsetToolItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class LmpsItems {
    public static final Item OFFSET_TOOL = new OffsetToolItem(new Item.Properties());

    private LmpsItems() {
    }

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(LMPS.MOD_ID, "offset_tool"), OFFSET_TOOL);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(itemGroup -> itemGroup.accept(new ItemStack(OFFSET_TOOL)));
    }
}
