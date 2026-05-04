package com.direwolf20.justdirethings.setup;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

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

    public static void register() {
        //registerServerConfigs();
        registerCommonConfigs();
        //registerClientConfigs();
    }

    private static void registerClientConfigs() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_BUILDER.build());
    }

    private static void registerCommonConfigs() {
        generalConfig();
        generatorT1Config();
        energyTransmitter();
        fuelCanisterConfig();
        pocketGeneratorConfig();
        timeCrystalConfig();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_BUILDER.build());
    }

    private static void registerServerConfigs() {

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_BUILDER.build());
    }

    private static void generalConfig() {
        COMMON_BUILDER.comment("General settings").push(CATEGORY_GENERAL);
        MINIMUM_MACHINE_TICK_SPEED = COMMON_BUILDER.comment("The minimum tick speed machines can be set to. Defaults to 1, meaning every tick")
                .defineInRange("minimum_machine_tick_speed", 1, 1, 100);
        COMMON_BUILDER.pop();
    }

    private static void generatorT1Config() {
        COMMON_BUILDER.comment("Generator T1").push(CATEGORY_GENERATOR_T1);
        GENERATOR_T1_FE_PER_FUEL_TICK = COMMON_BUILDER.comment("The amount of Forge Energy created per burn tick of fuel. Coal has 1600 burn ticks. Sticks have 100 burn ticks.")
                .defineInRange("generator_t1_fe_per_fuel_tick", 15, 1, Integer.MAX_VALUE);
        GENERATOR_T1_BURN_SPEED_MULTIPLIER = COMMON_BUILDER.comment("The multiplier for the burn speed, making the generator run faster. Coal is 1600 ticks to burn, if you set this to 10, it will burn in 160 ticks")
                .defineInRange("generator_t1_burn_speed_multiplier", 4, 1, 1000);
        GENERATOR_T1_MAX_FE = COMMON_BUILDER.comment("The maximum amount of Forge Energy the generator can hold in its buffer")
                .defineInRange("generator_t1_max_fe", 1000000, 1, Integer.MAX_VALUE);
        GENERATOR_T1_FE_PER_TICK = COMMON_BUILDER.comment("The FE per Tick that the generator outputs")
                .defineInRange("generator_t1_fe_per_tick", 1000, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();
    }

    private static void fuelCanisterConfig() {
        COMMON_BUILDER.comment("Fuel Canister").push(CATEGORY_FUEL_CANISTER);
        FUEL_CANISTER_MINIMUM_TICKS_CONSUMED = COMMON_BUILDER.comment("The amount of ticks 'consumed' per operation in the furnace. Lower is more efficient fuel use.")
                .defineInRange("fuel_canister_minimum_ticks_consumed", 200, 100, Integer.MAX_VALUE);
        FUEL_CANISTER_MAXIMUM_FUEL = COMMON_BUILDER.comment("The maximum amount of fuel (in ticks) permitted in the fuel canister.")
                .defineInRange("fuel_canister_maximum_fuel", 10000000, 100, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();
    }

    private static void energyTransmitter() {
        COMMON_BUILDER.comment("Energy Transmitter T1").push(ENERGY_TRANSMITTER_T1);
        ENERGY_TRANSMITTER_T1_MAX_RF = COMMON_BUILDER.comment("The maximum energy storage")
                .defineInRange("energy_transmitter_t1_max_rf", 1000000, 1, Integer.MAX_VALUE);
        ENERGY_TRANSMITTER_T1_RF_PER_TICK = COMMON_BUILDER.comment("The maximum RF transmitted per tick to machines and other transmitters")
                .defineInRange("energy_transmitter_t1_rf_per_tick", 1000, 1, Integer.MAX_VALUE);
        ENERGY_TRANSMITTER_T1_LOSS_PER_BLOCK = COMMON_BUILDER.comment("The energy loss per block distance in percent")
                .defineInRange("energy_transmitter_t1_loss_per_block", 1.0, 0, 100);
        COMMON_BUILDER.pop();
    }

    private static void pocketGeneratorConfig() {
        COMMON_BUILDER.comment("Pocket Generator").push(CATEGORY_POCKET_GENERATOR);
        POCKET_GENERATOR_FE_PER_FUEL_TICK = COMMON_BUILDER.comment("The amount of Forge Energy created per burn tick of fuel. Coal has 1600 burn ticks. Sticks have 100 burn ticks.")
                .defineInRange("pocket_gen_fe_per_fuel_tick", 15, 1, Integer.MAX_VALUE);
        POCKET_GENERATOR_BURN_SPEED_MULTIPLIER = COMMON_BUILDER.comment("The multiplier for the burn speed, making the generator run faster. Coal is 1600 ticks to burn, if you set this to 10, it will burn in 160 ticks")
                .defineInRange("pocket_gen_burn_speed_multiplier", 4, 1, 1000);
        POCKET_GENERATOR_MAX_FE = COMMON_BUILDER.comment("The maximum amount of Forge Energy the generator can hold in its buffer")
                .defineInRange("pocket_gen_max_fe", 1000000, 1, Integer.MAX_VALUE);
        POCKET_GENERATOR_FE_PER_TICK = COMMON_BUILDER.comment("The FE per Tick that the generator charges other items at")
                .defineInRange("pocket_gen_fe_per_tick", 5000, 1, Integer.MAX_VALUE);
        COMMON_BUILDER.pop();
    }

    private static void timeCrystalConfig() {
        COMMON_BUILDER.comment("Time Crystals").push(CATEGORY_TIME_CRYSTAL);
        TIME_CRYSTAL_CUSTOM_DIMENSIONS = COMMON_BUILDER.comment("Do you want to customize Time Crystal Growth Dimensions? If set to true, the following 3 fields MUST be populated. Defaults to false, which means normal growth rules: Stage 1 = Overworld, Stage 2 = Nether, Stage 3 = End.")
                .define("time_crystal_custom_dimensions", false);
        TIME_CRYSTAL_STAGE1_DIMENSIONS = COMMON_BUILDER.comment("Dimensions where Stage 0 -> Stage 1 growth occurs. Example: [\"minecraft:overworld\"]")
                .defineListAllowEmpty(List.of("time_crystal_stage1_dimensions"), () -> List.of("minecraft:overworld"), s -> s instanceof String);
        TIME_CRYSTAL_STAGE2_DIMENSIONS = COMMON_BUILDER.comment("Dimensions where Stage 1 -> Stage 2 growth occurs. Example: [\"minecraft:the_nether\"]")
                .defineListAllowEmpty(List.of("time_crystal_stage2_dimensions"), () -> List.of("minecraft:the_nether"), s -> s instanceof String);
        TIME_CRYSTAL_STAGE3_DIMENSIONS = COMMON_BUILDER.comment("Dimensions where Stage 2 -> Stage 3 growth occurs. Example: [\"minecraft:the_end\"]")
                .defineListAllowEmpty(List.of("time_crystal_stage3_dimensions"), () -> List.of("minecraft:the_end"), s -> s instanceof String);
        COMMON_BUILDER.pop();
    }
}
