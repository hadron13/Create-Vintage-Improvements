package com.negodya1.vintageimprovements;

import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheMenu;
import com.negodya1.vintageimprovements.content.kinetics.lathe.LatheScreen;
import com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card.RecipeCardMenu;
import com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card.RecipeCardScreen;
import com.simibubi.create.content.equipment.blueprint.BlueprintMenu;
import com.simibubi.create.content.equipment.blueprint.BlueprintScreen;
import com.simibubi.create.content.equipment.toolbox.ToolboxMenu;
import com.simibubi.create.content.equipment.toolbox.ToolboxScreen;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.content.logistics.filter.AttributeFilterScreen;
import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.content.logistics.filter.FilterScreen;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerMenu;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.content.schematics.cannon.SchematicannonMenu;
import com.simibubi.create.content.schematics.cannon.SchematicannonScreen;
import com.simibubi.create.content.schematics.table.SchematicTableMenu;
import com.simibubi.create.content.schematics.table.SchematicTableScreen;
import com.simibubi.create.content.trains.schedule.ScheduleMenu;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.tterrag.registrate.builders.MenuBuilder.ForgeMenuFactory;
import com.tterrag.registrate.builders.MenuBuilder.ScreenFactory;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class VintageMenuTypes {

	public static final MenuEntry<RecipeCardMenu> RECIPE_CARD =
		register("recipe_card", RecipeCardMenu::new, () -> RecipeCardScreen::new);

	public static final MenuEntry<LatheMenu> LATHE =
			register("lathe", LatheMenu::new, () -> LatheScreen::new);

	private static <C extends AbstractContainerMenu, S extends Screen & MenuAccess<C>> MenuEntry<C> register(
		String name, ForgeMenuFactory<C> factory, NonNullSupplier<ScreenFactory<C, S>> screenFactory) {
		return VintageImprovements.MY_REGISTRATE
			.menu(name, factory, screenFactory)
			.register();
	}

	public static void register() {}

}
