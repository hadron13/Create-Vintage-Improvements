package com.negodya1.vintageimprovements.content.kinetics.curving_press;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.negodya1.vintageimprovements.VintagePartialModels;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;

public class CurvingPressInstance extends ShaftInstance<CurvingPressBlockEntity> implements DynamicInstance {

	public CurvingPressInstance(MaterialManager materialManager, CurvingPressBlockEntity blockEntity) {
		super(materialManager, blockEntity);
	}

	@Override
	public void beginFrame() {}
}
