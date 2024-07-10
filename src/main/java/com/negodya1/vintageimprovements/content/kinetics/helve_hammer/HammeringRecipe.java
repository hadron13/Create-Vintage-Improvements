package com.negodya1.vintageimprovements.content.kinetics.helve_hammer;

import java.util.*;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyCentrifugation;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyHammering;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyVibrating;
import com.negodya1.vintageimprovements.foundation.utility.VintageLang;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Iterate;

import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

public class HammeringRecipe extends ProcessingRecipe<SmartInventory> implements IAssemblyRecipe {

	int hammerBlows;
	Item anvilBlock;

	public HammeringRecipe(ProcessingRecipeParams params) {
		super(VintageRecipes.HAMMERING, params);
		hammerBlows = 1;
		anvilBlock = Blocks.AIR.asItem();
	}

	public Item getAnvilBlock() {
		return anvilBlock;
	}

	public static boolean match(HelveBlockEntity centrifuge, Recipe<?> recipe) {
		return apply(centrifuge, recipe, true);
	}

	public static boolean apply(HelveBlockEntity centrifuge, Recipe<?> recipe) {
		return apply(centrifuge, recipe, false);
	}

	private static boolean apply(HelveBlockEntity centrifuge, Recipe<?> recipe, boolean test) {
		IItemHandlerModifiable availableItems = (IItemHandlerModifiable) centrifuge.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
				.orElse(null);

		if (availableItems == null)
			return false;

		if (recipe instanceof HammeringRecipe hammeringRecipe)
			if (!centrifuge.anvilBlock.asItem().getDefaultInstance().is(hammeringRecipe.anvilBlock))
				return false;

		List<ItemStack> recipeOutputItems = new ArrayList<>();
		List<Ingredient> ingredients = new LinkedList<>(recipe.getIngredients());

		for (boolean simulate : Iterate.trueAndFalse) {
			if (!simulate && test)
				return true;

			int[] extractedItemsFromSlot = new int[availableItems.getSlots()];

			Ingredients: for (int i = 0; i < ingredients.size(); i++) {
				Ingredient ingredient = ingredients.get(i);

				for (int slot = 0; slot < availableItems.getSlots(); slot++) {
					if (simulate && availableItems.getStackInSlot(slot)
							.getCount() <= extractedItemsFromSlot[slot])
						continue;
					ItemStack extracted = availableItems.getStackInSlot(slot);

					if (!ingredient.test(extracted))
						continue;
					if (!simulate)
						extracted.shrink(1);

					extractedItemsFromSlot[slot]++;
					continue Ingredients;
				}

				// something wasn't found
				return false;
			}

			if (simulate) {
				if (recipe instanceof HammeringRecipe centrifugeRecipe) {
					recipeOutputItems.addAll(centrifugeRecipe.rollResults());
					recipeOutputItems.addAll(centrifugeRecipe.getRemainingItems(centrifuge.getInputInventory()));
				}
			}

			if (!centrifuge.acceptOutputs(recipeOutputItems, simulate))
				return false;
		}

		return true;
	}

	@Override
	protected int getMaxInputCount() {
		return 3;
	}

	@Override
	protected int getMaxOutputCount() {
		return 3;
	}

	@Override
	public boolean matches(SmartInventory inv, @Nonnull Level worldIn) {
		if (inv.isEmpty())
			return false;
		if (ingredients.isEmpty())
			return !fluidIngredients.isEmpty();

		for (Ingredient ingredient : ingredients)
			if (inv.countItem(ingredient.getItems()[0].getItem()) < ingredient.getItems().length) return false;

		return true;
	}

	@Override
	public void addAssemblyIngredients(List<Ingredient> list) {}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Component getDescriptionForAssembly() {
		MutableComponent result = VintageLang.translateDirect("recipe.assembly.hammering");
		if (ingredients.size() > 1) {
			if (ingredients.get(1).getItems().length > 0)
				result.append(" ").append(VintageLang.translateDirect("recipe.assembly.with")).append(" ").append(ingredients.get(1).getItems()[0].getItem().getDescription());

			if (ingredients.size() > 2) {
				for (int i = 2; i < ingredients.size() - 1; i++)
					if (ingredients.get(i).getItems().length > 0)
						result.append(", ").append(ingredients.get(i).getItems()[0].getItem().getDescription());
				if (ingredients.get(ingredients.size() - 1).getItems().length > 0)
					result.append(" ").append(VintageLang.translateDirect("recipe.assembly.and").append(" ").append(ingredients.get(ingredients.size() - 1).getItems()[0].getItem().getDescription()));
			}
		}

		if (anvilBlock != Blocks.AIR.asItem()) {
			result.append(" ").append(VintageLang.translateDirect("recipe.assembly.on")).append(" ")
					.append(Components.translatable(anvilBlock.getDescriptionId()));
		}

		return result;
	}

	@Override
	public void addRequiredMachines(Set<ItemLike> list) {
		list.add(VintageBlocks.HELVE.get());
	}

	@Override
	public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
		return () -> AssemblyHammering::new;
	}

	@Override
	public void readAdditional(JsonObject json) {
		if (json.has("hammerBlows")) hammerBlows = json.get("hammerBlows").getAsInt();
		else hammerBlows = 1;

		if (json.has("anvilBlock")) anvilBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(json.get("anvilBlock").getAsString())).asItem();
		else anvilBlock = Items.AIR;
	}

	@Override
	public void readAdditional(FriendlyByteBuf buffer) {
		hammerBlows = buffer.readInt();
		anvilBlock = buffer.readItem().getItem();
	}

	@Override
	public void writeAdditional(JsonObject json) {
		json.addProperty("hammerBlows", hammerBlows);
		if (!anvilBlock.equals(Blocks.AIR)) json.addProperty("anvilBlock", anvilBlock.toString());
	}

	@Override
	public void writeAdditional(FriendlyByteBuf buffer) {
		buffer.writeInt(hammerBlows);
		buffer.writeItem(new ItemStack(anvilBlock));
	}

	public int getHammerBlows() {
		return hammerBlows;
	}
}
