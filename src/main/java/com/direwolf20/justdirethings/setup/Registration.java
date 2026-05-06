package com.direwolf20.justdirethings.setup;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.common.blockentities.*;
import com.direwolf20.justdirethings.common.blockentities.basebe.BaseMachineBE;
import com.direwolf20.justdirethings.common.blockentities.gooblocks.GooBlockBE_Tier1;
import com.direwolf20.justdirethings.common.blockentities.gooblocks.GooBlockBE_Tier2;
import com.direwolf20.justdirethings.common.blockentities.gooblocks.GooBlockBE_Tier3;
import com.direwolf20.justdirethings.common.blockentities.gooblocks.GooBlockBE_Tier4;
import com.direwolf20.justdirethings.common.blocks.*;
import com.direwolf20.justdirethings.common.blocks.gooblocks.*;
import com.direwolf20.justdirethings.common.blocks.resources.*;
import com.direwolf20.justdirethings.common.items.resources.TimeCrystal;
import com.direwolf20.justdirethings.common.blocks.soil.GooSoilTier1;
import com.direwolf20.justdirethings.common.blocks.soil.GooSoilTier2;
import com.direwolf20.justdirethings.common.blocks.soil.GooSoilTier3;
import com.direwolf20.justdirethings.common.blocks.soil.GooSoilTier4;
import com.direwolf20.justdirethings.common.containers.*;
import com.direwolf20.justdirethings.common.entities.CreatureCatcherEntity;
import com.direwolf20.justdirethings.common.entities.ParadoxEntity;
import com.direwolf20.justdirethings.common.entities.JustDireArrow;
import com.direwolf20.justdirethings.common.items.tools.BlazegoldBow;
import com.direwolf20.justdirethings.common.items.tools.CelestigemBow;
import com.direwolf20.justdirethings.common.items.tools.EclipseAlloyBow;
import com.direwolf20.justdirethings.common.items.tools.FerricoreBow;
import com.direwolf20.justdirethings.common.items.*;
import com.direwolf20.justdirethings.common.items.armors.BlazegoldBoots;
import com.direwolf20.justdirethings.common.items.armors.BlazegoldChestplate;
import com.direwolf20.justdirethings.common.items.armors.BlazegoldHelmet;
import com.direwolf20.justdirethings.common.items.armors.BlazegoldLeggings;
import com.direwolf20.justdirethings.common.items.armors.CelestigemBoots;
import com.direwolf20.justdirethings.common.items.armors.CelestigemChestplate;
import com.direwolf20.justdirethings.common.items.armors.CelestigemHelmet;
import com.direwolf20.justdirethings.common.items.armors.CelestigemLeggings;
import com.direwolf20.justdirethings.common.items.armors.EclipseAlloyBoots;
import com.direwolf20.justdirethings.common.items.armors.EclipseAlloyChestplate;
import com.direwolf20.justdirethings.common.items.armors.EclipseAlloyHelmet;
import com.direwolf20.justdirethings.common.items.armors.EclipseAlloyLeggings;
import com.direwolf20.justdirethings.common.items.armors.FerricoreBoots;
import com.direwolf20.justdirethings.common.items.armors.FerricoreChestplate;
import com.direwolf20.justdirethings.common.items.armors.FerricoreHelmet;
import com.direwolf20.justdirethings.common.items.armors.FerricoreLeggings;
import com.direwolf20.justdirethings.common.items.resources.*;
import com.direwolf20.justdirethings.common.items.tools.*;
import com.direwolf20.justdirethings.datagen.recipes.GooSpreadRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.direwolf20.justdirethings.JustDireThings.MODID;
import static com.direwolf20.justdirethings.client.particles.ModParticles.PARTICLE_TYPES;

public class Registration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Block> SIDEDBLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Item> TOOLS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Item> BOWS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<Item> ARMORS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    public static final Supplier<RecipeType<GooSpreadRecipe>> GOO_SPREAD_RECIPE_TYPE = RECIPE_TYPES.register("goospreadrecipe", () -> RecipeType.simple(new ResourceLocation(MODID, "goospreadrecipe")));

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, JustDireThings.MODID);
    public static final Supplier<GooSpreadRecipe.Serializer> GOO_SPREAD_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("goospread", GooSpreadRecipe.Serializer::new);

    public static void init(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        SIDEDBLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TOOLS.register(eventBus);
        BOWS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
        CONTAINERS.register(eventBus);
        RECIPE_SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
        PARTICLE_TYPES.register(eventBus);
        ENTITY_TYPES.register(eventBus);
        ARMORS.register(eventBus);
    }

    //Gooblocks
    public static final RegistryObject<GooBlock_Tier1> GooBlock_Tier1 = BLOCKS.register("gooblock_tier1", GooBlock_Tier1::new);
    public static final RegistryObject<BlockItem> GooBlock_Tier1_ITEM = ITEMS.register("gooblock_tier1", () -> new BlockItem(GooBlock_Tier1.get(), new Item.Properties()));
    public static final RegistryObject<GooBlock_Tier2> GooBlock_Tier2 = BLOCKS.register("gooblock_tier2", GooBlock_Tier2::new);
    public static final RegistryObject<BlockItem> GooBlock_Tier2_ITEM = ITEMS.register("gooblock_tier2", () -> new BlockItem(GooBlock_Tier2.get(), new Item.Properties()));
    public static final RegistryObject<GooBlock_Tier3> GooBlock_Tier3 = BLOCKS.register("gooblock_tier3", GooBlock_Tier3::new);
    public static final RegistryObject<BlockItem> GooBlock_Tier3_ITEM = ITEMS.register("gooblock_tier3", () -> new BlockItem(GooBlock_Tier3.get(), new Item.Properties()));
    public static final RegistryObject<GooBlock_Tier4> GooBlock_Tier4 = BLOCKS.register("gooblock_tier4", GooBlock_Tier4::new);
    public static final RegistryObject<BlockItem> GooBlock_Tier4_ITEM = ITEMS.register("gooblock_tier4", () -> new BlockItem(GooBlock_Tier4.get(), new Item.Properties()));

    public static final RegistryObject<GooPatternBlock> GooPatternBlock = BLOCKS.register("goopatternblock", GooPatternBlock::new);


    //Blocks
    public static final RegistryObject<GooSoilTier1> GooSoil_Tier1 = BLOCKS.register("goosoil_tier1", GooSoilTier1::new);
    public static final RegistryObject<BlockItem> GooSoil_ITEM_Tier1 = ITEMS.register("goosoil_tier1", () -> new BlockItem(GooSoil_Tier1.get(), new Item.Properties()));
    public static final RegistryObject<GooSoilTier2> GooSoil_Tier2 = BLOCKS.register("goosoil_tier2", GooSoilTier2::new);
    public static final RegistryObject<BlockItem> GooSoil_ITEM_Tier2 = ITEMS.register("goosoil_tier2", () -> new BlockItem(GooSoil_Tier2.get(), new Item.Properties()));
    public static final RegistryObject<GooSoilTier3> GooSoil_Tier3 = BLOCKS.register("goosoil_tier3", GooSoilTier3::new);
    public static final RegistryObject<BlockItem> GooSoil_ITEM_Tier3 = ITEMS.register("goosoil_tier3", () -> new BlockItem(GooSoil_Tier3.get(), new Item.Properties()));
    public static final RegistryObject<GooSoilTier4> GooSoil_Tier4 = BLOCKS.register("goosoil_tier4", GooSoilTier4::new);
    public static final RegistryObject<BlockItem> GooSoil_ITEM_Tier4 = ITEMS.register("goosoil_tier4", () -> new BlockItem(GooSoil_Tier4.get(), new Item.Properties()));
    public static final RegistryObject<EclipseGateBlock> EclipseGateBlock = BLOCKS.register("eclipsegateblock", EclipseGateBlock::new);

    //Machines
    public static final RegistryObject<ItemCollector> ItemCollector = BLOCKS.register("itemcollector", ItemCollector::new);
    public static final RegistryObject<BlockItem> ItemCollector_ITEM = ITEMS.register("itemcollector", () -> new BlockItem(ItemCollector.get(), new Item.Properties()));
    public static final RegistryObject<BlockBreakerT1> BlockBreakerT1 = SIDEDBLOCKS.register("blockbreakert1", BlockBreakerT1::new);
    public static final RegistryObject<BlockItem> BlockBreakerT1_ITEM = ITEMS.register("blockbreakert1", () -> new BlockItem(BlockBreakerT1.get(), new Item.Properties()));
    public static final RegistryObject<BlockBreakerT2> BlockBreakerT2 = BLOCKS.register("blockbreakert2", BlockBreakerT2::new);
    public static final RegistryObject<BlockItem> BlockBreakerT2_ITEM = ITEMS.register("blockbreakert2", () -> new BlockItem(BlockBreakerT2.get(), new Item.Properties()));
    public static final RegistryObject<BlockPlacerT1> BlockPlacerT1 = SIDEDBLOCKS.register("blockplacert1", BlockPlacerT1::new);
    public static final RegistryObject<BlockItem> BlockPlacerT1_ITEM = ITEMS.register("blockplacert1", () -> new BlockItem(BlockPlacerT1.get(), new Item.Properties()));
    public static final RegistryObject<BlockPlacerT2> BlockPlacerT2 = BLOCKS.register("blockplacert2", BlockPlacerT2::new);
    public static final RegistryObject<BlockItem> BlockPlacerT2_ITEM = ITEMS.register("blockplacert2", () -> new BlockItem(BlockPlacerT2.get(), new Item.Properties()));
    public static final RegistryObject<ClickerT1> ClickerT1 = SIDEDBLOCKS.register("clickert1", ClickerT1::new);
    public static final RegistryObject<BlockItem> ClickerT1_ITEM = ITEMS.register("clickert1", () -> new BlockItem(ClickerT1.get(), new Item.Properties()));
    public static final RegistryObject<ClickerT2> ClickerT2 = BLOCKS.register("clickert2", ClickerT2::new);
    public static final RegistryObject<BlockItem> ClickerT2_ITEM = ITEMS.register("clickert2", () -> new BlockItem(ClickerT2.get(), new Item.Properties()));
    public static final RegistryObject<SensorT1> SensorT1 = SIDEDBLOCKS.register("sensort1", SensorT1::new);
    public static final RegistryObject<BlockItem> SensorT1_ITEM = ITEMS.register("sensort1", () -> new BlockItem(SensorT1.get(), new Item.Properties()));
    public static final RegistryObject<SensorT2> SensorT2 = BLOCKS.register("sensort2", SensorT2::new);
    public static final RegistryObject<BlockItem> SensorT2_ITEM = ITEMS.register("sensort2", () -> new BlockItem(SensorT2.get(), new Item.Properties()));
    public static final RegistryObject<DropperT1> DropperT1 = SIDEDBLOCKS.register("droppert1", DropperT1::new);
    public static final RegistryObject<BlockItem> DropperT1_ITEM = ITEMS.register("droppert1", () -> new BlockItem(DropperT1.get(), new Item.Properties()));
    public static final RegistryObject<DropperT2> DropperT2 = BLOCKS.register("droppert2", DropperT2::new);
    public static final RegistryObject<BlockItem> DropperT2_ITEM = ITEMS.register("droppert2", () -> new BlockItem(DropperT2.get(), new Item.Properties()));
    public static final RegistryObject<BlockSwapperT1> BlockSwapperT1 = SIDEDBLOCKS.register("blockswappert1", BlockSwapperT1::new);
    public static final RegistryObject<BlockItem> BlockSwapperT1_ITEM = ITEMS.register("blockswappert1", () -> new BlockItem(BlockSwapperT1.get(), new Item.Properties()));
    public static final RegistryObject<BlockSwapperT2> BlockSwapperT2 = BLOCKS.register("blockswappert2", BlockSwapperT2::new);
    public static final RegistryObject<BlockItem> BlockSwapperT2_ITEM = ITEMS.register("blockswappert2", () -> new BlockItem(BlockSwapperT2.get(), new Item.Properties()));
    public static final RegistryObject<PlayerAccessor> PlayerAccessor = BLOCKS.register("playeraccessor", PlayerAccessor::new);
    public static final RegistryObject<BlockItem> PlayerAccessor_ITEM = ITEMS.register("playeraccessor", () -> new BlockItem(PlayerAccessor.get(), new Item.Properties()));

    //Power Machines
    public static final RegistryObject<GeneratorT1> GeneratorT1 = BLOCKS.register("generatort1", GeneratorT1::new);
    public static final RegistryObject<BlockItem> GeneratorT1_ITEM = ITEMS.register("generatort1", () -> new BlockItem(GeneratorT1.get(), new Item.Properties()));
    public static final RegistryObject<EnergyTransmitter> EnergyTransmitter = BLOCKS.register("energytransmitter", EnergyTransmitter::new);
    public static final RegistryObject<BlockItem> EnergyTransmitter_ITEM = ITEMS.register("energytransmitter", () -> new BlockItem(EnergyTransmitter.get(), new Item.Properties()));

    //Blocks - Raw Resources
    public static final RegistryObject<RawFerricoreOre> RawFerricoreOre = BLOCKS.register("raw_ferricore_ore", RawFerricoreOre::new);
    public static final RegistryObject<BlockItem> RawFerricoreOre_ITEM = ITEMS.register("raw_ferricore_ore", () -> new BlockItem(RawFerricoreOre.get(), new Item.Properties()));
    public static final RegistryObject<RawBlazegoldOre> RawBlazegoldOre = BLOCKS.register("raw_blazegold_ore", RawBlazegoldOre::new);
    public static final RegistryObject<BlockItem> RawBlazegoldOre_ITEM = ITEMS.register("raw_blazegold_ore", () -> new BlockItem(RawBlazegoldOre.get(), new Item.Properties()));
    public static final RegistryObject<RawCelestigemOre> RawCelestigemOre = BLOCKS.register("raw_celestigem_ore", RawCelestigemOre::new);
    public static final RegistryObject<BlockItem> RawCelestigemOre_ITEM = ITEMS.register("raw_celestigem_ore", () -> new BlockItem(RawCelestigemOre.get(), new Item.Properties()));
    public static final RegistryObject<RawEclipseAlloyOre> RawEclipseAlloyOre = BLOCKS.register("raw_eclipsealloy_ore", RawEclipseAlloyOre::new);
    public static final RegistryObject<BlockItem> RawEclipseAlloyOre_ITEM = ITEMS.register("raw_eclipsealloy_ore", () -> new BlockItem(RawEclipseAlloyOre.get(), new Item.Properties()));
    public static final RegistryObject<RawCoal_T1> RawCoal_T1 = BLOCKS.register("raw_coal_t1_ore", RawCoal_T1::new);
    public static final RegistryObject<BlockItem> RawCoal_T1_ITEM = ITEMS.register("raw_coal_t1_ore", () -> new BlockItem(RawCoal_T1.get(), new Item.Properties()));
    public static final RegistryObject<RawCoal_T2> RawCoal_T2 = BLOCKS.register("raw_coal_t2_ore", RawCoal_T2::new);
    public static final RegistryObject<BlockItem> RawCoal_T2_ITEM = ITEMS.register("raw_coal_t2_ore", () -> new BlockItem(RawCoal_T2.get(), new Item.Properties()));
    public static final RegistryObject<RawCoal_T3> RawCoal_T3 = BLOCKS.register("raw_coal_t3_ore", RawCoal_T3::new);
    public static final RegistryObject<BlockItem> RawCoal_T3_ITEM = ITEMS.register("raw_coal_t3_ore", () -> new BlockItem(RawCoal_T3.get(), new Item.Properties()));
    public static final RegistryObject<RawCoal_T4> RawCoal_T4 = BLOCKS.register("raw_coal_t4_ore", RawCoal_T4::new);
    public static final RegistryObject<BlockItem> RawCoal_T4_ITEM = ITEMS.register("raw_coal_t4_ore", () -> new BlockItem(RawCoal_T4.get(), new Item.Properties()));

    //Blocks - Time Crystal
    public static final RegistryObject<TimeCrystalBlock> TimeCrystalBlock = BLOCKS.register("time_crystal_block", TimeCrystalBlock::new);
    public static final RegistryObject<BlockItem> TimeCrystalBlock_ITEM = ITEMS.register("time_crystal_block", () -> new BlockItem(TimeCrystalBlock.get(), new Item.Properties()));
    public static final RegistryObject<TimeCrystalBuddingBlock> TimeCrystalBuddingBlock = BLOCKS.register("time_crystal_budding_block", TimeCrystalBuddingBlock::new);
    public static final RegistryObject<BlockItem> TimeCrystalBuddingBlock_ITEM = ITEMS.register("time_crystal_budding_block", () -> new BlockItem(TimeCrystalBuddingBlock.get(), new Item.Properties()));
    public static final RegistryObject<TimeCrystalCluster> TimeCrystalCluster = BLOCKS.register("time_crystal_cluster", () -> new TimeCrystalCluster(
            7, 3,
            Block.Properties.of()
                    .forceSolidOn()
                    .noOcclusion()
                    .sound(SoundType.AMETHYST_CLUSTER)
                    .strength(1.5F)
                    .lightLevel(s -> 5)
                    .pushReaction(PushReaction.DESTROY)
    ));
    public static final RegistryObject<BlockItem> TimeCrystalCluster_ITEM = ITEMS.register("time_crystal_cluster", () -> new BlockItem(TimeCrystalCluster.get(), new Item.Properties()));
    public static final RegistryObject<TimeCrystalCluster> TimeCrystalCluster_Small = BLOCKS.register("time_crystal_cluster_small", () -> new TimeCrystalCluster(
            3, 4, Block.Properties.copy(TimeCrystalCluster.get()).sound(SoundType.SMALL_AMETHYST_BUD).lightLevel(s -> 1)
    ));
    public static final RegistryObject<BlockItem> TimeCrystalCluster_Small_ITEM = ITEMS.register("time_crystal_cluster_small", () -> new BlockItem(TimeCrystalCluster_Small.get(), new Item.Properties()));
    public static final RegistryObject<TimeCrystalCluster> TimeCrystalCluster_Medium = BLOCKS.register("time_crystal_cluster_medium", () -> new TimeCrystalCluster(
            4, 3, Block.Properties.copy(TimeCrystalCluster.get()).sound(SoundType.MEDIUM_AMETHYST_BUD).lightLevel(s -> 2)
    ));
    public static final RegistryObject<BlockItem> TimeCrystalCluster_Medium_ITEM = ITEMS.register("time_crystal_cluster_medium", () -> new BlockItem(TimeCrystalCluster_Medium.get(), new Item.Properties()));
    public static final RegistryObject<TimeCrystalCluster> TimeCrystalCluster_Large = BLOCKS.register("time_crystal_cluster_large", () -> new TimeCrystalCluster(
            5, 3, Block.Properties.copy(TimeCrystalCluster.get()).sound(SoundType.LARGE_AMETHYST_BUD).lightLevel(s -> 4)
    ));
    public static final RegistryObject<BlockItem> TimeCrystalCluster_Large_ITEM = ITEMS.register("time_crystal_cluster_large", () -> new BlockItem(TimeCrystalCluster_Large.get(), new Item.Properties()));

    //Blocks - Charcoal
    public static final RegistryObject<CharcoalBlock> CharcoalBlock = BLOCKS.register("charcoal", CharcoalBlock::new);
    public static final RegistryObject<BlockItem> CharcoalBlock_ITEM = ITEMS.register("charcoal", () -> new BlockItem(CharcoalBlock.get(), new Item.Properties()));

    //Blocks Consolidated Resources
    public static final RegistryObject<FerricoreBlock> FerricoreBlock = BLOCKS.register("ferricore_block", FerricoreBlock::new);
    public static final RegistryObject<BlockItem> FerricoreBlock_ITEM = ITEMS.register("ferricore_block", () -> new BlockItem(FerricoreBlock.get(), new Item.Properties()));
    public static final RegistryObject<BlazeGoldBlock> BlazeGoldBlock = BLOCKS.register("blazegold_block", BlazeGoldBlock::new);
    public static final RegistryObject<BlockItem> BlazeGoldBlock_ITEM = ITEMS.register("blazegold_block", () -> new BlockItem(BlazeGoldBlock.get(), new Item.Properties()));
    public static final RegistryObject<CelestigemBlock> CelestigemBlock = BLOCKS.register("celestigem_block", CelestigemBlock::new);
    public static final RegistryObject<BlockItem> CelestigemBlock_ITEM = ITEMS.register("celestigem_block", () -> new BlockItem(CelestigemBlock.get(), new Item.Properties()));
    public static final RegistryObject<EclipseAlloyBlock> EclipseAlloyBlock = BLOCKS.register("eclipsealloy_block", EclipseAlloyBlock::new);
    public static final RegistryObject<BlockItem> EclipseAlloyBlock_ITEM = ITEMS.register("eclipsealloy_block", () -> new BlockItem(EclipseAlloyBlock.get(), new Item.Properties()));
    public static final RegistryObject<CoalBlock_T1> CoalBlock_T1 = BLOCKS.register("coalblock_t1", CoalBlock_T1::new);
    public static final RegistryObject<BlockItem> CoalBlock_T1_ITEM = ITEMS.register("coalblock_t1", () -> new BlockItem(CoalBlock_T1.get(), new Item.Properties()));
    public static final RegistryObject<CoalBlock_T2> CoalBlock_T2 = BLOCKS.register("coalblock_t2", CoalBlock_T2::new);
    public static final RegistryObject<BlockItem> CoalBlock_T2_ITEM = ITEMS.register("coalblock_t2", () -> new BlockItem(CoalBlock_T2.get(), new Item.Properties()));
    public static final RegistryObject<CoalBlock_T3> CoalBlock_T3 = BLOCKS.register("coalblock_t3", CoalBlock_T3::new);
    public static final RegistryObject<BlockItem> CoalBlock_T3_ITEM = ITEMS.register("coalblock_t3", () -> new BlockItem(CoalBlock_T3.get(), new Item.Properties()));
    public static final RegistryObject<CoalBlock_T4> CoalBlock_T4 = BLOCKS.register("coalblock_t4", CoalBlock_T4::new);
    public static final RegistryObject<BlockItem> CoalBlock_T4_ITEM = ITEMS.register("coalblock_t4", () -> new BlockItem(CoalBlock_T4.get(), new Item.Properties()));

    //BlockEntities (Not TileEntities - Honest)
    public static final RegistryObject<BlockEntityType<GooBlockBE_Tier1>> GooBlockBE_Tier1 = BLOCK_ENTITIES.register("gooblock_tier1", () -> BlockEntityType.Builder.of(GooBlockBE_Tier1::new, GooBlock_Tier1.get()).build(null));
    public static final RegistryObject<BlockEntityType<GooBlockBE_Tier2>> GooBlockBE_Tier2 = BLOCK_ENTITIES.register("gooblock_tier2", () -> BlockEntityType.Builder.of(GooBlockBE_Tier2::new, GooBlock_Tier2.get()).build(null));
    public static final RegistryObject<BlockEntityType<GooBlockBE_Tier3>> GooBlockBE_Tier3 = BLOCK_ENTITIES.register("gooblock_tier3", () -> BlockEntityType.Builder.of(GooBlockBE_Tier3::new, GooBlock_Tier3.get()).build(null));
    public static final RegistryObject<BlockEntityType<GooBlockBE_Tier4>> GooBlockBE_Tier4 = BLOCK_ENTITIES.register("gooblock_tier4", () -> BlockEntityType.Builder.of(GooBlockBE_Tier4::new, GooBlock_Tier4.get()).build(null));
    public static final RegistryObject<BlockEntityType<GooSoilBE>> GooSoilBE = BLOCK_ENTITIES.register("goosoilbe", () -> BlockEntityType.Builder.of(GooSoilBE::new, GooSoil_Tier3.get(), GooSoil_Tier4.get()).build(null));
    public static final RegistryObject<BlockEntityType<ItemCollectorBE>> ItemCollectorBE = BLOCK_ENTITIES.register("itemcollectorbe", () -> BlockEntityType.Builder.of(ItemCollectorBE::new, ItemCollector.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockBreakerT1BE>> BlockBreakerT1BE = BLOCK_ENTITIES.register("blockbreakert1", () -> BlockEntityType.Builder.of(BlockBreakerT1BE::new, BlockBreakerT1.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockBreakerT2BE>> BlockBreakerT2BE = BLOCK_ENTITIES.register("blockbreakert2", () -> BlockEntityType.Builder.of(BlockBreakerT2BE::new, BlockBreakerT2.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockPlacerT1BE>> BlockPlacerT1BE = BLOCK_ENTITIES.register("blockplacert1", () -> BlockEntityType.Builder.of(BlockPlacerT1BE::new, BlockPlacerT1.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockPlacerT2BE>> BlockPlacerT2BE = BLOCK_ENTITIES.register("blockplacert2", () -> BlockEntityType.Builder.of(BlockPlacerT2BE::new, BlockPlacerT2.get()).build(null));
    public static final RegistryObject<BlockEntityType<ClickerT1BE>> ClickerT1BE = BLOCK_ENTITIES.register("clickert1", () -> BlockEntityType.Builder.of(ClickerT1BE::new, ClickerT1.get()).build(null));
    public static final RegistryObject<BlockEntityType<ClickerT2BE>> ClickerT2BE = BLOCK_ENTITIES.register("clickert2", () -> BlockEntityType.Builder.of(ClickerT2BE::new, ClickerT2.get()).build(null));
    public static final RegistryObject<BlockEntityType<SensorT1BE>> SensorT1BE = BLOCK_ENTITIES.register("sensort1be", () -> BlockEntityType.Builder.of(SensorT1BE::new, SensorT1.get()).build(null));
    public static final RegistryObject<BlockEntityType<SensorT2BE>> SensorT2BE = BLOCK_ENTITIES.register("sensort2be", () -> BlockEntityType.Builder.of(SensorT2BE::new, SensorT2.get()).build(null));
    public static final RegistryObject<BlockEntityType<DropperT1BE>> DropperT1BE = BLOCK_ENTITIES.register("droppert1", () -> BlockEntityType.Builder.of(DropperT1BE::new, DropperT1.get()).build(null));
    public static final RegistryObject<BlockEntityType<DropperT2BE>> DropperT2BE = BLOCK_ENTITIES.register("droppert2", () -> BlockEntityType.Builder.of(DropperT2BE::new, DropperT2.get()).build(null));
    public static final RegistryObject<BlockEntityType<GeneratorT1BE>> GeneratorT1BE = BLOCK_ENTITIES.register("generatort1", () -> BlockEntityType.Builder.of(GeneratorT1BE::new, GeneratorT1.get()).build(null));
    public static final RegistryObject<BlockEntityType<EnergyTransmitterBE>> EnergyTransmitterBE = BLOCK_ENTITIES.register("energytransmitter", () -> BlockEntityType.Builder.of(EnergyTransmitterBE::new, EnergyTransmitter.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockSwapperT1BE>> BlockSwapperT1BE = BLOCK_ENTITIES.register("blockswappert1", () -> BlockEntityType.Builder.of(BlockSwapperT1BE::new, BlockSwapperT1.get()).build(null));
    public static final RegistryObject<BlockEntityType<BlockSwapperT2BE>> BlockSwapperT2BE = BLOCK_ENTITIES.register("blockswappert2", () -> BlockEntityType.Builder.of(BlockSwapperT2BE::new, BlockSwapperT2.get()).build(null));
    public static final RegistryObject<BlockEntityType<PlayerAccessorBE>> PlayerAccessorBE = BLOCK_ENTITIES.register("playeraccessorbe", () -> BlockEntityType.Builder.of(PlayerAccessorBE::new, PlayerAccessor.get()).build(null));
    public static final RegistryObject<BlockEntityType<EclipseGateBE>> EclipseGateBE = BLOCK_ENTITIES.register("eclipsegatebe", () -> BlockEntityType.Builder.of(EclipseGateBE::new, EclipseGateBlock.get()).build(null));

    //Items - Time Crystal
    public static final RegistryObject<TimeCrystal> TimeCrystal = ITEMS.register("time_crystal", TimeCrystal::new);

    //Items - Raw Resources
    public static final RegistryObject<RawFerricore> RawFerricore = ITEMS.register("raw_ferricore", RawFerricore::new);
    public static final RegistryObject<RawBlazegold> RawBlazegold = ITEMS.register("raw_blazegold", RawBlazegold::new);
    public static final RegistryObject<RawEclipseAlloy> RawEclipseAlloy = ITEMS.register("raw_eclipsealloy", RawEclipseAlloy::new);

    //Items - Resources
    public static final RegistryObject<FerricoreIngot> FerricoreIngot = ITEMS.register("ferricore_ingot", FerricoreIngot::new);
    public static final RegistryObject<BlazeGoldIngot> BlazegoldIngot = ITEMS.register("blazegold_ingot", BlazeGoldIngot::new);
    public static final RegistryObject<Celestigem> Celestigem = ITEMS.register("celestigem", Celestigem::new);
    public static final RegistryObject<EclipseAlloyIngot> EclipseAlloyIngot = ITEMS.register("eclipsealloy_ingot", EclipseAlloyIngot::new);
    public static final RegistryObject<Coal_T1> Coal_T1 = ITEMS.register("coal_t1", Coal_T1::new);
    public static final RegistryObject<Coal_T2> Coal_T2 = ITEMS.register("coal_t2", Coal_T2::new);
    public static final RegistryObject<Coal_T3> Coal_T3 = ITEMS.register("coal_t3", Coal_T3::new);
    public static final RegistryObject<Coal_T4> Coal_T4 = ITEMS.register("coal_t4", Coal_T4::new);

    //Items
    public static final RegistryObject<FuelCanister> Fuel_Canister = ITEMS.register("fuel_canister", FuelCanister::new);
    public static final RegistryObject<PocketGenerator> Pocket_Generator = ITEMS.register("pocket_generator", PocketGenerator::new);

    public static final RegistryObject<FerricoreWrench> FerricoreWrench = ITEMS.register("ferricore_wrench", FerricoreWrench::new);
    public static final RegistryObject<TotemOfDeathRecall> TotemOfDeathRecall = ITEMS.register("totem_of_death_recall", TotemOfDeathRecall::new);
    public static final RegistryObject<BlazejetWand> BlazejetWand = ITEMS.register("blazejet_wand", BlazejetWand::new);
    public static final RegistryObject<VoidshiftWand> VoidshiftWand = ITEMS.register("voidshift_wand", VoidshiftWand::new);
    public static final RegistryObject<EclipsegateWand> EclipsegateWand = ITEMS.register("eclipsegate_wand", EclipsegateWand::new);
    public static final RegistryObject<CreatureCatcher> CreatureCatcher = ITEMS.register("creaturecatcher", CreatureCatcher::new);

    //Items - Tools
    public static final RegistryObject<FerricoreSword> FerricoreSword = TOOLS.register("ferricore_sword", FerricoreSword::new);
    public static final RegistryObject<FerricorePickaxe> FerricorePickaxe = TOOLS.register("ferricore_pickaxe", FerricorePickaxe::new);
    public static final RegistryObject<FerricoreShovel> FerricoreShovel = TOOLS.register("ferricore_shovel", FerricoreShovel::new);
    public static final RegistryObject<FerricoreAxe> FerricoreAxe = TOOLS.register("ferricore_axe", FerricoreAxe::new);
    public static final RegistryObject<FerricoreHoe> FerricoreHoe = TOOLS.register("ferricore_hoe", FerricoreHoe::new);
    public static final RegistryObject<BlazegoldSword> BlazegoldSword = TOOLS.register("blazegold_sword", BlazegoldSword::new);
    public static final RegistryObject<BlazegoldPickaxe> BlazegoldPickaxe = TOOLS.register("blazegold_pickaxe", BlazegoldPickaxe::new);
    public static final RegistryObject<BlazegoldShovel> BlazegoldShovel = TOOLS.register("blazegold_shovel", BlazegoldShovel::new);
    public static final RegistryObject<BlazegoldAxe> BlazegoldAxe = TOOLS.register("blazegold_axe", BlazegoldAxe::new);
    public static final RegistryObject<BlazegoldHoe> BlazegoldHoe = TOOLS.register("blazegold_hoe", BlazegoldHoe::new);
    public static final RegistryObject<CelestigemSword> CelestigemSword = TOOLS.register("celestigem_sword", CelestigemSword::new);
    public static final RegistryObject<CelestigemPickaxe> CelestigemPickaxe = TOOLS.register("celestigem_pickaxe", CelestigemPickaxe::new);
    public static final RegistryObject<CelestigemShovel> CelestigemShovel = TOOLS.register("celestigem_shovel", CelestigemShovel::new);
    public static final RegistryObject<CelestigemAxe> CelestigemAxe = TOOLS.register("celestigem_axe", CelestigemAxe::new);
    public static final RegistryObject<CelestigemHoe> CelestigemHoe = TOOLS.register("celestigem_hoe", CelestigemHoe::new);
    public static final RegistryObject<CelestigemPaxel> CelestigemPaxel = TOOLS.register("celestigem_paxel", CelestigemPaxel::new);
    public static final RegistryObject<EclipseAlloySword> EclipseAlloySword = TOOLS.register("eclipsealloy_sword", EclipseAlloySword::new);
    public static final RegistryObject<EclipseAlloyPickaxe> EclipseAlloyPickaxe = TOOLS.register("eclipsealloy_pickaxe", EclipseAlloyPickaxe::new);
    public static final RegistryObject<EclipseAlloyShovel> EclipseAlloyShovel = TOOLS.register("eclipsealloy_shovel", EclipseAlloyShovel::new);
    public static final RegistryObject<EclipseAlloyAxe> EclipseAlloyAxe = TOOLS.register("eclipsealloy_axe", EclipseAlloyAxe::new);
    public static final RegistryObject<EclipseAlloyHoe> EclipseAlloyHoe = TOOLS.register("eclipsealloy_hoe", EclipseAlloyHoe::new);
    public static final RegistryObject<EclipseAlloyPaxel> EclipseAlloyPaxel = TOOLS.register("eclipsealloy_paxel", EclipseAlloyPaxel::new);

    //Items - Bows
    public static final RegistryObject<FerricoreBow> FerricoreBow = BOWS.register("bow_ferricore", FerricoreBow::new);
    public static final RegistryObject<BlazegoldBow> BlazegoldBow = BOWS.register("bow_blazegold", BlazegoldBow::new);
    public static final RegistryObject<CelestigemBow> CelestigemBow = BOWS.register("bow_celestigem", CelestigemBow::new);
    public static final RegistryObject<EclipseAlloyBow> EclipseAlloyBow = BOWS.register("bow_eclipsealloy", EclipseAlloyBow::new);

    //Items - Armor
    public static final RegistryObject<FerricoreBoots> FerricoreBoots = ARMORS.register("ferricore_boots", FerricoreBoots::new);
    public static final RegistryObject<FerricoreChestplate> FerricoreChestplate = ARMORS.register("ferricore_chestplate", FerricoreChestplate::new);
    public static final RegistryObject<FerricoreLeggings> FerricoreLeggings = ARMORS.register("ferricore_leggings", FerricoreLeggings::new);
    public static final RegistryObject<FerricoreHelmet> FerricoreHelmet = ARMORS.register("ferricore_helmet", FerricoreHelmet::new);

    public static final RegistryObject<BlazegoldBoots> BlazegoldBoots = ARMORS.register("blazegold_boots", BlazegoldBoots::new);
    public static final RegistryObject<BlazegoldChestplate> BlazegoldChestplate = ARMORS.register("blazegold_chestplate", BlazegoldChestplate::new);
    public static final RegistryObject<BlazegoldLeggings> BlazegoldLeggings = ARMORS.register("blazegold_leggings", BlazegoldLeggings::new);
    public static final RegistryObject<BlazegoldHelmet> BlazegoldHelmet = ARMORS.register("blazegold_helmet", BlazegoldHelmet::new);

    public static final RegistryObject<CelestigemBoots> CelestigemBoots = ARMORS.register("celestigem_boots", CelestigemBoots::new);
    public static final RegistryObject<CelestigemChestplate> CelestigemChestplate = ARMORS.register("celestigem_chestplate", CelestigemChestplate::new);
    public static final RegistryObject<CelestigemLeggings> CelestigemLeggings = ARMORS.register("celestigem_leggings", CelestigemLeggings::new);
    public static final RegistryObject<CelestigemHelmet> CelestigemHelmet = ARMORS.register("celestigem_helmet", CelestigemHelmet::new);

    public static final RegistryObject<EclipseAlloyBoots> EclipseAlloyBoots = ARMORS.register("eclipsealloy_boots", EclipseAlloyBoots::new);
    public static final RegistryObject<EclipseAlloyChestplate> EclipseAlloyChestplate = ARMORS.register("eclipsealloy_chestplate", EclipseAlloyChestplate::new);
    public static final RegistryObject<EclipseAlloyLeggings> EclipseAlloyLeggings = ARMORS.register("eclipsealloy_leggings", EclipseAlloyLeggings::new);
    public static final RegistryObject<EclipseAlloyHelmet> EclipseAlloyHelmet = ARMORS.register("eclipsealloy_helmet", EclipseAlloyHelmet::new);

    //Entities
    public static final RegistryObject<EntityType<CreatureCatcherEntity>> CreatureCatcherEntity = ENTITY_TYPES.register("creature_catcher",
            () -> EntityType.Builder.<CreatureCatcherEntity>of(CreatureCatcherEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("creature_catcher"));

    public static final RegistryObject<EntityType<JustDireArrow>> JustDireArrow = ENTITY_TYPES.register("justdire_arrow",
            () -> EntityType.Builder.<JustDireArrow>of(JustDireArrow::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("justdire_arrow"));

    //Containers
    public static final RegistryObject<MenuType<FuelCanisterContainer>> FuelCanister_Container = CONTAINERS.register("fuelcanister",
            () -> IForgeMenuType.create((windowId, inv, data) -> new FuelCanisterContainer(windowId, inv, inv.player, data)));
    public static final RegistryObject<MenuType<PocketGeneratorContainer>> PocketGenerator_Container = CONTAINERS.register("pocketgenerator",
            () -> IForgeMenuType.create((windowId, inv, data) -> new PocketGeneratorContainer(windowId, inv, inv.player, data)));
    public static final RegistryObject<MenuType<ToolSettingContainer>> Tool_Settings_Container = CONTAINERS.register("tool_settings",
            () -> IForgeMenuType.create((windowId, inv, data) -> new ToolSettingContainer(windowId, inv, inv.player, data)));
    public static final RegistryObject<MenuType<ItemCollectorContainer>> Item_Collector_Container = CONTAINERS.register("item_collector_container",
            () -> IForgeMenuType.create(ItemCollectorContainer::new));
    public static final RegistryObject<MenuType<BlockBreakerT1Container>> BlockBreakerT1_Container = CONTAINERS.register("blockbreakert1_container",
            () -> IForgeMenuType.create(BlockBreakerT1Container::new));
    public static final RegistryObject<MenuType<BlockBreakerT2Container>> BlockBreakerT2_Container = CONTAINERS.register("blockbreakert2_container",
            () -> IForgeMenuType.create(BlockBreakerT2Container::new));
    public static final RegistryObject<MenuType<BlockPlacerT1Container>> BlockPlacerT1_Container = CONTAINERS.register("blockplacert1_container",
            () -> IForgeMenuType.create(BlockPlacerT1Container::new));
    public static final RegistryObject<MenuType<BlockPlacerT2Container>> BlockPlacerT2_Container = CONTAINERS.register("blockplacert2_container",
            () -> IForgeMenuType.create(BlockPlacerT2Container::new));
    public static final RegistryObject<MenuType<ClickerT1Container>> ClickerT1_Container = CONTAINERS.register("clickert1_container",
            () -> IForgeMenuType.create(ClickerT1Container::new));
    public static final RegistryObject<MenuType<ClickerT2Container>> ClickerT2_Container = CONTAINERS.register("clickert2_container",
            () -> IForgeMenuType.create(ClickerT2Container::new));
    public static final RegistryObject<MenuType<SensorT1Container>> SensorT1_Container = CONTAINERS.register("sensort1_container",
            () -> IForgeMenuType.create(SensorT1Container::new));
    public static final RegistryObject<MenuType<SensorT2Container>> SensorT2_Container = CONTAINERS.register("sensort2_container",
            () -> IForgeMenuType.create(SensorT2Container::new));
    public static final RegistryObject<MenuType<DropperT1Container>> DropperT1_Container = CONTAINERS.register("droppert1_container",
            () -> IForgeMenuType.create(DropperT1Container::new));
    public static final RegistryObject<MenuType<DropperT2Container>> DropperT2_Container = CONTAINERS.register("droppert2_container",
            () -> IForgeMenuType.create(DropperT2Container::new));
    public static final RegistryObject<MenuType<GeneratorT1Container>> GeneratorT1_Container = CONTAINERS.register("generatort1_container",
            () -> IForgeMenuType.create(GeneratorT1Container::new));
    public static final RegistryObject<MenuType<EnergyTransmitterContainer>> EnergyTransmitter_Container = CONTAINERS.register("energytransmitter_container",
            () -> IForgeMenuType.create(EnergyTransmitterContainer::new));
    public static final RegistryObject<MenuType<BlockSwapperT1Container>> BlockSwapperT1_Container = CONTAINERS.register("blockswappert1_container",
            () -> IForgeMenuType.create(BlockSwapperT1Container::new));
    public static final RegistryObject<MenuType<BlockSwapperT2Container>> BlockSwapperT2_Container = CONTAINERS.register("blockswappert2_container",
            () -> IForgeMenuType.create(BlockSwapperT2Container::new));
    public static final RegistryObject<MenuType<PlayerAccessorContainer>> PlayerAccessor_Container = CONTAINERS.register("playeraccessor_container",
            () -> IForgeMenuType.create(PlayerAccessorContainer::new));

    // ── New Machines ──────────────────────────────────────────────────────────

    public static final RegistryObject<ExperienceHolder> ExperienceHolder = BLOCKS.register("experienceholder", ExperienceHolder::new);
    public static final RegistryObject<BlockItem> ExperienceHolder_ITEM = ITEMS.register("experienceholder", () -> new BlockItem(ExperienceHolder.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<ExperienceHolderBE>> ExperienceHolderBE = BLOCK_ENTITIES.register("experienceholderbe", () -> BlockEntityType.Builder.of(ExperienceHolderBE::new, ExperienceHolder.get()).build(null));
    public static final RegistryObject<MenuType<ExperienceHolderContainer>> ExperienceHolder_Container = CONTAINERS.register("experienceholder_container",
            () -> IForgeMenuType.create(ExperienceHolderContainer::new));

    public static final RegistryObject<FluidCollectorT1> FluidCollectorT1 = SIDEDBLOCKS.register("fluidcollectort1", FluidCollectorT1::new);
    public static final RegistryObject<BlockItem> FluidCollectorT1_ITEM = ITEMS.register("fluidcollectort1", () -> new BlockItem(FluidCollectorT1.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<FluidCollectorT1BE>> FluidCollectorT1BE = BLOCK_ENTITIES.register("fluidcollectort1", () -> BlockEntityType.Builder.of(FluidCollectorT1BE::new, FluidCollectorT1.get()).build(null));
    public static final RegistryObject<MenuType<FluidCollectorT1Container>> FluidCollectorT1_Container = CONTAINERS.register("fluidcollectort1_container",
            () -> IForgeMenuType.create(FluidCollectorT1Container::new));

    public static final RegistryObject<FluidCollectorT2> FluidCollectorT2 = BLOCKS.register("fluidcollectort2", FluidCollectorT2::new);
    public static final RegistryObject<BlockItem> FluidCollectorT2_ITEM = ITEMS.register("fluidcollectort2", () -> new BlockItem(FluidCollectorT2.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<FluidCollectorT2BE>> FluidCollectorT2BE = BLOCK_ENTITIES.register("fluidcollectort2", () -> BlockEntityType.Builder.of(FluidCollectorT2BE::new, FluidCollectorT2.get()).build(null));
    public static final RegistryObject<MenuType<FluidCollectorT2Container>> FluidCollectorT2_Container = CONTAINERS.register("fluidcollectort2_container",
            () -> IForgeMenuType.create(FluidCollectorT2Container::new));

    public static final RegistryObject<FluidPlacerT1> FluidPlacerT1 = SIDEDBLOCKS.register("fluidplacert1", FluidPlacerT1::new);
    public static final RegistryObject<BlockItem> FluidPlacerT1_ITEM = ITEMS.register("fluidplacert1", () -> new BlockItem(FluidPlacerT1.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<FluidPlacerT1BE>> FluidPlacerT1BE = BLOCK_ENTITIES.register("fluidplacert1", () -> BlockEntityType.Builder.of(FluidPlacerT1BE::new, FluidPlacerT1.get()).build(null));
    public static final RegistryObject<MenuType<FluidPlacerT1Container>> FluidPlacerT1_Container = CONTAINERS.register("fluidplacert1_container",
            () -> IForgeMenuType.create(FluidPlacerT1Container::new));

    public static final RegistryObject<FluidPlacerT2> FluidPlacerT2 = BLOCKS.register("fluidplacert2", FluidPlacerT2::new);
    public static final RegistryObject<BlockItem> FluidPlacerT2_ITEM = ITEMS.register("fluidplacert2", () -> new BlockItem(FluidPlacerT2.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<FluidPlacerT2BE>> FluidPlacerT2BE = BLOCK_ENTITIES.register("fluidplacert2", () -> BlockEntityType.Builder.of(FluidPlacerT2BE::new, FluidPlacerT2.get()).build(null));
    public static final RegistryObject<MenuType<FluidPlacerT2Container>> FluidPlacerT2_Container = CONTAINERS.register("fluidplacert2_container",
            () -> IForgeMenuType.create(FluidPlacerT2Container::new));

    public static final RegistryObject<GeneratorFluidT1> GeneratorFluidT1 = BLOCKS.register("generatorfluidt1", GeneratorFluidT1::new);
    public static final RegistryObject<BlockItem> GeneratorFluidT1_ITEM = ITEMS.register("generatorfluidt1", () -> new BlockItem(GeneratorFluidT1.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<GeneratorFluidT1BE>> GeneratorFluidT1BE = BLOCK_ENTITIES.register("generatorfluidt1", () -> BlockEntityType.Builder.of(GeneratorFluidT1BE::new, GeneratorFluidT1.get()).build(null));
    public static final RegistryObject<MenuType<GeneratorFluidT1Container>> GeneratorFluidT1_Container = CONTAINERS.register("generatorfluidt1_container",
            () -> IForgeMenuType.create(GeneratorFluidT1Container::new));

    public static final RegistryObject<InventoryHolder> InventoryHolder = BLOCKS.register("inventoryholder", InventoryHolder::new);
    public static final RegistryObject<BlockItem> InventoryHolder_ITEM = ITEMS.register("inventoryholder", () -> new BlockItem(InventoryHolder.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<InventoryHolderBE>> InventoryHolderBE = BLOCK_ENTITIES.register("inventoryholderbe", () -> BlockEntityType.Builder.of(InventoryHolderBE::new, InventoryHolder.get()).build(null));
    public static final RegistryObject<MenuType<InventoryHolderContainer>> InventoryHolder_Container = CONTAINERS.register("inventoryholder_container",
            () -> IForgeMenuType.create(InventoryHolderContainer::new));

    public static final RegistryObject<ParadoxMachine> ParadoxMachine = BLOCKS.register("paradoxmachine", ParadoxMachine::new);
    public static final RegistryObject<BlockItem> ParadoxMachine_ITEM = ITEMS.register("paradoxmachine", () -> new BlockItem(ParadoxMachine.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<ParadoxMachineBE>> ParadoxMachineBE = BLOCK_ENTITIES.register("paradoxmachinebe", () -> BlockEntityType.Builder.of(ParadoxMachineBE::new, ParadoxMachine.get()).build(null));
    public static final RegistryObject<MenuType<ParadoxMachineContainer>> ParadoxMachine_Container = CONTAINERS.register("paradoxmachine_container",
            () -> IForgeMenuType.create(ParadoxMachineContainer::new));

    public static final RegistryObject<EntityType<ParadoxEntity>> ParadoxEntity = ENTITY_TYPES.register("paradox_entity",
            () -> EntityType.Builder.<ParadoxEntity>of(ParadoxEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .updateInterval(3)
                    .build("paradox_entity"));

}
