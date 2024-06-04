package com.negodya1.vintageimprovements.content.kinetics.curving_press;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.VintageRecipesList;
import com.negodya1.vintageimprovements.foundation.utility.VintageLang;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
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
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.Components;
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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import net.minecraftforge.registries.ForgeRegistries;

public class CurvingPressBlockEntity extends KineticBlockEntity implements CurvingBehaviourSpecifics {

	private static final Object curvingRecipesKey = new Object();

	public int mode;
	public SmartInventory itemAsHead;
	public int durability;
	boolean redstoneModule;

	public CurvingBehaviour pressingBehaviour;

	public CurvingPressBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		mode = 0;
		durability = 0;
		itemAsHead = new SmartInventory(1, this);
		redstoneModule = false;
	}

	@Override
	public void destroy() {
		super.destroy();
		if (mode > 0) {
			SmartInventory headInv = new SmartInventory(1, this);

			if (mode < 5) {
				ItemStack stack;

				switch (mode) {
					case 2 -> stack = new ItemStack(VintageItems.CONCAVE_CURVING_HEAD.get());
					case 3 -> stack = new ItemStack(VintageItems.W_SHAPED_CURVING_HEAD.get());
					case 4 -> stack = new ItemStack(VintageItems.V_SHAPED_CURVING_HEAD.get());
					default -> stack = new ItemStack(VintageItems.CONVEX_CURVING_HEAD.get());
				}

				stack.setDamageValue(1000 - durability);
				ItemHandlerHelper.insertItemStacked(headInv, stack, false);
			}
			else {
				if (!itemAsHead.isEmpty()) ItemHandlerHelper.insertItemStacked(headInv, itemAsHead.getItem(0), false);
				else ItemHandlerHelper.insertItemStacked(headInv, ItemStack.EMPTY, false);
			}

			ItemHelper.dropContents(level, worldPosition, headInv);
		}

		if (redstoneModule) {
			SmartInventory headInv = new SmartInventory(1, this);
			ItemHandlerHelper.insertItemStacked(headInv, VintageItems.REDSTONE_MODULE.asStack(), false);
			ItemHelper.dropContents(level, worldPosition, headInv);
		}
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

	public boolean addRedstoneApp(ItemStack items) {
		if (redstoneModule) return false;
		if (items.getItem() != VintageItems.REDSTONE_MODULE.get()) return false;
		redstoneModule = true;
		return true;
	}

	public void onItemPressed(ItemStack result, int damage) {
		if (damage < 0) damage = VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get();
		if (damage < 0) damage = 0;

		if (damage > 0) {
			if (mode < 5 && mode > 0) {
				durability -= damage;
				if (durability <= 0)
					mode = 0;
			}
			if (mode == 5 && itemAsHead.getItem(0).isDamageableItem()) {
				itemAsHead.getItem(0).hurt(damage, level.random, null );
				if (itemAsHead.getItem(0).getDamageValue() >= itemAsHead.getItem(0).getMaxDamage()) {
					itemAsHead.getItem(0).shrink(1);
					mode = 0;
				}
				else durability = itemAsHead.getItem(0).getDamageValue();
			}
		}

		setChanged();
		sendData();
	}

	public CurvingBehaviour getPressingBehaviour() {
		return pressingBehaviour;
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		compound.putInt("HeadMode", mode);
		compound.putInt("Durability", durability);
		if (!itemAsHead.isEmpty())
			compound.put("ItemAsHead", itemAsHead.serializeNBT());
		compound.putBoolean("Redstone", redstoneModule);
		super.write(compound, clientPacket);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		mode = compound.getInt("HeadMode");
		durability = compound.getInt("Durability");
		if (compound.contains("ItemAsHead")) {
			itemAsHead.deserializeNBT(compound.getCompound("ItemAsHead"));
			if (itemAsHead.getItem(0).isDamageableItem())
				itemAsHead.getItem(0).setDamageValue(durability);
		}
		else itemAsHead.clearContent();
		redstoneModule = compound.getBoolean("Redstone");
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

			if (recipe.getResultItem().getCount() < 3 && itemEntity.getItem().getCount() < 3) return false;

			if (simulate) return true;

			if (recipe.getResultItem().getCount() >= 3) {
				ItemStack itemCreated = ItemStack.EMPTY;
				pressingBehaviour.particleItems.add(item);

				if (item.getCount() == 1) {
					itemEntity.setItem(new ItemStack(recipe.getResultItem().copy().getItem()));
					onItemPressed(itemEntity.getItem(), VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get());
				} else {
					if (itemCreated.isEmpty())
						itemCreated = recipe.getResultItem().copy();
					ItemEntity created =
							new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), new ItemStack(recipe.getResultItem().copy().getItem()));
					created.setDefaultPickUpDelay();
					created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
					level.addFreshEntity(created);
					item.shrink(1);
				}

				if (!itemCreated.isEmpty())
					onItemPressed(itemCreated, VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get());
			}
			else {
				ItemStack itemCreated = ItemStack.EMPTY;
				pressingBehaviour.particleItems.add(item);

				if (item.getCount() == 3) {
					itemEntity.setItem(new ItemStack(recipe.getResultItem().copy().getItem()));
					onItemPressed(itemEntity.getItem(), VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get());
				} else {
					if (itemCreated.isEmpty())
						itemCreated = recipe.getResultItem().copy();
					ItemEntity created =
							new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), recipe.getResultItem().copy());
					created.setDefaultPickUpDelay();
					created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
					level.addFreshEntity(created);
					item.shrink(3);
				}

				if (!itemCreated.isEmpty())
					onItemPressed(itemCreated, VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get());
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

			outputList.add(new ItemStack(recipe.getResultItem().copy().getItem()));
			pressingBehaviour.particleItems.add(item);
			for (ItemStack created : outputList) {
				if (!created.isEmpty()) {
					onItemPressed(created, VintageConfig.server().recipes.damageHeadAfterAutoCurvingRecipe.get());
					break;
				}
			}
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
			if (!itemAsHead.getItem(0).is(recipe.get().itemAsHead)) return false;
		}
		if (itemEntity.getItem().getCount() < recipe.get().getIngredients().size()) return false;
		if (simulate)
			return true;

		ItemStack itemCreated = ItemStack.EMPTY;
		pressingBehaviour.particleItems.add(item.copy());
		for (ItemStack result : RecipeApplier.applyRecipeOn(ItemHandlerHelper.copyStackWithSize(item, 1),
				recipe.get())) {
			if (itemCreated.isEmpty())
				itemCreated = result.copy();
			ItemEntity created =
					new ItemEntity(level, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
			created.setDefaultPickUpDelay();
			created.setDeltaMovement(VecHelper.offsetRandomly(Vec3.ZERO, level.random, .05f));
			level.addFreshEntity(created);
		}
		item.shrink(recipe.get().getIngredients().size());

		if (!itemCreated.isEmpty())
			onItemPressed(itemCreated, recipe.get().headDamage);
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
			if (!itemAsHead.getItem(0).is(recipe.get().itemAsHead)) return false;
		}
		if (input.stack.getCount() < recipe.get().getIngredients().size()) return false;

		if (simulate)
			return true;
		pressingBehaviour.particleItems.add(input.stack);
		List<ItemStack> outputs = RecipeApplier.applyRecipeOn(ItemHandlerHelper.copyStackWithSize(input.stack, 1), recipe.get());
		input.stack.shrink(recipe.get().getIngredients().size() - 1);

		for (ItemStack created : outputs) {
			if (!created.isEmpty()) {
				onItemPressed(created, recipe.get().headDamage);
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
		if (assemblyRecipe.isPresent()) return assemblyRecipe;

		pressingInv.setItem(0, item);
		assemblyRecipe = VintageRecipes.CURVING.find(pressingInv, level);

		if (assemblyRecipe.isPresent())
			if (mode == assemblyRecipe.get().getMode()) {
				if (mode == 5) {
					if (itemAsHead.getItem(0).is(assemblyRecipe.get().getItemAsHead())) return assemblyRecipe;
				}
				else return assemblyRecipe;
			}

		Predicate<Recipe<?>> types = RecipeConditions.isOfType(VintageRecipes.CURVING.getType());

		List<Recipe<?>> startedSearch = RecipeFinder.get(curvingRecipesKey, level, types);
		startedSearch = startedSearch.stream()
				.filter(RecipeConditions.firstIngredientMatches(item))
				.filter(r -> !VintageRecipes.shouldIgnoreInAutomation(r))
				.filter(r -> {if (r instanceof CurvingRecipe curvingRecipe)
					if (mode == curvingRecipe.mode) {
						if (mode == 5)
							return itemAsHead.getItem(0).is(curvingRecipe.getItemAsHead());
						return true;}
					return false;})
				.collect(Collectors.toList());

		for (Recipe recipe : startedSearch) {
			if (recipe instanceof CurvingRecipe curvingRecipe)
				return Optional.ofNullable(curvingRecipe);
		}

		return VintageRecipes.CURVING.find(pressingInv, level);
	}

	@Override
	public float getKineticSpeed() {
		return getSpeed();
	}

	@Override
	public boolean canProcessInBulk() {
		return false;
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
			case 2 -> VintageLang.translate("gui.goggles.curving_head").add(Components.translatable(VintageItems.CONCAVE_CURVING_HEAD.get().getDescriptionId()).withStyle(ChatFormatting.GOLD))
					.forGoggles(tooltip);
			case 3 -> VintageLang.translate("gui.goggles.curving_head").add(Components.translatable(VintageItems.W_SHAPED_CURVING_HEAD.get().getDescriptionId()).withStyle(ChatFormatting.BLUE))
					.forGoggles(tooltip);
			case 4 -> VintageLang.translate("gui.goggles.curving_head").add(Components.translatable(VintageItems.V_SHAPED_CURVING_HEAD.get().getDescriptionId()).withStyle(ChatFormatting.YELLOW))
					.forGoggles(tooltip);
			case 5 -> {
				if (!itemAsHead.isEmpty()) VintageLang.translate("gui.goggles.curving_head").add(Components.translatable(itemAsHead.getItem(0).getDescriptionId()).withStyle(ChatFormatting.LIGHT_PURPLE))
						.forGoggles(tooltip);
			}
			default -> VintageLang.translate("gui.goggles.curving_head").add(Components.translatable(VintageItems.CONVEX_CURVING_HEAD.get().getDescriptionId()).withStyle(ChatFormatting.GREEN))
					.forGoggles(tooltip);
		}

		if (mode == 5) {
			if (itemAsHead.getItem(0).isDamageableItem())
				VintageLang.translate("gui.goggles.durability")
						.add(Components.literal(": " + (itemAsHead.getItem(0).getMaxDamage() - durability)))
						.forGoggles(tooltip);
		}
		else
			VintageLang.translate("gui.goggles.durability")
					.add(Components.literal(": " + durability))
					.forGoggles(tooltip);

		if (redstoneModule) {
			VintageLang.translate("gui.goggles.redstone_module")
					.style(ChatFormatting.DARK_PURPLE).forGoggles(tooltip);
		}

		return true;
	}

	public int getAnalogSignal() {
		if (!redstoneModule) return 0;
		return (mode > 0 ? 15 : 0);
	}

}