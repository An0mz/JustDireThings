# Changelog

## 1.2.0
- Added the Earthquake upgrade to the Eclipse Alloy Boots (it was implemented but no item could use it). Boots Ground Stomp cooldown is now 200 ticks, matching 1.21.1.
- Lingering potion arrows now use their own effect cloud: harmful effects never hit the shooter and beneficial effects only affect the shooter.
- Goo Soil auto-harvest now also harvests nether wart, sweet berry bushes and other bush-type crops, and pumpkin/melon fruit from attached stems.
- The Tool Settings screen now updates the potion canister slots when you click a different tool, instead of only showing them for the tool held when the screen opened.
- The Pocket Generator now also charges items in Curios slots when Curios is installed.
- Rotating a machine with the Ferricore Wrench now turns its default area with it.
- Machine filters now accept Creature Catchers for entity filtering, with optional NBT comparison.
- Machines now implement `Clearable`, so `/fill` and `/setblock` clear their contents.
- Ability tuning now matches 1.21.1: Cauterize Wounds on swords has a 1200 tick cooldown, and the Airburst and Eclipsegate abilities on the Blazejet, Voidshift and Eclipsegate wands use the 1.21.1 ranges.
- Just Dire Things fluids now use their own animated textures instead of tinted water.
- Armor now shows trims on its inventory icon.
- Crafting recipes that previously never unlocked in the recipe book now unlock when you pick up their ingredients.
- Added the `justdirethings:goorecipe_tier/1` to `/4` item tags.
- Empty Creature Catchers now stack to 64, and entities that cannot be saved can no longer be captured.
- The Creature Catcher now renders in hands, on the ground, in item frames and in the GUI the way it does in 1.21.1.
- The JEI goo recipe pages now read their goo tier lists from the `goorecipe_tier` tags.
- The Pocket Generator now burns a vanilla Coal Block at normal speed like 1.21.1 instead of 9 times faster. The extra speed is the `pocket_gen_vanilla_coal_block_multiplier` config option (default 1).
- The "Empty" label on fluid bar tooltips is now translatable.

## 1.1.13
- Time Wand, Polymorphic Wands and Advanced Portal Gun now only accept their own fluid (Time, Polymorphic and Portal Fluid). Before, right-clicking a fluid or piping one in could fill them with water, XP or any other fluid.

## 1.1.12
- Fixed dead Goo blocks not showing the floating "feed me" item on dedicated servers (feeding still worked, only the hint was missing).
- Time Crystal dimension settings (`time_crystal_custom_dimensions` and the three stage dimension lists) moved from the common config to the server config, matching 1.21.1. The server now syncs them to clients, so the growth particles always match the server's rules. **If you customized these, set them again in each world's `serverconfig/justdirethings-server.toml`.** To make Time Crystals grow in modded dimensions, enable `time_crystal_custom_dimensions` and add those dimension IDs to the stage lists.

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
