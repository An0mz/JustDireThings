package com.direwolf20.justdirethings.datagen;

import com.direwolf20.justdirethings.JustDireThings;
import com.direwolf20.justdirethings.setup.Registration;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class JustDireItemTags extends ItemTagsProvider {
	public JustDireItemTags(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider,
			BlockTagsProvider blockTags, ExistingFileHelper helper) {
		super(packOutput, lookupProvider, blockTags.contentsGetter(), JustDireThings.MODID, helper);
	}

	public static final TagKey<Item> WRENCHES = forgeTag("wrenches");
	public static final TagKey<Item> TOOLS_WRENCH = forgeTag("tools/wrench");

	private static TagKey<Item> forgeTag(String name) {
		return ItemTags.create(new ResourceLocation("forge", name));
	}

	public static final TagKey<Item> FUEL_CANISTER_DENY = ItemTags
			.create(new ResourceLocation(JustDireThings.MODID, "deny_fuel_canister"));
	public static final TagKey<Item> PARADOX_DENY = ItemTags
			.create(new ResourceLocation(JustDireThings.MODID, "paradox_deny"));
	public static final TagKey<Item> AUTO_SMELT_DENY = ItemTags
			.create(new ResourceLocation(JustDireThings.MODID, "auto_smelt_deny"));
	public static final TagKey<Item> AUTO_SMOKE_DENY = ItemTags
			.create(new ResourceLocation(JustDireThings.MODID, "auto_smoke_deny"));
	public static final TagKey<Item> GOO_REVIVE_TIER_1 = ItemTags
			.create(new ResourceLocation(JustDireThings.MODID, "goo_revive_tier_1"));
	public static final TagKey<Item> GOO_REVIVE_TIER_2 = ItemTags
			.create(new ResourceLocation(JustDireThings.MODID, "goo_revive_tier_2"));
	public static final TagKey<Item> GOO_REVIVE_TIER_3 = ItemTags
			.create(new ResourceLocation(JustDireThings.MODID, "goo_revive_tier_3"));
	public static final TagKey<Item> GOO_REVIVE_TIER_4 = ItemTags
			.create(new ResourceLocation(JustDireThings.MODID, "goo_revive_tier_4"));

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(AUTO_SMELT_DENY);
		tag(PARADOX_DENY);
		tag(AUTO_SMOKE_DENY);
		tag(FUEL_CANISTER_DENY).add(Items.LAVA_BUCKET);
		tag(GOO_REVIVE_TIER_1).add(Items.SUGAR).add(Items.ROTTEN_FLESH);
		tag(GOO_REVIVE_TIER_2).add(Items.NETHER_WART).add(Items.BLAZE_POWDER);
		tag(GOO_REVIVE_TIER_3).add(Items.CHORUS_FRUIT).add(Items.ENDER_PEARL);
		tag(GOO_REVIVE_TIER_4).add(Items.SCULK).add(Items.SCULK_CATALYST);
		tag(ItemTags.SWORDS).add(Registration.FerricoreSword.get()).add(Registration.BlazegoldSword.get())
				.add(Registration.CelestigemSword.get()).add(Registration.EclipseAlloySword.get());
		tag(ItemTags.PICKAXES).add(Registration.FerricorePickaxe.get()).add(Registration.BlazegoldPickaxe.get())
				.add(Registration.CelestigemPickaxe.get()).add(Registration.EclipseAlloyPickaxe.get())
				.add(Registration.CelestigemPaxel.get()).add(Registration.EclipseAlloyPaxel.get());
		tag(ItemTags.SHOVELS).add(Registration.FerricoreShovel.get()).add(Registration.BlazegoldShovel.get())
				.add(Registration.CelestigemShovel.get()).add(Registration.EclipseAlloyShovel.get())
				.add(Registration.CelestigemPaxel.get()).add(Registration.EclipseAlloyPaxel.get());
		tag(ItemTags.AXES).add(Registration.FerricoreAxe.get()).add(Registration.BlazegoldAxe.get())
				.add(Registration.CelestigemAxe.get()).add(Registration.EclipseAlloyAxe.get())
				.add(Registration.CelestigemPaxel.get()).add(Registration.EclipseAlloyPaxel.get());
		tag(ItemTags.HOES).add(Registration.FerricoreHoe.get()).add(Registration.BlazegoldHoe.get())
				.add(Registration.CelestigemHoe.get()).add(Registration.EclipseAlloyHoe.get());
		tag(Tags.Items.INGOTS).add(Registration.FerricoreIngot.get()).add(Registration.BlazegoldIngot.get())
				.add(Registration.EclipseAlloyIngot.get());
		tag(Tags.Items.RAW_MATERIALS).add(Registration.RawFerricore.get()).add(Registration.RawBlazegold.get())
				.add(Registration.RawEclipseAlloy.get());
		tag(Tags.Items.GEMS).add(Registration.Celestigem.get());
		tag(WRENCHES).add(Registration.FerricoreWrench.get());
		tag(TOOLS_WRENCH).add(Registration.FerricoreWrench.get());
		tag(Tags.Items.ARMORS_BOOTS).add(Registration.FerricoreBoots.get());
		tag(Tags.Items.ARMORS_LEGGINGS).add(Registration.FerricoreLeggings.get());
		tag(Tags.Items.ARMORS_CHESTPLATES).add(Registration.FerricoreChestplate.get());
		tag(Tags.Items.ARMORS_HELMETS).add(Registration.FerricoreHelmet.get());
	}

	@Override
	public String getName() {
		return "JustDireThings Item Tags";
	}
}
