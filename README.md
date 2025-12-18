# Lower My Paths and Slabs!
A **_cursed_** Minecraft mod that lets you place blocks on top of dirt paths without turning them back to dirt, and lowers the block model & collision! It also works with slabs (so you can have a bed on a bottom half slab...).

**This mod doesn't modify anything permanently, therefore you can remove it or add it back at anytime!**

<img width="1920" height="1009" alt="2025-12-18_17 09 19" src="https://github.com/user-attachments/assets/6b81f495-112c-4c0e-97a7-2f46cbf7f9b5" />

**Known Issues (to be fixed):**
- Raycast is off
- Blocks above a block lowered onto a slab should not also be lowered (not really a bug, but it really depends on those who use this mod, let me know)
- Doesn't work with chests (the model itself isn't lowered); enchantment table book is not lowered

## To make other blocks be lowered:
- Read this to know how datapacks work first: https://minecraft.wiki/w/Tutorial:Creating_a_data_pack
- Create `data/lmps/tags/block/path_friendly_supports.json` in your datapack, then list the blocks you want, such as:
```
{
  "replace": false,
  "values": [
    "#minecraft:fences",
    "#minecraft:fence_gates",
    "#minecraft:walls",
    "#minecraft:wool_carpets",
    "minecraft:anvil",
    "minecraft:chipped_anvil",
    "minecraft:damaged_anvil"
  ]
}
```
