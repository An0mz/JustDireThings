# Changelog

## 1.1.7
- Clickers no longer get their per-click cost multiplied by tick-accelerator mods, fixing a major source of server lag when Clickers are boosted. Controlled by the new `clicker_limit_tick_acceleration` config option (default `true`); set it to `false` to let tick accelerators fully speed up Clickers again.

## 1.1.6
- Fixed pocket generator not generating power

## 1.1.5
- Hammer upgrade on hoes can now be used correctly

## 1.1.4
- Added the Hammer upgrade to Blazegold, Celestigem, and Eclipse Alloy Hoes, letting them till a 3x3/5x5/7x7 area at once.

## 1.1.3
- Fixed a crash when running alongside Immersive Portals (mixin priority conflict on `Entity#canEnterPose`).

## 1.1.2
- Added tagged goo spreading recipes, with JEI support.
- Added Dropper T1/T2 pickup delay setting.
- Added a proper hide/show render toggle button.
- Goo now spawns dead when placed; feed it to activate.
- Reworked Potion Canister screen with an animated fluid bar.
- Phase now lets you see through walls instead of the block-inside overlay, and grants brief night vision while active.
- Fixed portal linking across dimensions and unloaded chunks.
- Fixed Pocket Generators accepting external charging.
- Fixed Fluid Canister not vaporizing fluids that should vaporize on placement.
- Fixed Unstable Portal Fluid being convertible to an infinite source block.
- Re-synced ja_jp, pt_br, ru_ru, uk_ua, zh_cn translations.
