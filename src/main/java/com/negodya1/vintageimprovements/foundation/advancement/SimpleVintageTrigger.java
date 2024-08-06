package com.negodya1.vintageimprovements.foundation.advancement;

import com.negodya1.vintageimprovements.VintageImprovements;
import com.simibubi.create.foundation.advancement.SimpleCreateTrigger;

import net.minecraft.resources.ResourceLocation;

public class SimpleVintageTrigger extends SimpleCreateTrigger {

    private ResourceLocation trueID;

    public SimpleVintageTrigger(String id) {
        super(id);
        trueID = VintageImprovements.asResource(id);
    };

    @Override
    public ResourceLocation getId() {
        return trueID;
    };
    
};
