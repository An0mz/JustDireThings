package com.direwolf20.justdirethings.datagen;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.items.interfaces.Ability;
import com.direwolf20.justdirethings.setup.ModSetup;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.data.PackOutput;

public class JustDireLanguageProvider extends net.minecraftforge.common.data.LanguageProvider {
	public JustDireLanguageProvider(PackOutput output, String locale) {
		super(output, JustDireThings.MODID, locale);
	}

	@Override
	protected void addTranslations() {
		add("itemGroup." + ModSetup.TAB_JUSTDIRETHINGS, "Just Dire Things");

		// Blocks
		add(Registration.GooSoil_Tier1.get(), "Primogel Soil");
		add(Registration.GooSoil_Tier2.get(), "Blazebloom Soil");
		add(Registration.GooSoil_Tier3.get(), "VoidShimmer Soil");
		add(Registration.GooSoil_Tier4.get(), "Shadowpulse Soil");
		add(Registration.GooBlock_Tier1.get(), "Primogel Goo");
		add(Registration.GooBlock_Tier2.get(), "Blazebloom Goo");
		add(Registration.GooBlock_Tier3.get(), "VoidShimmer Goo");
		add(Registration.GooBlock_Tier4.get(), "Shadowpulse Goo");
		add(Registration.ItemCollector.get(), "Item Collector");
		add(Registration.BlockBreakerT1.get(), "Simple Block Breaker");
		add(Registration.BlockBreakerT2.get(), "Advanced Block Breaker");
		add(Registration.BlockPlacerT1.get(), "Simple Block Placer");
		add(Registration.BlockPlacerT2.get(), "Advanced Block Placer");
		add(Registration.ClickerT1.get(), "Simple Clicker");
		add(Registration.ClickerT2.get(), "Advanced Clicker");
		add(Registration.SensorT1.get(), "Simple Sensor");
		add(Registration.SensorT2.get(), "Advanced Sensor");
		add(Registration.DropperT1.get(), "Simple Dropper");
		add(Registration.DropperT2.get(), "Advanced Dropper");
		add(Registration.GeneratorT1.get(), "Simple Coal Generator");
		add(Registration.EnergyTransmitter.get(), "Energy Transmitter");
		add(Registration.BlockSwapperT1.get(), "Simple Swapper");
		add(Registration.BlockSwapperT2.get(), "Advanced Swapper");
		add(Registration.PlayerAccessor.get(), "Player Accessor");
		add(Registration.ExperienceHolder.get(), "Experience Holder");
		add(Registration.FluidCollectorT1.get(), "Simple Fluid Collector");
		add(Registration.FluidCollectorT2.get(), "Advanced Fluid Collector");
		add(Registration.FluidPlacerT1.get(), "Simple Fluid Placer");
		add(Registration.FluidPlacerT2.get(), "Advanced Fluid Placer");
		add(Registration.GeneratorFluidT1.get(), "Simple Fuel Generator");
		add(Registration.InventoryHolder.get(), "Inventory Holder");
		add(Registration.ParadoxMachine.get(), "Paradox Machine");
		add(Registration.EclipseGateBlock.get(), "Eclipse Gate");
		add(Registration.TimeCrystalBlock.get(), "Time Crystal Block");
		add(Registration.TimeCrystalBuddingBlock.get(), "Budding Time Crystal Block");
		add(Registration.TimeCrystalCluster.get(), "Time Crystal Cluster");
		add(Registration.TimeCrystalCluster_Small.get(), "Small Time Crystal Cluster");
		add(Registration.TimeCrystalCluster_Medium.get(), "Medium Time Crystal Cluster");
		add(Registration.TimeCrystalCluster_Large.get(), "Large Time Crystal Cluster");
		add(Registration.CharcoalBlock.get(), "Charcoal Block");

		// Resources
		add(Registration.FerricoreBlock.get(), "Ferricore Block");
		add(Registration.RawFerricoreOre.get(), "Raw Ferricore Ore");
		add(Registration.BlazeGoldBlock.get(), "Blazegold Block");
		add(Registration.RawBlazegoldOre.get(), "Raw Blazegold Ore");
		add(Registration.RawCelestigemOre.get(), "Raw Celestigem Ore");
		add(Registration.CelestigemBlock.get(), "Celestigem Block");
		add(Registration.RawEclipseAlloyOre.get(), "Raw Eclipse Alloy Ore");
		add(Registration.EclipseAlloyBlock.get(), "Eclipse Alloy Block");
		add(Registration.RawCoal_T1.get(), "Raw Primal Coal");
		add(Registration.CoalBlock_T1.get(), "Primal Coal Block");
		add(Registration.RawCoal_T2.get(), "Raw Blaze Ember");
		add(Registration.CoalBlock_T2.get(), "Blaze Ember Block");
		add(Registration.RawCoal_T3.get(), "Raw Voidflame Coal");
		add(Registration.CoalBlock_T3.get(), "Voidflame Coal Block");
		add(Registration.RawCoal_T4.get(), "Raw Eclipse Ember");
		add(Registration.CoalBlock_T4.get(), "Eclipse Ember Block");

		// Items
		add(Registration.Fuel_Canister.get(), "Fuel Canister");
		add(Registration.Pocket_Generator.get(), "Pocket Generator");
		add(Registration.TotemOfDeathRecall.get(), "Totem of Death Recall");
		add(Registration.BlazejetWand.get(), "Blazejet Wand");
		add(Registration.VoidshiftWand.get(), "Voidshift Wand");
		add(Registration.EclipsegateWand.get(), "Eclipsegate Wand");
		add(Registration.CreatureCatcher.get(), "Creature Catcher");

		// Tools
		add(Registration.FerricoreSword.get(), "Ferricore Sword");
		add(Registration.FerricorePickaxe.get(), "Ferricore Pickaxe");
		add(Registration.FerricoreShovel.get(), "Ferricore Shovel");
		add(Registration.FerricoreAxe.get(), "Ferricore Axe");
		add(Registration.FerricoreHoe.get(), "Ferricore Hoe");
		add(Registration.BlazegoldSword.get(), "Blazegold Sword");
		add(Registration.BlazegoldPickaxe.get(), "Blazegold Pickaxe");
		add(Registration.BlazegoldShovel.get(), "Blazegold Shovel");
		add(Registration.BlazegoldAxe.get(), "Blazegold Axe");
		add(Registration.BlazegoldHoe.get(), "Blazegold Hoe");
		add(Registration.CelestigemSword.get(), "Celestigem Sword");
		add(Registration.CelestigemPickaxe.get(), "Celestigem Pickaxe");
		add(Registration.CelestigemShovel.get(), "Celestigem Shovel");
		add(Registration.CelestigemAxe.get(), "Celestigem Axe");
		add(Registration.CelestigemHoe.get(), "Celestigem Hoe");
		add(Registration.EclipseAlloySword.get(), "Eclipse Alloy Sword");
		add(Registration.EclipseAlloyPickaxe.get(), "Eclipse Alloy Pickaxe");
		add(Registration.EclipseAlloyShovel.get(), "Eclipse Alloy Shovel");
		add(Registration.EclipseAlloyAxe.get(), "Eclipse Alloy Axe");
		add(Registration.EclipseAlloyHoe.get(), "Eclipse Alloy Hoe");
		add(Registration.CelestigemPaxel.get(), "Celestigem Paxel");
		add(Registration.EclipseAlloyPaxel.get(), "Eclipse Alloy Paxel");
		add(Registration.FerricoreWrench.get(), "Ferricore Wrench");

		// Armors
		add(Registration.FerricoreBoots.get(), "Ferricore Boots");
		add(Registration.FerricoreChestplate.get(), "Ferricore Chestplate");
		add(Registration.FerricoreLeggings.get(), "Ferricore Leggings");
		add(Registration.FerricoreHelmet.get(), "Ferricore Helmet");
		add(Registration.BlazegoldBoots.get(), "Blazegold Boots");
		add(Registration.BlazegoldChestplate.get(), "Blazegold Chestplate");
		add(Registration.BlazegoldLeggings.get(), "Blazegold Leggings");
		add(Registration.BlazegoldHelmet.get(), "Blazegold Helmet");
		add(Registration.CelestigemBoots.get(), "Celestigem Boots");
		add(Registration.CelestigemChestplate.get(), "Celestigem Chestplate");
		add(Registration.CelestigemLeggings.get(), "Celestigem Leggings");
		add(Registration.CelestigemHelmet.get(), "Celestigem Helmet");
		add(Registration.EclipseAlloyBoots.get(), "Eclipse Alloy Boots");
		add(Registration.EclipseAlloyChestplate.get(), "Eclipse Alloy Chestplate");
		add(Registration.EclipseAlloyLeggings.get(), "Eclipse Alloy Leggings");
		add(Registration.EclipseAlloyHelmet.get(), "Eclipse Alloy Helmet");

		// Bows
		add(Registration.FerricoreBow.get(), "Ferricore Bow");
		add(Registration.BlazegoldBow.get(), "Blazegold Bow");
		add(Registration.CelestigemBow.get(), "Celestigem Bow");
		add(Registration.EclipseAlloyBow.get(), "Eclipse Alloy Bow");

		// Resources
		add(Registration.FerricoreIngot.get(), "Ferricore Ingot");
		add(Registration.RawFerricore.get(), "Raw Ferricore");
		add(Registration.BlazegoldIngot.get(), "Blazegold Ingot");
		add(Registration.RawBlazegold.get(), "Raw Blazegold");
		add(Registration.Celestigem.get(), "Celestigem");
		add(Registration.EclipseAlloyIngot.get(), "Eclipse Alloy Ingot");
		add(Registration.RawEclipseAlloy.get(), "Raw Eclipse Alloy");
		add(Registration.Coal_T1.get(), "Primal Coal");
		add(Registration.Coal_T2.get(), "Blaze Ember");
		add(Registration.Coal_T3.get(), "Voidflame Coal");
		add(Registration.Coal_T4.get(), "Eclipse Ember");
		add(Registration.TimeCrystal.get(), "Time Crystal");

		// New Items
		add(Registration.FluidCanister.get(), "Fluid Canister");
		add(Registration.PotionCanister.get(), "Potion Canister");
		add(Registration.PolymorphicCatalyst.get(), "Polymorphic Catalyst");
		add(Registration.PortalFluidCatalyst.get(), "Portal Fluid Catalyst");
		add(Registration.MachineSettingsCopier.get(), "Machine Settings Copier");
		add(Registration.PolymorphicWand.get(), "Polymorphic Wand");

		// Misc
		add("justdirethings.missingupgrade", " (Missing)");
		add("justdirethings.shiftmoreinfo", "Hold Shift for details");
		add("justdirethings.presshotkey", "<Press %s>");
		add("justdirethings.enabled", "Enabled");
		add("justdirethings.disabled", "Disabled");
		add("justdirethings.fuelcanisteramt", "Cook time (ticks): %d");
		add("justdirethings.fuelcanisteramtstack", "Stack Cook time (ticks): %d");
		add("justdirethings.fuelcanisteritemsamt", "Fuel Amount: %f");
		add("justdirethings.fuelcanisteritemsamtstack", "Stack Fuel Amount: %f");
		add("justdirethings.pocketgeneratorburntime", "Burn Time: %f / %f");
		add("justdirethings.pocketgeneratorfuelstack", "Fuel: %f %s");
		add("justdirethings.pocketgeneratornofuel", "Fuel Empty");
		add("justdirethings.festored", "Energy: %s / %s");
		add("justdirethings.boundto", "Bound to: %s:%s");
		add("justdirethings.boundto-missing", "Bound to (MISSING BLOCK): %s:%s");
		add("justdirethings.unbound", " -Not Bound");
		add("justdirethings.bindfailed", "Binding Failed");
		add("justdirethings.bindremoved", "Binding Removed");
		add("justdirethings.unbound-screen", "Not Bound");
		add("justdirethings.bound-key", "Bound to: %s");
		add("justdirethings.bound-mouse", "Mouse Button: %s");
		add("justdirethings.boundside", " -Bound Side: ");
		add("justdirethings.creature", "Creature: ");
		add("justdirethings.ability", "Ability: %s - %s");

		// Keys
		add("justdirethings.key.category", "Just Dire Things");
		add("justdirethings.key.toggle_tool", "Toggle Tool Abilities");
		add("justdirethings.key.tool_ui", "Open Tool UI");

		// Abilities
		add(Ability.MOBSCANNER.getLocalization(), "Mob Scanner");
		add(Ability.ORESCANNER.getLocalization(), "Ore Scanner");
		add(Ability.OREMINER.getLocalization(), "Ore Miner");
		add(Ability.LAWNMOWER.getLocalization(), "Lawnmower");
		add(Ability.SKYSWEEPER.getLocalization(), "Sky Sweeper");
		add(Ability.TREEFELLER.getLocalization(), "Tree Feller");
		add(Ability.LEAFBREAKER.getLocalization(), "Leaf Breaker");
		add(Ability.SMELTER.getLocalization(), "Auto Smelter");
		add(Ability.SMOKER.getLocalization(), "Auto Smoker");
		add(Ability.LAVAREPAIR.getLocalization(), "Lava Repair");
		add(Ability.POLYMORPH_RANDOM.getLocalization(), "Polymorph Random");
		add(Ability.POLYMORPH_TARGET.getLocalization(), "Polymorph Target");
		add(Ability.CAUTERIZEWOUNDS.getLocalization(), "Cauterize Wounds");
		add(Ability.HAMMER.getLocalization(), "Hammer");
		add(Ability.HAMMER.getLocalization() + "_off", "Hammer: Disabled");
		add(Ability.HAMMER.getLocalization() + "_3", "Hammer: 3x3");
		add(Ability.HAMMER.getLocalization() + "_5", "Hammer: 5x5");
		add(Ability.HAMMER.getLocalization() + "_7", "Hammer: 7x7");
		add(Ability.OREXRAY.getLocalization(), "X-Ray");
		add(Ability.DROPTELEPORT.getLocalization(), "Drops Teleporter");
		add(Ability.GLOWING.getLocalization(), "Mob X-Ray");
		add(Ability.INSTABREAK.getLocalization(), "Instant Break");
		add(Ability.AIRBURST.getLocalization(), "Air Burst");
		add(Ability.VOIDSHIFT.getLocalization(), "Void Shift");
		add(Ability.ECLIPSEGATE.getLocalization(), "Eclipse Gate");
		add(Ability.RUNSPEED.getLocalization(), "Run Speed");
		add(Ability.WALKSPEED.getLocalization(), "Walk Speed");
		add(Ability.STEPHEIGHT.getLocalization(), "Step Assist");
		add(Ability.JUMPBOOST.getLocalization(), "Jump Boost");
		add(Ability.MINDFOG.getLocalization(), "Mind Fog");
		add(Ability.INVULNERABILITY.getLocalization(), "Invulnerability");
		add(Ability.POTIONARROW.getLocalization(), "Potion Arrow");
		add(Ability.SPLASH.getLocalization(), "Splash Arrow");
		add(Ability.LINGERING.getLocalization(), "Lingering Arrow");
		add(Ability.HOMING.getLocalization(), "Homing Arrow");
		add(Ability.EPICARROW.getLocalization(), "Epic Arrow");
		add(Ability.SWIMSPEED.getLocalization(), "Swim Speed");
		add(Ability.GROUNDSTOMP.getLocalization(), "Ground Stomp");
		add(Ability.EXTINGUISH.getLocalization(), "Extinguish");
		add(Ability.STUPEFY.getLocalization(), "Stupefy");
		add(Ability.NEGATEFALLDAMAGE.getLocalization(), "Negate Fall Damage");
		add(Ability.NIGHTVISION.getLocalization(), "Night Vision");
		add(Ability.ELYTRA.getLocalization(), "Elytra Flight");
		add(Ability.DECOY.getLocalization(), "Decoy");
		add(Ability.WATERBREATHING.getLocalization(), "Water Breathing");
		add(Ability.DEATHPROTECTION.getLocalization(), "Death Protection");
		add(Ability.DEBUFFREMOVER.getLocalization(), "Debuff Remover");
		add(Ability.EARTHQUAKE.getLocalization(), "Earthquake");
		add(Ability.NOAI.getLocalization(), "Mental Obliteration");
		add(Ability.FLIGHT.getLocalization(), "Flight");
		add(Ability.LAVAIMMUNITY.getLocalization(), "Lava Immunity");
		add(Ability.PHASE.getLocalization(), "Phase");
		add(Ability.TIMEPROTECTION.getLocalization(), "Time Protection");
		add("entity.justdirethings.justdire_arrow", "JustDire Arrow");

		// Time Wand & Portal Guns
		add(Registration.TimeWand.get(), "Time Wand");
		add(Registration.PortalGun.get(), "Portal Gun");
		add(Registration.PortalGunV2.get(), "Advanced Portal Gun");
		add(Registration.PolymorphicWandV2.get(), "Advanced Polymorphic Wand");
		add("entity.justdirethings.time_wand_entity", "Time Wand Effect");
		add("entity.justdirethings.portal_projectile", "Portal Projectile");
		add("entity.justdirethings.portal_entity", "DirePortal");
		add("justdirethings.lowenergy", "Insufficient Energy");
		add("justdirethings.lowtimefluid", "Insufficient Time Fluid");
		add("justdirethings.lowportalfluid", "Insufficient Portal Fluid");
		add("justdirethings.timefluidamt", "Time Fluid: %s / %s");
		add("justdirethings.portalfluidamt", "Portal Fluid: %s / %s");

		// GUI
		add("justdirethings.screen.energy", "Energy: %s/%s FE");
		add("justdirethings.screen.energycost", "Energy Cost: %s");
		add("justdirethings.screen.fluid", "Fluid: %s (%s/%s mB)");
		add("justdirethings.screen.paradoxfluidcost", "Fluid Cost: %s mB");
		add("justdirethings.screen.paradoxenergycost", "Energy Cost: %s FE");
		add("justdirethings.paradoxenergy", "Paradox Energy: %s/%s");
		add("justdirethings.screen.fepertick", "FE/T: %s");
		add("justdirethings.screen.no_fuel", "Fuel source empty");
		add("justdirethings.screen.burn_time", "Burn time left: %ss");
		add("justdirethings.screen.ignored", "Ignored");
		add("justdirethings.screen.low", "Low");
		add("justdirethings.screen.high", "High");
		add("justdirethings.screen.pulse", "Pulse");
		add("justdirethings.screen.allowlist", "Allow List");
		add("justdirethings.screen.denylist", "Deny List");
		add("justdirethings.screen.renderarea", "Render Area");
		add("justdirethings.screen.comparenbt", "Compare NBT");
		add("justdirethings.screen.direction-down", "Down");
		add("justdirethings.screen.direction-up", "Up");
		add("justdirethings.screen.direction-north", "North");
		add("justdirethings.screen.direction-south", "South");
		add("justdirethings.screen.direction-west", "West");
		add("justdirethings.screen.direction-east", "East");
		add("justdirethings.screen.direction-none", "None");
		add("justdirethings.screen.filter-block", "Filter: Block");
		add("justdirethings.screen.filter-item", "Filter: Item");
		add("justdirethings.screen.tickspeed", "Speed (Ticks)");
		add("justdirethings.screen.click-right", "Right Click");
		add("justdirethings.screen.click-left", "Left Click");
		add("justdirethings.screen.click-custom", "Custom Binding");
		add("justdirethings.screen.setbinding", "Set Binding");
		add("justdirethings.screen.requireequipped", "Activate if Equipped");
		add("justdirethings.screen.notrequireequipped", "Activate from Inventory");
		add("justdirethings.screen.target-block", "Target Blocks");
		add("justdirethings.screen.target-noblock", "Ignore Blocks");
		add("justdirethings.screen.target-air", "Target Air");
		add("justdirethings.screen.target-hostile", "Target Hostile");
		add("justdirethings.screen.target-passive", "Target Passive");
		add("justdirethings.screen.target-adult", "Target Adult");
		add("justdirethings.screen.target-child", "Target Child");
		add("justdirethings.screen.target-player", "Target Player");
		add("justdirethings.screen.target-living", "Target All Living");
		add("justdirethings.screen.target-item", "Target Items");
		add("justdirethings.screen.entity-none", "No Entities");
		add("justdirethings.screen.entity-all", "All Entities");
		add("justdirethings.screen.sneak-click", "Sneak Click");
		add("justdirethings.screen.showfakeplayer", "Show Fake Player");
		add("justdirethings.screen.redstone-weak", "Weak Signal");
		add("justdirethings.screen.redstone-strong", "Strong Signal");
		add("justdirethings.screen.senseamount", "Sense Amount");
		add("justdirethings.screen.greaterthan", "Greater Than");
		add("justdirethings.screen.lessthan", "Less Than");
		add("justdirethings.screen.equals", "Equals");
		add("justdirethings.screen.dropcount", "Drop Amount");
		add("justdirethings.screen.showparticles", "Show Particles");
		add("justdirethings.screen.storeexp", "Store EXP");
		add("justdirethings.screen.retrieveexp", "Retrieve EXP");
		add("justdirethings.screen.owneronly", "Owner Only");
		add("justdirethings.screen.collectexp", "Collect EXP");
		add("justdirethings.screen.targetexp", "Target Level");
		add("justdirethings.screen.showrender", "Show Render");
		add("justdirethings.screen.burnspeedmultiplier", "Burn Speed Multiplier: %s");
		add("justdirethings.screen.click-hold", "Hold Click");
		add("justdirethings.screen.click-hold-for", "Hold Click For (ticks)");
		add("justdirethings.screen.inv-normal", "Inventory Slots");
		add("justdirethings.screen.inv-armor", "Armor Slots");
		add("justdirethings.screen.inv-offhand", "Offhand Slots");
		add("justdirethings.screen.rightclicksettings", "Right Click for Settings");
		add("justdirethings.fillmode.none", "Fill Mode: Off");
		add("justdirethings.fillmode.jdtonly", "Fill Mode: JDT Only");
		add("justdirethings.fillmode.all", "Fill Mode: All");
		add("justdirethings.fluidname", "Fluid: %s");
		add("justdirethings.fluidamt", "Amount: %s mB");
		add("justdirethings.fillmode", "Fill Mode: %s");
		add("justdirethings.settingscopied", "Settings Copied");
		add("justdirethings.settingspasted", "Settings Pasted");
		add("justdirethings.polymorphicfluidamt", "Polymorphic Fluid: %s / %s mB");
		add("justdirethings.polymorphset", "Polymorph Target: %s");
		add("justdirethings.invalidpolymorphentity", "Invalid Entity for Polymorphing");
		add("justdirethings.polymorphsuccess", "Transformed into: %s");
		add("justdirethings.polymorphblacklisted",
				"That entity is blacklisted and cannot be created by the Polymorphic Wand");
		add("justdirethings.hint.dropinwater", "Drop in water to restore");
		add("justdirethings.paradox.snapshot_accepted", "Snapshot taken: %d blocks, %d entities");

		// Buttons
		// add("justdirethings.buttons.save", "Save");

		// Messages to Player
		// add("justdirethings.messages.invalidblock", "Invalid Block");

		add("justdirethings.screen.copyarea", "Copy Area");
		add("justdirethings.screen.copyoffset", "Copy Offset");
		add("justdirethings.screen.copyfilter", "Copy Filter");
		add("justdirethings.screen.copyredstone", "Copy Redstone");
		add("justdirethings.screen.filteronlytrue", "Filter Only");
		add("justdirethings.screen.comparecounts", "Compare Counts");
		add("justdirethings.screen.renderparadox", "Render Paradox");
		add("justdirethings.screen.paradoxall", "Paradox: All");
		add("justdirethings.screen.paradoxblock", "Paradox: Blocks");
		add("justdirethings.screen.paradoxentity", "Paradox: Entities");
		add("justdirethings.screen.snapshotarea", "Snapshot Area");
		add("justdirethings.screen.senditems", "Send Items");
		add("justdirethings.screen.pullitems", "Pull Items");
		add("justdirethings.screen.swapitems", "Swap Items");

		// Upgrade Smithing Templates
		add(Registration.TEMPLATE_FERRICORE.get(), "Ferricore Smithing Template");
		add(Registration.TEMPLATE_BLAZEGOLD.get(), "Blazegold Smithing Template");
		add(Registration.TEMPLATE_CELESTIGEM.get(), "Celestigem Smithing Template");
		add(Registration.TEMPLATE_ECLIPSEALLOY.get(), "Eclipse Alloy Smithing Template");

		// Upgrades
		add(Registration.UPGRADE_BASE.get(), "Blank Upgrade");
		add(Registration.UPGRADE_MOBSCANNER.get(), "Mob Scanner Upgrade");
		add(Registration.UPGRADE_ORESCANNER.get(), "Ore Scanner Upgrade");
		add(Registration.UPGRADE_OREMINER.get(), "Ore Miner Upgrade");
		add(Registration.UPGRADE_LAWNMOWER.get(), "Lawnmower Upgrade");
		add(Registration.UPGRADE_SKYSWEEPER.get(), "Sky Sweeper Upgrade");
		add(Registration.UPGRADE_TREEFELLER.get(), "Tree Feller Upgrade");
		add(Registration.UPGRADE_LEAFBREAKER.get(), "Leaf Breaker Upgrade");
		add(Registration.UPGRADE_SMELTER.get(), "Auto Smelter Upgrade");
		add(Registration.UPGRADE_SMOKER.get(), "Auto Smoker Upgrade");
		add(Registration.UPGRADE_HAMMER.get(), "Hammer Upgrade");
		add(Registration.UPGRADE_CAUTERIZEWOUNDS.get(), "Cauterize Wounds Upgrade");
		add(Registration.UPGRADE_OREXRAY.get(), "X-Ray Upgrade");
		add(Registration.UPGRADE_DROPTELEPORT.get(), "Drops Teleporter Upgrade");
		add(Registration.UPGRADE_GLOWING.get(), "Mob X-Ray Upgrade");
		add(Registration.UPGRADE_INSTABREAK.get(), "Instant Break Upgrade");
		add(Registration.UPGRADE_RUNSPEED.get(), "Run Speed Upgrade");
		add(Registration.UPGRADE_WALKSPEED.get(), "Walk Speed Upgrade");
		add(Registration.UPGRADE_STEPHEIGHT.get(), "Step Assist Upgrade");
		add(Registration.UPGRADE_JUMPBOOST.get(), "Jump Boost Upgrade");
		add(Registration.UPGRADE_MINDFOG.get(), "Mind Fog Upgrade");
		add(Registration.UPGRADE_INVULNERABILITY.get(), "Invulnerability Upgrade");
		add(Registration.UPGRADE_POTIONARROW.get(), "Potion Arrow Upgrade");
		add(Registration.UPGRADE_SPLASH.get(), "Splash Arrow Upgrade");
		add(Registration.UPGRADE_LINGERING.get(), "Lingering Arrow Upgrade");
		add(Registration.UPGRADE_HOMING.get(), "Homing Arrow Upgrade");
		add(Registration.UPGRADE_EPICARROW.get(), "Epic Arrow Upgrade");
		add(Registration.UPGRADE_SWIMSPEED.get(), "Swim Speed Upgrade");
		add(Registration.UPGRADE_GROUNDSTOMP.get(), "Ground Stomp Upgrade");
		add(Registration.UPGRADE_EXTINGUISH.get(), "Extinguish Upgrade");
		add(Registration.UPGRADE_STUPEFY.get(), "Stupefy Upgrade");
		add(Registration.UPGRADE_NEGATEFALLDAMAGE.get(), "Negate Fall Damage Upgrade");
		add(Registration.UPGRADE_NIGHTVISION.get(), "Night Vision Upgrade");
		add(Registration.UPGRADE_ELYTRA.get(), "Elytra Flight Upgrade");
		add(Registration.UPGRADE_DECOY.get(), "Decoy Upgrade");
		add(Registration.UPGRADE_WATERBREATHING.get(), "Water Breathing Upgrade");
		add(Registration.UPGRADE_DEATHPROTECTION.get(), "Death Protection Upgrade");
		add(Registration.UPGRADE_DEBUFFREMOVER.get(), "Debuff Remover Upgrade");
		add(Registration.UPGRADE_EARTHQUAKE.get(), "Earthquake Upgrade");
		add(Registration.UPGRADE_NOAI.get(), "Upgrade: Mental Obliteration");
		add(Registration.UPGRADE_FLIGHT.get(), "Flight Upgrade");
		add(Registration.UPGRADE_LAVAIMMUNITY.get(), "Lava Immunity Upgrade");
		add(Registration.UPGRADE_PHASE.get(), "Phase Upgrade");
		add(Registration.UPGRADE_TIMEPROTECTION.get(), "Time Protection Upgrade");

		// Recipes
		add("justdirethings.goospreadrecipe.title", "Goo Spreading Recipes");
		add("justdirethings.fluiddroprecipe.title", "Drop in Fluid Recipes");
		add("justdirethings.oretoresource.title", "Ores to Resources");

		// Time Crystal tooltips
		add("justdirethings.timecrystaltooltip",
				"The crystal pulses with temporal energy, accelerating or slowing time around you.");
		add("justdirethings.timecrystaltooltiptwo",
				"A sliver of crystallized time. Its effects are unpredictable but undeniably powerful.");

		// Upgrade Tooltip Details (detailtext = green, flavortext = gray italic)
		add("justdirethings." + Ability.MOBSCANNER.getName() + ".detailtext", "Show the location of nearby mobs");
		add("justdirethings." + Ability.MOBSCANNER.getName() + ".flavortext", "Whats making THAT noise?!");
		add("justdirethings." + Ability.OREMINER.getName() + ".detailtext", "Auto Harvest connected ores");
		add("justdirethings." + Ability.ORESCANNER.getName() + ".detailtext", "Show the location of nearby ores");
		add("justdirethings." + Ability.LAWNMOWER.getName() + ".detailtext", "Harvest all nearby grass");
		add("justdirethings." + Ability.SKYSWEEPER.getName() + ".detailtext",
				"Clear falling blocks above the one you break");
		add("justdirethings." + Ability.SKYSWEEPER.getName() + ".flavortext", "Oww my head!");
		add("justdirethings." + Ability.TREEFELLER.getName() + ".detailtext", "Chop down trees in 1 fell swoop");
		add("justdirethings." + Ability.LEAFBREAKER.getName() + ".detailtext", "Clear nearby leaves");
		add("justdirethings." + Ability.LEAFBREAKER.getName() + ".flavortext",
				"Seriously, who doesn't have fast leaf decay");
		add("justdirethings." + Ability.RUNSPEED.getName() + ".detailtext", "Run Faster");
		add("justdirethings." + Ability.WALKSPEED.getName() + ".detailtext", "Walk Faster");
		add("justdirethings." + Ability.STEPHEIGHT.getName() + ".detailtext", "Automatically step up 1 block");
		add("justdirethings." + Ability.JUMPBOOST.getName() + ".detailtext", "Jump Higher");
		add("justdirethings." + Ability.MINDFOG.getName() + ".detailtext", "Mobs are less likely to notice you");
		add("justdirethings." + Ability.INVULNERABILITY.getName() + ".detailtext",
				"Activate for a few seconds of invulnerability");
		add("justdirethings." + Ability.INVULNERABILITY.getName() + ".flavortext", "Bring it!!");
		add("justdirethings." + Ability.POTIONARROW.getName() + ".detailtext",
				"Insert a Potion Canister to apply effects to enemies");
		add("justdirethings." + Ability.POTIONARROW.getName() + ".flavortext",
				"Like Vanilla, without inventory issues...");
		add("justdirethings." + Ability.SMELTER.getName() + ".detailtext", "Auto Smelt Block Drops");
		add("justdirethings." + Ability.SMOKER.getName() + ".detailtext", "Auto Smelt Mob Drops");
		add("justdirethings." + Ability.HAMMER.getName() + ".detailtext", "3x3, 5x5, or 7x7 depending on tool tier");
		add("justdirethings." + Ability.LAVAREPAIR.getName() + ".detailtext", "Drop item in Lava to repair it");
		add("justdirethings." + Ability.CAUTERIZEWOUNDS.getName() + ".detailtext", "Activate to heal yourself");
		add("justdirethings." + Ability.CAUTERIZEWOUNDS.getName() + ".flavortext", "Feel the Burn!");
		add("justdirethings." + Ability.AIRBURST.getName() + ".detailtext",
				"Launch yourself in the direction you're looking");
		add("justdirethings." + Ability.AIRBURST.getName() + ".flavortext", "Safer than Fireworks");
		add("justdirethings." + Ability.SWIMSPEED.getName() + ".detailtext", "Swim Faster");
		add("justdirethings." + Ability.GROUNDSTOMP.getName() + ".detailtext", "Activate to push mobs away");
		add("justdirethings." + Ability.EXTINGUISH.getName() + ".detailtext", "Removes Burning Effect");
		add("justdirethings." + Ability.STUPEFY.getName() + ".detailtext",
				"Activate to make the targeted mob forget you");
		add("justdirethings." + Ability.STUPEFY.getName() + ".flavortext", "Professor Lockhart would be proud");
		add("justdirethings." + Ability.SPLASH.getName() + ".detailtext", "Add splash effect to your potions");
		add("justdirethings." + Ability.DROPTELEPORT.getName() + ".detailtext",
				"Bind tool to chest, and drops will teleport there");
		add("justdirethings." + Ability.DROPTELEPORT.getName() + ".flavortext",
				"My inventory is a mess -Dire Probably");
		add("justdirethings." + Ability.VOIDSHIFT.getName() + ".detailtext", "Teleport to where you're looking");
		add("justdirethings." + Ability.NEGATEFALLDAMAGE.getName() + ".detailtext", "No fall damage");
		add("justdirethings." + Ability.NIGHTVISION.getName() + ".detailtext", "Automatic Night Vision");
		add("justdirethings." + Ability.ELYTRA.getName() + ".detailtext", "Built In Elytra");
		add("justdirethings.decoy", "Decoy");
		add("justdirethings." + Ability.DECOY.getName() + ".detailtext",
				"Activate to summon a decoy that mobs will attack");
		add("justdirethings." + Ability.LINGERING.getName() + ".detailtext", "Lingering effect on potions");
		add("justdirethings." + Ability.HOMING.getName() + ".detailtext", "Arrows seek their targets");
		add("justdirethings." + Ability.OREXRAY.getName() + ".detailtext", "See all nearby ores");
		add("justdirethings." + Ability.OREXRAY.getName() + ".flavortext", "Thats Overpowered!!");
		add("justdirethings." + Ability.GLOWING.getName() + ".detailtext", "See all nearby mobs");
		add("justdirethings." + Ability.INSTABREAK.getName() + ".detailtext", "Instantly break all blocks");
		add("justdirethings." + Ability.ECLIPSEGATE.getName() + ".detailtext",
				"Temporarily remove blocks you click on");
		add("justdirethings." + Ability.ECLIPSEGATE.getName() + ".flavortext", "I just missed the portable hole, ok?");
		add("justdirethings." + Ability.DEATHPROTECTION.getName() + ".detailtext",
				"Prevents death once every 5 minutes");
		add("justdirethings." + Ability.DEBUFFREMOVER.getName() + ".detailtext", "Activate to Remove negative effects");
		add("justdirethings." + Ability.DEBUFFREMOVER.getName() + ".flavortext", "Milk not included");
		add("justdirethings." + Ability.EARTHQUAKE.getName() + ".detailtext", "Slow all nearby mobs");
		add("justdirethings." + Ability.NOAI.getName() + ".detailtext", "Nearby mobs completely stop. Forever.");
		add("justdirethings." + Ability.FLIGHT.getName() + ".detailtext", "Creative mode style flight");
		add("justdirethings." + Ability.LAVAIMMUNITY.getName() + ".detailtext", "No Damage from Lava or Fire");
		add("justdirethings." + Ability.LAVAIMMUNITY.getName() + ".flavortext", "Fancy a swim?");
		add("justdirethings." + Ability.PHASE.getName() + ".detailtext", "Walk through walls");
		add("justdirethings." + Ability.EPICARROW.getName() + ".detailtext", "Arrows can hit multiple targets");
		add("justdirethings." + Ability.EPICARROW.getName() + ".flavortext", "I'm Mary Poppins Ya'll");
		add("justdirethings." + Ability.TIMEPROTECTION.getName() + ".detailtext",
				"Protection from Time Altering Effects");
		add("justdirethings." + Ability.WATERBREATHING.getName() + ".detailtext",
				"Allows player to breath under water");
		add("justdirethings." + Ability.WATERBREATHING.getName() + ".flavortext",
				"Just keep swimming, just keep swimming!");

		// Fluid Buckets
		add(Registration.REFINED_T2_FLUID_BUCKET.get(), "Blaze Ember Fuel Bucket");
		add(Registration.REFINED_T3_FLUID_BUCKET.get(), "Voidflame Fuel Bucket");
		add(Registration.REFINED_T4_FLUID_BUCKET.get(), "Eclipse Ember Fuel Bucket");
		add(Registration.UNREFINED_T2_FLUID_BUCKET.get(), "Unrefined Blaze Ember Fuel Bucket");
		add(Registration.UNREFINED_T3_FLUID_BUCKET.get(), "Unrefined Voidflame Fuel Bucket");
		add(Registration.UNREFINED_T4_FLUID_BUCKET.get(), "Unrefined Eclipse Ember Fuel Bucket");
		add(Registration.PORTAL_FLUID_BUCKET.get(), "Portal Fluid Bucket");
		add(Registration.UNSTABLE_PORTAL_FLUID_BUCKET.get(), "Unstable Portal Fluid Bucket");
		add(Registration.TIME_FLUID_BUCKET.get(), "Time Fluid Bucket");
		add(Registration.XP_FLUID_BUCKET.get(), "XP Fluid Bucket");
		add(Registration.POLYMORPHIC_FLUID_BUCKET.get(), "Polymorphic Fluid Bucket");

		// Fluid Block Names (used by Jade/HWYLA and F3 overlay)
		add("block.justdirethings.refined_t2_fluid_block", "Blaze Ember Fuel");
		add("block.justdirethings.refined_t3_fluid_block", "Voidflame Fuel");
		add("block.justdirethings.refined_t4_fluid_block", "Eclipse Ember Fuel");
		add("block.justdirethings.unrefined_t2_fluid_block", "Unrefined Blaze Ember Fuel");
		add("block.justdirethings.unrefined_t3_fluid_block", "Unrefined Voidflame Fuel");
		add("block.justdirethings.unrefined_t4_fluid_block", "Unrefined Eclipse Ember Fuel");
		add("block.justdirethings.portal_fluid_block", "Portal Fluid");
		add("block.justdirethings.unstable_portal_fluid_block", "Unstable Portal Fluid");
		add("block.justdirethings.time_fluid_block", "Time Fluid");
		add("block.justdirethings.xp_fluid_block", "XP Fluid");
		add("block.justdirethings.polymorphic_fluid_block", "Polymorphic Fluid");

		// Fluid Type Names
		add("fluid_type.justdirethings.refined_t2_fluid_type", "Blaze Ember Fuel");
		add("fluid_type.justdirethings.refined_t3_fluid_type", "Voidflame Fuel");
		add("fluid_type.justdirethings.refined_t4_fluid_type", "Eclipse Ember Fuel");
		add("fluid_type.justdirethings.unrefined_t2_fluid_type", "Unrefined Blaze Ember Fuel");
		add("fluid_type.justdirethings.unrefined_t3_fluid_type", "Unrefined Voidflame Fuel");
		add("fluid_type.justdirethings.unrefined_t4_fluid_type", "Unrefined Eclipse Ember Fuel");
		add("fluid_type.justdirethings.portal_fluid_type", "Portal Fluid");
		add("fluid_type.justdirethings.unstable_portal_fluid_type", "Unstable Portal Fluid");
		add("fluid_type.justdirethings.time_fluid_type", "Time Fluid");
		add("fluid_type.justdirethings.xp_fluid_type", "XP Fluid");
		add("fluid_type.justdirethings.polymorphic_fluid_type", "Polymorphic Fluid");

	}
}
