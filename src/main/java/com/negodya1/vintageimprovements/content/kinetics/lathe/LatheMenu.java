package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.negodya1.vintageimprovements.VintageMenuTypes;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card.RecipeCardItem;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LatheMenu extends MenuBase<LatheMovingBlockEntity> {

	private static final Object turningRecipesKey = new Object();
	private final Level level;
	List<TurningRecipe> recipes;
	private Slot inputSlot;

	public LatheMenu(MenuType<?> type, int id, Inventory inv, FriendlyByteBuf extraData) {
		super(type, id, inv, extraData);
		level = inv.player.level();
		recipes = new ArrayList<>();
	}

	public LatheMenu(MenuType<?> type, int id, Inventory inv, LatheMovingBlockEntity be) {
		super(type, id, inv, be);
		level = inv.player.level();
		recipes = new ArrayList<>();
	}

	public static LatheMenu create(int id, Inventory inv, LatheMovingBlockEntity be) {
		return new LatheMenu(VintageMenuTypes.LATHE.get(), id, inv, be);
	}

	@Override
	protected void initAndReadInventory(LatheMovingBlockEntity contentHolder) {
	}

	@Override
	protected LatheMovingBlockEntity createOnClient(FriendlyByteBuf extraData) {
		ClientLevel world = Minecraft.getInstance().level;
		BlockEntity blockEntity = world.getBlockEntity(extraData.readBlockPos());
		if (blockEntity instanceof LatheMovingBlockEntity lathe) {
			lathe.readClient(extraData.readNbt());
			return lathe;
		}
		return null;
	}

	@Override
	protected void addSlots() {
		addPlayerSlots(8, 131);
		inputSlot = new SlotItemHandler(contentHolder.getInputInventory(), 0, 23, 51) {
			@Override
			public boolean mayPlace(ItemStack stack) {
				return false;
			}

			@Override
			public boolean mayPickup(Player playerIn) {
				return false;
			}
		};

		addSlot(inputSlot);
	}

	@Override
	public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
		if (slotId == playerInventory.selected && clickTypeIn != ClickType.THROW)
			return;
		super.clicked(slotId, dragType, clickTypeIn, player);
	}

	private void setupRecipeList() {
		recipes = contentHolder.getRecipes();
	}

	public List<TurningRecipe> getRecipes() {
		if (recipes != null)
			if (!recipes.isEmpty())
				if (recipes.get(0).getIngredients().get(0).test(inputSlot.getItem()))
					return recipes;

		setupRecipeList();
		return recipes;
	}

	@Override
	public boolean clickMenuButton(Player player, int index) {
		if (isValidRecipeIndex(index)) {
			contentHolder.currentRecipe = getRecipes().get(index);
			contentHolder.index = index;
		}
		return true;
	}

	public boolean isValidRecipeIndex(int index) {
		return index >= 0 && index < getRecipes().size();
	}

	@Override
	protected void saveData(LatheMovingBlockEntity contentHolder) {
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		return ItemStack.EMPTY;
	}

	public int getSelectedRecipeIndex() {
		return contentHolder.getTemporaryIndex();
	}
}
