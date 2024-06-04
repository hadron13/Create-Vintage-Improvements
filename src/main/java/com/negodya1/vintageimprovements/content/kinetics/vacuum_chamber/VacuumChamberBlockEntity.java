package com.negodya1.vintageimprovements.content.kinetics.vacuum_chamber;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.negodya1.vintageimprovements.VintageRecipes;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlock;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlockEntity;
import com.negodya1.vintageimprovements.foundation.utility.VintageLang;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.fluids.FluidPropagator;
import com.simibubi.create.content.fluids.FluidTransportBehaviour;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.CombinedTankWrapper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import com.simibubi.create.foundation.utility.*;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

public class VacuumChamberBlockEntity extends BasinOperatingBlockEntity {

	private static final Object vacuumizingRecipesKey = new Object();

	public int runningTicks;
	public int processingTicks;
	public boolean running;

	public SmartFluidTankBehaviour outputTank;
	public SmartFluidTankBehaviour inputTank;
	public LazyOptional<IFluidHandler> fluidCapability;
	boolean contentsChanged;
	boolean mode;

	public VacuumChamberBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
		mode = false;
	}

	public boolean changeMode() {
		mode = !mode;
		return mode;
	}

	public float getRenderedHeadOffset(float partialTicks) {
		int localTick;
		float offset = 0;
		if (running) {
			if (runningTicks < 20) {
				localTick = runningTicks;
				float num = (localTick + partialTicks) / 20f;
				num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
				offset = num - .5f;
				offset = Mth.clamp(offset,0, 10 / 16f);
			} else if (runningTicks <= 20) {
				offset = 10 / 16f;
			} else {
				localTick = 40 - runningTicks;
				float num = (localTick - partialTicks) / 20f;
				num = ((2 - Mth.cos((float) (num * Math.PI))) / 2);
				offset = num - .5f;
				offset = Mth.clamp(offset,0, 10 / 16f);
			}
		}
		return offset + 7 / 16f;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);

		inputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, this, 2, 1000, true)
				.whenFluidUpdates(() -> contentsChanged = true);
		outputTank = new SmartFluidTankBehaviour(SmartFluidTankBehaviour.OUTPUT, this, 2, 1000, true)
				.whenFluidUpdates(() -> contentsChanged = true)
				.forbidInsertion();
		behaviours.add(inputTank);
		behaviours.add(outputTank);

		fluidCapability = LazyOptional.of(() -> {
			LazyOptional<? extends IFluidHandler> inputCap = inputTank.getCapability();
			LazyOptional<? extends IFluidHandler> outputCap = outputTank.getCapability();
			return new VacuumChamberTanksHandler(outputCap.orElse(null), inputCap.orElse(null));
		});
	}

	private class VacuumChamberTanksHandler extends CombinedTankWrapper {
		public VacuumChamberTanksHandler(IFluidHandler... fluidHandlers) {
			super(fluidHandlers);
		}
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).expandTowards(0, -1.5, 0);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		running = compound.getBoolean("Running");
		runningTicks = compound.getInt("Ticks");
		mode = compound.getBoolean("Mode");
		super.read(compound, clientPacket);

		if (clientPacket && hasLevel())
			getBasin().ifPresent(bte -> bte.setAreFluidsMoving(running && runningTicks <= 20));
	}

	@Override
	public void write(CompoundTag compound, boolean clientPacket) {
		compound.putBoolean("Running", running);
		compound.putInt("Ticks", runningTicks);
		compound.putBoolean("Mode", mode);
		super.write(compound, clientPacket);
	}

	@Override
	public void tick() {
		super.tick();

		if (runningTicks >= 40) {
			running = false;
			runningTicks = 0;
			basinChecker.scheduleUpdate();
			return;
		}

		float speed = Math.abs(getSpeed());
		if (running && level != null) {
			if (level.isClientSide && runningTicks == 20)
				renderParticles();

			if ((!level.isClientSide || isVirtual()) && runningTicks == 20) {
				if (processingTicks < 0) {
					float recipeSpeed = 1;
					if (currentRecipe instanceof ProcessingRecipe) {
						int t = ((ProcessingRecipe<?>) currentRecipe).getProcessingDuration();
						if (t != 0)
							recipeSpeed = t / 100f;
					}

					processingTicks = Mth.clamp((Mth.log2((int) (512 / speed))) * Mth.ceil(recipeSpeed * 15) + 1, 1, 512);

					Optional<BasinBlockEntity> basin = getBasin();
					if (basin.isPresent()) {
						Couple<SmartFluidTankBehaviour> tanks = basin.get()
								.getTanks();
						if (!tanks.getFirst()
								.isEmpty()
								|| !tanks.getSecond()
								.isEmpty())
							level.playSound(null, worldPosition, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT,
									SoundSource.BLOCKS, .75f, speed < 65 ? .75f : 1.5f);
					}

				} else {
					processingTicks--;
					if (processingTicks == 0) {
						runningTicks++;
						processingTicks = -1;
						applyBasinRecipe();
						sendData();
					}
				}
			}

			if (runningTicks != 20)
				runningTicks++;
		}
	}

	@Override
	protected void applyBasinRecipe() {
		if (currentRecipe == null)
			return;

		Optional<BasinBlockEntity> optionalBasin = getBasin();
		if (!optionalBasin.isPresent())
			return;
		BasinBlockEntity basin = optionalBasin.get();
		boolean wasEmpty = basin.canContinueProcessing();
		if (!mode)
			if (!VacuumizingRecipe.apply(basin, currentRecipe, this))
				return;
		if (mode)
			if (!PressurizingRecipe.apply(basin, currentRecipe, this))
				return;
		getProcessedRecipeTrigger().ifPresent(this::award);
		basin.inputTank.sendDataImmediately();

		// Continue mixing
		if (wasEmpty && matchBasinRecipe(currentRecipe)) {
			continueWithPreviousRecipe();
			sendData();
		}

		basin.notifyChangeOfContents();
	}

	@Override
	protected List<Recipe<?>> getMatchingRecipes() {
		Optional<BasinBlockEntity> basin = getBasin();

		if (basin.isEmpty()) return null;

		if (mode) {
			for (int i = 0; i < basin.get().getInputInventory().getSlots(); i++) {
				Optional<PressurizingRecipe> assemblyRecipe = SequencedAssemblyRecipe.
						getRecipe(level, basin.get().getInputInventory().getStackInSlot(i),
								VintageRecipes.PRESSURIZING.getType(), PressurizingRecipe.class);
				if (assemblyRecipe.isPresent() && basin.get().getFilter().test(assemblyRecipe.get()
						.getResultItem())) {
					if (!assemblyRecipe.get().getRequiredHeat().testBlazeBurner(BlazeBurnerBlock.getHeatLevelOf(level.getBlockState(getBlockPos().below(3)))))
						return getRecipes();

					for (Ingredient cur : assemblyRecipe.get().getIngredients()) {
						boolean find = false;

						for (ItemStack item : cur.getItems()) {
							if (item.getCount() <= basin.get().getInputInventory().countItem(item.getItem())) {
								find = true;
								break;
							}
						}

						if (!find) return getRecipes();
					}

					for (FluidIngredient cur : assemblyRecipe.get().getFluidIngredients()) {
						if (cur.test(basin.get().inputTank.getPrimaryHandler().getFluid())) break;

						return getRecipes();
					}

					return ImmutableList.of(assemblyRecipe.get());
				}
			}
		}
		else {
			for (int i = 0; i < basin.get().getInputInventory().getSlots(); i++) {
				Optional<VacuumizingRecipe> assemblyRecipe = SequencedAssemblyRecipe.
						getRecipe(level, basin.get().getInputInventory().getStackInSlot(i),
								VintageRecipes.VACUUMIZING.getType(), VacuumizingRecipe.class);
				if (assemblyRecipe.isPresent() && basin.get().getFilter().test(assemblyRecipe.get()
						.getResultItem())) {
					if (!assemblyRecipe.get().getRequiredHeat().testBlazeBurner(BlazeBurnerBlock.getHeatLevelOf(level.getBlockState(getBlockPos().below(3)))))
						return getRecipes();

					for (Ingredient cur : assemblyRecipe.get().getIngredients()) {
						boolean find = false;

						for (ItemStack item : cur.getItems()) {
							if (item.getCount() <= basin.get().getInputInventory().countItem(item.getItem())) {
								find = true;
								break;
							}
						}

						if (!find) return getRecipes();
					}

					for (FluidIngredient cur : assemblyRecipe.get().getFluidIngredients()) {
						if (cur.test(basin.get().inputTank.getPrimaryHandler().getFluid()))
							break;

						return getRecipes();
					}

					return ImmutableList.of(assemblyRecipe.get());
				}
			}
		}

		return getRecipes();
	}

	List<Recipe<?>> getRecipes() {
		List<Recipe<?>> res = new ArrayList<>();

		for (Recipe recipe : super.getMatchingRecipes()) {
			if (mode && recipe instanceof PressurizingRecipe) res.add(recipe);
			else if (!mode && recipe instanceof VacuumizingRecipe) res.add(recipe);
		}

		return res;
	}

	@Override
	protected <C extends Container> boolean matchBasinRecipe(Recipe<C> recipe) {
		if (recipe == null)
			return false;
		Optional<BasinBlockEntity> basin = getBasin();
		if (!basin.isPresent())
			return false;

		if (recipe instanceof VacuumizingRecipe r)
			return r.match(basin.get(), recipe, this);
		if (recipe instanceof PressurizingRecipe r)
			return r.match(basin.get(), recipe, this);

		return BasinRecipe.match(basin.get(), recipe);
	}

	public boolean acceptOutputs(List<FluidStack> outputFluids, boolean simulate) {
		outputTank.allowInsertion();
		boolean acceptOutputsInner = acceptOutputsInner(outputFluids, simulate);
		outputTank.forbidInsertion();
		return acceptOutputsInner;
	}

	private boolean acceptOutputsInner(List<FluidStack> outputFluids, boolean simulate) {
		BlockState blockState = getBlockState();
		if (!(blockState.getBlock() instanceof VacuumChamberBlock))
			return false;

		IFluidHandler targetTank = outputTank.getCapability()
				.orElse(null);

		if (outputFluids.isEmpty())
			return true;
		if (targetTank == null)
			return false;
		if (!acceptFluidOutputsIntoCentrifuge(outputFluids, simulate, targetTank))
			return false;

		return true;
	}

	private boolean acceptFluidOutputsIntoCentrifuge(List<FluidStack> outputFluids, boolean simulate,
													 IFluidHandler targetTank) {
		for (FluidStack fluidStack : outputFluids) {
			IFluidHandler.FluidAction action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
			int fill = targetTank instanceof SmartFluidTankBehaviour.InternalFluidHandler
					? ((SmartFluidTankBehaviour.InternalFluidHandler) targetTank).forceFill(fluidStack.copy(), action)
					: targetTank.fill(fluidStack.copy(), action);
			if (fill != fluidStack.getAmount())
				return false;
		}
		return true;
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return fluidCapability.cast();
		return super.getCapability(cap, side);
	}

	public void renderParticles() {
		Optional<BasinBlockEntity> basin = getBasin();
		if (!basin.isPresent() || level == null)
			return;

		if (!level.getBlockState(getBlockPos().above()).is(Blocks.AIR)) return;

		if (Mth.abs(getSpeed()) < IRotate.SpeedLevel.MEDIUM.getSpeedValue()) return;


		float angle = level.random.nextFloat() * 360;
		Vec3 offset = new Vec3(0, 0, 0.25f);
		offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
		Vec3 target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y)
				.add(0, .25f, 0);
		Vec3 center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		if (mode) level.addParticle(ParticleTypes.CLOUD, center.x + target.x * 10, center.y + 0.5f + target.y * 10, center.z + target.z * 10, -target.x * 0.6, -target.y * 0.6, -target.z * 0.6);
		else level.addParticle(ParticleTypes.CLOUD, center.x, center.y + 0.5f, center.z, target.x, target.y, target.z);
	}

	@Override
	protected <C extends Container> boolean matchStaticFilters(Recipe<C> r) {
		return r.getType() == VintageRecipes.VACUUMIZING.getType() || r.getType() == VintageRecipes.PRESSURIZING.getType();
	}

	@Override
	public void startProcessingBasin() {
		if (running && runningTicks <= 20)
			return;
		super.startProcessingBasin();
		running = true;
		runningTicks = 0;
	}

	@Override
	public boolean continueWithPreviousRecipe() {
		runningTicks = 20;
		return true;
	}

	@Override
	protected void onBasinRemoved() {
		if (!running)
			return;
		runningTicks = 40;
		running = false;
	}

	@Override
	protected Object getRecipeCacheKey() {
		return vacuumizingRecipesKey;
	}

	@Override
	protected boolean isRunning() {
		return running;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void tickAudio() {
		super.tickAudio();

		if (runningTicks == 25)
			AllSoundEvents.STEAM.playAt(level, worldPosition, 3, 1, true);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		super.addToGoggleTooltip(tooltip, isPlayerSneaking);

		if (mode) VintageLang.translate("gui.goggles.current_mode")
				.add(Lang.text(" ")).add(VintageLang.translate("gui.goggles.pressurizing_mode"))
				.style(ChatFormatting.DARK_PURPLE).forGoggles(tooltip);
		else VintageLang.translate("gui.goggles.current_mode")
				.add(Lang.text(" ")).add(VintageLang.translate("gui.goggles.vacuumizing_mode"))
				.style(ChatFormatting.DARK_AQUA).forGoggles(tooltip);

		IFluidHandler fluids = fluidCapability.orElse(new FluidTank(0));
		boolean isEmpty = true;

		LangBuilder mb = Lang.translate("generic.unit.millibuckets");
		for (int i = 0; i < fluids.getTanks(); i++) {
			FluidStack fluidStack = fluids.getFluidInTank(i);
			if (fluidStack.isEmpty())
				continue;
			Lang.text("")
					.add(Lang.fluidName(fluidStack)
							.add(Lang.text(" "))
							.style(ChatFormatting.GRAY)
							.add(Lang.number(fluidStack.getAmount())
									.add(mb)
									.style(ChatFormatting.BLUE)))
					.forGoggles(tooltip, 1);
			isEmpty = false;
		}

		if (isEmpty)
			tooltip.remove(0);

		return true;
	}

}
