package com.negodya1.vintageimprovements.content.kinetics.centrifuge;

import com.negodya1.vintageimprovements.content.kinetics.vibration.VibratingTableBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class CentrifugeGenerator extends SpecialBlockStateGen {

	@Override
	protected int getXRotation(BlockState state) {
		return 0;
	}

	@Override
	protected int getYRotation(BlockState state) {
		Direction facing = state.getValue(VibratingTableBlock.HORIZONTAL_FACING);
		return horizontalAngle(facing);
	}

	@Override
	public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
												BlockState state) {
		String path = "block/" + ctx.getName() + "/";

		return prov.models()
				.getExistingFile(prov.modLoc(path + "block"));
	}

}
