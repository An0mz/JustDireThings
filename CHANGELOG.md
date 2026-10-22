# Changelog

## 1.1.11
- Fixed the `justdirethings:paradox_deny` block tag failing to load because it referenced a nonexistent block ID (`paradox_machine` instead of `paradoxmachine`). This removes the "Couldn't load certain entries" error on world load and /reload.

## 1.1.10
- Fixed Advanced (T2) machines taking up to ~20 seconds to notice new blocks after their area was empty. The idle rescan delay was multiplied by the machine's tick speed; it is now a flat 1 second. Affects the Advanced Block Breaker, Block Placer, Clicker, Fluid Collector, and Fluid Placer.
- Block Breakers now save the tool's durability reliably after breaking a block, instead of it sometimes resetting after a relog or chunk reload.

## 1.1.9
- Fluid drop crafting (dropping items into fluids) now runs only for ticking item entities instead of scanning every entity each tick, reducing server overhead with many loose items.
- Fluids produced by fluid drop crafting now vaporize where they should (e.g. water in the Nether).

## 1.1.8
- Fixed Just Dire Things fluids not being recognized as liquids, so AE2 Annihilation Planes (and other mods that check for liquids) can now pick them up. Pistons now also break these fluids like vanilla water instead of pushing them.

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
