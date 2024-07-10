package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LatheRotatingInstance extends SingleRotatingInstance<LatheRotatingBlockEntity> {

	public LatheRotatingInstance(MaterialManager materialManager, LatheRotatingBlockEntity blockEntity) {
		super(materialManager, blockEntity);
	}

	@Override
	protected Instancer<RotatingData> getModel() {
		BlockState referenceState = blockState;
		Direction facing = referenceState.getValue(BlockStateProperties.HORIZONTAL_FACING);
		return getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, referenceState, facing);
	}
}
