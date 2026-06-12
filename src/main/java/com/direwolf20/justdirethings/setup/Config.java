package com.direwolf20.justdirethings.setup;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("removal")
public class Config {
	public static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

	public static final String CATEGORY_GENERAL = "general";
	public static ForgeConfigSpec.IntValue MINIMUM_MACHINE_TICK_SPEED;

	public static final String CATEGORY_GENERATOR_T1 = "generator_t1";
	public static ForgeConfigSpec.IntValue GENERATOR_T1_FE_PER_FUEL_TICK;
	public static ForgeConfigSpec.IntValue GENERATOR_T1_BURN_SPEED_MULTIPLIER;
	public static ForgeConfigSpec.IntValue GENERATOR_T1_MAX_FE;
	public static ForgeConfigSpec.IntValue GENERATOR_T1_FE_PER_TICK;

	public static final String CATEGORY_FUEL_CANISTER = "fuel_canister";
	public static ForgeConfigSpec.IntValue FUEL_CANISTER_MINIMUM_TICKS_CONSUMED;
	public static ForgeConfigSpec.IntValue FUEL_CANISTER_MAXIMUM_FUEL;

	public static final String ENERGY_TRANSMITTER_T1 = "energy_transmitter_t1";
	public static ForgeConfigSpec.DoubleValue ENERGY_TRANSMITTER_T1_LOSS_PER_BLOCK;
	public static ForgeConfigSpec.IntValue ENERGY_TRANSMITTER_T1_MAX_RF;
	public static ForgeConfigSpec.IntValue ENERGY_TRANSMITTER_T1_RF_PER_TICK;

	public static final String CATEGORY_POCKET_GENERATOR = "pocket_generator";
	public static ForgeConfigSpec.IntValue POCKET_GENERATOR_FE_PER_FUEL_TICK;
	public static ForgeConfigSpec.IntValue POCKET_GENERATOR_BURN_SPEED_MULTIPLIER;
	public static ForgeConfigSpec.IntValue POCKET_GENERATOR_MAX_FE;
	public static ForgeConfigSpec.IntValue POCKET_GENERATOR_FE_PER_TICK;

	public static final String CATEGORY_TIME_CRYSTAL = "time_crystal";
	public static ForgeConfigSpec.BooleanValue TIME_CRYSTAL_CUSTOM_DIMENSIONS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> TIME_CRYSTAL_STAGE1_DIMENSIONS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> TIME_CRYSTAL_STAGE2_DIMENSIONS;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> TIME_CRYSTAL_STAGE3_DIMENSIONS;

	public static final String CATEGORY_GENERATOR_FLUID_T1 = "generator_fluid_t1";
	public static ForgeConfigSpec.IntValue GENERATOR_FLUID_T1_MAX_FE;
	public static ForgeConfigSpec.IntValue GENERATOR_FLUID_T1_FE_PER_TICK;

	public static final String CATEGORY_FLUID_FUEL = "fluid_fuel";
	public static ForgeConfigSpec.IntValue FUEL_TIER2_FE_PER_MB;
	public static ForgeConfigSpec.IntValue FUEL_TIER3_FE_PER_MB;
	public static ForgeConfigSpec.IntValue FUEL_TIER4_FE_PER_MB;

	public static final String CATEGORY_GOO = "goo";
	public static ForgeConfigSpec.BooleanValue GOO_CAN_DIE;
	public static ForgeConfigSpec.DoubleValue GOO_DEATH_CHANCE;
	public static ForgeConfigSpec.ConfigValue<List<? extends Object>> GOO_DIMENSION_RESTRICTIONS;

	public static final String CATEGORY_PARADOX = "paradox_machine";
	public static ForgeConfigSpec.IntValue PARADOX_TOTAL_RF_CAPACITY;
	public static ForgeConfigSpec.IntValue PARADOX_TOTAL_FLUID_CAPACITY;
	public static ForgeConfigSpec.IntValue PARADOX_RF_PER_BLOCK;
	public static ForgeConfigSpec.IntValue PARADOX_RF_PER_ENTITY;
	public static ForgeConfigSpec.IntValue PARADOX_FLUID_PER_BLOCK;
	public static ForgeConfigSpec.IntValue PARADOX_FLUID_PER_ENTITY;
	public static ForgeConfigSpec.DoubleValue PARADOX_ENERGY_PER_BLOCK;
	public static ForgeConfigSpec.DoubleValue PARADOX_ENERGY_PER_ENTITY;
	public static ForgeConfigSpec.DoubleValue PARADOX_ENERGY_MAX;
	public static ForgeConfigSpec.BooleanValue PARADOX_RESTRICTED_MOBS;

	public static final String CATEGORY_POLYMORPHIC_WAND = "polymorphic_wand";
	public static ForgeConfigSpec.IntValue POLYMORPHIC_WAND_MAX_FLUID;

	public static final String CATEGORY_TIME_WAND = "time_wand";
	public static ForgeConfigSpec.IntValue TIME_WAND_MAX_FE;
	public static ForgeConfigSpec.IntValue TIME_WAND_FE_COST;
	public static ForgeConfigSpec.IntValue TIME_WAND_MAX_FLUID;
	public static ForgeConfigSpec.DoubleValue TIME_WAND_FLUID_COST;
	public static ForgeConfigSpec.IntValue TIME_WAND_MAX_MULTIPLIER;
	public static ForgeConfigSpec.BooleanValue TIME_WAND_FAKE_PLAYER_ALLOWED;

	public static final String CATEGORY_POLYMORPHIC_WAND_V2 = "polymorphic_wand_v2";
	public static ForgeConfigSpec.IntValue POLYMORPHIC_WAND_V2_MAX_FE;
	public static ForgeConfigSpec.IntValue POLYMORPHIC_WAND_V2_MAX_FLUID;
	public static ForgeConfigSpec.IntValue RANDOM_POLYMORPH_COST;
	public static ForgeConfigSpec.IntValue TARGET_POLYMORPH_COST;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> POLYMORPH_BLACKLIST;

	public static final String CATEGORY_PORTAL_GUN = "portal_gun";
	public static ForgeConfigSpec.IntValue PORTAL_GUN_MAX_FE;
	public static ForgeConfigSpec.IntValue PORTAL_GUN_FE_COST;
	public static ForgeConfigSpec.IntValue PORTAL_GUN_MAX_FLUID;
	public static ForgeConfigSpec.IntValue PORTAL_GUN_FLUID_COST;
	public static ForgeConfigSpec.IntValue PORTAL_GUN_LIFESPAN;

	public static final String CATEGORY_PORTAL_GUN_ORIGINAL = "portal_gun_original";
	public static ForgeConfigSpec.IntValue PORTAL_GUN_ORIGINAL_MAX_FLUID;
	public static ForgeConfigSpec.IntValue PORTAL_GUN_ORIGINAL_FLUID_COST;
	public static ForgeConfigSpec.IntValue PORTAL_GUN_ORIGINAL_LIFESPAN;

	public static final String CATEGORY_BLOCK_SWAPPER = "block_swapper";
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> SWAPPER_ENTITY_BLACKLIST;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> SWAPPER_BLOCK_BLACKLIST;

	public static void register() {
		// registerServerConfigs();
		registerCommonConfigs();
		// registerClientConfigs();
	}

	private static void registerClientConfigs() {

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
	}

	private static void registerCommonConfigs() {
		generalConfig();
		gooConfig();
		generatorT1Config();
		generatorFluidT1Config();
		energyTransmitter();
		fuelCanisterConfig();
		pocketGeneratorConfig();
		timeCrystalConfig();
		fluidFuelConfig();
		paradoxConfig();
		timeWandConfig();
		portalGunConfig();
		portalGunOriginalConfig();
		polymorphicWandV2Config();
		swapperConfig();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
	}

	private static void registerServerConfigs() {

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
	}

	private static void generalConfig() {
		COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
		MINIMUM_MACHINE_TICK_SPEED = COMMON_BUILDER
				.comment("The minimum tick speed machines can be set to. Defaults to 1, meaning every tick")
				.defineInRange("minimum_machine_tick_speed", 1, 1, 100);
		COMMON_BUILDER.pop();
	}

	private static void gooConfig() {
		COMMON_BUILDER.comment("Goo settings").push(CATEGORY_GOO);
		GOO_CAN_DIE = COMMON_BUILDER
				.comment("If true, goo blocks have a chance to deactivate after each successful block conversion")
				.define("goo_can_die", true);
		GOO_DEATH_CHANCE = COMMON_BUILDER.comment(
				"The chance (0.0 to 1.0) that a goo block deactivates after each block conversion. Default is 0.1 (10%)")
				.defineInRange("goo_death_chance", 0.1, 0.0, 1.0);
		GOO_DIMENSION_RESTRICTIONS = COMMON_BUILDER
				.comment(
						"Restricts goo spreading for specific input blocks to only work in certain dimensions.",
						"Each entry is a pair [\"block_id\", \"dimension_id\"].",
						"A block listed here will ONLY be converted by goo while the goo is in the specified dimension.",
						"Blocks not listed here are unrestricted and spread in any dimension.")
				.defineListAllowEmpty(List.of("gooSpreadingSpecificDimensions"), List::of,
						e -> e instanceof List<?> lst && lst.size() == 2
								&& lst.get(0) instanceof String && lst.get(1) instanceof String);
		COMMON_BUILDER.pop();
	}

	private static void generatorT1Config() {
		COMMON_BUILDER.comment("Generator T1").push(CATEGORY_GENERATOR_T1);
		GENERATOR_T1_FE_PER_FUEL_TICK = COMMON_BUILDER.comment(
				"The amount of Energy created per burn tick of fuel. Coal has 1600 burn ticks. Sticks have 100 burn ticks.")
				.defineInRange("generator_t1_fe_per_fuel_tick", 15, 1, Integer.MAX_VALUE);
		GENERATOR_T1_BURN_SPEED_MULTIPLIER = COMMON_BUILDER.comment(
				"The multiplier for the burn speed, making the generator run faster. Coal is 1600 ticks to burn, if you set this to 10, it will burn in 160 ticks")
				.defineInRange("generator_t1_burn_speed_multiplier", 4, 1, 1000);
		GENERATOR_T1_MAX_FE = COMMON_BUILDER
				.comment("The maximum amount of Energy the generator can hold in its buffer")
				.defineInRange("generator_t1_max_fe", 1000000, 1, Integer.MAX_VALUE);
		GENERATOR_T1_FE_PER_TICK = COMMON_BUILDER.comment("The FE per Tick that the generator outputs")
				.defineInRange("generator_t1_fe_per_tick", 1000, 1, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();
	}

	private static void fuelCanisterConfig() {
		COMMON_BUILDER.comment("Fuel Canister").push(CATEGORY_FUEL_CANISTER);
		FUEL_CANISTER_MINIMUM_TICKS_CONSUMED = COMMON_BUILDER.comment(
				"The amount of ticks 'consumed' per operation in the furnace. Lower is more efficient fuel use.")
				.defineInRange("fuel_canister_minimum_ticks_consumed", 200, 100, Integer.MAX_VALUE);
		FUEL_CANISTER_MAXIMUM_FUEL = COMMON_BUILDER
				.comment("The maximum amount of fuel (in ticks) permitted in the fuel canister.")
				.defineInRange("fuel_canister_maximum_fuel", 10000000, 100, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();
	}

	private static void energyTransmitter() {
		COMMON_BUILDER.comment("Energy Transmitter T1").push(ENERGY_TRANSMITTER_T1);
		ENERGY_TRANSMITTER_T1_MAX_RF = COMMON_BUILDER.comment("The maximum energy storage")
				.defineInRange("energy_transmitter_t1_max_rf", 1000000, 1, Integer.MAX_VALUE);
		ENERGY_TRANSMITTER_T1_RF_PER_TICK = COMMON_BUILDER
				.comment("The maximum RF transmitted per tick to machines and other transmitters")
				.defineInRange("energy_transmitter_t1_rf_per_tick", 1000, 1, Integer.MAX_VALUE);
		ENERGY_TRANSMITTER_T1_LOSS_PER_BLOCK = COMMON_BUILDER.comment("The energy loss per block distance in percent")
				.defineInRange("energy_transmitter_t1_loss_per_block", 1.0, 0, 100);
		COMMON_BUILDER.pop();
	}

	private static void pocketGeneratorConfig() {
		COMMON_BUILDER.comment("Pocket Generator").push(CATEGORY_POCKET_GENERATOR);
		POCKET_GENERATOR_FE_PER_FUEL_TICK = COMMON_BUILDER.comment(
				"The amount of Energy created per burn tick of fuel. Coal has 1600 burn ticks. Sticks have 100 burn ticks.")
				.defineInRange("pocket_gen_fe_per_fuel_tick", 15, 1, Integer.MAX_VALUE);
		POCKET_GENERATOR_BURN_SPEED_MULTIPLIER = COMMON_BUILDER.comment(
				"The multiplier for the burn speed, making the generator run faster. Coal is 1600 ticks to burn, if you set this to 10, it will burn in 160 ticks")
				.defineInRange("pocket_gen_burn_speed_multiplier", 4, 1, 1000);
		POCKET_GENERATOR_MAX_FE = COMMON_BUILDER
				.comment("The maximum amount of Energy the generator can hold in its buffer")
				.defineInRange("pocket_gen_max_fe", 1000000, 1, Integer.MAX_VALUE);
		POCKET_GENERATOR_FE_PER_TICK = COMMON_BUILDER
				.comment("The FE per Tick that the generator charges other items at")
				.defineInRange("pocket_gen_fe_per_tick", 5000, 1, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();
	}

	private static void timeCrystalConfig() {
		COMMON_BUILDER.comment("Time Crystals").push(CATEGORY_TIME_CRYSTAL);
		TIME_CRYSTAL_CUSTOM_DIMENSIONS = COMMON_BUILDER.comment(
				"Do you want to customize Time Crystal Growth Dimensions? If set to true, the following 3 fields MUST be populated. Defaults to false, which means normal growth rules: Stage 1 = Overworld, Stage 2 = Nether, Stage 3 = End.")
				.define("time_crystal_custom_dimensions", false);
		TIME_CRYSTAL_STAGE1_DIMENSIONS = COMMON_BUILDER
				.comment("Dimensions where Stage 0 -> Stage 1 growth occurs. Example: [\"minecraft:overworld\"]")
				.defineListAllowEmpty(List.of("time_crystal_stage1_dimensions"), () -> List.of("minecraft:overworld"),
						s -> s instanceof String);
		TIME_CRYSTAL_STAGE2_DIMENSIONS = COMMON_BUILDER
				.comment("Dimensions where Stage 1 -> Stage 2 growth occurs. Example: [\"minecraft:the_nether\"]")
				.defineListAllowEmpty(List.of("time_crystal_stage2_dimensions"), () -> List.of("minecraft:the_nether"),
						s -> s instanceof String);
		TIME_CRYSTAL_STAGE3_DIMENSIONS = COMMON_BUILDER
				.comment("Dimensions where Stage 2 -> Stage 3 growth occurs. Example: [\"minecraft:the_end\"]")
				.defineListAllowEmpty(List.of("time_crystal_stage3_dimensions"), () -> List.of("minecraft:the_end"),
						s -> s instanceof String);
		COMMON_BUILDER.pop();
	}

	private static void generatorFluidT1Config() {
		COMMON_BUILDER.comment("Generator Fluid T1").push(CATEGORY_GENERATOR_FLUID_T1);
		GENERATOR_FLUID_T1_MAX_FE = COMMON_BUILDER.comment("Max FE capacity for the Fluid Generator T1")
				.defineInRange("generator_fluid_t1_max_fe", 1000000, 1, Integer.MAX_VALUE);
		GENERATOR_FLUID_T1_FE_PER_TICK = COMMON_BUILDER.comment("FE output per tick for the Fluid Generator T1")
				.defineInRange("generator_fluid_t1_fe_per_tick", 1000, 1, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();
	}

	private static void fluidFuelConfig() {
		COMMON_BUILDER.comment("Fluid Fuel FE per mB values").push(CATEGORY_FLUID_FUEL);
		FUEL_TIER2_FE_PER_MB = COMMON_BUILDER.comment("FE generated per mB of Refined Fuel T2")
				.defineInRange("fuel_tier2_fe_per_mb", 4000, 1, Integer.MAX_VALUE);
		FUEL_TIER3_FE_PER_MB = COMMON_BUILDER.comment("FE generated per mB of Refined Fuel T3")
				.defineInRange("fuel_tier3_fe_per_mb", 16000, 1, Integer.MAX_VALUE);
		FUEL_TIER4_FE_PER_MB = COMMON_BUILDER.comment("FE generated per mB of Refined Fuel T4")
				.defineInRange("fuel_tier4_fe_per_mb", 64000, 1, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();
	}

	private static void paradoxConfig() {
		COMMON_BUILDER.comment("Paradox Machine").push(CATEGORY_PARADOX);
		PARADOX_TOTAL_RF_CAPACITY = COMMON_BUILDER.comment("Max RF capacity of the Paradox Machine")
				.defineInRange("paradox_total_rf_capacity", 10000000, 1, Integer.MAX_VALUE);
		PARADOX_TOTAL_FLUID_CAPACITY = COMMON_BUILDER.comment("Max fluid capacity (mB) of the Paradox Machine")
				.defineInRange("paradox_total_fluid_capacity", 64000, 1, Integer.MAX_VALUE);
		PARADOX_RF_PER_BLOCK = COMMON_BUILDER.comment("RF cost per block restored")
				.defineInRange("paradox_rf_per_block", 1000, 0, Integer.MAX_VALUE);
		PARADOX_RF_PER_ENTITY = COMMON_BUILDER.comment("RF cost per entity restored")
				.defineInRange("paradox_rf_per_entity", 5000, 0, Integer.MAX_VALUE);
		PARADOX_FLUID_PER_BLOCK = COMMON_BUILDER.comment("Fluid cost (mB) per block restored")
				.defineInRange("paradox_fluid_per_block", 10, 0, Integer.MAX_VALUE);
		PARADOX_FLUID_PER_ENTITY = COMMON_BUILDER.comment("Fluid cost (mB) per entity restored")
				.defineInRange("paradox_fluid_per_entity", 50, 0, Integer.MAX_VALUE);
		PARADOX_ENERGY_PER_BLOCK = COMMON_BUILDER.comment("Paradox energy generated per block restored")
				.defineInRange("paradox_energy_per_block", 0.5, 0, Double.MAX_VALUE);
		PARADOX_ENERGY_PER_ENTITY = COMMON_BUILDER.comment("Paradox energy generated per entity restored")
				.defineInRange("paradox_energy_per_entity", 2.0, 0, Double.MAX_VALUE);
		PARADOX_ENERGY_MAX = COMMON_BUILDER.comment("Max paradox energy before a ParadoxEntity spawns")
				.defineInRange("paradox_energy_max", 100.0, 1, Double.MAX_VALUE);
		PARADOX_RESTRICTED_MOBS = COMMON_BUILDER.comment("If true, only safe mob data fields are restored")
				.define("paradox_restricted_mobs", true);
		COMMON_BUILDER.pop();

		COMMON_BUILDER.comment("Polymorphic Wand").push(CATEGORY_POLYMORPHIC_WAND);
		POLYMORPHIC_WAND_MAX_FLUID = COMMON_BUILDER.comment("Maximum mB of polymorphic fluid the wand can hold")
				.defineInRange("polymorphic_wand_max_fluid", 8000, 1, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();
	}

	private static void timeWandConfig() {
		COMMON_BUILDER.comment("Time Wand").push(CATEGORY_TIME_WAND);
		TIME_WAND_MAX_FE = COMMON_BUILDER.comment("The maximum amount of Energy the Time Wand can hold in its buffer")
				.defineInRange("time_wand_rf_capacity", 100000, 1, Integer.MAX_VALUE);
		TIME_WAND_FE_COST = COMMON_BUILDER.comment("FE cost per use, multiplied by the acceleration rate (2^level)")
				.defineInRange("time_wand_rf_cost", 100, 0, Integer.MAX_VALUE);
		TIME_WAND_MAX_FLUID = COMMON_BUILDER.comment("Max mB of time fluid the Time Wand can hold")
				.defineInRange("time_wand_max_fluid", 8000, 1, Integer.MAX_VALUE);
		TIME_WAND_FLUID_COST = COMMON_BUILDER
				.comment("Time Fluid cost per use, multiplied by the acceleration rate (2^level)")
				.defineInRange("time_wand_fluid_cost", 0.5, 0.0, Double.MAX_VALUE);
		TIME_WAND_MAX_MULTIPLIER = COMMON_BUILDER
				.comment("Maximum tick speed multiplier (must be a power of 2, e.g. 256 = up to 2^8 = 256x)")
				.defineInRange("time_wand_max_multiplier", 256, 2, Integer.MAX_VALUE);
		TIME_WAND_FAKE_PLAYER_ALLOWED = COMMON_BUILDER.comment("Can fake players (e.g. Clickers) use the Time Wand?")
				.define("time_wand_fake_player_allowed", true);
		COMMON_BUILDER.pop();
	}

	public static int logBase2(int n) {
		return (int) (Math.log(n) / Math.log(2));
	}

	private static void portalGunConfig() {
		COMMON_BUILDER.comment("Advanced Portal Gun").push(CATEGORY_PORTAL_GUN);
		PORTAL_GUN_MAX_FE = COMMON_BUILDER.comment("Max FE capacity of the Portal Gun")
				.defineInRange("portal_gun_max_fe", 1000000, 1, Integer.MAX_VALUE);
		PORTAL_GUN_FE_COST = COMMON_BUILDER.comment("FE cost per portal shot").defineInRange("portal_gun_fe_cost", 5000,
				0, Integer.MAX_VALUE);
		PORTAL_GUN_MAX_FLUID = COMMON_BUILDER.comment("Max mB of portal fluid the Portal Gun can hold")
				.defineInRange("portal_gun_max_fluid", 8000, 1, Integer.MAX_VALUE);
		PORTAL_GUN_FLUID_COST = COMMON_BUILDER.comment("mB of portal fluid consumed per shot")
				.defineInRange("portal_gun_fluid_cost", 500, 0, Integer.MAX_VALUE);
		PORTAL_GUN_LIFESPAN = COMMON_BUILDER
				.comment("How many ticks a portal remains open (200 = 10 seconds, -1 for infinite)")
				.defineInRange("portal_gun_lifespan", 200, -1, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();
	}

	private static void portalGunOriginalConfig() {
		COMMON_BUILDER.comment("Portal Gun").push(CATEGORY_PORTAL_GUN_ORIGINAL);
		PORTAL_GUN_ORIGINAL_MAX_FLUID = COMMON_BUILDER
				.comment("Max mB of unstable portal fluid the original Portal Gun can hold")
				.defineInRange("portal_gun_original_max_fluid", 4000, 1, Integer.MAX_VALUE);
		PORTAL_GUN_ORIGINAL_FLUID_COST = COMMON_BUILDER.comment("mB of unstable portal fluid consumed per shot")
				.defineInRange("portal_gun_original_fluid_cost", 500, 0, Integer.MAX_VALUE);
		PORTAL_GUN_ORIGINAL_LIFESPAN = COMMON_BUILDER.comment("How many ticks a portal remains open (-1 for infinite)")
				.defineInRange("portal_gun_original_lifespan", 3000, -1, Integer.MAX_VALUE);
		COMMON_BUILDER.pop();
	}

	private static void swapperConfig() {
		COMMON_BUILDER.comment("Block Swapper").push(CATEGORY_BLOCK_SWAPPER);
		SWAPPER_ENTITY_BLACKLIST = COMMON_BUILDER
				.comment("Entities or mods that the Block Swapper cannot teleport.",
						"Use full entity IDs (e.g. \"draconicevolution:chaos_guardian\") for specific entities,",
						"or just the mod ID (e.g. \"draconicevolution\") to block all entities from that mod.")
				.defineListAllowEmpty(List.of("swapper_entity_blacklist"), () -> List.of(), s -> s instanceof String);
		SWAPPER_BLOCK_BLACKLIST = COMMON_BUILDER
				.comment("Blocks or mods that the Block Swapper cannot move.",
						"Use full block IDs (e.g. \"draconicevolution:chaos_crystal\") for specific blocks,",
						"or just the mod ID (e.g. \"draconicevolution\") to block all blocks from that mod.",
						"Note: the swapper_deny block tag also controls this and is checked first.")
				.defineListAllowEmpty(List.of("swapper_block_blacklist"), () -> List.of(), s -> s instanceof String);
		COMMON_BUILDER.pop();
	}

	private static void polymorphicWandV2Config() {
		COMMON_BUILDER.comment("Polymorphic Wand V2").push(CATEGORY_POLYMORPHIC_WAND_V2);
		POLYMORPHIC_WAND_V2_MAX_FE = COMMON_BUILDER.comment("Maximum FE the Polymorphic Wand V2 can hold")
				.defineInRange("polymorphic_wand_v2_max_fe", 200000, 1, Integer.MAX_VALUE);
		POLYMORPHIC_WAND_V2_MAX_FLUID = COMMON_BUILDER
				.comment("Maximum mB of polymorphic fluid the Polymorphic Wand V2 can hold")
				.defineInRange("polymorphic_wand_v2_max_fluid", 32000, 1, Integer.MAX_VALUE);
		RANDOM_POLYMORPH_COST = COMMON_BUILDER.comment("mB of polymorphic fluid consumed per random polymorph")
				.defineInRange("random_polymorph_cost", 1000, 0, Integer.MAX_VALUE);
		TARGET_POLYMORPH_COST = COMMON_BUILDER.comment("mB of polymorphic fluid consumed per targeted polymorph")
				.defineInRange("target_polymorph_cost", 1000, 0, Integer.MAX_VALUE);
		POLYMORPH_BLACKLIST = COMMON_BUILDER
				.comment("Entities or mods that the Polymorphic Wand cannot create.",
						"Use full entity IDs (e.g. \"somebossmod:big_boss\") for specific entities,",
						"or just the mod ID (e.g. \"somebossmod\") to block all entities from that mod.")
				.defineListAllowEmpty(List.of("polymorph_blacklist"), () -> List.of(), s -> s instanceof String);
		COMMON_BUILDER.pop();
	}
}
