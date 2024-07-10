package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.jozufozu.flywheel.backend.Backend;
import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.negodya1.vintageimprovements.VintageImprovements;
import com.negodya1.vintageimprovements.VintagePartialModels;
import com.negodya1.vintageimprovements.content.kinetics.centrifuge.CentrifugeBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.grinder.GrinderBlockEntity;
import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlockEntity;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;

import com.simibubi.create.foundation.utility.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;

import com.jozufozu.flywheel.util.transform.TransformStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour.TankSegment;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import com.simibubi.create.foundation.utility.IntAttached;
import com.simibubi.create.foundation.utility.VecHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.DirectionalBlock.FACING;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;

public class LatheMovingRenderer extends KineticBlockEntityRenderer<LatheMovingBlockEntity> {

	public LatheMovingRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRenderOffScreen(LatheMovingBlockEntity be) {
		return true;
	}

	@Override
	protected void renderSafe(LatheMovingBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		renderSlot(be, ms, buffer, light, overlay);

		if (Backend.canUseInstancing(be.getLevel()))
			return;

		renderShaft(be, ms, buffer, light, overlay);
	}

	protected void renderShaft(LatheMovingBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		KineticBlockEntityRenderer.renderRotatingBuffer(be, getRotatedModel(be, be.getBlockState()), ms, buffer.getBuffer(RenderType.solid()), light);
	}

	protected void renderSlot(LatheMovingBlockEntity be, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		BlockState blockState = be.getBlockState();
		VertexConsumer vb = buffer.getBuffer(RenderType.solid());

		PartialModel partial = (be.recipeSlot.isEmpty() ? VintagePartialModels.LATHE_SLOT : VintagePartialModels.LATHE_SLOT_FULL);

		SuperByteBuffer superBuffer = CachedBufferer.partial(partial, blockState);
		superBuffer.rotateCentered(Direction.UP,
				blockState.getValue(FACING) == Direction.SOUTH ? (180*(float)Math.PI/180f) :
						blockState.getValue(FACING) == Direction.WEST ? (90*(float)Math.PI/180f) :
								blockState.getValue(FACING) == Direction.EAST ? (270*(float)Math.PI/180f) : 0)
				.light(light).renderInto(ms, vb);
	}

	protected SuperByteBuffer getRotatedModel(LatheMovingBlockEntity be, BlockState state) {
		return CachedBufferer.block(KineticBlockEntityRenderer.KINETIC_BLOCK,
				getRenderedBlockState(be));
	}

	protected BlockState getRenderedBlockState(LatheMovingBlockEntity be) {
		return KineticBlockEntityRenderer.shaft(KineticBlockEntityRenderer.getRotationAxisOf(be));
	}

}
