package com.direwolf20.justdirethings.datagen;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("removal")
public class JustDireBlockTags extends BlockTagsProvider {

	public JustDireBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
			@Nullable ExistingFileHelper existingFileHelper) {
		super(output, lookupProvider, JustDireThings.MODID, existingFileHelper);
	}

	public static final TagKey<Block> LAWNMOWERABLE = BlockTags
			.create(new ResourceLocation(JustDireThings.MODID, "lawnmowerable"));
	public static final TagKey<Block> NO_AUTO_CLICK = BlockTags
			.create(new ResourceLocation(JustDireThings.MODID, "noautoclick"));
	public static final TagKey<Block> PARADOX_ALLOW = BlockTags
			.create(new ResourceLocation(JustDireThings.MODID, "paradox_allow"));
	public static final TagKey<Block> PARADOX_DENY = BlockTags
			.create(new ResourceLocation(JustDireThings.MODID, "paradox_deny"));
	public static final TagKey<Block> PARADOX_ABSORB_DENY = BlockTags
			.create(new ResourceLocation(JustDireThings.MODID, "paradox_absorb_deny"));
	public static final TagKey<Block> SWAPPERDENY = BlockTags
			.create(new ResourceLocation(JustDireThings.MODID, "swapper_deny"));
	public static final TagKey<Block> ECLISEGATEDENY = BlockTags
			.create(new ResourceLocation(JustDireThings.MODID, "eclipsegate_deny"));
	public static final TagKey<Block> NO_MOVE = BlockTags
			.create(new ResourceLocation("forge", "relocation_not_supported"));
	public static final TagKey<Block> TICK_SPEED_DENY = BlockTags
			.create(new ResourceLocation(JustDireThings.MODID, "tick_speed_deny"));
	public static final TagKey<Block> PHASEDENY = BlockTags
			.create(new ResourceLocation(JustDireThings.MODID, "phase_deny"));
	public static final TagKey<Block> BUDDING_BLOCKS = BlockTags
			.create(new ResourceLocation("forge", "budding_blocks"));
	public static final TagKey<Block> BUDS = BlockTags.create(new ResourceLocation("forge", "buds"));
	public static final TagKey<Block> CLUSTERS = BlockTags.create(new ResourceLocation("forge", "clusters"));

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.MINEABLE_WITH_SHOVEL).add(Registration.GooBlock_Tier1.get())
				.add(Registration.GooBlock_Tier2.get()).add(Registration.GooBlock_Tier3.get())
				.add(Registration.GooBlock_Tier4.get()).add(Registration.GooSoil_Tier1.get())
				.add(Registration.GooSoil_Tier2.get()).add(Registration.GooSoil_Tier3.get())
				.add(Registration.GooSoil_Tier4.get());
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Registration.FerricoreBlock.get())
				.add(Registration.RawFerricoreOre.get()).add(Registration.BlazeGoldBlock.get())
				.add(Registration.RawBlazegoldOre.get()).add(Registration.CelestigemBlock.get())
				.add(Registration.RawCelestigemOre.get()).add(Registration.EclipseAlloyBlock.get())
				.add(Registration.RawEclipseAlloyOre.get()).add(Registration.ItemCollector.get())
				.add(Registration.BlockBreakerT1.get()).add(Registration.BlockBreakerT2.get())
				.add(Registration.BlockPlacerT1.get()).add(Registration.BlockPlacerT2.get())
				.add(Registration.ClickerT1.get()).add(Registration.ClickerT2.get()).add(Registration.SensorT1.get())
				.add(Registration.SensorT2.get()).add(Registration.DropperT1.get()).add(Registration.DropperT2.get())
				.add(Registration.GeneratorT1.get()).add(Registration.EnergyTransmitter.get())
				.add(Registration.RawCoal_T1.get()).add(Registration.RawCoal_T2.get())
				.add(Registration.RawCoal_T3.get()).add(Registration.RawCoal_T4.get())
				.add(Registration.CoalBlock_T1.get()).add(Registration.CoalBlock_T2.get())
				.add(Registration.CoalBlock_T3.get()).add(Registration.CoalBlock_T4.get())
				.add(Registration.BlockSwapperT1.get()).add(Registration.BlockSwapperT2.get())
				.add(Registration.PlayerAccessor.get()).add(Registration.ExperienceHolder.get())
				.add(Registration.FluidCollectorT1.get()).add(Registration.FluidCollectorT2.get())
				.add(Registration.FluidPlacerT1.get()).add(Registration.FluidPlacerT2.get())
				.add(Registration.GeneratorFluidT1.get()).add(Registration.InventoryHolder.get())
				.add(Registration.ParadoxMachine.get()).add(Registration.TimeCrystalCluster.get())
				.add(Registration.TimeCrystalBlock.get()).add(Registration.CharcoalBlock.get());
		tag(LAWNMOWERABLE).addTag(BlockTags.FLOWERS).add(Blocks.TALL_GRASS).add(Blocks.GRASS).add(Blocks.DEAD_BUSH)
				.add(Blocks.SWEET_BERRY_BUSH).add(Blocks.FERN).add(Blocks.LARGE_FERN);
		tag(Tags.Blocks.ORES).add(Registration.RawFerricoreOre.get()).add(Registration.RawBlazegoldOre.get())
				.add(Registration.RawCelestigemOre.get()).add(Registration.RawEclipseAlloyOre.get());
		tag(BlockTags.BAMBOO_PLANTABLE_ON).add(Registration.GooSoil_Tier1.get()).add(Registration.GooSoil_Tier2.get())
				.add(Registration.GooSoil_Tier3.get()).add(Registration.GooSoil_Tier4.get());
		tag(PARADOX_ALLOW).addTag(Tags.Blocks.ORES);
		tag(PARADOX_ABSORB_DENY).add(Blocks.BEDROCK).add(Blocks.END_PORTAL_FRAME).add(Blocks.END_PORTAL)
				.add(Blocks.NETHER_PORTAL).addTag(BlockTags.PORTALS);
		tag(PARADOX_DENY).add(Blocks.BEDROCK).add(Blocks.END_PORTAL_FRAME).add(Blocks.END_PORTAL)
				.add(Blocks.NETHER_PORTAL).addTag(BlockTags.PORTALS);
		tag(NO_AUTO_CLICK);
		tag(SWAPPERDENY).add(Blocks.PISTON_HEAD).add(Blocks.MOVING_PISTON).add(Blocks.BEDROCK)
				.add(Blocks.END_PORTAL_FRAME).add(Blocks.CANDLE_CAKE).addTag(BlockTags.BEDS).addTag(BlockTags.PORTALS)
				.addTag(BlockTags.DOORS);
		tag(ECLISEGATEDENY).addTag(BlockTags.PORTALS);
		tag(TICK_SPEED_DENY).addTag(BlockTags.PORTALS).add(Registration.TimeCrystalBuddingBlock.get())
				.add(Registration.GeneratorT1.get()).add(Registration.GeneratorFluidT1.get());
		tag(PHASEDENY).addTag(BlockTags.PORTALS).add(Blocks.BARRIER).add(Blocks.BEDROCK).add(Blocks.END_PORTAL)
				.add(Blocks.END_PORTAL_FRAME).add(Blocks.END_GATEWAY).add(Blocks.STRUCTURE_BLOCK).add(Blocks.JIGSAW);
		tag(BUDDING_BLOCKS).add(Registration.TimeCrystalBuddingBlock.get());
		tag(BUDS).add(Registration.TimeCrystalCluster_Small.get()).add(Registration.TimeCrystalCluster_Medium.get())
				.add(Registration.TimeCrystalCluster_Large.get());
		tag(CLUSTERS).add(Registration.TimeCrystalCluster.get());
	}

	@Override
	public String getName() {
		return "JustDireThings Tags";
	}
}
