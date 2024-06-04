package com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyPressurizing;
import com.negodya1.vintageimprovements.foundation.utility.VintageLang;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.*;
import java.util.function.Supplier;

public class PressurizingRecipe extends BasinRecipe implements IAssemblyRecipe {

	int secondaryFluidResults;
	int secondaryFluidInputs;

	public PressurizingRecipe(ProcessingRecipeParams params) {
		super(VintageRecipes.PRESSURIZING, params);
		secondaryFluidResults = -1;
		secondaryFluidInputs = -1;
	}

	@Override
	protected boolean canSpecifyDuration() {
		return true;
	}

	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Component getDescriptionForAssembly() {
		MutableComponent result = VintageLang.translateDirect("recipe.assembly.pressurizing");
		if (ingredients.size() > 1 || !fluidIngredients.isEmpty())
			result.append(" ").append(VintageLang.translateDirect("recipe.assembly.with")).append(" ");
		else return result;
		if (ingredients.size() > 1) {
			if (ingredients.get(1).getItems().length > 0)
				result.append(ingredients.get(1).getItems()[0].getItem().getDescription());
		}
		else if (!fluidIngredients.isEmpty()) {
			if (!fluidIngredients.get(0).getMatchingFluidStacks().isEmpty())
				result.append(fluidIngredients.get(0).getMatchingFluidStacks().get(0).getDisplayName());
		}

		if (ingredients.size() > 2) {
			for (int i = 2; i < ingredients.size() - 1; i++)
				if (ingredients.get(i).getItems().length > 0)
					result.append(", ").append(ingredients.get(i).getItems()[0].getItem().getDescription());
			if (ingredients.get(ingredients.size() - 1).getItems().length > 0) {
				if (fluidIngredients.isEmpty())
					result.append(" ").append(VintageLang.translateDirect("recipe.assembly.and").append(" ").append(ingredients.get(ingredients.size() - 1).getItems()[0].getItem().getDescription()));
				else
					result.append(", ").append(ingredients.get(ingredients.size() - 1).getItems()[0].getItem().getDescription());
			}
		}

		int startNum = ingredients.size() > 1 ? 0 : 1;

		if (fluidIngredients.size() > startNum) {
			for (int i = startNum; i < fluidIngredients.size() - 1; i++)
				if (!fluidIngredients.get(i).getMatchingFluidStacks().isEmpty())
					result.append(", ").append(fluidIngredients.get(i).getMatchingFluidStacks().get(0).getDisplayName());
			if (!fluidIngredients.get(fluidIngredients.size() - 1).getMatchingFluidStacks().isEmpty())
				result.append(" ").append(VintageLang.translateDirect("recipe.assembly.and").append(" ")
						.append(fluidIngredients.get(fluidIngredients.size() - 1).getMatchingFluidStacks().get(0).getDisplayName()));

		}

		return result;
	}

	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(VintageBlocks.VACUUM_CHAMBER.get());
	}

	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> AssemblyPressurizing::new;
	}

	public static boolean match(BasinBlockEntity basin, Recipe<?> recipe, VacuumChamberBlockEntity be) {
		FilteringBehaviour filter = basin.getFilter();
		if (filter == null)
			return false;

		boolean filterTest = filter.test(recipe.getResultItem());
		if (recipe instanceof BasinRecipe) {
			BasinRecipe basinRecipe = (BasinRecipe) recipe;
			if (basinRecipe.getRollableResults()
					.isEmpty()
					&& !basinRecipe.getFluidResults()
					.isEmpty())
				filterTest = filter.test(basinRecipe.getFluidResults()
						.get(0));
		}

		if (!filterTest)
			return false;

		return apply(basin, recipe, be, true);
	}

	public static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, VacuumChamberBlockEntity be) {
		return apply(basin, recipe, be, false);
	}

	private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, VacuumChamberBlockEntity be, boolean test) {
		boolean isBasinRecipe = recipe instanceof BasinRecipe;
		IItemHandler availableItems = basin.getCapability(ForgeCapabilities.ITEM_HANDLER)
				.orElse(null);
		IFluidHandler availableFluids = basin.getCapability(ForgeCapabilities.FLUID_HANDLER)
				.orElse(null);
		IFluidHandler availableSecondaryFluids = be.fluidCapability.orElse(null);

		if (availableItems == null || availableFluids == null || availableSecondaryFluids == null)
			return false;

		BlazeBurnerBlock.HeatLevel heat = BasinBlockEntity.getHeatLevelOf(basin.getLevel()
				.getBlockState(basin.getBlockPos()
						.below(1)));
		if (isBasinRecipe && !((BasinRecipe) recipe).getRequiredHeat()
				.testBlazeBurner(heat))
			return false;

		List<ItemStack> recipeOutputItems = new ArrayList<>();
		List<FluidStack> recipeOutputFluids = new ArrayList<>();
		List<FluidStack> recipeSecondaryOutputFluids = new ArrayList<>();

		List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());
		List<FluidIngredient> fluidIngredients =
				isBasinRecipe ? ((BasinRecipe) recipe).getFluidIngredients() : Collections.emptyList();

		for (boolean simulate : Iterate.trueAndFalse) {

			if (!simulate && test)
				return true;

			int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
			int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];
			int[] extractedSecondaryFluidsFromTank = new int[availableSecondaryFluids.getTanks()];

			Ingredients: for (int i = 0; i < ingredients.size(); i++) {
				Ingredient ingredient = ingredients.get(i);

				for (int slot = 0; slot < availableItems.getSlots(); slot++) {
					if (simulate && availableItems.getStackInSlot(slot)
							.getCount() <= extractedItemsFromSlot[slot])
						continue;
					ItemStack extracted = availableItems.extractItem(slot, 1, true);
					if (!ingredient.test(extracted))
						continue;
					if (!simulate)
						availableItems.extractItem(slot, 1, false);
					extractedItemsFromSlot[slot]++;
					continue Ingredients;
				}

				// something wasn't found
				return false;
			}

			boolean fluidsAffected = false;
			FluidIngredients: for (int i = 0; i < fluidIngredients.size(); i++) {
				FluidIngredient fluidIngredient = fluidIngredients.get(i);
				int amountRequired = fluidIngredient.getRequiredAmount();

				if (recipe instanceof PressurizingRecipe basinRecipe && basinRecipe.secondaryFluidInputs == i) {
					for (int tank = 0; tank < availableSecondaryFluids.getTanks(); tank++) {
						FluidStack fluidStack = availableSecondaryFluids.getFluidInTank(tank);
						if (simulate && fluidStack.getAmount() <= extractedSecondaryFluidsFromTank[tank])
							continue;
						if (!fluidIngredient.test(fluidStack))
							continue;
						int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
						if (!simulate) {
							fluidStack.shrink(drainedAmount);
							fluidsAffected = true;
						}
						amountRequired -= drainedAmount;
						if (amountRequired != 0)
							continue;
						extractedSecondaryFluidsFromTank[tank] += drainedAmount;
						continue FluidIngredients;
					}
				}
				else {
					for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
						FluidStack fluidStack = availableFluids.getFluidInTank(tank);
						if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank])
							continue;
						if (!fluidIngredient.test(fluidStack))
							continue;
						int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
						if (!simulate) {
							fluidStack.shrink(drainedAmount);
							fluidsAffected = true;
						}
						amountRequired -= drainedAmount;
						if (amountRequired != 0)
							continue;
						extractedFluidsFromTank[tank] += drainedAmount;
						continue FluidIngredients;
					}
				}


				// something wasn't found
				return false;
			}

			if (fluidsAffected) {
				basin.getBehaviour(SmartFluidTankBehaviour.INPUT)
						.forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
				basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
						.forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
				be.getBehaviour(SmartFluidTankBehaviour.INPUT)
						.forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
				be.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
						.forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
			}

			if (simulate) {
				if (recipe instanceof PressurizingRecipe basinRecipe) {
					recipeOutputItems.addAll(basinRecipe.rollResults());
					recipeOutputItems.addAll(basinRecipe.getRemainingItems(basin.getInputInventory()));

					NonNullList<FluidStack> fss = basinRecipe.getFluidResults();

					if (basinRecipe.secondaryFluidResults >= 0 && basinRecipe.secondaryFluidResults + 1 <= fss.size())
						recipeSecondaryOutputFluids.add(fss.get(basinRecipe.secondaryFluidResults));
					for (int i = 0; i < basinRecipe.secondaryFluidResults; i++)
						recipeOutputFluids.add(fss.get(i));
					for (int i = basinRecipe.secondaryFluidResults + 1; i < fss.size(); i++)
						recipeOutputFluids.add(fss.get(i));
				}
			}

			if (!basin.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate))
				return false;

			if (recipe instanceof PressurizingRecipe basinRecipe)
				if (basinRecipe.secondaryFluidResults >= 0)
					if (!be.acceptOutputs(recipeSecondaryOutputFluids, simulate))
						return false;
		}

		return true;
	}

	@Override
	public void readAdditional(JsonObject json) {
		if (json.has("secondaryFluidResults")) secondaryFluidResults = json.get("secondaryFluidResults").getAsInt();
		if (json.has("secondaryFluidInputs")) secondaryFluidInputs = json.get("secondaryFluidInputs").getAsInt();
	}

	@Override
	public void readAdditional(FriendlyByteBuf buffer) {
		secondaryFluidResults = buffer.readInt();
		secondaryFluidInputs = buffer.readInt();
	}

	@Override
	public void writeAdditional(JsonObject json) {
		json.addProperty("secondaryFluidResults", secondaryFluidResults);
		json.addProperty("secondaryFluidInputs", secondaryFluidInputs);
	}

	@Override
	public void writeAdditional(FriendlyByteBuf buffer) {
		buffer.writeInt(secondaryFluidResults);
		buffer.writeInt(secondaryFluidInputs);
	}

	public int getSecondaryFluidResults() {
		return secondaryFluidResults;
	}

	public int getSecondaryFluidInputs() {
		return secondaryFluidInputs;
	}

}
