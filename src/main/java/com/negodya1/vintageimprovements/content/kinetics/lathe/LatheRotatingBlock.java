package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.negodya1.vintageimprovements.VintageBlockEntity;
import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HelveItem;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HelveKineticBlock;
import com.negodya1.vintageimprovements.content.kinetics.helve_hammer.HelveStructuralBlock;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import com.simibubi.create.foundation.utility.Components;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class LatheRotatingBlock extends HorizontalKineticBlock implements IBE<LatheRotatingBlockEntity> {
	public LatheRotatingBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockState stateForPlacement = super.getStateForPlacement(context);
		Direction direction = context.getHorizontalDirection();
		Player player = context.getPlayer();

		stateForPlacement = stateForPlacement.setValue(HORIZONTAL_FACING, direction.getOpposite());

		for (int x = 0; x <= 1; x++) {
			int xOffset = x;
			if (direction == Direction.NORTH || direction == Direction.WEST) xOffset *= -1;

			BlockPos offset = new BlockPos((direction == Direction.NORTH || direction == Direction.SOUTH ? 0 : xOffset), 0, (direction == Direction.NORTH || direction == Direction.SOUTH ? xOffset : 0));
			if (offset.equals(BlockPos.ZERO))
				continue;
			BlockState occupiedState = context.getLevel()
					.getBlockState(context.getClickedPos().offset(offset));
			if (!occupiedState.canBeReplaced(context))
				return null;
		}

		return stateForPlacement;
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING).getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face == state.getValue(HORIZONTAL_FACING);
	}

	@Override
	public SpeedLevel getMinimumRequiredSpeedLevel() {
		return SpeedLevel.FAST;
	}

	@Override
	public Class<LatheRotatingBlockEntity> getBlockEntityClass() {
		return LatheRotatingBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends LatheRotatingBlockEntity> getBlockEntityType() {
		return VintageBlockEntity.LATHE_ROTATING.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

	public static boolean isHorizontal(BlockState state) {
		return true;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
		if (worldIn.isClientSide)
			return InteractionResult.SUCCESS;

		if (player.getItemInHand(handIn)
				.isEmpty())
			withBlockEntityDo(worldIn, pos, lathe -> {
				boolean emptyOutput = true;
				IItemHandlerModifiable inv = lathe.outputInv;
				for (int slot = 0; slot < inv.getSlots(); slot++) {
					ItemStack stackInSlot = inv.getStackInSlot(slot);
					if (!stackInSlot.isEmpty())
						emptyOutput = false;
					player.getInventory()
							.placeItemBackInInventory(stackInSlot);
					inv.setStackInSlot(slot, ItemStack.EMPTY);
				}

				if (emptyOutput) {
					inv = lathe.inputInv;
					for (int slot = 0; slot < inv.getSlots(); slot++) {
						player.getInventory()
								.placeItemBackInInventory(inv.getStackInSlot(slot));
						inv.setStackInSlot(slot, ItemStack.EMPTY);
					}
				}

					lathe.setChanged();
					lathe.sendData();
		});
		else {
			withBlockEntityDo(worldIn, pos, lathe -> {
				if (lathe.checkItem(player.getItemInHand(handIn))) {
					player.setItemInHand(handIn, lathe.inputInv.insertItem(0, player.getItemInHand(handIn), false));

					lathe.setChanged();
					lathe.sendData();
				}
			});
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, level, pos, oldState, isMoving);
		if (!level.getBlockTicks()
				.hasScheduledTick(pos, this))
			level.scheduleTick(pos, this, 1);
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		IBE.onRemove(pState, pLevel, pPos, pNewState);
		pLevel.removeBlockEntity(pPos);
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRandom) {

		Direction side = pState.getValue(HORIZONTAL_FACING);
		BlockPos structurePos = pPos.relative(side.getOpposite());
		BlockState occupiedState = pLevel.getBlockState(structurePos);
		BlockState requiredStructure = VintageBlocks.LATHE_MOVING.getDefaultState()
				.setValue(HelveStructuralBlock.FACING, side);
		if (occupiedState != requiredStructure) {
			if (!occupiedState.getMaterial().isReplaceable()) {
				pLevel.destroyBlock(pPos, false);
				return;
			}
			pLevel.setBlockAndUpdate(structurePos, requiredStructure);
		}
	}

	public static BlockPos getSlave(BlockGetter level, BlockPos pos, BlockState state) {
		Direction direction = state.getValue(HORIZONTAL_FACING);
		BlockPos targetedPos = pos.relative(direction.getOpposite(), 1);
		BlockState targetedState = level.getBlockState(targetedPos);
		if (targetedState.is(VintageBlocks.LATHE_ROTATING.get()))
			return getSlave(level, targetedPos, targetedState);
		return targetedPos;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack itemStack, @Nullable BlockGetter getter, List<Component> list, TooltipFlag flag) {
		list.add(Components.translatable(VintageImprovements.MODID + ".item_description.machine_rpm_requirements").append(" " + SpeedLevel.FAST.getSpeedValue()).withStyle(ChatFormatting.GOLD));
		list.add(Components.translatable(VintageImprovements.MODID + ".item_description.additional_machine_rpm_requirements").append(" " + SpeedLevel.MEDIUM.getSpeedValue()).withStyle(ChatFormatting.GOLD));
	}

}
