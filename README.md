# Lower My Paths and Slabs!
A Minecraft mod that lets you place blocks on top of dirt paths and slabs, by lowering the model and collision of the block. Read more to find out about the Offset Tool!

**This mod doesn't modify anything permanently, therefore you can remove it or add it back at anytime!**

<img width="1920" height="1009" alt="2025-12-26_01 51 49" src="https://github.com/user-attachments/assets/52ebce96-4e0b-4a1b-b0a1-2d59f023f1be" />

There is an Offset Tool (found in the Tools & Utilities tab) which toggles lowering per-block. Offsets are off by default except for blocks sitting directly on a dirt path or bottom slab, and you can turn them on or off by right-clicking with the Offset Tool. By default, toggling offsets requires permission level 4 (configurable in `config/lmps_permissions.json` on the server).

To mark other blocks as supports, see `config/lmps_supports.json` on the server. For example, the configuration below would add support for red carpets (so you could offset blocks onto red carpets):
```
{
  "supports": [
    "minecraft:dirt_path",
    "#minecraft:slabs",
    "minecraft:red_carpet"
  ]
}
```

Multipart blocks only offset when every part sits above a path block or bottom slab.
