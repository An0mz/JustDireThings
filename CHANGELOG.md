# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## 1.2.0 - 2026-11-20

### Added
- Earthquake upgrade for the Eclipse Alloy Boots (it was implemented, but no item could use it).
- Machine filters now accept Creature Catchers for entity filtering, with optional NBT comparison.
- The Pocket Generator now also charges items in Curios slots when Curios is installed.
- `pocket_gen_vanilla_coal_block_multiplier` config option (default 1): extra burn speed when a vanilla Coal Block is used as fuel in the Pocket Generator.
- `justdirethings:goorecipe_tier/1` to `/4` item tags.
- Armor now shows trims on its inventory icon.

### Changed
- Boots Ground Stomp cooldown is now 200 ticks, matching 1.21.1.
- Ability tuning now matches 1.21.1: Cauterize Wounds on swords has a 1200 tick cooldown, and the Airburst and Eclipsegate abilities on the Blazejet, Voidshift and Eclipsegate wands use the 1.21.1 ranges.
- Lingering potion arrows now use their own effect cloud: harmful effects never hit the shooter and beneficial effects only affect the shooter.
- Goo Soil auto-harvest now also harvests nether wart, sweet berry bushes and other bush-type crops, and pumpkin/melon fruit from attached stems.
- Rotating a machine with the Ferricore Wrench now turns its default area with it.
- Machines now implement `Clearable`, so `/fill` and `/setblock` clear their contents.
- Just Dire Things fluids now use their own animated textures instead of tinted water.
- Empty Creature Catchers now stack to 64.
- The Creature Catcher now renders in hands, on the ground, in item frames and in the GUI the way it does in 1.21.1.
- The JEI goo recipe pages now read their goo tier lists from the `goorecipe_tier` tags.
- The Pocket Generator now burns a vanilla Coal Block at normal speed like 1.21.1 instead of 9 times faster.
- The network protocol version was bumped, so 1.2.0 clients and servers can no longer connect to older versions.

### Fixed
- The Tool Settings screen now updates the potion canister slots when you click a different tool, instead of only showing them for the tool held when the screen opened.
- Crafting recipes that previously never unlocked in the recipe book now unlock when you pick up their ingredients.
- Entities that cannot be saved can no longer be captured by the Creature Catcher.
- The Dropper pickup delay field no longer covers the item slots on the Simple and Advanced Dropper screens.
- The "Empty" label on fluid bar tooltips is now translatable.

## 1.1.13 - 2026-11-18

### Fixed
- Time Wand, Polymorphic Wands and Advanced Portal Gun now only accept their own fluid (Time, Polymorphic and Portal Fluid). Before, right-clicking a fluid or piping one in could fill them with water, XP or any other fluid.

## 1.1.12 - 2026-11-09

### Changed
- Time Crystal dimension settings (`time_crystal_custom_dimensions` and the three stage dimension lists) moved from the common config to the server config, matching 1.21.1. The server now syncs them to clients, so the growth particles always match the server's rules. **If you customized these, set them again in each world's `serverconfig/justdirethings-server.toml`.** To make Time Crystals grow in modded dimensions, enable `time_crystal_custom_dimensions` and add those dimension IDs to the stage lists.

### Fixed
- Dead Goo blocks not showing the floating "feed me" item on dedicated servers (feeding still worked, only the hint was missing).

## 1.1.11 - 2026-10-22

### Fixed
- The `justdirethings:paradox_deny` block tag failing to load because it referenced a nonexistent block ID (`paradox_machine` instead of `paradoxmachine`). This removes the "Couldn't load certain entries" error on world load and /reload.

## 1.1.10 - 2026-10-11

### Fixed
- Advanced (T2) machines taking up to ~20 seconds to notice new blocks after their area was empty. The idle rescan delay was multiplied by the machine's tick speed; it is now a flat 1 second. Affects the Advanced Block Breaker, Block Placer, Clicker, Fluid Collector, and Fluid Placer.
- Block Breakers now save the tool's durability reliably after breaking a block, instead of it sometimes resetting after a relog or chunk reload.

## 1.1.9 - 2026-10-01

### Changed
- Fluid drop crafting (dropping items into fluids) now runs only for ticking item entities instead of scanning every entity each tick, reducing server overhead with many loose items.

### Fixed
- Fluids produced by fluid drop crafting now vaporize where they should (e.g. water in the Nether).

## 1.1.8 - 2026-10-01

### Fixed
- Just Dire Things fluids not being recognized as liquids, so AE2 Annihilation Planes (and other mods that check for liquids) can now pick them up. Pistons now also break these fluids like vanilla water instead of pushing them.

## 1.1.7 - 2026-08-25

### Added
- `clicker_limit_tick_acceleration` config option (default `true`); set it to `false` to let tick accelerators fully speed up Clickers again.

### Changed
- Clickers no longer get their per-click cost multiplied by tick-accelerator mods, fixing a major source of server lag when Clickers are boosted.

## 1.1.6 - 2026-08-12

### Fixed
- Pocket generator not generating power.

## 1.1.5 - 2026-08-08

### Fixed
- Hammer upgrade on hoes can now be used correctly.

## 1.1.4

### Added
- Hammer upgrade for Blazegold, Celestigem, and Eclipse Alloy Hoes, letting them till a 3x3/5x5/7x7 area at once.

## 1.1.3 - 2026-07-27

### Fixed
- A crash when running alongside Immersive Portals (mixin priority conflict on `Entity#canEnterPose`).

## 1.1.2 - 2026-07-24

### Added
- Tagged goo spreading recipes, with JEI support.
- Dropper T1/T2 pickup delay setting.
- A proper hide/show render toggle button.

### Changed
- Goo now spawns dead when placed; feed it to activate.
- Reworked Potion Canister screen with an animated fluid bar.
- Phase now lets you see through walls instead of the block-inside overlay, and grants brief night vision while active.
- Re-synced ja_jp, pt_br, ru_ru, uk_ua, zh_cn translations.

### Fixed
- Portal linking across dimensions and unloaded chunks.
- Pocket Generators accepting external charging.
- Fluid Canister not vaporizing fluids that should vaporize on placement.
- Unstable Portal Fluid being convertible to an infinite source block.
