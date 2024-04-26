package com.negodya1.vintageimprovements.content.kinetics.curving_press;

import com.negodya1.vintageimprovements.VintageBlockEntity;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintageItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CurvingPressBlock extends HorizontalKineticBlock implements IBE<CurvingPressBlockEntity> {

	public static final TagKey<Item> headTag = ItemTags.create(new ResourceLocation("vintageimprovements", "curving_heads"));

	public CurvingPressBlock(Properties properties) {
		super(properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext
			&& ((EntityCollisionContext) context).getEntity() instanceof Player)
			return AllShapes.CASING_14PX.get(Direction.DOWN);

		return AllShapes.MECHANICAL_PROCESSOR_SHAPE;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
		return !AllBlocks.BASIN.has(worldIn.getBlockState(pos.below()));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction prefferedSide = getPreferredHorizontalFacing(context);
		if (prefferedSide != null)
			return defaultBlockState().setValue(HORIZONTAL_FACING, prefferedSide);
		return super.getStateForPlacement(context);
	}

	@Override
	public Axis getRotationAxis(BlockState state) {
		return state.getValue(HORIZONTAL_FACING)
			.getAxis();
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return face.getAxis() == state.getValue(HORIZONTAL_FACING)
			.getAxis();
	}

	@Override
	public Class<CurvingPressBlockEntity> getBlockEntityClass() {
		return CurvingPressBlockEntity.class;
	}

	@Override
	public BlockEntityType<? extends CurvingPressBlockEntity> getBlockEntityType() {
		return VintageBlockEntity.CURVING_PRESS.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}

	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
								 BlockHitResult hit) {
		ItemStack heldItem = player.getItemInHand(handIn);

		return onBlockEntityUse(worldIn, pos, be -> {
			if (be.mode == 0) {

				be.itemAsHead = ItemStack.EMPTY;

				if (heldItem.is(VintageItems.CONVEX_CURVING_HEAD.get())) {
					be.mode = 1;
					if (!player.isCreative())
						player.getItemInHand(handIn).shrink(1);
					if (worldIn.isClientSide())
						AllSoundEvents.WRENCH_ROTATE.playAt(worldIn, pos, 3, 1,true);
					return InteractionResult.SUCCESS;
				}

				if (heldItem.is(VintageItems.CONCAVE_CURVING_HEAD.get())) {
					be.mode = 2;
					if (!player.isCreative())
						player.getItemInHand(handIn).shrink(1);
					if (worldIn.isClientSide())
						AllSoundEvents.WRENCH_ROTATE.playAt(worldIn, pos, 3, 1,true);
					return InteractionResult.SUCCESS;
				}

				if (heldItem.is(VintageItems.W_SHAPED_CURVING_HEAD.get())) {
					be.mode = 3;
					if (!player.isCreative())
						player.getItemInHand(handIn).shrink(1);
					if (worldIn.isClientSide())
						AllSoundEvents.WRENCH_ROTATE.playAt(worldIn, pos, 3, 1,true);
					return InteractionResult.SUCCESS;
				}

				if (heldItem.is(VintageItems.V_SHAPED_CURVING_HEAD.get())) {
					be.mode = 4;
					if (!player.isCreative())
						player.getItemInHand(handIn).shrink(1);
					if (worldIn.isClientSide())
						AllSoundEvents.WRENCH_ROTATE.playAt(worldIn, pos, 3, 1,true);
					return InteractionResult.SUCCESS;
				}

				if (heldItem.is(headTag)) {
					be.mode = 5;
					be.itemAsHead = new ItemStack(heldItem.getItem());
					if (!player.isCreative())
						player.getItemInHand(handIn).shrink(1);
					if (worldIn.isClientSide())
						AllSoundEvents.WRENCH_ROTATE.playAt(worldIn, pos, 3, 1,true);
					return InteractionResult.SUCCESS;
				}

			}
			else {
				if (heldItem.is(AllItems.WRENCH.asItem())) {
					ItemStack stack;

					switch (be.mode) {
						case 2 -> stack = new ItemStack(VintageItems.CONCAVE_CURVING_HEAD.get());
						case 3 -> stack = new ItemStack(VintageItems.W_SHAPED_CURVING_HEAD.get());
						case 4 -> stack = new ItemStack(VintageItems.V_SHAPED_CURVING_HEAD.get());
						case 5 -> {
							if (be.itemAsHead != null) stack = be.itemAsHead.copy();
							else stack = ItemStack.EMPTY;
							be.itemAsHead = ItemStack.EMPTY;
						}
						default -> stack = new ItemStack(VintageItems.CONVEX_CURVING_HEAD.get());
					}

					player.getInventory()
							.placeItemBackInInventory(stack);

					be.mode = 0;

					if (worldIn.isClientSide())
						AllSoundEvents.WRENCH_ROTATE.playAt(worldIn, pos, 3, 1,true);

					return InteractionResult.SUCCESS;
				}
			}

			return InteractionResult.PASS;
		});
	}

}
