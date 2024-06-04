package com.negodya1.vintageimprovements;

import java.util.Random;

import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.negodya1.vintageimprovements.infrastructure.ponder.VintagePonder;
import com.negodya1.vintageimprovements.infrastructure.ponder.scenes.BeltGrinderScenes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderStoryBoardEntry;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.logging.LogUtils;
import com.simibubi.create.api.behaviour.BlockSpoutingBehaviour;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.computercraft.ComputerCraftProxy;
import com.simibubi.create.compat.curios.Curios;
import com.simibubi.create.content.contraptions.ContraptionMovementSetting;
import com.simibubi.create.content.decoration.palettes.AllPaletteBlocks;
import com.simibubi.create.content.equipment.potatoCannon.BuiltinPotatoProjectileTypes;
import com.simibubi.create.content.fluids.tank.BoilerHeaters;
import com.simibubi.create.content.kinetics.TorquePropagator;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.schematics.ServerSchematicLoader;
import com.simibubi.create.content.trains.GlobalRailwayManager;
import com.simibubi.create.content.trains.bogey.BogeySizes;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.AllTriggers;
import com.simibubi.create.foundation.block.CopperRegistries;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper.Palette;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.AttachedRegistry;
import com.simibubi.create.infrastructure.command.ServerLagger;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.data.CreateDatagen;
import com.simibubi.create.infrastructure.worldgen.AllFeatures;
import com.simibubi.create.infrastructure.worldgen.AllPlacementModifiers;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.mojang.logging.LogUtils;
import com.negodya1.vintageimprovements.foundation.data.VintageRegistrate;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import com.negodya1.vintageimprovements.VintageBlocks;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VintageImprovements.MODID)
public class VintageImprovements {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "vintageimprovements";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void logThis(String str) {
        LOGGER.info(str);
    }

    public static final VintageRegistrate MY_REGISTRATE = VintageRegistrate.create(MODID);

    public static boolean adAstraLoaded = ModList.get().isLoaded("ad_astra");
    public static boolean twilightForestLoaded = ModList.get().isLoaded("twilightforest");
    public static boolean tConstructLoaded = ModList.get().isLoaded("tconstruct");
    public static boolean bigCannonsLoaded = ModList.get().isLoaded("createbigcannons");

    static {
        MY_REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
    }

    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace

    public static final RegistryObject<Item> CALORITE_ROD = ITEMS.register("calorite_rod", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> OSTRUM_ROD = ITEMS.register("ostrum_rod", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> DESH_ROD = ITEMS.register("desh_rod", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> NETHERITE_ROD = ITEMS.register("netherite_rod", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance).fireResistant()));
    final RegistryObject<Item> NETHERSTEEL_ROD = ITEMS.register("nethersteel_rod", () -> {
        if (bigCannonsLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> IRONWOOD_ROD = ITEMS.register("ironwood_rod", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> KNIGHTMETAL_ROD = ITEMS.register("knightmetal_rod", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> QUEENS_SLIME_ROD = ITEMS.register("queens_slime_rod", () -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SLIMESTEEL_ROD = ITEMS.register("slimesteel_rod", () -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> VANADIUM_ROD = ITEMS.register("vanadium_rod", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> ANDESITE_ROD = ITEMS.register("andesite_rod", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> ZINC_ROD = ITEMS.register("zinc_rod", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SHADOW_STEEL_ROD = ITEMS.register("shadow_steel_rod", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> CALORITE_WIRE = ITEMS.register("calorite_wire", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> OSTRUM_WIRE = ITEMS.register("ostrum_wire", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> DESH_WIRE = ITEMS.register("desh_wire", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> BRASS_WIRE = ITEMS.register("brass_wire", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    final RegistryObject<Item> IRONWOOD_WIRE = ITEMS.register("ironwood_wire", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> KNIGHTMETAL_WIRE = ITEMS.register("knightmetal_wire", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> QUEENS_SLIME_WIRE = ITEMS.register("queens_slime_wire", () -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SLIMESTEEL_WIRE = ITEMS.register("slimesteel_wire", () -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> VANADIUM_WIRE = ITEMS.register("vanadium_wire", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> FIERY_WIRE = ITEMS.register("fiery_wire", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance).rarity(Rarity.UNCOMMON).fireResistant());
        return new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant());
    });
    public static final RegistryObject<Item> ANDESITE_WIRE = ITEMS.register("andesite_wire", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> ZINC_WIRE = ITEMS.register("zinc_wire", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SHADOW_STEEL_WIRE = ITEMS.register("shadow_steel_wire", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> NETHERSTEEL_WIRE = ITEMS.register("nethersteel_wire", () -> {
        if (bigCannonsLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> NETHERITE_WIRE = ITEMS.register("netherite_wire", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance).fireResistant()));

    public static final RegistryObject<Item> CALORITE_SPRING = ITEMS.register("calorite_spring", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> OSTRUM_SPRING = ITEMS.register("ostrum_spring", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> DESH_SPRING = ITEMS.register("desh_spring", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> IRON_SPRING = ITEMS.register("iron_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> GOLDEN_SPRING = ITEMS.register("golden_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> COPPER_SPRING = ITEMS.register("copper_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> BRASS_SPRING = ITEMS.register("brass_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> NETHERITE_SPRING = ITEMS.register("netherite_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance).fireResistant()));
    public static final RegistryObject<Item> NETHERSTEEL_SPRING = ITEMS.register("nethersteel_spring", () -> {
        if (bigCannonsLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> BLAZE_SPRING = ITEMS.register("blaze_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> IRONWOOD_SPRING = ITEMS.register("ironwood_spring", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> KNIGHTMETAL_SPRING = ITEMS.register("knightmetal_spring", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> QUEENS_SLIME_SPRING = ITEMS.register("queens_slime_spring", () -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SLIMESTEEL_SPRING = ITEMS.register("slimesteel_spring",() -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> VANADIUM_SPRING = ITEMS.register("vanadium_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> ANDESITE_SPRING = ITEMS.register("andesite_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> ZINC_SPRING = ITEMS.register("zinc_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SHADOW_STEEL_SPRING = ITEMS.register("shadow_steel_spring", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> SMALL_CALORITE_SPRING = ITEMS.register("small_calorite_spring", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SMALL_OSTRUM_SPRING = ITEMS.register("small_ostrum_spring", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SMALL_DESH_SPRING = ITEMS.register("small_desh_spring", () -> {
        if (adAstraLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SMALL_IRON_SPRING = ITEMS.register("small_iron_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SMALL_GOLDEN_SPRING = ITEMS.register("small_golden_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SMALL_COPPER_SPRING = ITEMS.register("small_copper_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SMALL_BRASS_SPRING = ITEMS.register("small_brass_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SMALL_IRONWOOD_SPRING = ITEMS.register("small_ironwood_spring", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SMALL_KNIGHTMETAL_SPRING = ITEMS.register("small_knightmetal_spring", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SMALL_QUEENS_SLIME_SPRING = ITEMS.register("small_queens_slime_spring", () -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SMALL_SLIMESTEEL_SPRING = ITEMS.register("small_slimesteel_spring", () -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SMALL_VANADIUM_SPRING = ITEMS.register("small_vanadium_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SMALL_FIERY_SPRING = ITEMS.register("small_fiery_spring", () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance).rarity(Rarity.UNCOMMON).fireResistant());
        return new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant());
    });
    public static final RegistryObject<Item> SMALL_ANDESITE_SPRING = ITEMS.register("small_andesite_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SMALL_ZINC_SPRING = ITEMS.register("small_zinc_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SMALL_SHADOW_STEEL_SPRING = ITEMS.register("small_shadow_steel_spring", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SMALL_NETHERSTEEL_SPRING = ITEMS.register("small_nethersteel_spring", () -> {
        if (bigCannonsLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SMALL_NETHERITE_SPRING = ITEMS.register("small_netherite_spring", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance).fireResistant()));

    public static final RegistryObject<Item> GRINDER_BELT = ITEMS.register("grinder_belt", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SPRING_COILING_MACHINE_WHEEL = ITEMS.register("spring_coiling_machine_wheel", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));

    public static final RegistryObject<Item> SULFUR_CHUNK = ITEMS.register("sulfur_chunk", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SULFUR = ITEMS.register("sulfur", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> VANADIUM_INGOT = ITEMS.register("vanadium_ingot", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> VANADIUM_NUGGET = ITEMS.register("vanadium_nugget", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));

    public static final RegistryObject<Item> NETHERITE_SHEET = ITEMS.register("netherite_sheet", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance).fireResistant()));
    public static final RegistryObject<Item> NETHERSTEEL_SHEET = ITEMS.register("nethersteel_sheet",  () -> {
        if (bigCannonsLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> IRONWOOD_SHEET = ITEMS.register("ironwood_sheet",  () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> KNIGHTMETAL_SHEET = ITEMS.register("knightmetal_sheet",  () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> QUEENS_SLIME_SHEET = ITEMS.register("queens_slime_sheet",  () -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> SLIMESTEEL_SHEET = ITEMS.register("slimesteel_sheet",  () -> {
        if (tConstructLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance));
        return new Item(new Item.Properties());
    });
    public static final RegistryObject<Item> VANADIUM_SHEET = ITEMS.register("vanadium_sheet", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> FIERY_SHEET = ITEMS.register("fiery_sheet",  () -> {
        if (twilightForestLoaded)
            return new Item(new Item.Properties().tab(VintageCreativeTab.instance).rarity(Rarity.UNCOMMON).fireResistant());
        return new Item(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant());
    });
    public static final RegistryObject<Item> ANDESITE_SHEET = ITEMS.register("andesite_sheet", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> ZINC_SHEET = ITEMS.register("zinc_sheet", () -> new Item(new Item.Properties().tab(VintageCreativeTab.instance)));
    public static final RegistryObject<Item> SHADOW_STEEL_SHEET = ITEMS.register("shadow_steel_sheet", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON)));

    public VintageImprovements() {
        onCtor();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void onCtor() {
        ModLoadingContext modLoadingContext = ModLoadingContext.get();

        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                .getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        MY_REGISTRATE.registerEventListeners(modEventBus);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        VintageBlocks.register();
        VintageBlockEntity.register();
        VintageRecipes.register(modEventBus);
        VintagePartialModels.init();
        VintageItems.register();
        VintageFluids.register();

        modEventBus.addListener(VintageImprovements::commonSetup);

        VintageConfig.register(modLoadingContext);
    }

    private static void commonSetup(final FMLCommonSetupEvent event) {}

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        VintageRecipesList.init(event.getServer());
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            VintagePonder.register();
        }
    }

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static class VintageCreativeTab extends CreativeModeTab {
        private VintageCreativeTab(int index, String label) {
            super(index, label);
        }

        @Override
        public ItemStack makeIcon() {
            return new ItemStack(VintageBlocks.BELT_GRINDER.get());
        }

        public static final VintageCreativeTab instance = new VintageCreativeTab(CreativeModeTab.TABS.length, "vintageimprovements");
    }
}
