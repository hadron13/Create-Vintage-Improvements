package com.negodya1.vintageimprovements;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class VintagePartialModels {

	public static final PartialModel

			GRINDER_BELT_ACTIVE = block("belt_grinder/belt_active"),
			GRINDER_BELT_INACTIVE = block("belt_grinder/belt_inactive"),
			GRINDER_BELT_REVERSED = block("belt_grinder/belt_reversed"),
			COILING_WHEEL = block("spring_coiling_machine/coiling_part_wheel"),
			COILING_SPRING = block("spring_coiling_machine/coiling_part_spring"),
			VACUUM_COG = block("vacuum_chamber/cog"),
			VACUUM_PIPE = block("vacuum_chamber/head"),
			VIBRATING_TABLE = block("vibrating_table/head"),
			CENTRIFUGE_BEAMS = block("centrifuge/head"),
			BASIN = block("centrifuge/basin"),
			CURVING_HEAD = block("curving_press/head"),
			GRINDER_BELT_ACTIVE_RED = block("belt_grinder/belt_active_red"),
			GRINDER_BELT_INACTIVE_RED = block("belt_grinder/belt_inactive_red"),
			GRINDER_BELT_REVERSED_RED = block("belt_grinder/belt_reversed_red"),
			GRINDER_BELT_ACTIVE_DIAMOND = block("belt_grinder/belt_active_diamond"),
			GRINDER_BELT_INACTIVE_DIAMOND = block("belt_grinder/belt_inactive_diamond"),
			GRINDER_BELT_REVERSED_DIAMOND = block("belt_grinder/belt_reversed_diamond"),
			GRINDER_BELT_ACTIVE_IRON = block("belt_grinder/belt_active_iron"),
			GRINDER_BELT_INACTIVE_IRON = block("belt_grinder/belt_inactive_iron"),
			GRINDER_BELT_REVERSED_IRON = block("belt_grinder/belt_reversed_iron"),
			GRINDER_BELT_ACTIVE_OBSIDIAN = block("belt_grinder/belt_active_obsidian"),
			GRINDER_BELT_INACTIVE_OBSIDIAN = block("belt_grinder/belt_inactive_obsidian"),
			GRINDER_BELT_REVERSED_OBSIDIAN = block("belt_grinder/belt_reversed_obsidian"),
			VACUUM_CHAMBER_ARROWS = block("vacuum_chamber/arrows"),
			HELVE_HAMMER = block("helve_hammer/head"),
			CURVING_POLE = block("curving_press/pole"),
			CURVING_HEAD_2 = block("curving_press/head_2"),
			CURVING_HEAD_3 = block("curving_press/head_3"),
			CURVING_HEAD_4 = block("curving_press/head_4"),
			CURVING_HEAD_5 = block("curving_press/head_5"),
			REDSTONE_MODULE_CENTRIFUGE = block("centrifuge/redstone"),
			REDSTONE_MODULE_CURVING_PRESS = block("curving_press/redstone");

	private static PartialModel block(String path) {
		return new PartialModel(VintageImprovements.asResource("block/" + path));
	}

	private static PartialModel entity(String path) {
		return new PartialModel(VintageImprovements.asResource("entity/" + path));
	}

	public static void init() {
		// init static fields
	}

}
