package com.negodya1.vintageimprovements;

import static com.negodya1.vintageimprovements.VintageImprovements.MY_REGISTRATE;
import static com.negodya1.vintageimprovements.foundation.data.recipe.VintageCompatMetals.*;

import com.negodya1.vintageimprovements.content.equipment.CurvingHeadItem;
import com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card.RecipeCardItem;
import com.negodya1.vintageimprovements.foundation.data.recipe.VintageCompatMetals;

import com.negodya1.vintageimprovements.content.equipment.CopperSulfateItem;
import com.simibubi.create.AllTags;
import com.simibubi.create.AllTags.AllItemTags;
import com.simibubi.create.content.contraptions.glue.SuperGlueItem;
import com.simibubi.create.content.contraptions.minecart.MinecartCouplingItem;
import com.simibubi.create.content.contraptions.mounted.MinecartContraptionItem;
import com.simibubi.create.content.equipment.BuildersTeaItem;
import com.simibubi.create.content.equipment.TreeFertilizerItem;
import com.simibubi.create.content.equipment.armor.AllArmorMaterials;
import com.simibubi.create.content.equipment.armor.BacktankItem;
import com.simibubi.create.content.equipment.armor.BacktankItem.BacktankBlockItem;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.equipment.armor.DivingHelmetItem;
import com.simibubi.create.content.equipment.blueprint.BlueprintItem;
import com.simibubi.create.content.equipment.extendoGrip.ExtendoGripItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.equipment.goggles.GogglesModel;
import com.simibubi.create.content.equipment.potatoCannon.PotatoCannonItem;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.content.equipment.symmetryWand.SymmetryWandItem;
import com.simibubi.create.content.equipment.wrench.WrenchItem;
import com.simibubi.create.content.equipment.zapper.terrainzapper.WorldshaperItem;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import com.simibubi.create.content.kinetics.gearbox.VerticalGearboxItem;
import com.simibubi.create.content.legacy.ChromaticCompoundColor;
import com.simibubi.create.content.legacy.ChromaticCompoundItem;
import com.simibubi.create.content.legacy.RefinedRadianceItem;
import com.simibubi.create.content.legacy.ShadowSteelItem;
import com.simibubi.create.content.logistics.filter.FilterItem;
import com.simibubi.create.content.materials.ExperienceNuggetItem;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.content.schematics.SchematicAndQuillItem;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.trains.schedule.ScheduleItem;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.CombustibleItem;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.TagDependentIngredientItem;
import com.tterrag.registrate.util.entry.ItemEntry;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;

public class VintageItems {

	static {
		MY_REGISTRATE.setCreativeTab(VintageImprovements.VINTAGE_IMPROVEMENT_TAB);
	}

	public static final ItemEntry<TagDependentIngredientItem> ALUMINUM_SHEET = compatSheet(ALUMINUM);
	public static final ItemEntry<TagDependentIngredientItem> AMETHYST_BRONZE_SHEET = compatSheet(AMETHYST_BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> BRONZE_SHEET = compatSheet(BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> CAST_IRON_SHEET = compatSheet(CAST_IRON);
	public static final ItemEntry<TagDependentIngredientItem> COBALT_SHEET = compatSheet(COBALT);
	public static final ItemEntry<TagDependentIngredientItem> CONSTANTAN_SHEET = compatSheet(CONSTANTAN);
	public static final ItemEntry<TagDependentIngredientItem> ENDERIUM_SHEET = compatSheet(ENDERIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> HEPATIZON_SHEET = compatSheet(HEPATIZON);
	public static final ItemEntry<TagDependentIngredientItem> INVAR_SHEET = compatSheet(INVAR);
	public static final ItemEntry<TagDependentIngredientItem> LEAD_SHEET = compatSheet(LEAD);
	public static final ItemEntry<TagDependentIngredientItem> LUMIUM_SHEET = compatSheet(LUMIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> MANYULLYN_SHEET = compatSheet(MANYULLYN);
	public static final ItemEntry<TagDependentIngredientItem> NICKEL_SHEET = compatSheet(NICKEL);
	public static final ItemEntry<TagDependentIngredientItem> OSMIUM_SHEET = compatSheet(OSMIUM);
	public static final ItemEntry<TagDependentIngredientItem> PALLADIUM_SHEET = compatSheet(PALLADIUM);
	public static final ItemEntry<TagDependentIngredientItem> PIG_IRON_SHEET = compatSheet(PIG_IRON);
	public static final ItemEntry<TagDependentIngredientItem> PLATINUM_SHEET = compatSheet(PLATINUM);
	public static final ItemEntry<TagDependentIngredientItem> PURE_GOLD_SHEET = compatSheet(PURE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_GLOWSTONE_SHEET = compatSheet(REFINED_GLOWSTONE);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_OBSIDIAN_SHEET = compatSheet(REFINED_OBSIDIAN);
	public static final ItemEntry<TagDependentIngredientItem> RHODIUM_SHEET = compatSheet(RHODIUM);
	public static final ItemEntry<TagDependentIngredientItem> ROSE_GOLD_SHEET = compatSheet(ROSE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> SIGNALUM_SHEET = compatSheet(SIGNALUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SILVER_SHEET = compatSheet(SILVER);
	public static final ItemEntry<TagDependentIngredientItem> TIN_SHEET = compatSheet(TIN);
	public static final ItemEntry<TagDependentIngredientItem> URANIUM_SHEET = compatSheet(URANIUM);

	public static final ItemEntry<TagDependentIngredientItem> ALUMINUM_ROD = compatRod(ALUMINUM);
	public static final ItemEntry<TagDependentIngredientItem> AMETHYST_BRONZE_ROD = compatRod(AMETHYST_BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> BRONZE_ROD = compatRod(BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> CAST_IRON_ROD = compatRod(CAST_IRON);
	public static final ItemEntry<TagDependentIngredientItem> COBALT_ROD = compatRod(COBALT);
	public static final ItemEntry<TagDependentIngredientItem> CONSTANTAN_ROD = compatRod(CONSTANTAN);
	public static final ItemEntry<TagDependentIngredientItem> ENDERIUM_ROD = compatRod(ENDERIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> HEPATIZON_ROD = compatRod(HEPATIZON);
	public static final ItemEntry<TagDependentIngredientItem> INVAR_ROD = compatRod(INVAR);
	public static final ItemEntry<TagDependentIngredientItem> LEAD_ROD = compatRod(LEAD);
	public static final ItemEntry<TagDependentIngredientItem> LUMIUM_ROD = compatRod(LUMIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> MANYULLYN_ROD = compatRod(MANYULLYN);
	public static final ItemEntry<TagDependentIngredientItem> NICKEL_ROD = compatRod(NICKEL);
	public static final ItemEntry<TagDependentIngredientItem> OSMIUM_ROD = compatRod(OSMIUM);
	public static final ItemEntry<TagDependentIngredientItem> PALLADIUM_ROD = compatRod(PALLADIUM);
	public static final ItemEntry<TagDependentIngredientItem> PIG_IRON_ROD = compatRod(PIG_IRON);
	public static final ItemEntry<TagDependentIngredientItem> PLATINUM_ROD = compatRod(PLATINUM);
	public static final ItemEntry<TagDependentIngredientItem> PURE_GOLD_ROD = compatRod(PURE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_GLOWSTONE_ROD = compatRod(REFINED_GLOWSTONE);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_OBSIDIAN_ROD = compatRod(REFINED_OBSIDIAN);
	public static final ItemEntry<TagDependentIngredientItem> RHODIUM_ROD = compatRod(RHODIUM);
	public static final ItemEntry<TagDependentIngredientItem> ROSE_GOLD_ROD = compatRod(ROSE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> SIGNALUM_ROD = compatRod(SIGNALUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SILVER_ROD = compatRod(SILVER);
	public static final ItemEntry<TagDependentIngredientItem> STEEL_ROD = compatRod(STEEL);
	public static final ItemEntry<TagDependentIngredientItem> TIN_ROD = compatRod(TIN);
	public static final ItemEntry<TagDependentIngredientItem> URANIUM_ROD = compatRod(URANIUM);

	public static final ItemEntry<TagDependentIngredientItem> ALUMINUM_SPRING = compatSpring(ALUMINUM);
	public static final ItemEntry<TagDependentIngredientItem> AMETHYST_BRONZE_SPRING = compatSpring(AMETHYST_BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> BRONZE_SPRING = compatSpring(BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> CAST_IRON_SPRING = compatSpring(CAST_IRON);
	public static final ItemEntry<TagDependentIngredientItem> COBALT_SPRING = compatSpring(COBALT);
	public static final ItemEntry<TagDependentIngredientItem> CONSTANTAN_SPRING = compatSpring(CONSTANTAN);
	public static final ItemEntry<TagDependentIngredientItem> ELECTRUM_SPRING = compatSpring(ELECTRUM);
	public static final ItemEntry<TagDependentIngredientItem> ENDERIUM_SPRING = compatSpring(ENDERIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> HEPATIZON_SPRING = compatSpring(HEPATIZON);
	public static final ItemEntry<TagDependentIngredientItem> INVAR_SPRING = compatSpring(INVAR);
	public static final ItemEntry<TagDependentIngredientItem> LEAD_SPRING = compatSpring(LEAD);
	public static final ItemEntry<TagDependentIngredientItem> LUMIUM_SPRING = compatSpring(LUMIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> MANYULLYN_SPRING = compatSpring(MANYULLYN);
	public static final ItemEntry<TagDependentIngredientItem> NICKEL_SPRING = compatSpring(NICKEL);
	public static final ItemEntry<TagDependentIngredientItem> OSMIUM_SPRING = compatSpring(OSMIUM);
	public static final ItemEntry<TagDependentIngredientItem> PALLADIUM_SPRING = compatSpring(PALLADIUM);
	public static final ItemEntry<TagDependentIngredientItem> PIG_IRON_SPRING = compatSpring(PIG_IRON);
	public static final ItemEntry<TagDependentIngredientItem> PLATINUM_SPRING = compatSpring(PLATINUM);
	public static final ItemEntry<TagDependentIngredientItem> PURE_GOLD_SPRING = compatSpring(PURE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_GLOWSTONE_SPRING = compatSpring(REFINED_GLOWSTONE);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_OBSIDIAN_SPRING = compatSpring(REFINED_OBSIDIAN);
	public static final ItemEntry<TagDependentIngredientItem> RHODIUM_SPRING = compatSpring(RHODIUM);
	public static final ItemEntry<TagDependentIngredientItem> ROSE_GOLD_SPRING = compatSpring(ROSE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> SIGNALUM_SPRING = compatSpring(SIGNALUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SILVER_SPRING = compatSpring(SILVER);
	public static final ItemEntry<TagDependentIngredientItem> STEEL_SPRING = compatSpring(STEEL);
	public static final ItemEntry<TagDependentIngredientItem> TIN_SPRING = compatSpring(TIN);
	public static final ItemEntry<TagDependentIngredientItem> URANIUM_SPRING = compatSpring(URANIUM);

	public static final ItemEntry<TagDependentIngredientItem> SMALL_ALUMINUM_SPRING = compatSmallSpring(ALUMINUM);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_AMETHYST_BRONZE_SPRING = compatSmallSpring(AMETHYST_BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_BRONZE_SPRING = compatSmallSpring(BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_CAST_IRON_SPRING = compatSmallSpring(CAST_IRON);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_COBALT_SPRING = compatSmallSpring(COBALT);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_CONSTANTAN_SPRING = compatSmallSpring(CONSTANTAN);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_ELECTRUM_SPRING = compatSmallSpring(ELECTRUM);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_ENDERIUM_SPRING = compatSmallSpring(ENDERIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_HEPATIZON_SPRING = compatSmallSpring(HEPATIZON);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_INVAR_SPRING = compatSmallSpring(INVAR);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_LEAD_SPRING = compatSmallSpring(LEAD);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_LUMIUM_SPRING = compatSmallSpring(LUMIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_MANYULLYN_SPRING = compatSmallSpring(MANYULLYN);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_NICKEL_SPRING = compatSmallSpring(NICKEL);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_OSMIUM_SPRING = compatSmallSpring(OSMIUM);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_PALLADIUM_SPRING = compatSmallSpring(PALLADIUM);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_PIG_IRON_SPRING = compatSmallSpring(PIG_IRON);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_PLATINUM_SPRING = compatSmallSpring(PLATINUM);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_PURE_GOLD_SPRING = compatSmallSpring(PURE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_REFINED_GLOWSTONE_SPRING = compatSmallSpring(REFINED_GLOWSTONE);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_REFINED_OBSIDIAN_SPRING = compatSmallSpring(REFINED_OBSIDIAN);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_RHODIUM_SPRING = compatSmallSpring(RHODIUM);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_ROSE_GOLD_SPRING = compatSmallSpring(ROSE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_SIGNALUM_SPRING = compatSmallSpring(SIGNALUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_SILVER_SPRING = compatSmallSpring(SILVER);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_STEEL_SPRING = compatSmallSpring(STEEL);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_TIN_SPRING = compatSmallSpring(TIN);
	public static final ItemEntry<TagDependentIngredientItem> SMALL_URANIUM_SPRING = compatSmallSpring(URANIUM);

	public static final ItemEntry<TagDependentIngredientItem> ALUMINUM_WIRE = compatWire(ALUMINUM);
	public static final ItemEntry<TagDependentIngredientItem> AMETHYST_BRONZE_WIRE = compatWire(AMETHYST_BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> BRONZE_WIRE = compatWire(BRONZE);
	public static final ItemEntry<TagDependentIngredientItem> CAST_IRON_WIRE = compatWire(CAST_IRON);
	public static final ItemEntry<TagDependentIngredientItem> COBALT_WIRE = compatWire(COBALT);
	public static final ItemEntry<TagDependentIngredientItem> CONSTANTAN_WIRE = compatWire(CONSTANTAN);
	public static final ItemEntry<TagDependentIngredientItem> ENDERIUM_WIRE = compatWire(ENDERIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> HEPATIZON_WIRE = compatWire(HEPATIZON);
	public static final ItemEntry<TagDependentIngredientItem> INVAR_WIRE = compatWire(INVAR);
	public static final ItemEntry<TagDependentIngredientItem> LEAD_WIRE = compatWire(LEAD);
	public static final ItemEntry<TagDependentIngredientItem> LUMIUM_WIRE = compatWire(LUMIUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> MANYULLYN_WIRE = compatWire(MANYULLYN);
	public static final ItemEntry<TagDependentIngredientItem> NICKEL_WIRE = compatWire(NICKEL);
	public static final ItemEntry<TagDependentIngredientItem> OSMIUM_WIRE = compatWire(OSMIUM);
	public static final ItemEntry<TagDependentIngredientItem> PALLADIUM_WIRE = compatWire(PALLADIUM);
	public static final ItemEntry<TagDependentIngredientItem> PIG_IRON_WIRE = compatWire(PIG_IRON);
	public static final ItemEntry<TagDependentIngredientItem> PLATINUM_WIRE = compatWire(PLATINUM);
	public static final ItemEntry<TagDependentIngredientItem> PURE_GOLD_WIRE = compatWire(PURE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_GLOWSTONE_WIRE = compatWire(REFINED_GLOWSTONE);
	public static final ItemEntry<TagDependentIngredientItem> REFINED_OBSIDIAN_WIRE = compatWire(REFINED_OBSIDIAN);
	public static final ItemEntry<TagDependentIngredientItem> RHODIUM_WIRE = compatWire(RHODIUM);
	public static final ItemEntry<TagDependentIngredientItem> ROSE_GOLD_WIRE = compatWire(ROSE_GOLD);
	public static final ItemEntry<TagDependentIngredientItem> SIGNALUM_WIRE = compatWire(SIGNALUM, Rarity.UNCOMMON);
	public static final ItemEntry<TagDependentIngredientItem> SILVER_WIRE = compatWire(SILVER);
	public static final ItemEntry<TagDependentIngredientItem> STEEL_WIRE = compatWire(STEEL);
	public static final ItemEntry<TagDependentIngredientItem> TIN_WIRE = compatWire(TIN);
	public static final ItemEntry<TagDependentIngredientItem> URANIUM_WIRE = compatWire(URANIUM);

	public static final ItemEntry<Item> REDSTONE_MODULE = ingredient("redstone_module");
	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_REDSTONE_MODULE = sequencedIngredient("incomplete_redstone_module");
	public static final ItemEntry<CopperSulfateItem> COPPER_SULFATE =
			MY_REGISTRATE.item("copper_sulfate", CopperSulfateItem::new).register();

	public static final ItemEntry<RefinedRadianceItem> REFINED_RADIANCE_SHEET =
			MY_REGISTRATE.item("refined_radiance_sheet", RefinedRadianceItem::new)
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();
	public static final ItemEntry<RefinedRadianceItem> REFINED_RADIANCE_ROD =
			MY_REGISTRATE.item("refined_radiance_rod", RefinedRadianceItem::new)
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();
	public static final ItemEntry<RefinedRadianceItem> REFINED_RADIANCE_SPRING =
			MY_REGISTRATE.item("refined_radiance_spring", RefinedRadianceItem::new)
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();
	public static final ItemEntry<RefinedRadianceItem> SMALL_REFINED_RADIANCE_SPRING =
			MY_REGISTRATE.item("small_refined_radiance_spring", RefinedRadianceItem::new)
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();
	public static final ItemEntry<RefinedRadianceItem> REFINED_RADIANCE_WIRE =
			MY_REGISTRATE.item("refined_radiance_wire", RefinedRadianceItem::new)
					.properties(p -> p.rarity(Rarity.UNCOMMON))
					.register();

	public static final ItemEntry<CurvingHeadItem> CONVEX_CURVING_HEAD = MY_REGISTRATE.item("convex_curving_head", CurvingHeadItem::new)
			.properties(p -> p.durability(1000))
			.register();
	public static final ItemEntry<CurvingHeadItem> CONCAVE_CURVING_HEAD = MY_REGISTRATE.item("concave_curving_head", CurvingHeadItem::new)
			.properties(p -> p.durability(1000))
			.register();
	public static final ItemEntry<CurvingHeadItem> W_SHAPED_CURVING_HEAD = MY_REGISTRATE.item("w_shaped_curving_head", CurvingHeadItem::new)
			.properties(p -> p.durability(1000))
			.register();
	public static final ItemEntry<CurvingHeadItem> V_SHAPED_CURVING_HEAD = MY_REGISTRATE.item("v_shaped_curving_head", CurvingHeadItem::new)
			.properties(p -> p.durability(1000))
			.register();

	public static final ItemEntry<RecipeCardItem> RECIPE_CARD =
			MY_REGISTRATE.item("recipe_card", RecipeCardItem::new).register();
	public static final ItemEntry<SequencedAssemblyItem> INCOMPLETE_RECIPE_CARD = sequencedIngredient("incomplete_recipe_card");

	private static ItemEntry<Item> ingredient(String name) {
		return MY_REGISTRATE.item(name, Item::new)
			.register();
	}

	private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
		return MY_REGISTRATE.item(name, SequencedAssemblyItem::new)
			.register();
	}

	@SafeVarargs
	private static ItemEntry<Item> taggedIngredient(String name, TagKey<Item>... tags) {
		return MY_REGISTRATE.item(name, Item::new)
			.tag(tags)
			.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatRod(VintageCompatMetals metal) {
		String metalName = metal.getName();
		return MY_REGISTRATE
			.item(metalName + "_rod",
				props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
			.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatSheet(VintageCompatMetals metal) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_sheet",
						props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatWire(VintageCompatMetals metal) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_wire",
						props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatSpring(VintageCompatMetals metal) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_spring",
						props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatSmallSpring(VintageCompatMetals metal) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item("small_" + metalName + "_spring",
						props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
				.register();
	}

	//With rarities

	private static ItemEntry<TagDependentIngredientItem> compatRod(VintageCompatMetals metal, Rarity rarity) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_rod",
						props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatSheet(VintageCompatMetals metal, Rarity rarity) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_sheet",
						props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatWire(VintageCompatMetals metal, Rarity rarity) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_wire",
						props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatSpring(VintageCompatMetals metal, Rarity rarity) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item(metalName + "_spring",
						props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}

	private static ItemEntry<TagDependentIngredientItem> compatSmallSpring(VintageCompatMetals metal, Rarity rarity) {
		String metalName = metal.getName();
		return MY_REGISTRATE
				.item("small_" + metalName + "_spring",
						props -> new TagDependentIngredientItem(props, AllTags.forgeItemTag("ingots/" + metalName)))
				.properties(p -> p.rarity(rarity))
				.register();
	}

	// Load this class

	public static void register() {}

}
