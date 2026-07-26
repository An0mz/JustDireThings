# Changelog — 1.1.1 → 1.1.2

## New Features
- **Tagged Goo Spreading Recipes**: Goo spreading recipes can now be defined by item tag instead of a single item, with full JEI display support.
- **Dropper Pickup Delay**: Dropper T1/T2 now have a configurable numeric pickup-delay setting.
- **Hide/Show Render Toggle**: Added a proper 2-state toggle button for hiding/showing tool rendering.
- **Goo Feeding Tooltip**: Goo block items now show a tooltip explaining they should be fed to a goo block (goo now correctly spawns dead on placement and must be fed to activate, rather than spawning alive).
- **Potion Canister Fluid Bar**: Reworked the Potion Canister screen to render an actual animated fluid bar (matching the 1.21.1 look) instead of plain text for potion type/amount.
- **Phase Ability — See-Through Walls**: While Phase is active, the "camera inside a solid block" screen overlay is now suppressed so you can see through the block instead of getting a zoomed-in texture cover. Phase also grants a short-refresh Night Vision effect while active.

## Fixes
- **Portal Linking Reliability**: Portals now store a direct partner reference (UUID, dimension, chunk) so their linked pair resolves correctly across dimensions and unloaded chunks, including for basic (non-advanced) one-shot portal placement. Previously, partner lookup relied on a full-server entity scan that could fail if the partner's chunk was unloaded.
- **Pocket Generator External Charging**: Pocket Generators no longer accept energy pushed in externally (chargers, pipes, etc.) — only their own internal fuel-burning can charge them.
- **Fluid Canister Vaporization**: Placing a fluid from a Fluid Canister now correctly vaporizes on placement when the fluid type specifies it (e.g. in the Nether), instead of always placing the fluid block.
- **Unstable Portal Fluid**: Unstable Portal Fluid can no longer convert into an infinite source block.
- **Translations**: Corrected and fully re-synced the ja_jp, pt_br, ru_ru, uk_ua, and zh_cn translation files against the current English reference.
