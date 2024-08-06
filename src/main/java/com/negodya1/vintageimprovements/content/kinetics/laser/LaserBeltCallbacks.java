package com.negodya1.vintageimprovements.content.kinetics.laser;

import com.negodya1.vintageimprovements.content.kinetics.laser.LaserBlock;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.Direction;

public class LaserBeltCallbacks {
	public static BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, LaserBlockEntity be) {
		return BeltProcessingBehaviour.ProcessingResult.HOLD;
	}

	public static BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler, LaserBlockEntity be) {
		return be.onLaser(transported, handler);
	}
}