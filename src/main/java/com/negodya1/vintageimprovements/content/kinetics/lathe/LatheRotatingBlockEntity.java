package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.google.common.collect.ImmutableList;
import com.negodya1.vintageimprovements.*;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugationRecipe;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlock;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderFilterSlot;
import com.negodya1.vintageimprovements.content.kinetics.grinder.PolishingRecipe;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HelveBlock;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HelveKineticBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.lathe.recipe_card.RecipeCardItem;
import com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber.PressurizingRecipe;
import com.negodya1.vintageimprovements.foundation.utility.VintageLang;
import com.negodya1.vintageimprovements.infrastructure.config.VintageConfig;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingInventory;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.recipe.RecipeConditions;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.*;
import com.simibubi.create.foundation.utility.animation.LerpedFloat;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.*;
import net.minecraft.core.Direction.Axis;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LatheRotatingBlockEntity extends KineticBlockEntity implements IHaveGoggleInformation {

	private static final Object turningRecipesKey = new Object();

	public SmartInventory inputInv;
	public SmartInventory outputInv;
	public LazyOptional<IItemHandler> capability;
	public float timer;
	public float initialTimer;
	private TurningRecipe lastRecipe;
	private ItemStack playEvent;

	public LatheRotatingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);

		inputInv = new SmartInventory(1, this, 1, false);
		outputInv = new SmartInventory(1, this);
		capability = LazyOptional.of(LatheRotatingBlockEntity.LatheInventoryHandler::new);
		playEvent = ItemStack.EMPTY;
	}

	public void resetRecipe() {
		lastRecipe = null;
		timer = 0;
		initialTimer = 0;
	}

	public float getRenderedHeadOffset() {
		if (inputInv.isEmpty() || timer <= 0) return 0;

		if (timer < 10)
			return timer / 3 / 16f;
		if (initialTimer - timer < 10)
			return (initialTimer - timer) / 3 / 16f;
		return (4 + Mth.sin(timer / 4) * 2) / 16f;
	}

	public List<TurningRecipe> getRecipes() {
		Optional<TurningRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, inputInv.getStackInSlot(0),
				VintageRecipes.TURNING.getType(), TurningRecipe.class);

		List<TurningRecipe> startedSearch = new ArrayList<>();

		if (assemblyRecipe.isPresent())
			startedSearch.add(assemblyRecipe.get());

		Predicate<Recipe<?>> types = RecipeConditions.isOfType(VintageRecipes.TURNING.getType());

		for (Recipe<?> recipe : RecipeFinder.get(turningRecipesKey, level, types))
			if (recipe instanceof TurningRecipe turningRecipe) startedSearch.add(turningRecipe);

		startedSearch = startedSearch.stream().filter(RecipeConditions.firstIngredientMatches(inputInv.getStackInSlot(0)))
				.filter(r -> !VintageRecipes.shouldIgnoreInAutomation(r))
				.collect(Collectors.toList());

		return startedSearch;
	}

	private Optional<TurningRecipe> getRecipe() {
		List<TurningRecipe> recipes = getRecipes();
		if (recipes.isEmpty()) return Optional.empty();

		LatheMovingBlockEntity be = (LatheMovingBlockEntity) level.getBlockEntity(LatheRotatingBlock.getSlave(level, worldPosition, this.getBlockState()));
		if (be == null)
			return Optional.empty();
		if (be.manualMode()) {
			return be.getTemporaryRecipe();
		}
		else {
			int index = be.getIndex(inputInv.getStackInSlot(0));
			if (index >= 0)
				return Optional.of(recipes.get(index));
		}

		return Optional.empty();
	}

	private ItemStack getResultItem() {
		LatheMovingBlockEntity be = (LatheMovingBlockEntity) level.getBlockEntity(LatheRotatingBlock.getSlave(level, worldPosition, this.getBlockState()));
		if (be == null)
			return ItemStack.EMPTY;
		if (!be.recipeSlot.getStackInSlot(0).isEmpty())
			if (be.recipeSlot.getStackInSlot(0).getItem() instanceof RecipeCardItem)
				return RecipeCardItem.getResultItems(be.recipeSlot.getStackInSlot(0)).getStackInSlot(0);
		return ItemStack.EMPTY;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		behaviours.add(new DirectBeltInputBehaviour(this));
		super.addBehaviours(behaviours);
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putFloat("Timer", timer);
		compound.putFloat("InitialTimer", initialTimer);
		compound.put("InputInventory", inputInv.serializeNBT());
		compound.put("OutputInventory", outputInv.serializeNBT());
		super.write(compound, clientPacket);

		if (!clientPacket || playEvent.isEmpty())
			return;
		compound.put("PlayEvent", playEvent.serializeNBT());
		playEvent = ItemStack.EMPTY;
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		timer = compound.getFloat("Timer");
		initialTimer = compound.getFloat("InitialTimer");
		inputInv.deserializeNBT(compound.getCompound("InputInventory"));
		outputInv.deserializeNBT(compound.getCompound("OutputInventory"));
		if (compound.contains("PlayEvent"))
			playEvent = ItemStack.of(compound.getCompound("PlayEvent"));
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(.125f);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void tickAudio() {
		super.tickAudio();
		if (getSpeed() == 0)
			return;

		if (!playEvent.isEmpty()) {
			playEvent = ItemStack.EMPTY;

			AllSoundEvents.SANDING_SHORT.playAt(level, worldPosition, 3, 1, true);
		}
	}

	@Override
	public void tick() {
		super.tick();

		if (inputInv.isEmpty()) {
			lastRecipe = null;
			timer = 0;
		}

		if (getSpeed() == 0 || getSlaveSpeed() == 0)
			return;

		if (Math.abs(getSpeed()) < IRotate.SpeedLevel.FAST.getSpeedValue()) return;
		if (Math.abs(getSlaveSpeed()) < IRotate.SpeedLevel.MEDIUM.getSpeedValue()) return;

		for (int i = 0; i < outputInv.getSlots(); i++)
			if (outputInv.getStackInSlot(i)
					.getCount() == outputInv.getSlotLimit(i))
				return;

		if (timer > 0 && lastRecipe != null) {
			timer -= getProcessingSpeed();

			if (timer <= 0) {
				if (level.isClientSide) {
					lastRecipe = null;
					return;
				}
				process();
			}
			return;
		}

		if (inputInv.getStackInSlot(0).isEmpty()) return;

		if (lastRecipe == null) {
			Optional<TurningRecipe> recipe = getRecipe();

			if (recipe.isPresent() && recipe.get().getIngredients().get(0).test(inputInv.getStackInSlot(0))) {
				lastRecipe = recipe.get();
				timer = lastRecipe.getProcessingDuration();
				initialTimer = timer;
				if (timer <= 0) {
					timer = 200;
					initialTimer = 200;
				}
				sendData();
			}
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		capability.invalidate();
	}

	@Override
	public void destroy() {
		super.destroy();
		ItemHelper.dropContents(level, worldPosition, inputInv);
		ItemHelper.dropContents(level, worldPosition, outputInv);
		LatheMovingBlockEntity be = (LatheMovingBlockEntity) level.getBlockEntity(LatheRotatingBlock.getSlave(level, worldPosition, this.getBlockState()));
		if (be != null)
			ItemHelper.dropContents(level, LatheRotatingBlock.getSlave(level, worldPosition, this.getBlockState()), be.recipeSlot);
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (isItemHandlerCap(cap))
			return capability.cast();
		return super.getCapability(cap, side);
	}

	public boolean checkItem(ItemStack stack) {
		if (!inputInv.isEmpty() || !outputInv.isEmpty()) return false;
		return canProcess(stack);
	}

	private boolean canProcess(ItemStack stack) {
		Optional<TurningRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(level, stack,
				VintageRecipes.TURNING.getType(), TurningRecipe.class);
		if (assemblyRecipe.isPresent()) return true;

		ItemStackHandler tester = new ItemStackHandler(1);
		tester.setStackInSlot(0, stack);
		RecipeWrapper inventoryIn = new RecipeWrapper(tester);

		if (lastRecipe != null && lastRecipe.matches(inventoryIn, level))
			return true;

		return VintageRecipes.TURNING.find(inventoryIn, level)
				.isPresent();
	}

	private void process() {
		RecipeWrapper inventoryIn = new RecipeWrapper(inputInv);

		if (lastRecipe == null || !lastRecipe.matches(inventoryIn, level)) {
			Optional<TurningRecipe> recipe = getRecipe();
			if (!recipe.isPresent()) return;
			lastRecipe = recipe.get();
		}

		ItemStack stackInSlot = inputInv.getStackInSlot(0);
		stackInSlot.shrink(1);
		inputInv.setStackInSlot(0, stackInSlot);

		lastRecipe.rollResults()
				.forEach(stack -> ItemHandlerHelper.insertItemStacked(outputInv, stack, false));

		lastRecipe = null;

		sendData();
		setChanged();
	}

	public float getProcessingSpeed() {
		return 1 + Mth.clamp((int) ((Math.abs(getSpeed()) - IRotate.SpeedLevel.FAST.getSpeedValue()) / (256 - IRotate.SpeedLevel.FAST.getSpeedValue())) / 2f, .0f, 0.5f)
				+ Mth.clamp((int) ((Math.abs(getSlaveSpeed()) - IRotate.SpeedLevel.MEDIUM.getSpeedValue()) / (256 - IRotate.SpeedLevel.MEDIUM.getSpeedValue())) / 2f, .0f, 0.5f);
	}

	public float getSlaveSpeed() {
		LatheMovingBlockEntity be = (LatheMovingBlockEntity) level.getBlockEntity(LatheRotatingBlock.getSlave(level, worldPosition, this.getBlockState()));
		if (be != null)
			return be.getSpeed();
		return 0;
	}

	public boolean isSlaveSpeedRequirementFulfilled() {
		LatheMovingBlockEntity be = (LatheMovingBlockEntity) level.getBlockEntity(LatheRotatingBlock.getSlave(level, worldPosition, this.getBlockState()));
		if (be != null)
			return be.isSpeedRequirementFulfilled();
		return true;
	}

	public float calculateSlaveStressApplied() {
		LatheMovingBlockEntity be = (LatheMovingBlockEntity) level.getBlockEntity(LatheRotatingBlock.getSlave(level, worldPosition, this.getBlockState()));
		if (be != null)
			return be.calculateStressApplied();
		return 0;
	}

	private class LatheInventoryHandler extends CombinedInvWrapper {

		public LatheInventoryHandler() {
			super(inputInv, outputInv);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			if (!inputInv.isEmpty() || !outputInv.isEmpty())
				return false;
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return false;
			return canProcess(stack) && super.isItemValid(slot, stack);
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			if (outputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return stack;
			if (!isItemValid(slot, stack))
				return stack;
			return super.insertItem(slot, stack, simulate);
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (inputInv == getHandlerFromIndex(getIndexForSlot(slot)))
				return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}

	}

	@Override
	public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		boolean isSuperAdded = super.addToTooltip(tooltip, isPlayerSneaking);

		boolean notFastEnough = !isSlaveSpeedRequirementFulfilled() && getSlaveSpeed() != 0;

		if (overStressed && AllConfigs.client().enableOverstressedTooltip.get()) {
			Lang.translate("gui.stressometer.overstressed")
					.style(GOLD)
					.forGoggles(tooltip);
			Component hint = Lang.translateDirect("gui.contraptions.network_overstressed");
			List<Component> cutString = TooltipHelper.cutTextComponent(hint, TooltipHelper.Palette.GRAY_AND_WHITE);
			for (int i = 0; i < cutString.size(); i++)
				Lang.builder()
						.add(cutString.get(i)
								.copy())
						.forGoggles(tooltip);
			return true;
		}

		if (notFastEnough) {
			Lang.translate("tooltip.speedRequirement")
					.style(GOLD)
					.forGoggles(tooltip);
			MutableComponent hint =
					Lang.translateDirect("gui.contraptions.not_fast_enough", I18n.get(getBlockState().getBlock()
							.getDescriptionId()));
			List<Component> cutString = TooltipHelper.cutTextComponent(hint, TooltipHelper.Palette.GRAY_AND_WHITE);
			for (int i = 0; i < cutString.size(); i++)
				Lang.builder()
						.add(cutString.get(i)
								.copy())
						.forGoggles(tooltip);
			return true;
		}

		return isSuperAdded;
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		float stressAtBase = calculateSlaveStressApplied();
		Lang.translate("gui.goggles.kinetic_stats")
				.forGoggles(tooltip);

		addStressImpactStats(tooltip, stressAtBase);

		LatheMovingBlockEntity be = (LatheMovingBlockEntity) level.getBlockEntity(LatheRotatingBlock.getSlave(level, worldPosition, this.getBlockState()));
		if (be != null) {
			if (be.manualMode()) {
				VintageLang.translate("gui.goggles.current_mode")
						.add(Lang.text(" ")).add(VintageLang.translate("gui.goggles.manual_mode"))
						.style(ChatFormatting.WHITE).forGoggles(tooltip);
				if (lastRecipe == null)
					VintageLang.translate("gui.goggles.current_recipe")
							.add(Lang.text(" ")).add(VintageLang.translate("gui.goggles.no_recipe"))
							.style(ChatFormatting.DARK_GRAY).forGoggles(tooltip);
				else VintageLang.translate("gui.goggles.current_recipe")
						.add(Lang.text(" ")).add(Components.translatable(lastRecipe.getResultItem().getDescriptionId()))
						.style(ChatFormatting.GREEN).forGoggles(tooltip);
			}
			else {
				VintageLang.translate("gui.goggles.current_mode")
						.add(Lang.text(" ")).add(VintageLang.translate("gui.goggles.automatic_mode"))
						.style(GOLD).forGoggles(tooltip);
				if (lastRecipe == null) {
					if (!getResultItem().is(Items.AIR))
						VintageLang.translate("gui.goggles.current_recipe")
							.add(Lang.text(" ")).add(Components.translatable(getResultItem().getDescriptionId()))
							.style(ChatFormatting.GREEN).forGoggles(tooltip);
					else VintageLang.translate("gui.goggles.current_recipe")
							.add(Lang.text(" ")).add(VintageLang.translate("gui.goggles.no_recipe"))
							.style(ChatFormatting.DARK_GRAY).forGoggles(tooltip);
				}
				else VintageLang.translate("gui.goggles.current_recipe")
						.add(Lang.text(" ")).add(Components.translatable(lastRecipe.getResultItem().getDescriptionId()))
						.style(ChatFormatting.GREEN).forGoggles(tooltip);
			}


		}

		return true;
	}
}
