package com.negodya1.vintageimprovements.content.kinetics.lathe;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.simibubi.create.content.kinetics.base.SingleRotatingInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;

public class LatheMovingInstance extends SingleRotatingInstance<LatheMovingBlockEntity> {

	public LatheMovingInstance(MaterialManager materialManager, LatheMovingBlockEntity blockEntity) {
		super(materialManager, blockEntity);
	}

	@Override
	protected Instancer<RotatingData> getModel() {
		return getRotatingMaterial().getModel(shaft());
	}
}
