package com.negodya1.vintageimprovements.content.kinetics.curving_press;

import java.util.List;
import java.util.Optional;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.VintageRecipesList;
import com.negodya1.vintageimprovements.foundation.utility.VintageLang;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.LangBuilder;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;

import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingBehaviour.Mode;
import com.negodya1.vintageimprovements.content.kinetics.curving_press.CurvingBehaviour.CurvingBehaviourSpecifics;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

public class CurvingPressBlockEntity extends KineticBlockEntity implements CurvingBehaviourSpecifics {

	private static final Object compressingRecipesKey = new Object();

	public int mode;
	public ItemStack itemAsHead;

	public CurvingBehaviour pressingBehaviour;

	public CurvingPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		mode = 0;
		itemAsHead = ItemStack.EMPTY;
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, -1.5, 0)
			.expandTowards(0, 1, 0);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		pressingBehaviour = new CurvingBehaviour(this);
		behaviours.add(pressingBehaviour);
	}

	public void onItemPressed(ItemStack result) {

	}

	@Override
	public void destroy() {
		super.destroy();
		if (mode > 0) {
			SmartInventory headInv = new SmartInventory(1, this);

			switch (mode) {
				case 2 -> ItemHandlerHelper.insertItemStacked(headInv, new ItemStack(VintageItems.CONCAVE_CURVING_HEAD.get()), false);
				case 3 -> ItemHandlerHelper.insertItemStacked(headInv, new ItemStack(VintageItems.W_SHAPED_CURVING_HEAD.get()), false);
				case 4 -> ItemHandlerHelper.insertItemStacked(headInv, new ItemStack(VintageItems.V_SHAPED_CURVING_HEAD.get()), false);
				case 5 -> {
					if (!itemAsHead.isEmpty()) ItemHandlerHelper.insertItemStacked(headInv, itemAsHead, false);
					else ItemHandlerHelper.insertItemStacked(headInv, ItemStack.EMPTY, false);
				}
				default -> ItemHandlerHelper.insertItemStacked(headInv, new ItemStack(VintageItems.CONVEX_CURVING_HEAD.get()), false);
			}

			ItemHelper.dropContents(level, worldPosition, headInv);
		}
	}

	public CurvingBehaviour getPressingBehaviour() {
		return pressingBehaviour;
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		compound.putInt("HeadMode", mode);
		if (!itemAsHead.isEmpty())
			compound.putString("ItemAsHead", itemAsHead.getItem().toString());
		super.write(compound, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		mode = compound.getInt("HeadMode");
		if (compound.contains("ItemAsHead")) itemAsHead = new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString("ItemAsHead"))));
		else itemAsHead = ItemStack.EMPTY;
		super.read(compound, clientPacket);
	}

	static public boolean canCurve(Recipe recipe) {
		return canCurve(recipe, 1);
	}

	static public boolean canCurve(Recipe recipe, int mode) {
		if (!(recipe instanceof CraftingRecipe)) return false;

		if (mode == 1 || mode == 2) {
			ItemStack item = null;

			NonNullList<Ingredient> in = recipe.getIngredients();
			if (in.get(mode - 1).isEmpty()) return false;

			int matches = 0;
			boolean it = mode == 2;

			for (Ingredient i : in) {
				it = !it;

				if (it) {
					if (!i.isEmpty()) { if (item == null) item = i.getItems()[0]; }
					else return false;

					if (i.test(item)) {
						matches++;
						continue;
					}
				}
				if (!i.isEmpty()) return false;
			}

			if (matches != 3) return false;

			return true;
		}
		else if (mode == 3 || mode == 4) {
			ItemStack item = null;

			NonNullList<Ingredient> in = recipe.getIngredients();
			if (mode == 4) {
				if (in.get(2).isEmpty() || in.get(3).isEmpty()) return false;

				int matches = 0;
				int empty = 0;

				for (Ingredient i : in) {
					if (!i.isEmpty()) {
						if (item == null) {
							if (i.getItems().length <= 0)
								return false;
							item = i.getItems()[0];
						}

						if (i.test(item)) {
							matches++;
						}
						else return false;
					}
					else {
						empty++;
						if (empty > 1) return false;
					}
				}

				return matches == 3 && empty == 1;
			}
			else {
				if (!in.get(2).isEmpty() && !in.get(3).isEmpty()) return false;

				int matches = 0;
				int empty = 0;

				for (Ingredient i : in) {
					if (!i.isEmpty()) {
						if (item == null) {
							if (i.getItems().length <= 0)
								return false;
							item = i.getItems()[0];
						}

						if (i.test(item)) {
							matches++;
						}
						else return false;
					}
					else {
						empty++;
						if (empty > 1) return false;
					}
				}

				return matches == 3 && empty == 1;
			}
		}

		return false;
	}

	private boolean tryToCurve(ItemEntity itemEntity, boolean simulate) {
		if (!VintageConfig.server().recipes.allowAutoCurvingRecipes.get()) return false;
		if (mode <= 0 || mode > 4) return false;

		List<CraftingRecipe> recipes = VintageRecipesList.getCurving(mode);
		Recipe: for (CraftingRecipe recipe : recipes) {
			if (recipe instanceof ShapelessRecipe) continue;
			if (!recipe.canCraftInDimensions(mode < 3 ? 3 : 2, 2)) continue;
			if (recipe.getIngredients().size() != 6 && mode < 3) continue;
			if (recipe.getIngredients().size() != 4 && mode >= 3) continue;

			ItemStack item = itemEntity.getItem();

			NonNullList<Ingredient> in = recipe.getIngredients();

			int matches = 0;

			for (Ingredient i : in) {
				if (i.test(item)) {
					matches++;
					continue;
				}
				if (!i.isEmpty()) continue Recipe;
			}

			if (matches != 3) continue;

			if (recipe.getResultItem(RegistryAccess.EMPTY).getCount() < 3 && itemEntity.getItem().getCount() < 3) return false;

			if (simulate) return true;

			if (recipe.getResultItem(RegistryAccess.EMPTY).getCount() >= 3) {
				ItemStack itemCreated = ItemStack.EMPTY;
				pressingBehaviour.particleItems.add(item);

				if (item.getCount() == 1) {
					itemEntity.setItem(new ItemStack(recipe.getResultItem(RegistryAccess.EMPTY).copy().getItem()));
				} else {
					if (itemCreated.isEmpty())
						itemCreated = recipe.getResultItem(RegistryAccess.EMPTY).copy();
					ItemEntity created =
							new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), new ItemStack(recipe.getResultItem(RegistryAccess.EMPTY).copy().getItem()));
					created.setDefaultPickUpDelay();
					created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
					level.addFreshEntity(created);
					item.shrink(1);
				}
			}
			else {
				ItemStack itemCreated = ItemStack.EMPTY;
				pressingBehaviour.particleItems.add(item);

				if (item.getCount() == 3) {
					itemEntity.setItem(new ItemStack(recipe.getResultItem(RegistryAccess.EMPTY).copy().getItem()));
				} else {
					if (itemCreated.isEmpty())
						itemCreated = recipe.getResultItem(RegistryAccess.EMPTY).copy();
					ItemEntity created =
							new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), recipe.getResultItem(RegistryAccess.EMPTY).copy());
					created.setDefaultPickUpDelay();
					created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
					level.addFreshEntity(created);
					item.shrink(3);
				}
			}

			return true;
		}

		return false;
	}

	private boolean tryToCurve(List<ItemStack> outputList, ItemStack item, boolean simulate) {
		if (!VintageConfig.server().recipes.allowAutoCurvingRecipes.get()) return false;
		if (mode <= 0 || mode >= 4) return false;

		List<CraftingRecipe> recipes = VintageRecipesList.getCurving(mode);
		Recipe: for (CraftingRecipe recipe : recipes) {
			if (recipe instanceof ShapelessRecipe) continue;
			if (!recipe.canCraftInDimensions(mode < 3 ? 3 : 2, 2)) continue;
			if (recipe.getIngredients().size() != 6 && mode < 3) continue;
			if (recipe.getIngredients().size() != 4 && mode >= 3) continue;

			NonNullList<Ingredient> in = recipe.getIngredients();

			int matches = 0;

			for (Ingredient i : in) {
				if (i.test(item)) {
					matches++;
					continue;
				}
				if (!i.isEmpty()) continue Recipe;
			}

			if (matches != 3) continue;

			if (simulate) return true;

			outputList.add(new ItemStack(recipe.getResultItem(RegistryAccess.EMPTY).copy().getItem()));
			pressingBehaviour.particleItems.add(item);
			return true;
		}

		return false;
	}

	@Override
	public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
		ItemStack item = itemEntity.getItem();
		Optional<CurvingRecipe> recipe = getRecipe(item);
		if (!recipe.isPresent())
			return tryToCurve(itemEntity, simulate);
		if (recipe.get().getMode() != mode)
			return false;
		if (mode == 5) {
			if (itemAsHead.isEmpty()) return false;
			if (!itemAsHead.is(recipe.get().itemAsHead)) return false;
		}
		if (simulate)
			return true;

		ItemStack itemCreated = ItemStack.EMPTY;
		pressingBehaviour.particleItems.add(item);
		if (canProcessInBulk() || item.getCount() == 1) {
			RecipeApplier.applyRecipeOn(itemEntity, recipe.get());
			itemCreated = itemEntity.getItem()
				.copy();
		} else {
			for (ItemStack result : RecipeApplier.applyRecipeOn(level, ItemHandlerHelper.copyStackWithSize(item, 1),
				recipe.get())) {
				if (itemCreated.isEmpty())
					itemCreated = result.copy();
				ItemEntity created =
					new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
				created.setDefaultPickUpDelay();
				created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
				level.addFreshEntity(created);
			}
			item.shrink(1);
		}

		if (!itemCreated.isEmpty())
			onItemPressed(itemCreated);
		return true;
	}

	@Override
	public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
		Optional<CurvingRecipe> recipe = getRecipe(input.stack);
		if (!recipe.isPresent())
			return tryToCurve(outputList, input.stack, simulate);
		if (recipe.get().getMode() != mode)
			return false;
		if (mode == 5) {
			if (itemAsHead.isEmpty()) return false;
			if (!itemAsHead.is(recipe.get().itemAsHead)) return false;
		}
		if (simulate)
			return true;
		pressingBehaviour.particleItems.add(input.stack);
		List<ItemStack> outputs = RecipeApplier.applyRecipeOn(level,
			canProcessInBulk() ? input.stack : ItemHandlerHelper.copyStackWithSize(input.stack, 1), recipe.get());

		for (ItemStack created : outputs) {
			if (!created.isEmpty()) {
				onItemPressed(created);
				break;
			}
		}

		outputList.addAll(outputs);
		return true;
	}

	private static final RecipeWrapper pressingInv = new RecipeWrapper(new ItemStackHandler(1));

	public Optional<CurvingRecipe> getRecipe(ItemStack item) {
		Optional<CurvingRecipe> assemblyRecipe =
			SequencedAssemblyRecipe.getRecipe(level, item, VintageRecipes.CURVING.getType(), CurvingRecipe.class);
		if (assemblyRecipe.isPresent())
			return assemblyRecipe;

		pressingInv.setItem(0, item);
		assemblyRecipe = VintageRecipes.CURVING.find(pressingInv, level);
		if (assemblyRecipe.isPresent()) return assemblyRecipe;

		return VintageRecipes.CURVING.find(pressingInv, level);
	}

	@Override
	public float getKineticSpeed() {
		return getSpeed();
	}

	@Override
	public boolean canProcessInBulk() {
		return AllConfigs.server().recipes.bulkPressing.get();
	}

	@Override
	public int getParticleAmount() {
		return 15;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		if (mode <= 0 || mode > 5) return true;

		switch (mode) {
			case 2 -> VintageLang.translate("gui.goggles.curving_head").add(Component.translatable(VintageItems.CONCAVE_CURVING_HEAD.get().getDescriptionId()).withStyle(ChatFormatting.GOLD))
					.forGoggles(tooltip);
			case 3 -> VintageLang.translate("gui.goggles.curving_head").add(Component.translatable(VintageItems.W_SHAPED_CURVING_HEAD.get().getDescriptionId()).withStyle(ChatFormatting.BLUE))
					.forGoggles(tooltip);
			case 4 -> VintageLang.translate("gui.goggles.curving_head").add(Component.translatable(VintageItems.V_SHAPED_CURVING_HEAD.get().getDescriptionId()).withStyle(ChatFormatting.YELLOW))
					.forGoggles(tooltip);
			case 5 -> {
				if (!itemAsHead.isEmpty()) VintageLang.translate("gui.goggles.curving_head").add(Component.translatable(itemAsHead.getDescriptionId()).withStyle(ChatFormatting.LIGHT_PURPLE))
						.forGoggles(tooltip);
			}
			default -> VintageLang.translate("gui.goggles.curving_head").add(Component.translatable(VintageItems.CONVEX_CURVING_HEAD.get().getDescriptionId()).withStyle(ChatFormatting.GREEN))
					.forGoggles(tooltip);
		}

		return true;
	}
}
